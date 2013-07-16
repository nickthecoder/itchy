/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.extras;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.Costume;
import uk.co.nickthecoder.itchy.Pose;

public class Follower extends Behaviour
{
    public Actor following;

    private double dx = 0;

    private double dy = 0;

    private double distance = 0;

    public Follower( Behaviour following )
    {
        this(following.getActor());
    }
    
    public Follower( Actor following )
    {
        this.following = following;
    }

    public Follower distance( double distance )
    {
        this.distance = distance;
        return this;
    }

    public Follower offset( double x, double y )
    {
        this.dx = x;
        this.dy = y;
        return this;
    }

    @Override
    public void tick()
    {
        updatePosition();
    }

    private void updatePosition()
    {
        this.actor.moveTo(this.following);
        this.actor.moveForward(this.distance);
        this.actor.moveBy(this.dx, this.dy);   
    }
    
    public Actor createActor()
    {
        return createActor(this.following.getCostume());
    }

    public Actor createActor( Costume costume )
    {
        Actor actor = new Actor(costume);
        updateActor(actor);
        return actor;
    }
    
    public Actor createActor( Pose pose )
    {
        Actor actor = new Actor(pose);
        updateActor(actor);
        return actor;
    }

    private void updateActor(Actor actor)
    {
        actor.moveTo(this.following);
        this.following.getLayer().add(actor);
        actor.setBehaviour(this);
        updatePosition();
    }

}
