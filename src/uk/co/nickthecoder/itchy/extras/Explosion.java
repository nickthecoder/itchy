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
import uk.co.nickthecoder.itchy.Pose;

/**
 * Creates many particles from a central point, spreading outwards. This is typically used when an
 * actor is destroyed.
 * 
 * Many of the methods return itself, which is designed to make setting up the explosion easier. For
 * example, this code snippet taken from "It's Gonna Rain" calls many methods :
 * 
 * <pre>
 * <code>
 * new itchy.extras.Explosion(this.actor)
 *     .projectiles(10).gravity(-0.2).forwards().fade(0.9, 3.5).speed(0.1, 1.5).vy(5)
 *     .createActor("droplet").activate(); 
 * </code>
 * </pre>
 * 
 * And is the same as this long winded form :
 * 
 * <pre>
 * <code>
 * Explosion explosion = new itchy.extras.Explosion(this.actor);
 * explosion.projectiles(10);
 * explosion.gravity(-0.2);
 * explosion.forwards();
 * explosion.fade(0.9, 3.5)
 * explosion.speed(0.1, 1.5)
 * explosion.vy(5)
 * explosion.createActor("droplet").activate();
 * </code>
 * </pre>
 * 
 * Note that the createActor method returns an Actor (not this Explosion), and therefore must be
 * last.
 */
public class Explosion extends Behaviour
{
    private static Random random = new Random();

    private boolean below = false;;

    private Actor source;

    private String poseName;

    public int projectileCount = 0;

    public int countPerTick = 1000;

    public double gravity = 0;

    public boolean rotate = false;

    public boolean sameDirection = true;

    public double spin = 0;

    public double randomSpin = 0;

    public double vx = 0;

    public double randomVx = 0;

    public double vy = 0;

    public double randomVy = 0;

    public double direction = 0;

    public double randomDirection = 360;

    public double heading = 0;

    public double randomHeading = 360;

    public double speed = 0;

    public double randomSpeed = 0;

    public double distance = 0;

    public double randomDistance = 0;

    public double fade = 0;

    public double randomFade = 0;

    public int life = 300;

    public int randomLife = 300;

    public int alpha = 255;

    public int randomAlpha = 0;

    public double scale = 1;

    public double randomScale = 0;

    public double grow = 1;

    public double randomGrow = 0;

    /**
     * Creates an explosion centred on the given actor.
     * 
     * @param actor
     */
    public Explosion( Actor actor )
    {
        super();
        this.direction = actor.getAppearance().getDirection();
        this.source = actor;
    }

    /**
     * Creates the explosion actor using the same pose (the same image) as the actor given in the
     * constructor.
     * 
     * @return A new actor, which has an Explosion Behaviour, has been added to the same layer as
     *         the actor in the constructor, but has not yet been activated.
     */
    public Actor createActor()
    {
        return this.createActor("");
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
    public Actor createActor( String poseName )
    {
        Actor result;
        if (this.source.getCostume() == null) {
            result = new Actor(this.source.getAppearance().getPose());
        } else {
            result = new Actor(this.source.getCostume());
        }
        this.poseName = poseName;

        result.moveTo(this.source);
        result.getAppearance().setAlpha(0);
        result.setBehaviour(this);

        if (this.below) {
            this.source.getLayer().addBelow(result, this.source);
        } else {
            this.source.getLayer().add(result);
        }

        return result;
    }

    /**
     * Sets the number of projectiles to create.
     * 
     * @param value
     * @return this
     */
    public Explosion projectiles( int value )
    {
        this.projectileCount = value;
        return this;
    }

    /**
     * @param value
     *        The number of projectiles to be created each frame. For an explosion, you probably
     *        want all of the projectiles to be created simultaneously, so don't call this method.
     * @return this
     */
    public Explosion projectilesPerClick( int value )
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
     * Are the particles images to be rotated? Typically set to false if the particles are small or
     * circular. Note, this is not the same as {@link #spin}.
     * 
     * @param value
     *        True if the objects should be rotated in the direction of movement.
     * @return this.
     */
    public Explosion rotate( boolean value )
    {
        this.rotate = value;
        return this;
    }

    /**
     * @param value
     *        How many degrees the projectiles should turn each frame.
     * @return this
     */
    public Explosion spin( double value )
    {
        return this.spin(value, value);
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
     * @param value
     *        The initial X velocity of each projectile. Use this if you want to carry the momentum
     *        of the exploding actor.
     * @return this
     */
    public Explosion vx( double value )
    {
        return this.vx(value, value);
    }

    /**
     * A random X velocity for each projectile between two values.
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
        return this.vy(value, value);
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

    public Explosion direction( double value )
    {
        return this.direction(value, value);
    }

    public Explosion direction( double from, double to )
    {
        this.direction = from;
        this.randomDirection = to - from;
        return this;
    }

    /**
     * All fragments point in the same direction as the source actor, The particles will move in the
     * direction given by the "heading" method, (which defaults to 0..360 degrees (i.e. randomly in
     * a full circle).
     * 
     * @return this
     */
    public Explosion forwards()
    {
        this.rotate(true);
        direction(this.source.getAppearance().getDirection());
        this.sameDirection = false;
        return this;
    }

    /**
     * @param value
     *        How far forward or backwards to move the centre of the explosion from the exploding
     *        actor. Useful if you want the explosion to happen at the front (or back) of the actor.
     * @return this
     */
    public Explosion distance( double value )
    {
        return this.distance(value, value);
    }

    // TODO Need sideways offset as well as forwards, so that the explosion can appear from any part
    // of the actor. This probably means that "distance" should be renamed.
    // Maybe use offset( x, y ) and offset( minX, maxX, minY, maxY )

    /**
     * Randomly pick the distance forwards or backwards of where each Projectile starts.
     * 
     * @param from
     *        The minimum amount forwards.
     * @param to
     *        The maximum amount forwards.
     * @return this
     */
    public Explosion distance( double from, double to )
    {
        this.distance = from;
        this.randomDistance = to - from;
        return this;
    }

    /**
     * Used in conjunction with {@link #speed}.
     * 
     * @param value
     *        The direction of movement in degrees.
     * @return this
     */
    public Explosion heading( double value )
    {
        return this.heading(value, value);
    }

    /**
     * Choose a random heading for each Projectile.
     * 
     * @param from
     *        The smallest heading (typically 0 to 360)
     * @param to
     *        The largest heading (typically 0 to 360)
     * @return this
     */
    public Explosion heading( double from, double to )
    {
        this.sameDirection = false;
        this.heading = from;
        this.randomHeading = to - from;
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
    public Explosion speed( double value )
    {
        return this.speed(value, value);
    }

    /**
     * Randomly choose a speed within a given range. See {@link #speed(double)} for more details.
     * 
     * @param from
     *        The minium speed
     * @param to
     *        The maximum speed
     * @return this
     */
    public Explosion speed( double from, double to )
    {
        this.speed = from;
        this.randomSpeed = to - from;
        return this;
    }

    /**
     * @param alpha
     *        The initial alpha value for each projectile (0 to 255).
     * @return this
     */
    public Explosion alpha( int alpha )
    {
        return alpha(alpha, alpha);
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
        this.alpha = from;
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
        return this.fade(value, value);
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
     * @param value
     *        The initial scale of the Projectile
     * @return this
     */
    public Explosion scale( double value )
    {
        return this.scale(value, value);
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
        this.scale = from;
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
        return this.grow(value, value);
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
     * @param value
     *        The number of frames that the projectile lasts for. The default value is 300 frames.
     * @return this
     */
    public Explosion life( int value )
    {
        return this.life(value, value);
    }

    /**
     * Sets the lifespan of the Projectile to a random number of frames within the range given. See
     * {@link #life(int)}
     * 
     * @param from
     *        The smallest lifespan (in frames).
     * @param to
     *        The largest lifespan (in frames).
     * @return this
     */
    public Explosion life( int from, int to )
    {
        this.life = from;
        this.randomLife = to - from;
        return this;
    }

    /**
     * Causes the projectiles to be added below the exploding actor, otherwise they are added at the
     * highest z-order.
     * 
     * @return this
     */
    public Explosion below()
    {
        this.below = true;
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

            if (this.projectileCount <= 0) {
                this.getActor().kill();
                return;
            }
            this.projectileCount--;

            Pose pose = null;
            if ((this.poseName != null) && (this.getActor().getCostume() != null)) {
                pose = this.getActor().getCostume().getPose(this.poseName);
            }
            if (pose == null) {
                pose = this.getActor().getAppearance().getPose();
            }

            Actor actor = new Actor(pose);
            Appearance appearance = actor.getAppearance();
            Projectile behaviour = new Projectile();

            double direction = this.direction + random.nextDouble() * this.randomDirection;
            if (this.rotate) {
                appearance.setDirection(direction);
            }
            appearance.setScale(this.scale + random.nextDouble() * this.randomScale);
            appearance.setAlpha(this.alpha + random.nextDouble() * this.randomAlpha);

            actor.moveTo(this.getActor());
            // TODO if rotate is false, this doesn't work???
            actor.moveForward(this.distance + random.nextDouble() * this.randomDistance);

            behaviour.growFactor = this.grow + random.nextDouble() * this.randomGrow;
            behaviour.life = this.life + (int) (random.nextDouble() * this.randomLife);
            behaviour.spin = this.spin + random.nextDouble() * this.randomSpin;

            behaviour.vx = this.vx + random.nextDouble() * this.randomVx;
            behaviour.vy = this.vy + random.nextDouble() * this.randomVy;
            behaviour.gravity = this.gravity;

            behaviour.fade = this.fade + random.nextDouble() * this.randomFade;

            // Do speed and randomSpeed
            if ((this.speed != 0) || (this.randomSpeed != 0)) {
                if (!this.sameDirection) {
                    direction = this.heading + random.nextDouble() * this.randomHeading;
                }
                double cos = Math.cos(direction / 180.0 * Math.PI);
                double sin = Math.sin(direction / 180.0 * Math.PI);
                behaviour.vx += cos * (this.speed + random.nextDouble() * this.randomSpeed);
                behaviour.vy -= sin * (this.speed + random.nextDouble() * this.randomSpeed);
            }

            appearance.setColorize(this.getActor().getAppearance().getColorize());

            actor.setBehaviour(behaviour);
            if (this.below) {
                this.getActor().getLayer().addBelow(actor, this.getActor());
            } else {
                this.getActor().getLayer().add(actor);
            }
            actor.activate();
        }
    }

}
