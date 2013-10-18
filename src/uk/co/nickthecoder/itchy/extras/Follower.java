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

public class Follower extends Behaviour
{
    public Actor following;

    private double dx = 0;

    private double dy = 0;

    private double distance = 0;

    private boolean rotate = false;

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
    
   public Follower rotate()
   {
       this.rotate = true;
       return this;
   }

    @Override
    public void tick()
    {
        follow();
    }

    private void follow()
    {
        this.getActor().moveTo(this.following);
        this.getActor().moveForward(this.distance);
        this.getActor().moveBy(this.dx, this.dy);
        if (this.rotate) {
            this.getActor().getAppearance()
                .setDirection(this.following.getAppearance().getDirection());
        }
    }

    // TODO Add the layer stuff 
    public Actor createActor( boolean below )
    {
        return createActor(this.following.getCostume(), below);
    }

    // TODO Add the layer stuff 
    public Actor createActor( Costume costume, boolean below )
    {
        return createdActor( new Actor(costume), below );
    }

    // TODO Add the layer stuff 
    public Actor createActor( Pose pose, boolean below )
    {
        return createdActor( new Actor(pose), below );
    }

    public Actor createActor( String poseName, boolean below )
    {
        return createdActor( new Actor(this.following.getCostume(), poseName), below );
    }
    
    private Actor createdActor( Actor actor, boolean below )
    {
        actor.setBehaviour(this);

        if ( below ) {
            this.following.getLayer().addBelow(actor, this.following);
        } else {
            this.following.getLayer().addAbove(actor, this.following);
        }
        
        follow();
        return actor;
    }


}
