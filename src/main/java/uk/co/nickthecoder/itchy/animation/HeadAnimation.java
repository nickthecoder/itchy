/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.animation;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.itchy.property.DoubleProperty;

public class HeadAnimation extends NumericAnimation
{
    protected static final List<AbstractProperty<Animation, ?>> properties = new ArrayList<AbstractProperty<Animation, ?>>();

    static {
        properties.add( new DoubleProperty<Animation>( "turn" ).hint( "degrees") );
        properties.add( new DoubleProperty<Animation>( "directionToo" ) );
        properties.addAll( NumericAnimation.properties );
    }

    /**
     * The total turn in degrees
     */
    public double turn;

    public boolean directionToo;

    public HeadAnimation()
    {
        this(200, Eases.linear, 1);
    }

    public HeadAnimation( int ticks, Ease ease, double turn )
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
        return "Head";
    }

    @Override
    public void tick( Actor actor, double amount, double delta )
    {
        double turnage = this.turn * delta;
        actor.adjustHeading(turnage);
        if (this.directionToo) {
            actor.adjustDirection(turnage);
        }
    }
}
