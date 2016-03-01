/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.RGBAProperty;
import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Rect;

/**
 * Renders a solid color over the whole area.
 */
public class RGBAView extends AbstractView
{
    protected static final List<Property<View,?>> properties = new ArrayList<Property<View,?>>();

    static {
        properties.add(new RGBAProperty<View>("color"));
    }
    
    @Override
    public List<Property<View,?>> getProperties()
    {
        return properties;
    }
    
    public RGBA color;

    public RGBAView()
    {
        super();
        this.color = new RGBA(0,0,0);
    }

    public RGBAView( Rect position, RGBA color )
    {
        super(position);
        this.color = color;
    }

    @Override
    public boolean isVisible()
    {
        return (this.color.a > 0) && super.isVisible();
    }

    @Override
    public void render( GraphicsContext gc )
    {
        gc.fill(new Rect(0,0,position.width, position.height), this.color);
    }

    @Override
    public String toString()
    {
        return "RGBAView " + this.color;
    }
}
