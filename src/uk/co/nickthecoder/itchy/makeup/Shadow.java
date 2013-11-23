/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.makeup;

import java.util.List;

import uk.co.nickthecoder.itchy.OffsetSurface;
import uk.co.nickthecoder.itchy.SimpleOffsetSurface;
import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.Surface.BlendMode;

public class Shadow implements Makeup
{
    private static final List<AbstractProperty<Makeup, ?>> properties =
        AbstractProperty.<Makeup> findAnnotations(Shadow.class);

    private int dx;

    private int dy;

    private RGBA color = RGBA.BLACK;

    private int seq = 0;

    @Property(label = "X Offset")
    public int getDx()
    {
        return this.dx;
    }

    public void setDx( int dx )
    {
        this.seq++;
        this.dx = dx;
    }

    @Property(label = "Y Offset")
    public int getDy()
    {
        return this.dy;
    }

    public void setDy( int dy )
    {
        this.seq++;
        this.dy = dy;
    }

    @Property(label = "Colour")
    public RGBA getColor()
    {
        return this.color;
    }

    public void setColor( RGBA color )
    {
        this.seq++;
        this.color = color;
    }

    @Override
    public int getChangeId()
    {
        return this.seq;
    }

    @Override
    public OffsetSurface apply( OffsetSurface src )
    {
        int shadowX = this.dx < 0 ? 0 : this.dx;
        int shadowY = this.dy > 0 ? 0 : -this.dy;

        int srcX = this.dx < 0 ? -this.dx : 0;
        int srcY = this.dy > 0 ? this.dy : 0;

        Surface combined = new Surface(
            src.getSurface().getWidth() + Math.abs(this.dx),
            src.getSurface().getHeight() + Math.abs(this.dy), true);

        Surface colorSurface = new Surface(src.getSurface().getWidth(), src.getSurface().getHeight(), true);
        colorSurface.fill(this.color);

        Surface shadow;
        if ( src.isShared() ) {
            shadow = src.getSurface().copy();
        } else {
            shadow = src.getSurface();
        }
        colorSurface.blit(shadow);
        colorSurface.free();
        
        shadow.blit(combined, shadowX, shadowY, BlendMode.COMPOSITE);
        src.getSurface().blit(combined, srcX, srcY, BlendMode.COMPOSITE);

        return new SimpleOffsetSurface(combined, src.getOffsetX() + srcX, src.getOffsetY() + srcY);
    }

    @Override
    public void applyGeometry( TransformationData src )
    {
        src.set(
            src.width + this.dx,
            src.height + this.dy,
            src.offsetX + (this.dx > 0 ? this.dx : 0),
            src.offsetY + (this.dy > 0 ? this.dy : 0));
    }

    @Override
    public List<AbstractProperty<Makeup, ?>> getProperties()
    {
        return properties;
    }

}
