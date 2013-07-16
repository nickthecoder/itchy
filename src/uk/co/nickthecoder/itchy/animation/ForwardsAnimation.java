/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.animation;

import uk.co.nickthecoder.itchy.Actor;

public class ForwardsAnimation extends NumericAnimation
{
    public double forwards;
    public double sideways;
    
    public ForwardsAnimation()
    {
        this(200, NumericAnimation.linear, 0, 0);
    }

    public ForwardsAnimation( int ticks, Profile profile, double forwards, double sideways )
    {
        super(ticks, profile);
        this.forwards = forwards;
        this.sideways = sideways;
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
