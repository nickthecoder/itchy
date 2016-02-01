/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.makeup;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.OffsetSurface;
import uk.co.nickthecoder.itchy.SimpleOffsetSurface;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.DoubleProperty;
import uk.co.nickthecoder.jame.Surface;

public class Scale extends AbstractMakeup
{
    protected static final List<Property<Makeup, ?>> properties = new ArrayList<Property<Makeup, ?>>();

    static {
        properties.add(new DoubleProperty<Makeup>("scaleX"));
        properties.add(new DoubleProperty<Makeup>("scaleY"));
    }
    
    private double scaleX = 1;

    private double scaleY = 1;

    private int seq = 0;

    public double getScaleX()
    {
        return this.scaleX;
    }

    public void setScaleX( double scaleX )
    {
        this.seq++;
        this.scaleX = scaleX;
    }

    public double getScaleY()
    {
        return this.scaleY;
    }

    public void setScaleY( double scaleY )
    {
        this.seq++;
        this.scaleY = scaleY;
    }

    @Override
    public int getChangeId()
    {
        return this.seq;
    }

    @Override
    public OffsetSurface apply( OffsetSurface src )
    {
        Surface scaled = src.getSurface().zoom(this.scaleX, this.scaleY, true);

        return new SimpleOffsetSurface(scaled, (int) (src.getOffsetX() * this.scaleX), (int) (src.getOffsetX() * this.scaleY));
    }

    @Override
    public List<Property<Makeup, ?>> getProperties()
    {
        return properties;
    }

    @Override
    public void applyGeometry( TransformationData src )
    {
        src.set(
            (int) (src.width * this.scaleX), (int) (src.height * this.scaleY),
            (int) (src.offsetX * this.scaleX), (int) (src.offsetY * this.scaleY));
    }

}
