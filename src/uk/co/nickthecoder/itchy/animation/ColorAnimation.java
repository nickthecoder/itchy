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
import uk.co.nickthecoder.itchy.TextPose;
import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.jame.RGBA;

public class ColorAnimation extends NumericAnimation
{
    private static final List<AbstractProperty<Animation, ?>> properties =
        AbstractProperty.<Animation> findAnnotations(ColorAnimation.class);

    @Property(label="Target Color")
    public RGBA targetColor;
    
    private RGBA startColor;

    public ColorAnimation()
    {
        this(200, Eases.linear, RGBA.WHITE);
    }
    
    public ColorAnimation(int ticks, Ease ease, RGBA color)
    {
        super(ticks,ease);
        this.targetColor = color;
    }

    @Override
    public List<AbstractProperty<Animation, ?>> getProperties()
    {
        return properties;
    }

    @Override
    public String getName()
    {
        return "Colour";
    }
    
    @Override
    public String getTagName()
    {
        return "color";
    }

    @Override
    public void start( Actor actor )
    {
        super.start(actor);
        if (actor.getAppearance().getPose() instanceof TextPose) {
            this.startColor = ((TextPose) actor.getAppearance().getPose()).getColor();
        } else {
            this.startColor = actor.getAppearance().getColorize();
        }
        
        if ( this.startColor == null ) {
            this.startColor = RGBA.WHITE;
        }
    }

    @Override
    public void tick( Actor actor, double amount, double delta )
    {
        double red = this.startColor.r + (this.targetColor.r - this.startColor.r) * amount;
        double green = this.startColor.g + (this.targetColor.g - this.startColor.g) * amount;
        double blue = this.startColor.b + (this.targetColor.b - this.startColor.b) * amount;
        double alpha = this.startColor.a + (this.targetColor.a - this.startColor.a) * amount;

        RGBA color = new RGBA((int) red, (int) green, (int) blue, (int) alpha);
        
        if (actor.getAppearance().getPose() instanceof TextPose) {
            ((TextPose) actor.getAppearance().getPose()).setColor(color);
        } else {
            actor.getAppearance().setColorize(color);
        }
    }

}
