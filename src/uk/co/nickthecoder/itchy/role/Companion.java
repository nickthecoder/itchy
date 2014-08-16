/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.role;

import uk.co.nickthecoder.itchy.AbstractRole;
import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Costume;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Pose;
import uk.co.nickthecoder.itchy.TextPose;
import uk.co.nickthecoder.jame.RGBA;

/**
 * Companions are spawned by other Roles, for example a ship could fire a bullet (in this case the Companion would be a {@link Projectile}).
 * Another example is a Speech bubble, in which case use {@link Follower}. {@link Explosion} is also a Companion, which creates a set of
 * Projectiles.
 * <p>
 * Companions will typically share the same {@link Costume} as their source, but will use a different {@link Pose}.
 */
public abstract class Companion<T extends Companion<T>> extends AbstractRole
{
    protected Actor source;

    // Yes this looks horrible, but using "me" means just one SuppressWarning, rather than in every
    // method.
    // Please tell me if there is a better way to accomplish the same ends.
    // i.e. Have Companion methods return the correct type T, where T will be the subclass
    // that extends this class.
    @SuppressWarnings("unchecked")
    protected T me = (T) this;

    public double offsetForwards = 0;

    public double offsetSidewards = 0;

    public double offsetX = 0;

    public double offsetY = 0;

    public Actor parent;

    public Pose pose;

    public Costume costume;

    public String eventName;

    public double direction;

    public double heading;

    public int zOrder;

    public double alpha = 255;

    public double scale;

    public boolean rotate = false;

    public RGBA colorize;

    public String text;

    public int fontSize = 20; // Arbitrary default size.

    public RGBA color;

    public Companion( Actor actor )
    {
        this.parent = actor;
        this.source = actor;
        this.costume = this.source.getCostume();
        this.zOrder = this.source.getZOrder();
        this.scale = this.source.getAppearance().getScale();
        this.direction = this.source.getAppearance().getDirection();
        this.heading = this.source.getHeading();
        this.colorize = this.source.getAppearance().getColorize();
    }

    /**
     * The Companion can have a different costume to the source actor's costume. For simple Companions, this is not needed. However, if the
     * Companion has many Poses, sounds or animations, it may be better for it to have its own costume rather than share the source's
     * costume.
     * 
     * @param costume
     * @return this
     */
    public T costume( Costume costume )
    {
        this.costume = costume;
        return this.me;
    }

    /**
     * Looks up the current game's resources to find a costume with the given name. This costume is then used for the Companion object.
     * 
     * @param costumeName
     *        The name of the costume.
     * @return this
     * @throws NullPointerException
     *         if the costume is not found.
     */
    public T costume( String costumeName )
    {
        Costume costume = Itchy.getGame().resources.getCostume(costumeName);
        if (costume == null) {
            throw new NullPointerException();
        }
        costume(costume);
        return this.me;
    }

    /**
     * Uses a different Costume, the costume is found by looking up 'companionType' within the source actor's costume, and using the result
     * as a costume name.
     * <p>
     * For example, imagine we have a costume called "bigShip". Create a string within bigShip, named "bullet" with a value "redBullet". We
     * can now create a bullet like so : <code>new Projectile( myBigShipActor ).companionCostume("bullet").createActor().activate();</code>
     * 
     * @param companionType
     *        The name of the string within the source actors costume. This is NOT the name of a costume!
     * 
     * @return this
     * @throws NullPointerException
     *         if the costume is not found.
     */
    public T companion( String companionType )
    {
        Costume costume = Itchy.getGame().resources.getCompanionCostume(
            this.source.getCostume(),
            companionType);

        if (costume == null) {
            throw new NullPointerException();
        }

        costume(costume);
        return this.me;
    }

    /**
     * Uses a given pose for the new Companion. It is rare to use this, instead select a Pose from the source's costume using
     * {@link #pose(String)}, or {@link #eventName(String)}.
     * 
     * @param pose
     *        The pose to use when creating the actor.
     * @return this
     */
    public T pose( Pose pose )
    {
        this.pose = pose;
        return this.me;
    }

    /**
     * Chooses which pose to use from the source's costume. Consider using {@link #eventName(String)} instead, as it can also initiate an
     * animation and a sound effect when the actor is created.
     * 
     * @param poseName
     *        The name of the pose from the source's costume.
     * @return this
     */
    public T pose( String poseName )
    {
        this.pose = this.source.getCostume().getPose(poseName);
        return this.me;
    }

    /**
     * The companion will fire this event when its Actor is created. The event can select a Pose, an Animation, and can play a sound. If you
     * only want to select a pose, then use {@link #pose(String)}.
     * 
     * @param eventName
     *        The name of the event to fire when the actor is created.
     * @return this
     */
    public T eventName( String eventName )
    {
        this.eventName = eventName;
        return this.me;
    }

    /**
     * When the Pose is a TextPose, then use this text
     */
    public T text( String text )
    {
        this.text = text;
        return this.me;
    }

    /**
     * When the Pose is a TextPose, then use this font size
     */
    public T fontSize( int fontSize )
    {
        this.fontSize = fontSize;
        return this.me;
    }

    /**
     * When the Pose is a TextPose, then use this colour
     */
    public T color( RGBA color )
    {
        this.color = color;
        return this.me;
    }

    /**
     * Offsets the companion relative to its source. The source's heading is not used. Can be used in conjunction with
     * {@link #offsetForwards(double)} and {@link #offsetSidewards(double)}
     * 
     * @param x
     *        The amount to offset left/right
     * @param y
     *        The amount to offset up/down.
     * @return this
     */
    public T offset( double x, double y )
    {
        this.offsetX = x;
        this.offsetY = y;
        return this.me;
    }

    /**
     * @param forwards
     *        How far forwards/backwards to move from the source actor. Can be used in conjunction with {@link #offset(double, double)} and
     *        {@link #offsetSidewards(double)}.
     * @return this
     */
    public T offsetForwards( double forwards )
    {
        this.offsetForwards = forwards;
        return this.me;
    }

    /**
     * @param sidewards
     *        How far sidewards to move from the source actor. Can be used in conjunction with {@link #offset(double, double)} and
     *        {@link #offsetForwards(double)}.
     * @return this
     */
    public T offsetSidewards( double sidewards )
    {
        this.offsetSidewards = sidewards;
        return this.me;
    }

    /**
     * The actor's image will point in the given direction. Note, the actor heading is not affected.
     * 
     * @param degrees
     *        The angle in derees.
     * @return this
     */
    public T direction( double degrees )
    {
        rotate();
        this.direction = degrees;
        return this.me;
    }

    /**
     * Aims the actor in the direction given. This will affect the direction the actor moves using {@link Actor#moveForwards(double)}. It
     * will NOT affect the orientation of the actor's image.
     * 
     * @param degrees
     *        The actor's heading in degrees
     * @return this
     */
    public T heading( double degrees )
    {
        this.heading = degrees;
        return this.me;
    }

    /**
     * Determines if the Actor's image should be rotate. The default role is for the image NOT to be rotated.
     * 
     * @param value
     *        Iff true, then the image will be rotated.
     * @return this
     */
    public T rotate( boolean value )
    {
        this.rotate = value;
        return this.me;
    }

    /**
     * The Actor's image will be rotated to point in the same direction as the source actor, or it can be overridden by
     * {@link #direction(double)}
     * 
     * @return this
     */
    public T rotate()
    {
        this.rotate = true;
        return this.me;
    }

    /**
     * Set the z-order. The default is for it to share the same z-order as the source actor, and as this actor is newer, it will appear
     * ABOVE the source.
     * 
     * @param zOrder
     *        The new z-order
     * @return this.
     */
    public T zOrder( int zOrder )
    {
        this.zOrder = zOrder;
        return this.me;
    }

    /**
     * Adds delta to the z-order. To ensure that the new copmanion is below the source actor, use a delta of -1. The default is for the
     * Companion to share the same z-order, and for it to appear ABOVE the source actor.
     * 
     * @param delta
     * @return this
     */
    public T adjustZOrder( int delta )
    {
        this.zOrder += delta;
        return this.me;
    }

    /**
     * @param alpha
     *        The initial alpha value for each projectile (0 to 255).
     * @return this
     */
    public T alpha( double alpha )
    {
        this.alpha = alpha;
        return this.me;
    }

    /**
     * Sets the scale for the Companion. The default is for the companion to share the same scale as its source.
     * 
     * @param scale
     * @return this
     */
    public T scale( double scale )
    {
        this.scale = scale;
        return this.me;
    }

    public T colorize( RGBA color )
    {
        this.colorize = color;
        return this.me;
    }

    /**
     * Creates a new Actor on the same stage as the source actor.
     * 
     * @return A new actor.
     */
    public Actor createActor()
    {
        Actor actor = new Actor(this.costume);
        if (this.pose != null) {
            actor.getAppearance().setPose(this.pose);
        }
        if (this.text != null) {
            if (actor.getAppearance().getPose() instanceof TextPose) {
                TextPose textPose = (TextPose) (actor.getAppearance().getPose());
                textPose.setText(this.text);
                textPose.setFontSize(this.fontSize);
                if (this.color != null) {
                    textPose.setColor(this.color);
                }
            }
        }

        if (this.eventName != null) {
            actor.event(this.eventName);
        }

        actor.setRole(this);
        actor.setZOrder(this.zOrder);
        if (this.rotate) {
            actor.getAppearance().setDirection(this.direction);
        } else {
            actor.getAppearance().setDirection(actor.getAppearance().getPose().getDirection());
        }

        actor.setHeading(this.heading);
        actor.moveTo(this.source);
        actor.moveForwards(this.offsetForwards * this.scale, this.offsetSidewards * this.scale);
        actor.moveBy(this.offsetX * this.scale, this.offsetY * this.scale);
        actor.getAppearance().setScale(this.scale);
        actor.getAppearance().setColorize(this.colorize);
        actor.getAppearance().setAlpha(this.alpha);

        this.source.getStage().add(actor);

        return actor;
    }

}
