/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.drunkinvaders;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.extras.Explosion;
import uk.co.nickthecoder.itchy.extras.Fragment;
import uk.co.nickthecoder.itchy.extras.Talk;
import uk.co.nickthecoder.itchy.util.Property;
import uk.co.nickthecoder.jame.Keys;
import uk.co.nickthecoder.jame.RGBA;

public class Ship extends Bouncy implements Shootable
{
    private static final int TIMER_DURATION = 40;

    private static final int SHIELD_POSE_COUNT = 7;

    private static RGBA SPEECH_COLOR = new RGBA(0, 0, 0);

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

    @Override
    public void init()
    {
        super.init();

        this.mass = 100000000000.0;

        this.radius = Math.sqrt(
            (this.getActor().getX() - this.ox) * (this.getActor().getX() - this.ox) +
                (this.getActor().getY() - this.oy) * (this.getActor().getY() - this.oy));

        this.angle = Math.atan2(this.getActor().getY() - this.oy, this.getActor().getX() - this.ox);
        this.getActor().getAppearance().setDirectionRadians(this.angle);
        this.turn(0); // calculates the direction

        // Create the fragments for the explosions when I get shot.
        new Fragment().actor(this.getActor()).createPoses("fragment");
    }

    @Override
    public void onAttach()
    {
        super.onAttach();

        this.getActor().addTag("killable");
        this.getActor().removeTag("bouncy");
    }

    @Override
    public void onDetach()
    {
        super.onDetach();

        this.getActor().removeTag("killable");
    }

    @Override
    public void onKill()
    {
        if (this.collisionStrategy != null) {
            this.collisionStrategy.remove();
            this.collisionStrategy = null;
        }
    }

    @Override
    public void tick()
    {

        if (Itchy.isKeyDown(Keys.LSHIFT) || Itchy.isKeyDown(Keys.RSHIFT)) {
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

            if (Itchy.isKeyDown(Keys.LEFT)) {
                this.turn(this.rotationSpeed);
            }
            if (Itchy.isKeyDown(Keys.RIGHT)) {
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

                if (Itchy.isKeyDown(Keys.SPACE)) {
                    this.fire();
                }
            }

            this.collisionStrategy.update();

            for (Actor other : touching(DEADLY_LIST)) {
                this.shot(other);
                if (other.getBehaviour() instanceof Shootable) {
                    ((Shootable) other.getBehaviour()).shot(this.getActor());
                }
                break;
            }

        }

    }

    private void turn( double speed )
    {
        double oldX = this.getActor().getX();
        double oldY = this.getActor().getY();
        double oldDirection = this.getActor().getAppearance().getDirection();

        this.angle += speed;

        this.getActor().getAppearance().setDirectionRadians(this.angle);
        this.getActor().setX(this.radius * Math.cos(this.angle) + this.ox);
        this.getActor().setY(this.radius * Math.sin(this.angle) + this.oy);

        if (this.getActor().isOffScreen()) {
            this.angle -= speed;
            this.getActor().moveTo(oldX, oldY);
            this.getActor().getAppearance().setDirection(oldDirection);
        }
    }

    public void activateShield()
    {
        long level = Math.round(this.shieldStrength * SHIELD_POSE_COUNT);
        if (level > 0) {
            this.event("shield");
            this.getActor().addTag("bouncy");
            long newLevel = Math.round(this.shieldStrength * SHIELD_POSE_COUNT);
            event("shielded" + newLevel);
            this.shielded = true;
        } else {
            this.event("shieldFailed");
        }
    }

    public void deactivateShield()
    {
        this.getActor().removeTag("bouncy");
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

        Actor bullet = new Actor(DrunkInvaders.game.resources.getCostume("bullet"), "default");
        this.latestBullet = bullet;
        bullet.moveTo(this.getActor());
        bullet.getAppearance().setDirection(this.getActor().getAppearance().getDirection());
        DrunkInvaders.game.mainLayer.add(bullet);
        bullet.moveForward(10);
        bullet.setBehaviour(new Bullet());
        bullet.addTag("killable");
        bullet.activate();

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
            .message("death")
            .font("vera", 18)
            .color(SPEECH_COLOR)
            .bubble("speechBubble2")
            .offset(0, 40)
            .margin(10, 10, 20, 10)
            .createActor();
        yell.activate();
        yell.deathEvent(this.getActor().getCostume(), "yell");

        new Explosion(this.getActor())
            .projectiles(20)
            .forwards()
            .speed(0.3, 0.9)
            .fade(.7)
            .spin(-0.2, 0.2)
            .createActor("fragment").activate();

        new Explosion(this.getActor())
            .projectiles(40)
            .distance(0, 10)
            .speed(1, 3)
            .fade(1.5)
            .createActor("pixel").activate();

        this.deathEvent("death");
    }
}
