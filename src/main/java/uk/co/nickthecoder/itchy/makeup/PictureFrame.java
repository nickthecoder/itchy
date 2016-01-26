/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.makeup;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.OffsetSurface;
import uk.co.nickthecoder.itchy.Renderable;
import uk.co.nickthecoder.itchy.SimpleOffsetSurface;
import uk.co.nickthecoder.itchy.property.IntegerProperty;
import uk.co.nickthecoder.itchy.property.NinePatchProperty;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.util.NinePatch;
import uk.co.nickthecoder.jame.Surface;

public class PictureFrame implements Makeup
{
    protected static final List<Property<Makeup, ?>> properties = new ArrayList<Property<Makeup, ?>>();

    static {
        properties.add(new NinePatchProperty<Makeup>("ninePatch").aliases("ninePathcName"));
        properties.add(new IntegerProperty<Makeup>("borderTop"));
        properties.add(new IntegerProperty<Makeup>("borderRight"));
        properties.add(new IntegerProperty<Makeup>("borderBottom"));
        properties.add(new IntegerProperty<Makeup>("borderLeft"));
    }

    private int borderTop;

    private int borderRight;

    private int borderBottom;

    private int borderLeft;

    private NinePatch ninePatch;
    
    /**
     * If a nine patch is not specified, then any Renderable can also be used.
     */
    private Renderable background;

    private int seq = 0;

    @Override
    public List<Property<Makeup, ?>> getProperties()
    {
        return properties;
    }

    public PictureFrame()
    {
    }

    public PictureFrame( int top, int right, int bottom, int left )
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

    public int getBorderTop()
    {
        return this.borderTop;
    }

    public void setBorderTop( int borderTop )
    {
        this.seq++;
        this.borderTop = borderTop;
    }

    public int getBorderRight()
    {
        return this.borderRight;
    }

    public void setBorderRight( int borderRight )
    {
        this.seq++;
        this.borderRight = borderRight;
    }

    public int getBorderBottom()
    {
        return this.borderBottom;
    }

    public void setBorderBottom( int borderBottom )
    {
        this.seq++;
        this.borderBottom = borderBottom;
    }

    public int getBorderLeft()
    {
        return this.borderLeft;
    }

    public void setBorderLeft( int borderLeft )
    {
        this.seq++;
        this.borderLeft = borderLeft;
    }

    public NinePatch getNinePatch()
    {
        return ninePatch;
    }
    
    public void setNinePatch( NinePatch ninePatch )
    {
        this.ninePatch = ninePatch;
        this.seq++;
    }
    
    public void setBackground( Renderable background )
    {
        this.background = background;
    }
    
    public Renderable getBackground()
    {
        return this.ninePatch == null ? this.background : this.ninePatch;
    }
    
    @Override
    public OffsetSurface apply( OffsetSurface src )
    {
        Renderable renderable = getBackground();
        
        if (renderable == null) {
            return src;
        }

        Surface srcSurface = src.getSurface();

        int width = srcSurface.getWidth() + this.borderLeft + this.borderRight;
        int height = srcSurface.getHeight() + this.borderTop + this.borderBottom;

        // Sometimes, the contents won't be big enough for the frame to be fully drawn.
        // If so, we render the whole frame, and place the contents in the middle
        int offsetX = 0;
        int offsetY = 0;
        int minFrameWidth = renderable.getMinimumWidth();
        int minFrameHeight = renderable.getMinimumHeight();

        if (width < minFrameWidth) {
            offsetX = (minFrameWidth - width) / 2;
            width = minFrameWidth;
        }
        if (height < minFrameHeight) {
            offsetY = (minFrameHeight - height) / 2;
            height = minFrameHeight;
        }

        Surface surface = new Surface(width, height, true);

        renderable.render(surface);
        srcSurface.blit(surface, this.borderLeft + offsetX, this.borderTop + offsetY);

        return new SimpleOffsetSurface(surface, src.getOffsetX() + this.borderLeft, src.getOffsetY() + this.borderTop);
    }

    @Override
    public void applyGeometry( TransformationData src )
    {
        if (getBackground() == null) {
            return;
        }

        src.set(
            src.width + this.borderLeft + this.borderRight,
            src.height + this.borderTop + this.borderTop,
            src.offsetX + this.borderLeft,
            src.offsetY + this.borderTop);
    }

}
