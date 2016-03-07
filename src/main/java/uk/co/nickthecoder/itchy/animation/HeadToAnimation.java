/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.animation;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.BooleanProperty;
import uk.co.nickthecoder.itchy.property.DoubleProperty;

public class HeadToAnimation extends NumericAnimation
{
    protected static final List<Property<Animation, ?>> properties = new ArrayList<Property<Animation, ?>>();

    static {
        properties.add( new DoubleProperty<Animation>( "heading" ).hint( "degrees") );
        properties.add( new BooleanProperty<Animation>( "longWay" ).label( "Long Way Round" ) );
        properties.addAll( NumericAnimation.properties );
    }

    /**
     * The total turn in degrees
     */
    public double heading;

    public boolean longWay = false;

    private double turn;

    public HeadToAnimation()
    {
        this(200, Eases.linear, 0);
    }

    public HeadToAnimation( int ticks, Ease ease, double heading )
    {
        super(ticks, ease);
        this.heading = heading;
    }

    @Override
    public List<Property<Animation, ?>> getProperties()
    {
        return properties;
    }

    @Override
    public String getName()
    {
        return "Head To";
    }

    @Override
    public void start( Actor actor )
    {
        this.turn = ((this.heading - actor.getHeading()) % 360 + 360) % 360;
        // Now 0..360

        if (this.longWay) {
            if (this.turn < 180) {
                this.turn -= 360;
            }
        } else {
            if (this.turn > 180) {
                this.turn -= 360;
            }
        }
        super.start(actor);
    }

    @Override
    public void tick( Actor actor, double amount, double delta )
    {
        actor.adjustHeading(this.turn * delta);
    }

}
