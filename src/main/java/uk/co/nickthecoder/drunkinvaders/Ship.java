/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.drunkinvaders;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Input;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.extras.Fragments;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.DoubleProperty;
import uk.co.nickthecoder.itchy.role.ExplosionBuilder;
import uk.co.nickthecoder.itchy.role.OnionSkinBuilder;
import uk.co.nickthecoder.itchy.role.TalkBuilder;
import uk.co.nickthecoder.itchy.util.Tag;

@Tag(names = { "killable" })
public class Ship extends Bouncy implements Shootable
{

    protected static final List<Property<Role, ?>> properties = new ArrayList<Property<Role, ?>>();

    static {
        properties.add(new DoubleProperty<Role>("ox").label("Planet's Center X"));
        properties.add(new DoubleProperty<Role>("oy").label("Planet's Center Y"));
        properties.add(new DoubleProperty<Role>("rotationSpeed"));
        properties.add(new DoubleProperty<Role>("shieldRechargeRate"));
        properties.add(new DoubleProperty<Role>("shieldDischargeRate"));
    }

    private static final int TIMER_DURATION = 40;

    private static final int SHIELD_POSE_COUNT = 7;

    public static final String[] DEADLY_LIST = new String[] { "deadly" };

    private Fragments fragments;

    public double ox = 320;

    public double oy = -1300;

    public double rotationSpeed = 0.003;

    public double shieldRechargeRate = 0.001;

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

    protected Input inputDie;

    @Override
    public List<Property<Role, ?>> getProperties()
    {
        return properties;
    }

    @Override
    public void onBirth()
    {
        super.onBirth();

        new OnionSkinBuilder(getActor()).alpha(128).create();
        this.mass = 100000000000.0;

        this.radius = Math.sqrt((getActor().getX() - this.ox) * (getActor().getX() - this.ox)
                        + (getActor().getY() - this.oy) * (getActor().getY() - this.oy));

        this.angle = Math.atan2(getActor().getY() - this.oy, getActor().getX() - this.ox);
        getActor().getAppearance().setDirectionRadians(this.angle);
        turn(0); // calculates the direction

        // Create the fragments for the explosions when I get shot.
        fragments = new Fragments().create( getActor().getCostume() );

        this.inputLeft = Input.find("left");
        this.inputRight = Input.find("right");
        this.inputShield = Input.find("shield");
        this.inputFire = Input.find("fire");
        this.inputDie = Input.find("die");
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
        if (this.inputDie.pressed()) {
            this.shot(this.getActor());
            return;
        }

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
                if ((this.recharge == 0) || ((this.latestBullet != null) && this.latestBullet.isDead())) {
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

    private void turn(double speed)
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
    public void shot(Actor other)
    {
        if (this.shielded) {
            this.event("deflect");
            return;
        }

        Actor yell = new TalkBuilder(getActor()).eventName("death").style("shout").offset(0, 60).alignment(0.5, 0)
                        .direction(0).create().getActor();
        yell.setCostume(getActor().getCostume());
        yell.deathEvent("shout");

        new ExplosionBuilder(getActor()).speed(1.5, 1.6, 0, 0).fade(2).spin(-0.4, 0.4).fragments(fragments).create();

        new ExplosionBuilder(getActor()).projectiles(40).offsetForwards(-10, 10).offsetSidewards(-10, 10)
                        .speed(1, 3, 0, 0).fade(2).eventName("pixel").create();

        deathEvent("death");
    }
}
