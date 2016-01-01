/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.animation;

import java.util.List;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.itchy.property.Property;

public class AlphaAnimation extends NumericAnimation
{
    private static final List<AbstractProperty<Animation, ?>> properties =
        AbstractProperty.<Animation> findAnnotations(AlphaAnimation.class);

    /**
     * The final delta value
     */
    @Property(label = "Target")
    public double target;

    private double initialValue;

    public AlphaAnimation()
    {
        this(200, Eases.linear, 255);
    }

    public AlphaAnimation( int ticks, Ease ease, double target )
    {
        super(ticks, ease);
        this.target = target;
    }

    @Override
    public List<AbstractProperty<Animation, ?>> getProperties()
    {
        return properties;
    }

    @Override
    public String getName()
    {
        return "Alpha";
    }

    @Override
    public void start( Actor actor )
    {
        super.start(actor);
        this.initialValue = actor.getAppearance().getAlpha();
    }

    @Override
    public void tick( Actor actor, double amount, double delta )
    {
        double value = this.initialValue + (this.target - this.initialValue) * amount;
        actor.getAppearance().setAlpha(value);
    }

}