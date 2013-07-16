/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.animation;

import uk.co.nickthecoder.itchy.Actor;

public class MoveAnimation extends NumericAnimation
{
    /**
     * The total X distance to move
     */
    public double dx;
    
    /**
     * The total Y distance to move.
     */
    public double dy;

    public MoveAnimation()
    {
        this(200, NumericAnimation.linear, 0, 0);
    }

    public MoveAnimation( int ticks, Profile profile, double dx, double dy )
    {
        super(ticks, profile);
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public String getName()
    {
        return "Move";
    }

    @Override
    public void tick( Actor actor, double amount, double delta )
    {
        actor.moveBy(this.dx * delta, this.dy * delta);
    }

}
