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

public class MoveAnimation extends NumericAnimation
{
    private static final List<AbstractProperty<Animation, ?>> properties =
        AbstractProperty.<Animation> findAnnotations(MoveAnimation.class);


    /**
     * The total X distance to move
     */
    @Property(label="X Distance")
    public double dx;
    
    /**
     * The total Y distance to move.
     */
    @Property(label="Y Distance")
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
    public List<AbstractProperty<Animation, ?>> getProperties()
    {
        return properties;
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
