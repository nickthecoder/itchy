/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.animation;

import uk.co.nickthecoder.itchy.Actor;

public class TurnAnimation extends NumericAnimation
{
    /**
     * The total turn in degrees
     */
    public double turn;
    
    
    public TurnAnimation()
    {
        this(200, linear, 1);
    }

    public TurnAnimation( int ticks, Profile profile, double turn)
    {
        super(ticks, profile);
        this.turn = turn;
    }

    @Override
    public String getName()
    {
        return "Turn";
    }
    
    @Override
    public void tick( Actor actor, double amount, double delta )
    {
        actor.getAppearance().adjustDirection(this.turn * delta );
    }

}
