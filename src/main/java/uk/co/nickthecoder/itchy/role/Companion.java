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
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.jame.RGBA;

/**
 * Companions are spawned by other Roles, for example a ship could fire a bullet
 * (in this case the Companion would be a {@link Projectile}). Another example
 * is a Speech bubble, in which case use {@link Follower}. {@link Explosion} is
 * also a Companion, which creates a set of Projectiles.
 * <p>
 * Companions will typically share the same {@link Costume} as their source, but will use a different {@link Pose}.
 */
public abstract class Companion extends AbstractRole
{
    protected Actor source;

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

    public Companion(Actor actor)
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

    public void setPose(String poseName)
    {
        this.pose = this.source.getCostume().getPose(poseName);
    }

    /**
     * Creates a new Actor on the same stage as the source actor.
     */
    public Actor createActor()
    {
        Actor actor = new Actor(this.costume);
        if (this.pose != null) {
            actor.getAppearance().setPose(this.pose);
        }

        if (this.eventName != null) {
            actor.event(this.eventName);
        }

        actor.setRole(this);
        actor.setZOrder(this.zOrder);
        if (this.rotate) {
            actor.getAppearance().setDirection(this.direction);
        } else {
            actor.getAppearance().setDirection(
                actor.getAppearance().getPose().getDirection());
        }

        actor.setHeading(this.heading);
        actor.moveTo(this.source);
        actor.moveForwards(this.offsetForwards * this.scale,
            this.offsetSidewards * this.scale);
        actor.moveBy(this.offsetX * this.scale, this.offsetY * this.scale);
        actor.getAppearance().setScale(this.scale);
        actor.getAppearance().setColorize(this.colorize);
        actor.getAppearance().setAlpha(this.alpha);

        this.source.getStage().add(actor);

        return actor;
    }

    public static abstract class AbstractCompanionBuilder<C extends Companion, B extends AbstractCompanionBuilder<C, B>>
    {
        protected C companion;

        protected abstract B getThis();

        public C create()
        {
            companion.createActor();
            return companion;
        }

        public C getCompanion()
        {
            return this.companion;
        }

        public Role getRole()
        {
            return getCompanion();
        }

        /**
         * The Companion can have a different costume to the source actor's
         * costume. For simple Companions, this is not needed. However, if the
         * Companion has many Poses, sounds or animations, it may be better for
         * it to have its own costume rather than share the source's costume.
         * 
         * @param costume
         * @return this
         */
        public B costume(Costume costume)
        {
            this.companion.costume = costume;
            return getThis();
        }

        /**
         * Looks up the current game's resources to find a costume with the
         * given name. This costume is then used for the Companion object.
         * 
         * @param costumeName
         *            The name of the costume.
         * @return this
         * @throws NullPointerException
         *             if the costume is not found.
         */
        public B costume(String costumeName)
        {
            Costume costume = Itchy.getGame().resources.getCostume(costumeName);
            if (costume == null) {
                throw new NullPointerException();
            }
            costume(costume);
            return getThis();
        }

        /**
         * Uses a different Costume, the costume is found by looking up
         * 'companionType' within the source actor's costume, and using the
         * result as a costume name.
         * <p>
         * For example, imagine we have a costume called "bigShip". Create a string within bigShip, named "bullet" with
         * a value "redBullet". We can now create a bullet like so :
         * <code>new Projectile( myBigShipActor ).companionCostume("bullet").createActor().activate();</code>
         * 
         * @param companionType
         *            The name of the string within the source actors costume.
         *            This is NOT the name of a costume!
         * 
         * @return this
         * @throws NullPointerException
         *             if the costume is not found.
         */
        public B companion(String companionType)
        {
            Costume costume = this.companion.source.getCostume().getCompanion(companionType);

            if (costume == null) {
                // Fall back to the old-fashioned way of doing it.
                costume = Itchy.getGame().resources.getCompanionCostume(this.companion.source.getCostume(),
                    companionType);
            }

            if (costume == null) {
                throw new NullPointerException();
            }

            costume(costume);
            return getThis();
        }

        /**
         * Uses a given pose for the new Companion. It is rare to use this,
         * instead select a Pose from the source's costume using {@link #pose(String)}, or {@link #eventName(String)}.
         * 
         * @param pose
         *            The pose to use when creating the actor.
         * @return this
         */
        public B pose(Pose pose)
        {
            this.companion.pose = pose;
            return getThis();
        }

        /**
         * Chooses which pose to use from the source's costume. Consider using {@link #eventName(String)} instead, as it
         * can also initiate an
         * animation and a sound effect when the actor is created.
         * 
         * @param poseName
         *            The name of the pose from the source's costume.
         * @return this
         */
        public B pose(String poseName)
        {
            this.companion.pose = this.companion.source.getCostume().getPose(
                poseName);
            return getThis();
        }

        /**
         * The companion will fire this event when its Actor is created. The
         * event can select a Pose, an Animation, and can play a sound. If you
         * only want to select a pose, then use {@link #pose(String)}.
         * 
         * @param eventName
         *            The name of the event to fire when the actor is created.
         * @return this
         */
        public B eventName(String eventName)
        {
            this.companion.eventName = eventName;
            return getThis();
        }

        /**
         * Offsets the companion relative to its source. The source's heading is
         * not used. Can be used in conjunction with {@link #offsetForwards(double)} and
         * {@link #offsetSidewards(double)}
         * 
         * @param x
         *            The amount to offset left/right
         * @param y
         *            The amount to offset up/down.
         * @return this
         */
        public B offset(double x, double y)
        {
            this.companion.offsetX = x;
            this.companion.offsetY = y;
            return getThis();
        }

        /**
         * @param forwards
         *            How far forwards/backwards to move from the source actor.
         *            Can be used in conjunction with {@link #offset(double, double)} and
         *            {@link #offsetSidewards(double)}.
         * @return this
         */
        public B offsetForwards(double forwards)
        {
            this.companion.offsetForwards = forwards;
            return getThis();
        }

        /**
         * @param sidewards
         *            How far sidewards to move from the source actor. Can be
         *            used in conjunction with {@link #offset(double, double)} and {@link #offsetForwards(double)}.
         * @return this
         */
        public B offsetSidewards(double sidewards)
        {
            this.companion.offsetSidewards = sidewards;
            return getThis();
        }

        /**
         * The actor's image will point in the given direction. Note, the actor
         * heading is not affected.
         * 
         * @param degrees
         *            The angle in derees.
         * @return this
         */
        public B direction(double degrees)
        {
            rotate();
            this.companion.direction = degrees;
            return getThis();
        }

        /**
         * Aims the actor in the direction given. This will affect the direction
         * the actor moves using {@link Actor#moveForwards(double)}. It will NOT
         * affect the orientation of the actor's image.
         * 
         * @param degrees
         *            The actor's heading in degrees
         * @return this
         */
        public B heading(double degrees)
        {
            this.companion.heading = degrees;
            return getThis();
        }

        /**
         * Determines if the Actor's image should be rotate. The default role is
         * for the image NOT to be rotated.
         * 
         * @param value
         *            Iff true, then the image will be rotated.
         * @return this
         */
        public B rotate(boolean value)
        {
            this.companion.rotate = value;
            return getThis();
        }

        /**
         * The Actor's image will be rotated to point in the same direction as
         * the source actor, or it can be overridden by {@link #direction(double)}
         * 
         * @return this
         */
        public B rotate()
        {
            this.companion.rotate = true;
            return getThis();
        }

        /**
         * Set the z-order. The default is for it to share the same z-order as
         * the source actor, and as this actor is newer, it will appear ABOVE
         * the source.
         * 
         * @param zOrder
         *            The new z-order
         * @return this.
         */
        public B zOrder(int zOrder)
        {
            this.companion.zOrder = zOrder;
            return getThis();
        }

        /**
         * Adds delta to the z-order. To ensure that the new copmanion is below
         * the source actor, use a delta of -1. The default is for the Companion
         * to share the same z-order, and for it to appear ABOVE the source
         * actor.
         * 
         * @param delta
         * @return this
         */
        public B adjustZOrder(int delta)
        {
            this.companion.zOrder += delta;
            return getThis();
        }

        /**
         * @param alpha
         *            The initial alpha value for each projectile (0 to 255).
         * @return this
         */
        public B alpha(double alpha)
        {
            this.companion.alpha = alpha;
            return getThis();
        }

        /**
         * Sets the scale for the Companion. The default is for the companion to
         * share the same scale as its source.
         * 
         * @param scale
         * @return this
         */
        public B scale(double scale)
        {
            this.companion.scale = scale;
            return getThis();
        }

        public B colorize(RGBA color)
        {
            this.companion.colorize = color;
            return getThis();
        }

    }

}
