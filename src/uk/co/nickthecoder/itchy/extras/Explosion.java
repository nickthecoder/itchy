/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.extras;

import java.util.Random;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Appearance;
import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.Itchy;

/**
 * Creates many particles from a central point, spreading outwards. This is typically used when an
 * actor is destroyed.
 * 
 * Many of the methods return itself, which is designed to make setting up the explosion easier. For
 * example, this code snippet taken from "It's Gonna Rain" calls many methods :
 * 
 * <pre>
 * <code>
 * new Explosion(actor)
 *     .projectiles(10).gravity(-0.2).fade(0.9, 3.5).speed(0.1, 1.5).vy(5)
 *     .pose("droplet").createActor().activate(); 
 * </code>
 * </pre>
 * 
 * And is the same as this long winded form :
 * 
 * <pre>
 * <code>
 * Explosion explosion = new itchy.extras.Explosion(actor);
 * explosion.projectiles(10);
 * explosion.gravity(-0.2);
 * explosion.fade(0.9, 3.5);
 * explosion.speed(0.1, 1.5);
 * explosion.vy(5);
 * explosion.pose("droplet");
 * explosion.createActor().activate();
 * </code>
 * </pre>
 * 
 * Note that the createActor method returns an Actor (not the Explosion), and therefore must be
 * last.
 */
public class Explosion extends Companion<Explosion>
{
    public static final double DEFAULT_LIFE_SECONDS = 6;

    private static Random random = new Random();

    private String poseName;

    public int totalProjectiles = 0;

    public int projectileCounter = 0;

    public int countPerTick = 1000;

    public double gravity = 0;

    public double spin = 0;

    public double randomSpin = 0;

    public double vx = 0;

    public double randomVx = 0;

    public double vy = 0;

    public double randomVy = 0;

    public double speedForwards = 0;

    public double speedSidewards = 0;

    public double randomSpeedForwards = 0;

    public double randomSpeedSidewards = 0;

    public double fade = 0;

    public double randomFade = 0;

    public int lifeTicks;

    public int randomLifeTicks = 0;

    public double grow = 1;

    public int randomAlpha = 0;

    public double randomDirection = 0;

    public boolean randomSpread = true;

    public double spreadFrom = 0;

    public double spreadTo = 360;

    public double randomGrow = 0;

    public double randomScale = 0;

    public double randomOffsetForwards = 0;

    public double randomOffsetSidewards = 0;

    public String projectileEventName;

    public Explosion( Behaviour behaviour )
    {
        this(behaviour.getActor());
    }

    /**
     * Creates an explosion centred on the given actor.
     * 
     * @param actor
     */
    public Explosion( Actor actor )
    {
        super(actor);
        this.direction = actor.getAppearance().getDirection();
        this.lifeTicks = (int) (DEFAULT_LIFE_SECONDS * Itchy.frameRate.getRequiredRate());
    }

    /**
     * Creates the explosion actor using a pose from the costume of the actor given in the
     * constructor. This allows the exploding projectiles to look different from the actor that
     * created the explosion.
     * 
     * Note that if the costume has multiple poses with the same pose name, then a random one is
     * chosen for each projectile. This is very handy if you want to mimic an object breaking apart
     * into pieces. You can either draw each fragment yourself, or use {@link Fragment} to cut your
     * image into pieces automatically.
     * 
     * @param poseName
     *        The name of the pose with the actor's costume.
     * @return A new actor, which has an Explosion Behaviour, has been added to the same layer as
     *         the actor in the constructor, but has not been activated yet.
     */
    @Override
    public Actor createActor()
    {
        Actor result = super.createActor();

        result.getAppearance().setAlpha(0);

        return result;
    }

    /**
     * The projectiles will fire this event when each Projectile's Actor is created. The event can
     * select a Pose, an Animation, and can play a sound. If you only want to select a pose, then
     * use {@link #pose(String)}. It will not fire the event when the Explosion's actor is created.
     * 
     * @param eventName
     *        The name of the event to fire when the actor is created.
     * @return this
     */
    @Override
    public Explosion startEvent( String eventName )
    {
        this.projectileEventName = eventName;
        return this;
    }

    /**
     * Sets the number of projectiles to create.
     * 
     * @param value
     * @return this
     */
    public Explosion projectiles( int value )
    {
        this.totalProjectiles = value;
        return this;
    }

    /**
     * @param value
     *        The number of projectiles to be created each frame. For an explosion, you probably
     *        want all of the projectiles to be created simultaneously, so don't call this method.
     * @return this
     */
    public Explosion projectilesPerTick( int value )
    {
        this.countPerTick = value;
        return this;
    }

    /**
     * @param value
     *        The amount of change to the projectiles' Y velocity each frame. A value of around -0.2
     *        gives a pleasing effect.
     * @return this
     */
    public Explosion gravity( double value )
    {
        this.gravity = value;
        return this;
    }

    /**
     * @param value
     *        How many degrees the projectiles should turn each frame.
     * @return this
     */
    public Explosion spin( double value )
    {
        return spin(value, value);
    }

    /**
     * Randomly spin the projectiles. Note that the spin is calculated once for each projectile. It
     * is typical to use <code>spin( -N, N )</code> so that the projectiles, where N defines the
     * maximum speed of rotation.
     * 
     * @param from
     *        The lowest number of degrees to turn the projectiles each frame.
     * @param to
     *        The highest number of degrees to turn the projectiles each frame.
     * @return this
     */
    public Explosion spin( double from, double to )
    {
        this.spin = from;
        this.randomSpin = to - from;
        return this;
    }

    /**
     * A constant X velocity for all projectiles. Note, this can be used in conjunction with speed,
     * the projectiles' velocities will be the sum of their speed in the direction of their heading,
     * and their (vx,vy) velocity.
     * 
     * @param value
     *        The initial X velocity of each projectile. Use this if you want to carry the momentum
     *        of the exploding actor.
     * 
     * @return this
     */
    public Explosion vx( double value )
    {
        return vx(value, value);
    }

    /**
     * A random X velocity for each projectile in a given range. Note, this can be used in
     * conjunction with speed, the projectiles' velocities will be the sum of their speed in the
     * direction of their heading, and their (vx,vy) velocity.
     * 
     * @param from
     *        The lowest X velocity in pixels per tick.
     * @param to
     *        the highest X velocity in pixels per tick.
     * @return this
     */
    public Explosion vx( double from, double to )
    {
        this.vx = from;
        this.randomVx = to - from;
        return this;
    }

    /**
     * See {@link #vx(double)}
     */
    public Explosion vy( double value )
    {
        return vy(value, value);
    }

    /**
     * See {@link #vx(double,double)}
     */
    public Explosion vy( double from, double to )
    {
        this.vy = from;
        this.randomVy = to - from;
        return this;
    }

    /**
     * Each projectile is points in a random direction within the given range. Note, if this method
     * is not called, but {@link #rotate} is called, then the projectiles will point in the same
     * direction as their heading.
     * 
     * @param from
     *        The minimum direction in degrees
     * @param to
     *        The maximum direction in degrees
     * @return this
     */
    public Explosion direction( double from, double to )
    {
        direction(from);
        this.randomDirection = to - from;
        return this;
    }

    public Explosion spread( double spreadFrom, double spreadTo )
    {
        this.spreadFrom = spreadFrom;
        this.spreadTo = spreadTo;

        return this;
    }

    public Explosion randomSpread()
    {
        this.randomSpread = true;
        return this;
    }

    public Explosion randomSpread( boolean value )
    {
        this.randomSpread = value;
        return this;
    }

    /**
     * Defines the speed of each Projectile. This is used in conjunction with
     * {@link #heading(double)}.
     * 
     * Note that the velecity of the Projectile can also be set using {@link #vx(double)} and
     * {@link #vy(double)}. Both can be used at the same time. For example, you can use vx and vy to
     * carry on the exploding actor's momentum, and use speed to define how fast the projectiles
     * move away from the centre of the explosion.
     * 
     * @param value
     *        The speed away from the centre of the explosion in pixels per frame.
     * @return this
     */
    public Explosion speed( double forwards, double sidewards )
    {
        return speed(forwards, forwards, sidewards, sidewards);
    }

    /**
     * Randomly choose a speed within a given range. See {@link #speed(double)} for more details.
     * 
     * @param from
     *        The minimum speed
     * @param to
     *        The maximum speed
     * @return this
     */
    public Explosion speed( double minForwards, double maxForwards, double minSidewards,
        double maxSidewards )
    {
        this.speedForwards = minForwards;
        this.randomSpeedForwards = maxForwards - minForwards;

        this.speedSidewards = minSidewards;
        this.randomSpeedSidewards = maxSidewards - minSidewards;
        return this;
    }

    /**
     * Randomly picks an initial alpha value for each projectile between the given range.
     * 
     * @param from
     *        The smallest alpha (0 to 255)
     * @param to
     *        The largest alpha (0 to 255)
     * @return this
     */
    public Explosion alpha( int from, int to )
    {
        alpha(from);
        this.randomAlpha = to - from;
        return this;
    }

    /**
     * Fades the projectiles by the given value each frame.
     * 
     * @param value
     *        The amount to subtract from the Projectile's alpha each frame.
     * @return this
     */
    public Explosion fade( double value )
    {
        return fade(value, value);
    }

    /**
     * Randomly picks the amount to fade each Projectile each frame. The amount is calculated once
     * per Projectile, so each Projectile fades by a constant amount.
     * 
     * Negative values means the projectile will become less faded (i.e. become more opaque).
     * Therefore the values will almost always be positive.
     * 
     * @param from
     *        The smallest amount of fade (-255 to 255)
     * @param to
     *        The largest amount of fade (-255 to 255)
     * @return this
     */
    public Explosion fade( double from, double to )
    {
        this.fade = from;
        this.randomFade = to - from;
        return this;
    }

    /**
     * Randomly picks an initial scale for each Projectile within the given range.
     * 
     * @param from
     *        The smallest scale (0 or more. 1 for normal size)
     * @param to
     *        The larset scale (0 or more. 1 for normal size)
     * @return this
     */
    public Explosion scale( double from, double to )
    {
        scale(from);
        this.randomScale = to - from;
        return this;
    }

    /**
     * @param value
     *        The amount the Projectile should grow each frame. i.e. the amount its scale is
     *        multiplied by each frame. each frame. Should be close to 1.0.
     * @return this
     */
    public Explosion grow( double value )
    {
        return grow(value, value);
    }

    /**
     * Randomly picks the growth factor for each Projectile within the given range.
     * 
     * @param from
     *        The smallest growth factor.
     * @param to
     *        The largest growth factor.
     * @return this
     */
    public Explosion grow( double from, double to )
    {
        this.grow = from;
        this.randomGrow = to - from;
        return this;
    }

    /**
     * Note, if you use {@link #fade}, then the actor will die when completely faded, or when its
     * life runs out, whichever is soonest. So if you want a long fade with no abrupt end, you can
     * set life to a large number, and let the fade kill the Projectile.
     * 
     * @param seconds
     *        The number of seconds that each of projectile lasts for.
     * @return this
     */
    public Explosion life( double seconds )
    {
        return life(seconds, seconds);
    }

    /**
     * Sets the lifespan of the Projectile within the range given. See {@link #life(double)}
     * 
     * @param from
     *        The minimum lifespan of the projectile in seconds.
     * @param to
     *        The maximum lifespan of the projectiles in seconds.
     * @return this
     */
    public Explosion life( double from, double to )
    {
        this.lifeTicks = (int) (Itchy.frameRate.getRequiredRate() * from);
        this.randomLifeTicks = (int) (Itchy.frameRate.getRequiredRate() * (to - from));
        return this;
    }

    public Explosion offsetForwards( double from, double to )
    {
        offsetForwards(from);
        this.randomOffsetForwards = to - from;
        return this;
    }

    public Explosion offsetSidewards( double from, double to )
    {
        offsetSidewards(from);
        this.randomOffsetSidewards = to - from;
        return this;
    }

    @Override
    public Explosion pose( String poseName )
    {
        super.pose(poseName);
        this.poseName = poseName;
        return this;
    }

    /**
     * Creates the Projectile objects and the dies. Unless projectilesPerClick is called, this will
     * only tick once, creating all of the Projectiles in one go.
     */
    @Override
    public void tick()
    {
        for (int i = 0; i < this.countPerTick; i++) {

            if (this.projectileCounter >= this.totalProjectiles) {
                getActor().kill();
                return;
            }

            Projectile projectile = new Projectile(getActor());
            if ((this.poseName != null) && (getActor().getCostume() != null)) {
                projectile.pose(this.poseName);
            }
            if (this.projectileEventName != null) {
                projectile.startEvent(this.projectileEventName);
            }

            Actor actor = projectile.createActor();
            Appearance appearance = actor.getAppearance();

            appearance.setScale(this.scale + random.nextDouble() * this.randomScale);
            appearance.setAlpha(this.alpha + random.nextDouble() * this.randomAlpha);

            if ((this.randomOffsetForwards != 0) && (this.randomOffsetSidewards != 0)) {
                actor.moveForward(
                    random.nextDouble() * this.randomOffsetForwards,
                    random.nextDouble() * this.randomOffsetSidewards);
            }

            if (this.rotate) {
                double actualDirection = this.direction + random.nextDouble() *
                    this.randomDirection;
                appearance.setDirection(actualDirection);
            }

            projectile.growFactor = this.grow + random.nextDouble() * this.randomGrow;
            projectile.lifeTicks = this.lifeTicks +
                (int) (random.nextDouble() * this.randomLifeTicks);
            projectile.spin = this.spin + random.nextDouble() * this.randomSpin;
            projectile.rotate = this.rotate;

            projectile.vx = this.vx + random.nextDouble() * this.randomVx;
            projectile.vy = this.vy + random.nextDouble() * this.randomVy;
            projectile.gravity = this.gravity;
            projectile.fade = this.fade + random.nextDouble() * this.randomFade;

            // Do speed and randomSpeed
            if ((this.speedForwards != 0) || (this.speedSidewards != 0) ||
                (this.randomSpeedForwards != 0) || (this.randomSpeedSidewards != 0)) {

                double actualSpeedForwards = this.speedForwards + random.nextDouble() *
                    this.randomSpeedForwards;
                double actualSpeedSidewards = this.speedSidewards + random.nextDouble() *
                    this.randomSpeedSidewards;
                double actualHeading = this.spreadFrom;

                if (this.randomSpread) {
                    actualHeading += +random.nextDouble() * (this.spreadTo - this.spreadFrom);
                } else {
                    actualHeading += (this.spreadTo - this.spreadFrom) /
                        (this.totalProjectiles - 1) *
                        this.projectileCounter;
                }
                
                double cos = Math.cos(actualHeading / 180.0 * Math.PI);
                double sin = Math.sin(actualHeading / 180.0 * Math.PI);

                projectile.vx = (cos * actualSpeedForwards) - (sin * actualSpeedSidewards);
                if (this.getActor().getYAxisPointsDown()) {
                    projectile.vy = (-sin * actualSpeedForwards) - (cos * actualSpeedSidewards);
                } else {
                    projectile.vy = (sin * actualSpeedForwards) + (cos * actualSpeedSidewards);
                }
            }

            getActor().getLayer().add(actor);

            actor.activate();
            this.projectileCounter++;
        }
    }

}
