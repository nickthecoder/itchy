/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.animation;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.IntegerProperty;

public class DelayAnimation extends AbstractAnimation
{
    protected static final List<Property<Animation, ?>> properties = new ArrayList<Property<Animation, ?>>();

    static {
        properties.add( new IntegerProperty<Animation>( "ticks" ) );
        properties.addAll( AbstractAnimation.properties );
    }

    public int ticks;

    protected int currentFrame;

    @Override
    public List<Property<Animation, ?>> getProperties()
    {
        return properties;
    }

    @Override
    public String getName()
    {
        return "Delay";
    }

    @Override
    public void start( Actor actor )
    {
        super.start(actor);
        
        this.currentFrame = 0;
    }

    @Override
    public void tick( Actor actor )
    {
        this.currentFrame++;
    }

    public void fastForward(Actor actor)
    {
        this.currentFrame = this.ticks;
        this.fireFinished(actor);
    }
    
    @Override
    public boolean isFinished()
    {
        return this.currentFrame >= this.ticks;
    }
}
