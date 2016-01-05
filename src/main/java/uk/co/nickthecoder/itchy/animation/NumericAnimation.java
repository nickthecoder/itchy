/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.animation;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.itchy.property.EaseProperty;
import uk.co.nickthecoder.itchy.property.IntegerProperty;

public abstract class NumericAnimation extends AbstractAnimation
{
    protected static final List<AbstractProperty<Animation, ?>> properties = new ArrayList<AbstractProperty<Animation, ?>>();

    static {
        properties.add( new IntegerProperty<Animation>( "ticks" ) );
        properties.add( new EaseProperty<Animation>( "ease" ).aliases("profile") );
        properties.addAll( AbstractAnimation.properties );
    }

    public int ticks;

    public Ease ease;

    private double previous;

    protected int currentFrame;

    public NumericAnimation( int ticks, Ease ease )
    {
        this.ticks = ticks;
        this.ease = ease;
        this.currentFrame = 0;
    }

    public String getEaseName()
    {
        return Itchy.registry.getEaseName(this.ease);
    }

    @Override
    public void start( Actor actor )
    {
        this.previous = 0;
        this.currentFrame = 0;
        if (this.ticks == 0) {
            this.tick(actor, 1, 1);
        }
    }

    @Override
    public void tick( Actor actor )
    {
        this.currentFrame++;

        double amount = this.currentFrame / (double) this.ticks;
        double eased = this.ease.amount(amount);
        double delta = eased - this.previous;

        this.tick(actor, eased, delta);
        this.previous = eased;

        super.tick(actor);
    }

    public void fastForward( Actor actor )
    {
        this.currentFrame = this.ticks -1;
        this.tick(actor);
    }
    
    @Override
    public boolean isFinished()
    {
        return this.currentFrame >= this.ticks;
    }

    public abstract void tick( Actor actor, double amount, double delta );

}
