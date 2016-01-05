/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.makeup;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.OffsetSurface;
import uk.co.nickthecoder.itchy.SimpleOffsetSurface;
import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.itchy.property.IntegerProperty;
import uk.co.nickthecoder.itchy.property.RGBAProperty;
import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;

public class SimpleFrame implements Makeup
{
    protected static final List<AbstractProperty<Makeup, ?>> properties = new ArrayList<AbstractProperty<Makeup, ?>>();

    static {
        properties.add(new RGBAProperty<Makeup>("backgroundColor"));
        properties.add(new RGBAProperty<Makeup>("borderColor"));
        properties.add(new IntegerProperty<Makeup>("borderWidth"));
        properties.add(new IntegerProperty<Makeup>("paddingTop"));
        properties.add(new IntegerProperty<Makeup>("paddingRight"));
        properties.add(new IntegerProperty<Makeup>("paddingBottom"));
        properties.add(new IntegerProperty<Makeup>("paddingLeft"));
    }

    private int paddingTop;

    private int paddingRight;

    private int paddingBottom;

    private int paddingLeft;

    private RGBA borderColor;

    private int borderWidth;

    private RGBA backgroundColor;

    private int seq = 0;

    @Override
    public List<AbstractProperty<Makeup, ?>> getProperties()
    {
        return properties;
    }

    public SimpleFrame()
    {
        this(RGBA.BLACK);
    }

    public SimpleFrame( RGBA background )
    {
        this(background, background, 0);
    }

    public SimpleFrame( RGBA background, int borderWidth )
    {
        this( background, background, 0, borderWidth, borderWidth, borderWidth, borderWidth );
    }

    public SimpleFrame( RGBA background, RGBA border, int borderWidth )
    {
        this(background, border, borderWidth, 0, 0, 0, 0);
    }

    public SimpleFrame( RGBA background, RGBA border, int borderWidth, int top, int right, int bottom, int left )
    {
        this.backgroundColor = background;
        this.borderColor = border;
        this.borderWidth = borderWidth;

        this.paddingTop = top;
        this.paddingBottom = bottom;
        this.paddingLeft = left;
        this.paddingRight = right;
    }

    @Override
    public int getChangeId()
    {
        return this.seq;
    }

    public RGBA getBackgroundColor()
    {
        return this.backgroundColor;
    }

    public void setBackgroundColor( RGBA backgroundColor )
    {
        this.seq++;
        this.backgroundColor = backgroundColor;
    }

    public RGBA getBorderColor()
    {
        return this.borderColor;
    }

    public void setBorderColor( RGBA borderColor )
    {
        this.seq++;
        this.borderColor = borderColor;
    }

    public int getBorderWidth()
    {
        return this.borderWidth;
    }

    public void setBorderWidth( int width )
    {
        this.seq++;
        this.borderWidth = width;
    }

    public int getBorderTop()
    {
        return this.paddingTop;
    }

    public void setPaddingTop( int paddingTop )
    {
        this.seq++;
        this.paddingTop = paddingTop;
    }

    public int getPaddingRight()
    {
        return this.paddingRight;
    }

    public void setPaddingRight( int paddingRight )
    {
        this.seq++;
        this.paddingRight = paddingRight;
    }

    public int getPaddingBottom()
    {
        return this.paddingBottom;
    }

    public void setPaddingBottom( int borderBottom )
    {
        this.seq++;
        this.paddingBottom = borderBottom;
    }

    public int getPaddingLeft()
    {
        return this.paddingLeft;
    }

    public void setPaddingLeft( int paddingLeft )
    {
        this.seq++;
        this.paddingLeft = paddingLeft;
    }

    @Override
    public OffsetSurface apply( OffsetSurface src )
    {
        Surface srcSurface = src.getSurface();

        int width = srcSurface.getWidth() + this.paddingLeft + this.paddingRight + this.borderWidth * 2;
        int height = srcSurface.getHeight() + this.paddingTop + this.paddingBottom + this.borderWidth * 2;

        Surface surface = new Surface(width, height, true);

        if (width == 0) {
            surface.fill(this.backgroundColor);
        } else {
            surface.fill(this.borderColor);
            Rect r = new Rect(this.borderWidth, this.borderWidth, width - this.borderWidth * 2, height - this.borderWidth * 2);
            surface.fill(r, this.backgroundColor);
        }
        int tx = this.paddingLeft + this.borderWidth;
        int ty = this.paddingTop + this.borderWidth;
        srcSurface.blit(surface, tx, ty);

        return new SimpleOffsetSurface(surface, src.getOffsetX() + tx, src.getOffsetY() + ty);
    }

    @Override
    public void applyGeometry( TransformationData src )
    {
        src.set(
            src.width + this.paddingLeft + this.paddingRight + this.borderWidth * 2,
            src.height + this.paddingTop + this.paddingTop + this.borderWidth * 2,
            src.offsetX + this.paddingLeft + this.borderWidth,
            src.offsetY + this.paddingTop + this.borderWidth * 2);
    }

}
