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
import uk.co.nickthecoder.itchy.property.EnumProperty;

public class ForwardsAnimation extends NumericAnimation
{
    protected static final List<Property<Animation, ?>> properties = new ArrayList<Property<Animation, ?>>();

    static {
        properties.add(new DoubleProperty<Animation>("forwards"));
        properties.add(new DoubleProperty<Animation>("sidewards").aliases("sideways"));
        properties.add(new EnumProperty<Animation, Using>("using", Using.class));
        properties.add(new DoubleProperty<Animation>("customAngle").hint("degrees"));
        properties.addAll(NumericAnimation.properties);
    }

    public double forwards;

    public double sidewards;

    public static enum Using
    {
        HEADING,
        DIRECTION,
        CUSTOM_ANGLE
    }

    public Using using = Using.HEADING;

    public double customAngle = 0;

    public ForwardsAnimation()
    {
        this(200, Eases.linear, 0, 0);
    }

    public ForwardsAnimation(int ticks, Ease ease, double forwards, double sidewards)
    {
        super(ticks, ease);
        this.forwards = forwards;
        this.sidewards = sidewards;
    }

    @Override
    public List<Property<Animation, ?>> getProperties()
    {
        return properties;
    }

    @Override
    public String getName()
    {
        return "Forwards";
    }

    @Override
    public void tick(Actor actor, double amount, double delta)
    {
        double degrees = this.customAngle;
        if (this.using == Using.HEADING) {
            degrees += actor.getHeading();
        } else if (this.using == Using.DIRECTION) {
            degrees += actor.getDirection();
        }

        actor.moveTo(actor.getPosition().translateDegrees(degrees, this.forwards * delta, this.sidewards * delta));
    }

}
