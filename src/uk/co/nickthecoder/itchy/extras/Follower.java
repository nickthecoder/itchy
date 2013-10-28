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
    
    private Pose pose;
    
    private Costume costume;

    private double dx = 0;

    private double dy = 0;

    private double distance = 0;

    private boolean rotate = false;
    
    private int zOrder;

    public Follower( Behaviour following )
    {
        this(following.getActor());
    }

    public Follower( Actor following )
    {
        this.following = following;
        this.costume = following.getCostume();
        this.zOrder = following.getZOrder();
    }

    public Follower pose( Pose pose )
    {
        this.pose = pose;
        return this;
    }
    
    public Follower poseName( String poseName )
    {
        this.pose = following.getCostume().getPose( poseName );
        return this;
    }
    
    public Follower costume( Costume costume )
    {
        this.costume = costume;
        return this;
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

    public Follower zOrder( int zOrder )
    {
        this.zOrder = zOrder;
        return this;
    }
    
    public Follower adjustZOrder( int delta )
    {
        this.zOrder += delta;
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

    public Actor createActor()
    {
        Actor actor = new Actor(costume);
        if (this.pose != null) {
            actor.getAppearance().setDirection(pose.getDirection());
            actor.getAppearance().setPose(pose);
        }

        actor.setBehaviour(this);
        actor.setZOrder( this.zOrder );
        this.following.getLayer().add(actor);
        
        follow();
        return actor;
    }


}
