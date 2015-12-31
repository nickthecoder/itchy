/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.animation;

import java.util.List;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.itchy.property.Property;

public class ForwardsAnimation extends NumericAnimation
{
    private static final List<AbstractProperty<Animation, ?>> properties =
        AbstractProperty.<Animation> findAnnotations(ForwardsAnimation.class);

    @Property(label = "Forwards")
    public double forwards;

    @Property(label = "Sidewards", aliases = { "sideways" })
    public double sidewards;

    public static enum Using
    {
    	HEADING,
    	DIRECTION,
    	CUSTOM_ANGLE
    }
    
    @Property(label = "Using")
    public Using using = Using.HEADING;
    
    @Property(label = "Custom Angle", hint="degrees")
    public double customAngle = 0;
    
    public ForwardsAnimation()
    {
        this(200, Eases.linear, 0, 0);
    }

    public ForwardsAnimation( int ticks, Ease ease, double forwards, double sidewards )
    {
        super(ticks, ease);
        this.forwards = forwards;
        this.sidewards = sidewards;
    }

    @Override
    public List<AbstractProperty<Animation, ?>> getProperties()
    {
        return properties;
    }

    @Override
    public String getName()
    {
        return "Forwards";
    }

    @Override
    public void tick( Actor actor, double amount, double delta )
    {
    	double degrees = this.customAngle;
    	if (this.using == Using.HEADING) {
    		degrees += actor.getHeading();
    	} else if ( this.using == Using.DIRECTION) {
    		degrees += actor.getDirection();
    	}
    	
        actor.moveAngle(degrees, this.forwards * delta, this.sidewards * delta);
    }

}
