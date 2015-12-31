/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.animation;

import java.util.List;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.itchy.property.Property;

public class DelayAnimation extends AbstractAnimation
{

    private static final List<AbstractProperty<Animation, ?>> properties =
        AbstractProperty.<Animation> findAnnotations(DelayAnimation.class);

    @Property(label = "ticks")
    public int ticks;

    protected int currentFrame;

    @Override
    public List<AbstractProperty<Animation, ?>> getProperties()
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
