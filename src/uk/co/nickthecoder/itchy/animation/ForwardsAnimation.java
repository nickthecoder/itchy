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

public class ForwardsAnimation extends NumericAnimation
{
    private static final List<AbstractProperty<Animation, ?>> properties =
        AbstractProperty.<Animation> findAnnotations(ForwardsAnimation.class);

    @Property(label="Forwards")
    public double forwards;

    // TODO Change to sidewards
    @Property(label="Sideways")
    public double sideways;
    
    public ForwardsAnimation()
    {
        this(200, NumericAnimation.linear, 0, 0);
    }

    public ForwardsAnimation( int ticks, Ease ease, double forwards, double sideways )
    {
        super(ticks, ease);
        this.forwards = forwards;
        this.sideways = sideways;
    }

    @Override
    public List<AbstractProperty<Animation, ?>> getProperties()
    {
        return properties;
    }
    
    @Override
    public String getName()
    {
        return "Forward";
    }

    @Override
    public void tick( Actor actor, double amount, double delta )
    {
        actor.moveForward(this.forwards * delta, this.sideways * delta);
    }

}
