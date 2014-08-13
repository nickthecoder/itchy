/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.makeup;

import java.util.List;

import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.OffsetSurface;
import uk.co.nickthecoder.itchy.Renderable;
import uk.co.nickthecoder.itchy.SimpleOffsetSurface;
import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.jame.Surface;

public class Frame implements Makeup
{
    private static final List<AbstractProperty<Makeup, ?>> properties =
        AbstractProperty.<Makeup> findAnnotations(Frame.class);

    private int borderTop;

    private int borderRight;

    private int borderBottom;

    private int borderLeft;

    private String ninePatchName;

    private Renderable ninePatch;

    private int seq = 0;

    @Override
    public List<AbstractProperty<Makeup, ?>> getProperties()
    {
        return properties;
    }

    public Frame()
    {
    }

    public Frame( int top, int right, int bottom, int left )
    {
        this.borderTop = top;
        this.borderBottom = bottom;
        this.borderLeft = left;
        this.borderRight = right;
    }

    @Override
    public int getChangeId()
    {
        return this.seq;
    }

    @Property(label = "Frame Top")
    public int getBorderTop()
    {
        return this.borderTop;
    }

    public void setBorderTop( int borderTop )
    {
        this.seq++;
        this.borderTop = borderTop;
    }

    @Property(label = "Frame Right")
    public int getBorderRight()
    {
        return this.borderRight;
    }

    public void setBorderRight( int borderRight )
    {
        this.seq++;
        this.borderRight = borderRight;
    }

    @Property(label = "Frame Bottom")
    public int getBorderBottom()
    {
        return this.borderBottom;
    }

    public void setBorderBottom( int borderBottom )
    {
        this.seq++;
        this.borderBottom = borderBottom;
    }

    @Property(label = "Frame Left")
    public int getBorderLeft()
    {
        return this.borderLeft;
    }

    public void setBorderLeft( int borderLeft )
    {
        this.seq++;
        this.borderLeft = borderLeft;
    }

    @Property(label = "Nine Patch Name")
    public String getNinePatchName()
    {
        return this.ninePatchName;
    }

    public void setNinePatchName( String ninePatchName )
    {
        this.seq++;
        this.ninePatchName = ninePatchName;
        this.ninePatch = Itchy.getGame().resources.getNinePatch(ninePatchName);
    }

    public void setNinePatch( Renderable ninePatch )
    {
        this.ninePatch = ninePatch;
    }

    @Override
    public OffsetSurface apply( OffsetSurface src )
    {
        if (this.ninePatch == null) {
            return src;
        }

        Surface srcSurface = src.getSurface();

        int width = srcSurface.getWidth() + this.borderLeft + this.borderRight;
        int height = srcSurface.getHeight() + this.borderTop + this.borderBottom;

        // Sometimes, the contents won't be big enough for the frame to be fully drawn.
        // If so, we render the whole frame, and place the contents in the middle
        int offsetX = 0;
        int offsetY = 0;
        int minFrameWidth = this.ninePatch.getMinimumWidth();
        int minFrameHeight = this.ninePatch.getMinimumHeight();

        if (width < minFrameWidth) {
            offsetX = (minFrameWidth - width) / 2;
            width = minFrameWidth;
        }
        if (height < minFrameHeight) {
            offsetY = (minFrameHeight - height) / 2;
            height = minFrameHeight;
        }

        Surface surface = new Surface(width, height, true);

        this.ninePatch.render(surface);
        srcSurface.blit(surface, this.borderLeft + offsetX, this.borderTop + offsetY);

        return new SimpleOffsetSurface(surface, src.getOffsetX() + this.borderLeft, src.getOffsetY() + this.borderTop);
    }

    @Override
    public void applyGeometry( TransformationData src )
    {
        if (this.ninePatch == null) {
            return;
        }

        src.set(
            src.width + this.borderLeft + this.borderRight,
            src.height + this.borderTop + this.borderTop,
            src.offsetX + this.borderLeft,
            src.offsetY + this.borderTop);
    }

}
