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

public class Projectile extends Behaviour
{
    public Actor source;

    private Pose pose;

    private Costume costume;

    private String eventName;
    
    public double gravity;

    public double vx;

    public double vy;

    public double speedForwards;

    public double speedSidewards;

    public double spin;

    public double fade;

    public double growFactor = 1;

    public int life = 1000;

    private int zOrder;

    private double scale;

    public Projectile( Behaviour source )
    {
        this(source.getActor());
    }

    public Projectile( Actor source )
    {
        this.costume = source.getCostume();
        this.source = source;
        this.zOrder = source.getZOrder();
        this.scale = source.getAppearance().getScale();
    }

    public Projectile pose( Pose pose )
    {
        this.pose = pose;
        return this;
    }

    @Deprecated
    public Projectile poseName( String poseName )
    {
        return pose(poseName);
    }

    public Projectile pose( String poseName )
    {
        this.pose = this.source.getCostume().getPose(poseName);
        return this;
    }
    
    public Projectile startEvent( String eventName )
    {
        this.eventName = eventName;
        return this;
    }

    public Projectile costume( Costume costume )
    {
        this.costume = costume;
        return this;
    }

    public Projectile scale( double scale )
    {
        this.scale = scale;
        return this;
    }

    public Projectile speed( double value )
    {
        this.speedForwards = value;
        return this;
    }

    public Projectile speed( double forwards, double sidewards )
    {
        this.speedForwards = forwards;
        this.speedSidewards = sidewards;
        return this;
    }

    public Projectile vx( double value )
    {
        this.vx = value;
        return this;
    }

    public Projectile vy( double value )
    {
        this.vy = value;
        return this;
    }

    public Projectile gravity( double value )
    {
        this.gravity = value;
        return this;
    }

    public Projectile spin( double value )
    {
        this.spin = value;
        return this;
    }

    public Projectile fade( double value )
    {
        this.fade = value;
        return this;
    }

    public Projectile growFactor( double value )
    {
        this.growFactor = value;
        return this;
    }

    public Projectile zOrder( int zOrder )
    {
        this.zOrder = zOrder;
        return this;
    }

    public Projectile adjustZOrder( int delta )
    {
        this.zOrder += delta;
        return this;
    }

    public Actor createActor()
    {
        Actor actor = new Actor(this.costume);
        if (this.pose != null) {
            actor.getAppearance().setPose(this.pose);
        }
        if (this.eventName != null) {
            actor.event(this.eventName);
        }
        // TODO Do we always want the projectile to be pointing the same way as the source?
        actor.getAppearance().setDirection(this.source.getAppearance().getDirection());
        actor.getAppearance().setScale(this.scale);
        actor.moveTo(this.source);
        actor.setBehaviour(this);

        actor.setZOrder(this.zOrder);
        this.source.getLayer().add(actor);

        return actor;
    }

    @Override
    public void tick()
    {
        this.getActor().moveBy(this.vx, this.vy);
        this.getActor().moveForward(this.speedForwards, this.speedSidewards);
        this.vy += this.gravity;
        this.getActor().getAppearance().adjustAlpha(-this.fade);
        this.getActor().getAppearance().adjustDirection(this.spin);

        if (this.growFactor != 1) {
            this.getActor().getAppearance().setScale(
                this.getActor().getAppearance().getScale() * this.growFactor);
        }

        if ((this.life-- < 0) || (this.getActor().getAppearance().getAlpha() <= 0)) {
            this.getActor().kill();
        }

    }
}
