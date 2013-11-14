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
import uk.co.nickthecoder.itchy.util.AbstractProperty;
import uk.co.nickthecoder.itchy.util.Property;

public class HeadingAnimation extends NumericAnimation
{
    private static final List<AbstractProperty<Animation, ?>> properties =
        AbstractProperty.<Animation> findAnnotations(HeadingAnimation.class);


    /**
     * The total turn in degrees
     */
    @Property(label="Turn")
    public double turn;
    
    
    public HeadingAnimation()
    {
        this(200, linear, 1);
    }

    public HeadingAnimation( int ticks, Ease ease, double turn)
    {
        super(ticks, ease);
        this.turn = turn;
    }
    
    @Override
    public List<AbstractProperty<Animation, ?>> getProperties()
    {
        return properties;
    }

    @Override
    public String getName()
    {
        return "Heading";
    }
    
    @Override
    public void tick( Actor actor, double amount, double delta )
    {
        actor.adjustHeading(this.turn * delta );
    }
}
