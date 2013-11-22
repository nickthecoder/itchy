/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.animation;

import java.util.List;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.itchy.property.Property;

public class ScaleAnimation extends NumericAnimation
{
    private static final List<AbstractProperty<Animation, ?>> properties =
        AbstractProperty.<Animation> findAnnotations(ScaleAnimation.class);

    @Property(label="Target Scale")
    public double target;
    
    private double initialScale;
    

    public ScaleAnimation()
    {
        this(200, Eases.linear, 1);
    }

    public ScaleAnimation( int ticks, Ease ease, double target )
    {
        super(ticks, ease);
        this.target = target;
    }

    @Override
    public List<AbstractProperty<Animation, ?>> getProperties()
    {
        return properties;
    }
    
    @Override
    public String getName()
    {
        return "Scale";
    }

    @Override
    public void start( Actor actor )
    {
        super.start(actor);
        this.initialScale = actor.getAppearance().getScale();
    }
    
    @Override
    public void tick( Actor actor, double amount, double delta )
    {
        double value = this.initialScale + (this.target - this.initialScale) * amount;
        actor.getAppearance().setScale(value);
    }

}
