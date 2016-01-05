/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.animation;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.itchy.property.BooleanProperty;
import uk.co.nickthecoder.itchy.property.DoubleProperty;

public class TurnToAnimation extends NumericAnimation
{

    protected static final List<AbstractProperty<Animation, ?>> properties = new ArrayList<AbstractProperty<Animation, ?>>();

    static {
        properties.add( new DoubleProperty<Animation>( "direction" ) );
        properties.add( new BooleanProperty<Animation>( "longWay" ).label( "Long Way Round") );
        properties.addAll( NumericAnimation.properties );
    }

    /**
     * The total turn in degrees
     */
    public double direction;

    public boolean longWay = false;

    private double turn;

    public TurnToAnimation()
    {
        this(200, Eases.linear, 0);
    }

    public TurnToAnimation( int ticks, Ease ease, double direction )
    {
        super(ticks, ease);
        this.direction = direction;
    }

    @Override
    public List<AbstractProperty<Animation, ?>> getProperties()
    {
        return properties;
    }

    @Override
    public String getName()
    {
        return "Turn To";
    }

    @Override
    public void start( Actor actor )
    {
        super.start(actor);
        this.turn = ((this.direction - actor.getAppearance().getDirection()) % 360 + 360) % 360;
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
    }

    @Override
    public void tick( Actor actor, double amount, double delta )
    {
        actor.getAppearance().adjustDirection(this.turn * delta);
    }

}
