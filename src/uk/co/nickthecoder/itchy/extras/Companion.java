/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.extras;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.Costume;
import uk.co.nickthecoder.itchy.Pose;

/**
 * The base class for Explosion, Projectile and Follower. Holds the common
 * 
 */
public abstract class Companion<T extends Companion<T>> extends Behaviour
{
    protected Actor source;

    // Yes this is horrible, but using "me" means just one SuppressWarning, rather than in every
    // method.
    // Please tell me if there is a better way to accomplish the same ends.
    // i.e. Have Companion methods return the correct type T, where T will be the subclass
    // that extends this class.
    @SuppressWarnings("unchecked")
    protected T me = (T) this;

    public double offsetForwards = 0;

    public double offsetSidewards = 0;

    protected double offsetX = 0;

    protected double offsetY = 0;

    protected Pose pose;

    protected Costume costume;

    protected String eventName;

    protected double direction;

    protected double heading;

    protected int zOrder;

    public int alpha = 255;

    private double scale;

    public Companion( Actor actor )
    {
        this.source = actor;
        this.costume = this.source.getCostume();
        this.zOrder = this.source.getZOrder();
        this.scale = this.source.getAppearance().getScale();
        this.direction = this.source.getAppearance().getDirection();
        this.heading = this.source.getHeading();
    }

    public T costume( Costume costume )
    {
        this.costume = costume;
        return this.me;
    }

    public T pose( Pose pose )
    {
        this.pose = pose;
        return this.me;
    }

    public T pose( String poseName )
    {
        this.pose = this.source.getCostume().getPose(poseName);
        return this.me;
    }

    public T startEvent( String eventName )
    {
        this.eventName = eventName;
        return this.me;
    }

    public T offset( double x, double y )
    {
        this.offsetX = x;
        this.offsetY = y;
        return this.me;
    }

    public T direction( double value )
    {
        this.direction = value;
        return this.me;
    }

    /**
     * @param value
     *        How far forwards/backwards to move the centre of the explosion from the exploding
     *        actor. Useful if you want the explosion to happen at the front (or back) of the actor.
     * @return this
     */
    public T offsetForwards( double forwards )
    {
        this.offsetForwards = forwards;
        return this.me;
    }

    /**
     * @param value
     *        How far sidewards to move the centre of the explosion from the exploding actor. Useful
     *        if you want the explosion to happen at the front (or back) of the actor.
     * @return this
     */
    public T offsetSidewards( double sidewards )
    {
        this.offsetSidewards = sidewards;
        return this.me;
    }

    public T zOrder( int zOrder )
    {
        this.zOrder = zOrder;
        return this.me;
    }

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
    public T alpha( int alpha )
    {
        this.alpha = alpha;
        return this.me;
    }

    public T scale( double scale )
    {
        this.scale = scale;
        return this.me;
    }

    public Actor createActor()
    {
        Actor actor = new Actor(this.costume);
        if (this.pose != null) {
            actor.getAppearance().setDirection(this.pose.getDirection());
            actor.getAppearance().setPose(this.pose);
        }

        if (this.eventName != null) {
            actor.event(this.eventName);
        }

        actor.setBehaviour(this);
        actor.setZOrder(this.zOrder);
        actor.setHeading(this.heading);
        actor.moveTo(this.source);
        actor.moveForward(this.offsetForwards, this.offsetSidewards);
        actor.getAppearance().setDirection(this.direction);
        actor.getAppearance().setScale(this.scale);

        this.source.getLayer().add(actor);

        return actor;
    }

}
