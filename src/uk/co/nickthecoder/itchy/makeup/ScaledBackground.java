/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.makeup;

import java.util.List;

import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.OffsetSurface;
import uk.co.nickthecoder.itchy.Pose;
import uk.co.nickthecoder.itchy.SimpleOffsetSurface;
import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.jame.Surface;

public class ScaledBackground implements Makeup
{
    private static final List<AbstractProperty<Makeup, ?>> properties =
        AbstractProperty.<Makeup> findAnnotations(ScaledBackground.class);

    private int borderTop;

    private int borderRight;

    private int borderBottom;

    private int borderLeft;

    private String poseName;

    private Pose pose;

    private int seq = 0;

    @Override
    public List<AbstractProperty<Makeup, ?>> getProperties()
    {
        return properties;
    }

    public ScaledBackground()
    {
    }

    public ScaledBackground( int top, int right, int bottom, int left )
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

    @Property(label = "Pose Name")
    public String getPoseName()
    {
        return this.poseName;
    }

    public void setPoseName( String poseName )
    {
        this.seq++;
        this.poseName = poseName;
        this.pose = Itchy.getGame().resources.getPose(this.poseName);
    }

    public void setPose( Pose pose )
    {
        this.seq++;
        this.pose = pose;
    }
    
    @Override
    public OffsetSurface apply( OffsetSurface src )
    {
        if (this.pose == null) {
            return src;
        }

        Surface srcSurface = src.getSurface();
        Surface background = this.pose.getSurface();
        
        // Find the amount of space in the background image once we have removed the borders
        int remainingWidth = background.getWidth() - this.borderLeft - this.borderRight;
        int remainingHeight = background.getHeight() - this.borderTop - this.borderBottom;

        // How much do we need to scale the background image, so that its remaining part (excluding borders) will fit the source image.
        double scaleX = ((double) srcSurface.getWidth()) / ((double) remainingWidth);
        double scaleY = ((double) srcSurface.getHeight()) / ((double) remainingHeight);
        
        // We also need to scale the top and left border by the same amount.
        int scaledMarginX = (int) (this.borderLeft * scaleX);
        int scaledMarginY = (int) (this.borderTop * scaleY);
        
        Surface zoomedBackground = background.zoom(scaleX, scaleY, true);
        
        srcSurface.blit(zoomedBackground, scaledMarginX, scaledMarginY);

        return new SimpleOffsetSurface(zoomedBackground, src.getOffsetX() + scaledMarginX, src.getOffsetY() + scaledMarginY);
    }

    @Override
    public void applyGeometry( TransformationData src )
    {
        if (this.pose == null) {
            return;
        }

        src.set(
            src.width + this.borderLeft + this.borderRight,
            src.height + this.borderTop + this.borderTop,
            src.offsetX + this.borderLeft,
            src.offsetY + this.borderTop);
    }

}
