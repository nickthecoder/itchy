/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.makeup;

import java.util.List;

import uk.co.nickthecoder.itchy.Makeup;
import uk.co.nickthecoder.itchy.OffsetSurface;
import uk.co.nickthecoder.itchy.SimpleOffsetSurface;
import uk.co.nickthecoder.itchy.TextPose;
import uk.co.nickthecoder.itchy.util.AbstractProperty;
import uk.co.nickthecoder.itchy.util.Property;
import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.Surface.BlendMode;
import uk.co.nickthecoder.jame.TrueTypeFont;

public class TextShadow implements Makeup
{
    private static final List<AbstractProperty<Makeup, ?>> properties =
        AbstractProperty.<Makeup> findAnnotations(TextShadow.class);

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

    @Property(label = "Colour",alpha=false)
    public RGBA getColor()
    {
        return this.color;
    }

    public void setColor( RGBA color )
    {
        this.seq++;
        this.color = color;
    }

    public int getChangeId()
    {
        return this.seq;
    }
    
    @Override
    public OffsetSurface apply( OffsetSurface src )
    {
        int shadowX = this.dx < 0 ? 0 : this.dx;
        int shadowY = this.dy > 0 ? 0 : -this.dy;

        int textX = this.dx < 0 ? -this.dx : 0;
        int textY = this.dy > 0 ? this.dy : 0;
        
        if (src instanceof TextPose) {
            TextPose pose = (TextPose) src;

            Surface combined = new Surface(pose.getWidth() + Math.abs(this.dx), pose.getHeight() + Math.abs(this.dy), true);

            TrueTypeFont ttf = pose.getTrueTypeFont();
            Surface shadow = ttf.renderBlended(pose.getText(), this.getColor());

            shadow.blit(combined, shadowX, shadowY, BlendMode.COMPOSITE);
            pose.getSurface().blit(combined, textX, textY, BlendMode.COMPOSITE);
            shadow.free();
            
            return new SimpleOffsetSurface(combined, src.getOffsetX() + textX, src.getOffsetY() + textY);
        }

        return src;
    }

    @Override
    public List<AbstractProperty<Makeup, ?>> getProperties()
    {
        return properties;
    }

}
