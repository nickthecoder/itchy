/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.role;

import java.util.Random;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Role;

/**
 * Creates many particles from a central point, spreading outwards. This is typically used when an actor is destroyed.
 * 
 * Many of the methods return itself, which is designed to make setting up the explosion easier. For example, this code snippet taken from
 * "It's Gonna Rain" calls many methods :
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
 * Note that the createActor method returns an Actor (not the Explosion), and therefore must be last.
 */
public class Explosion extends Companion
{
    public static final double DEFAULT_LIFE_SECONDS = 6;

    private static Random random = new Random();

    protected String poseName;

    public int totalProjectiles = 0;

    public int projectileCounter = 0;

    public int countPerTick = 1000;

    public int skipTicks = 0;

    public double gravity = 0;

    public double spin = 0;

    public double randomSpin = 0;

    public double vx = 0;

    public double randomVx = 0;

    public double vy = 0;

    public double randomVy = 0;

    public double distance = 0;

    public double randomDistance = 0;

    public double speedForwards = 0;

    public double speedSidewards = 0;

    public double randomSpeedForwards = 0;

    public double randomSpeedSidewards = 0;

    public double fade = 0;

    public double randomFade = 0;

    public int lifeTicks = 300;

    public int randomLifeTicks = 0;

    public double growFactor = 1;

    public double randomGrowFactor = 0;

    public int randomAlpha = 0;

    public double randomDirection = 0;

    public boolean randomSpread = true;

    public double spreadFrom = 0;

    public double spreadTo = 360;

    public double randomScale = 0;

    public double randomOffsetForwards = 0;

    public double randomOffsetSidewards = 0;

    public String projectileEventName;

    public boolean follow = false;

    public boolean dependent = false;

    private int toSkip = 0;

    /**
     * The equivalent of : <code>new Explosion(role.getActor())</code>
     * 
     * @param role
     */
    public Explosion( Role role )
    {
        this(role.getActor());
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
     * Creates the explosion actor.
     * 
     * @return A new actor, which has an Explosion Role, has been added to the same layer as the actor in the constructor, but has not been
     *         activated yet.
     */
    @Override
    public Actor createActor()
    {
        Actor result = super.createActor();

        result.getAppearance().setAlpha(0);

        return result;
    }

    /**
     * Creates the Projectile objects and then dies. Unless projectilesPerClick is called, this will only tick once, creating all of the
     * Projectiles in one go.
     */
    @Override
    public void tick()
    {
        if (this.skipTicks > 0) {
            this.toSkip -= 1;
            if (this.toSkip >= 0) {
                return;
            }
            this.toSkip = this.skipTicks;
        }

        if (this.dependent && this.source.isDead()) {
            this.getActor().kill();
            return;
        }

        if (this.follow) {
            getActor().moveTo(this.parent);
            getActor().moveBy(this.offsetX, this.offsetY);
        }

        for (int i = 0; i < this.countPerTick; i++) {

            ProjectileBuilder projectileBuilder = Projectile.builder(getActor());
            Projectile projectile = projectileBuilder.getCompanion();

            if ((this.poseName != null) && (getActor().getCostume() != null)) {
                projectileBuilder.pose(this.poseName);
            }
            if (this.projectileEventName != null) {
                projectileBuilder.eventName(this.projectileEventName);
            }

            projectileBuilder.scale(this.scale + random.nextDouble() * this.randomScale);
            projectileBuilder.alpha(this.alpha + random.nextDouble() * this.randomAlpha);
            projectile.growFactor = this.growFactor + random.nextDouble() * this.randomGrowFactor;
            projectile.lifeTicks = this.lifeTicks + (int) (random.nextDouble() * this.randomLifeTicks);
            projectile.spin = this.spin + random.nextDouble() * this.randomSpin;
            projectile.rotate = this.rotate;

            projectile.vx = this.vx + random.nextDouble() * this.randomVx;
            projectile.vy = this.vy + random.nextDouble() * this.randomVy;
            projectile.gravity = this.gravity;
            projectile.fade = this.fade + random.nextDouble() * this.randomFade;

            double actualHeading = this.spreadFrom;
            if (this.randomSpread) {
                actualHeading += +random.nextDouble() * (this.spreadTo - this.spreadFrom);
            } else {
                actualHeading += (this.spreadTo - this.spreadFrom) / this.totalProjectiles * this.projectileCounter;
            }
            if (this.rotate) {
                projectileBuilder.direction(this.direction + random.nextDouble() * this.randomDirection);
            }
            projectileBuilder.offsetForwards(random.nextDouble() * this.randomOffsetForwards);
            projectileBuilder.offsetSidewards(random.nextDouble() * this.randomOffsetSidewards);

            // Do speed and randomSpeed
            if ((this.speedForwards != 0) || (this.speedSidewards != 0) ||
                (this.randomSpeedForwards != 0) || (this.randomSpeedSidewards != 0)) {

                double actualSpeedForwards = this.speedForwards + random.nextDouble() * this.randomSpeedForwards;
                double actualSpeedSidewards = this.speedSidewards + random.nextDouble() * this.randomSpeedSidewards;

                double cos = Math.cos(actualHeading / 180.0 * Math.PI);
                double sin = Math.sin(actualHeading / 180.0 * Math.PI);

                projectile.vx += (cos * actualSpeedForwards) - (sin * actualSpeedSidewards);
                projectile.vy += (sin * actualSpeedForwards) + (cos * actualSpeedSidewards);

            }

            Actor actor = projectile.createActor();
            if (this.pose != null) {
                actor.getAppearance().setPose(this.pose);
            }

            actor.setHeading(actualHeading);
            actor.moveForwards(this.distance + random.nextDouble() * this.randomDistance);

            this.projectileCounter++;
            if (this.projectileCounter >= this.totalProjectiles) {
                getActor().kill();
                return;
            }

        }
    }

    public static abstract class AbstractExplosionBuilder<C extends Explosion, B extends AbstractExplosionBuilder<C, B>>
        extends AbstractCompanionBuilder<C, B>
    {
        /**
         * The projectiles will fire this event when each Projectile's Actor is created. The event can select a Pose, an Animation, and can
         * play a sound. If you only want to select a pose, then use {@link #pose(String)}. It will not fire the event when the Explosion's
         * actor is created.
         * 
         * @param eventName
         *        The name of the event to fire when the actor is created.
         * @return this
         */
        @Override
        public B eventName( String eventName )
        {
            this.companion.projectileEventName = eventName;
            return getThis();
        }

        /**
         * Sets the number of projectiles to create.
         * 
         * @param value
         * @return this
         */
        public B projectiles( int value )
        {
            this.companion.totalProjectiles = value;
            return getThis();
        }

        public B forever()
        {
            this.companion.totalProjectiles = Integer.MAX_VALUE;
            return getThis();
        }

        /**
         * @param value
         *        The number of projectiles to be created each frame. For an explosion, you probably want all of the projectiles to be
         *        created simultaneously, so don't call this method.
         * @return this
         */
        public B projectilesPerTick( int value )
        {
            this.companion.countPerTick = value;
            return getThis();
        }

        /**
         * @param value
         *        Skips N ticks between creating projectiles. You can think of this as the opposite of projectilesPerTick, but they can be
         *        used together. For example, new Explosion(foo).projectilesPerTick(2).everyNTicks(10).projectiles(20) will create 2
         *        projectiles, then wait 10 ticks, then create another 2, then wait another 10 etc.
         * @return this
         */
        public B slow( int value )
        {
            this.companion.skipTicks = value;
            return getThis();
        }

        /**
         * The radius of the explosion at time zero. Note, that this is different to {@link #offsetForwards(double)}, because offsetForwards
         * move the centre of the explosion forwards, whereas 'distance' moves each Projectile forwards relative to the centre of the
         * explosion.
         * 
         * @param distance
         * @return this
         */
        public B distance( double distance )
        {
            this.companion.distance = distance;
            return getThis();
        }

        public B distance( double from, double to )
        {
            this.companion.distance = from;
            this.companion.randomDistance = to - from;
            return getThis();
        }

        public B follow()
        {
            this.companion.follow = true;
            return getThis();
        }

        /**
         * @param value
         *        The amount of change to the projectiles' Y velocity each frame. A value of around -0.2 gives a pleasing effect.
         * @return this
         */
        public B gravity( double value )
        {
            this.companion.gravity = value;
            return getThis();
        }

        /**
         * @param value
         *        How many degrees the projectiles should turn each frame.
         * @return this
         */
        public B spin( double value )
        {
            return spin(value, value);
        }

        /**
         * Randomly spin the projectiles. Note that the spin is calculated once for each projectile. It is typical to use
         * <code>spin( -N, N )</code> so that the projectiles, where N defines the maximum speed of rotation.
         * 
         * @param from
         *        The lowest number of degrees to turn the projectiles each frame.
         * @param to
         *        The highest number of degrees to turn the projectiles each frame.
         * @return this
         */
        public B spin( double from, double to )
        {
            this.companion.spin = from;
            this.companion.randomSpin = to - from;
            return getThis();
        }

        /**
         * A constant X velocity for all projectiles. Note, this can be used in conjunction with speed, the projectiles' velocities will be
         * the sum of their speed in the direction of their heading, and their (vx,vy) velocity.
         * 
         * @param value
         *        The initial X velocity of each projectile. Use this if you want to carry the momentum of the exploding actor.
         * 
         * @return this
         */
        public B vx( double value )
        {
            return vx(value, value);
        }

        /**
         * A random X velocity for each projectile in a given range. Note, this can be used in conjunction with speed, the projectiles'
         * velocities will be the sum of their speed in the direction of their heading, and their (vx,vy) velocity.
         * 
         * @param from
         *        The lowest X velocity in pixels per tick.
         * @param to
         *        the highest X velocity in pixels per tick.
         * @return this
         */
        public B vx( double from, double to )
        {
            this.companion.vx = from;
            this.companion.randomVx = to - from;
            return getThis();
        }

        /**
         * See {@link #vx(double)}
         */
        public B vy( double value )
        {
            return vy(value, value);
        }

        /**
         * See {@link #vx(double,double)}
         */
        public B vy( double from, double to )
        {
            this.companion.vy = from;
            this.companion.randomVy = to - from;
            return getThis();
        }

        /**
         * Each projectile is points in a random direction within the given range. Note, if this method is not called, but {@link #rotate}
         * is called, then the projectiles will point in the same direction as their heading.
         * 
         * @param from
         *        The minimum direction in degrees
         * @param to
         *        The maximum direction in degrees
         * @return this
         */
        public B direction( double from, double to )
        {
            direction(from);
            this.companion.randomDirection = to - from;
            return getThis();
        }

        public B spread( double spreadFrom, double spreadTo )
        {
            this.companion.spreadFrom = spreadFrom;
            this.companion.spreadTo = spreadTo;

            return getThis();
        }

        public B randomSpread()
        {
            this.companion.randomSpread = true;
            return getThis();
        }

        public B randomSpread( boolean value )
        {
            this.companion.randomSpread = value;
            return getThis();
        }

        /**
         * Defines the speed of each Projectile. This is used in conjunction with {@link #heading(double)}.
         * 
         * Note that the velocity of the Projectile can also be set using {@link #vx(double)} and {@link #vy(double)}. Both can be used at
         * the same time. For example, you can use vx and vy to carry on the exploding actor's momentum, and use speed to define how fast
         * the projectiles move away from the centre of the explosion.
         * 
         * @param forwards
         *        The speed away from the centre of the explosion in pixels per frame.
         * @param sidewards
         *        The speed sidewards from the centre of the explosion in pixels per frame.
         * @return this
         */
        public B speed( double forwards, double sidewards )
        {
            return speed(forwards, forwards, sidewards, sidewards);
        }

        /**
         * Randomly choose a speed within a given range. See {@link #speed(double,double)} for more details.
         * 
         * @return this
         */
        public B speed( double minForwards, double maxForwards, double minSidewards, double maxSidewards )
        {
            this.companion.speedForwards = minForwards;
            this.companion.randomSpeedForwards = maxForwards - minForwards;

            this.companion.speedSidewards = minSidewards;
            this.companion.randomSpeedSidewards = maxSidewards - minSidewards;
            return getThis();
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
        public B alpha( int from, int to )
        {
            alpha(from);
            this.companion.randomAlpha = to - from;
            return getThis();
        }

        /**
         * Fades the projectiles by the given value each frame.
         * 
         * @param value
         *        The amount to subtract from the Projectile's alpha each frame.
         * @return this
         */
        public B fade( double value )
        {
            return fade(value, value);
        }

        /**
         * Randomly picks the amount to fade each Projectile each frame. The amount is calculated once per Projectile, so each Projectile
         * fades by a constant amount.
         * 
         * Negative values means the projectile will become less faded (i.e. become more opaque). Therefore the values will almost always be
         * positive.
         * 
         * @param from
         *        The smallest amount of fade (-255 to 255)
         * @param to
         *        The largest amount of fade (-255 to 255)
         * @return this
         */
        public B fade( double from, double to )
        {
            this.companion.fade = from;
            this.companion.randomFade = to - from;
            return getThis();
        }

        /**
         * Randomly picks an initial scale for each Projectile within the given range.
         * 
         * @param from
         *        The minimum scale (0 or more. 1 for normal size)
         * @param to
         *        The maximum scale (0 or more. 1 for normal size)
         * @return this
         */
        public B scale( double from, double to )
        {
            scale(from);
            this.companion.randomScale = to - from;
            return getThis();
        }

        /**
         * @param value
         *        The amount the Projectile should grow each frame. i.e. the amount its scale is multiplied by each frame. each frame.
         *        Should be close to 1.0.
         * @return this
         */
        public B grow( double value )
        {
            return grow(value, value);
        }

        /**
         * Randomly picks the growth factor for each Projectile within the given range.
         * 
         * @param from
         *        The minimum growth factor.
         * @param to
         *        The maximum growth factor.
         * @return this
         */
        public B grow( double from, double to )
        {
            this.companion.growFactor = from;
            this.companion.randomGrowFactor = to - from;
            return getThis();
        }

        /**
         * Note, if you use {@link #fade}, then the actor will die when completely faded, or when its life runs out, whichever is soonest.
         * So if you want a long fade with no abrupt end, you can set life to a large number, and let the fade kill the Projectile.
         * 
         * @param seconds
         *        The number of seconds that each of projectile lasts for.
         * @return this
         */
        public B life( double seconds )
        {
            return life(seconds, seconds);
        }

        /**
         * Sets the life span of the Projectile within the range given. See {@link #life(double)}
         * 
         * @param from
         *        The minimum life span of the projectile in seconds.
         * @param to
         *        The maximum life span of the projectiles in seconds.
         * @return this
         */
        public B life( double from, double to )
        {
            this.companion.lifeTicks = (int) (Itchy.frameRate.getRequiredRate() * from);
            this.companion.randomLifeTicks = (int) (Itchy.frameRate.getRequiredRate() * (to - from));
            return getThis();
        }

        public B offsetForwards( double from, double to )
        {
            offsetForwards(from);
            this.companion.randomOffsetForwards = to - from;
            return getThis();
        }

        public B offsetSidewards( double from, double to )
        {
            offsetSidewards(from);
            this.companion.randomOffsetSidewards = to - from;
            return getThis();
        }

        @Override
        public B pose( String poseName )
        {
            // super.pose(poseName);
            this.companion.poseName = poseName;
            return getThis();
        }

        public B dependent()
        {
            this.companion.dependent = true;
            return getThis();
        }

        public B dependent( boolean value )
        {
            this.companion.dependent = value;
            return getThis();
        }
    }

}
