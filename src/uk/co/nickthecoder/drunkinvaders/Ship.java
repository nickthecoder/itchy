/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.drunkinvaders;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Input;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.extras.Fragment;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.role.Explosion;
import uk.co.nickthecoder.itchy.role.OnionSkin;
import uk.co.nickthecoder.itchy.role.Talk;
import uk.co.nickthecoder.itchy.util.Tag;

@Tag(names = { "killable" })
public class Ship extends Bouncy implements Shootable
{
    private static final int TIMER_DURATION = 40;

    private static final int SHIELD_POSE_COUNT = 7;

    public static final String[] DEADLY_LIST = new String[] { "deadly" };

    @Property(label = "Planet Center X")
    public double ox = 320;

    @Property(label = "Planet Center Y")
    public double oy = -1300;

    @Property(label = "Speed")
    public double rotationSpeed = 0.003;

    @Property(label = "Shield's Recharge Rate")
    public double shieldRechargeRate = 0.001;

    @Property(label = "Shield's Discharge Rate")
    public double shieldDischargeRate = 0.01;

    public double radius;

    private double angle;

    private Actor latestBullet;

    private boolean shielded = false;

    private int recharge = 0;

    /**
     * 1 is fully charged 0 is fully drained.
     */
    private double shieldStrength = 1.0;

    protected Input inputLeft;

    protected Input inputRight;

    protected Input inputShield;

    protected Input inputFire;

    @Override
    public void onBirth()
    {
        super.onBirth();

        new OnionSkin(this).alpha(128).createActor();
        this.mass = 100000000000.0;

        this.radius = Math.sqrt(
            (getActor().getX() - this.ox) * (getActor().getX() - this.ox) + (getActor().getY() - this.oy) * (getActor().getY() - this.oy));

        this.angle = Math.atan2(getActor().getY() - this.oy, getActor().getX() - this.ox);
        getActor().getAppearance().setDirectionRadians(this.angle);
        turn(0); // calculates the direction

        // Create the fragments for the explosions when I get shot.
        new Fragment().actor(getActor()).createPoses("fragment");

        this.inputLeft = Input.find("left");
        this.inputRight = Input.find("right");
        this.inputShield = Input.find("shield");
        this.inputFire = Input.find("fire");
    }

    @Override
    public void onAttach()
    {
        // Do NOT call super, as we don't want to be bouncy while the shields are off.
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
    }

    @Override
    public void tick()
    {

        if (this.inputShield.pressed()) {
            if (!this.shielded) {
                activateShield();
            }
        } else {
            if (this.shielded) {
                deactivateShield();
            }
        }

        if (this.shielded) {
            super.tick();
            dischargeShield();

        } else {

            chargeShield();

            if (this.inputLeft.pressed()) {
                this.turn(this.rotationSpeed);
            }
            if (this.inputRight.pressed()) {
                this.turn(-this.rotationSpeed);
            }

            if (this.recharge > 0) {
                this.recharge--;
                if ((this.recharge == 0) ||
                    ((this.latestBullet != null) && this.latestBullet.isDead())) {
                    this.event("charged");
                    this.recharge = 0;
                }
            } else {

                if (this.inputFire.pressed()) {
                    this.fire();
                }
            }

            getCollisionStrategy().update();

            for (Role role : getCollisionStrategy().collisions(getActor(), DEADLY_LIST)) {
                Actor other = role.getActor();
                this.shot(other);
                if (other.getRole() instanceof Shootable) {
                    ((Shootable) other.getRole()).shot(getActor());
                }
                break;
            }

        }

    }

    private void turn( double speed )
    {
        double oldX = getActor().getX();
        double oldY = getActor().getY();
        double oldDirection = getActor().getHeading();

        this.angle += speed;

        getActor().setDirectionRadians(this.angle);
        getActor().setX(this.radius * Math.cos(this.angle) + this.ox);
        getActor().setY(this.radius * Math.sin(this.angle) + this.oy);

        if (!getActor().getAppearance().getWorldRectangle().within(DrunkInvaders.director.worldBounds)) {
            this.angle -= speed;
            getActor().moveTo(oldX, oldY);
            getActor().setDirection(oldDirection);
        }
    }

    public void activateShield()
    {
        long level = Math.round(this.shieldStrength * SHIELD_POSE_COUNT);
        if (level > 0) {
            this.event("shield");
            addTag("bouncy");
            long newLevel = Math.round(this.shieldStrength * SHIELD_POSE_COUNT);
            event("shielded" + newLevel);
            this.shielded = true;
        } else {
            this.event("shieldFailed");
        }
    }

    public void deactivateShield()
    {
        removeTag("bouncy");
        this.event("deshield");
        this.shielded = false;
    }

    private void dischargeShield()
    {
        long oldLevel = Math.round(this.shieldStrength * SHIELD_POSE_COUNT);
        this.shieldStrength -= this.shieldDischargeRate;
        long newLevel = Math.round(this.shieldStrength * SHIELD_POSE_COUNT);

        if (this.shieldStrength < 0) {
            this.deactivateShield();
            return;
        }

        if (oldLevel != newLevel) {
            event("shielded" + newLevel);
        }
    }

    private void chargeShield()
    {
        this.shieldStrength += this.shieldRechargeRate;
        if (this.shieldStrength > 1) {
            this.shieldStrength = 1;
        }
    }

    public void fire()
    {
        if (this.shielded) {
            return;
        }

        this.event("fire");

        Actor bulletActor = getActor().createCompanion("bullet");
        this.latestBullet = bulletActor;
        bulletActor.setDirection(getActor().getAppearance().getDirection());
        Bullet bullet = (Bullet) bulletActor.getRole();
        bullet.addTag("killable");
        bulletActor.moveForwards(10);

        this.recharge = TIMER_DURATION;
    }

    @Override
    public void shot( Actor other )
    {
        if (this.shielded) {
            this.event("deflect");
            return;
        }

        Actor yell = new Talk(this)
            .eventName("death").style("shout")
            .offset(0, 60).alignment(0.5, 0).direction(0)
            .createActor();
        yell.setCostume(getActor().getCostume());
        yell.deathEvent("shout");

        new Explosion(getActor())
            .projectiles(20)
            .speed(0.3, 0.9, 0, 0)
            .fade(2)
            .spin(-0.2, 0.2)
            .eventName("fragment")
            .createActor();

        new Explosion(getActor())
            .projectiles(40)
            .offsetForwards(-10, 10).offsetSidewards(-10, 10)
            .speed(1, 3, 0, 0)
            .fade(2)
            .eventName("pixel")
            .createActor();

        deathEvent("death");
    }
}
