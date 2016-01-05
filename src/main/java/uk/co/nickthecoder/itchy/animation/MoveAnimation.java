/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.animation;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.DoubleProperty;

public class MoveAnimation extends NumericAnimation
{
    protected static final List<Property<Animation, ?>> properties = new ArrayList<Property<Animation, ?>>();

    static {
        properties.add( new DoubleProperty<Animation>( "dx" ).label( "X Distance") );
        properties.add( new DoubleProperty<Animation>( "dy" ).label( "Y Distance") );
        properties.addAll( NumericAnimation.properties );
    }

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
        this(200, Eases.linear, 0, 0);
    }

    public MoveAnimation( int ticks, Ease ease, double dx, double dy )
    {
        super(ticks, ease);
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public List<Property<Animation, ?>> getProperties()
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
