/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.makeup;

import java.util.List;

import uk.co.nickthecoder.itchy.ImagePose;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.OffsetSurface;
import uk.co.nickthecoder.itchy.SimpleOffsetSurface;
import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.jame.Surface;

public class Textured implements Makeup
{
    private static final List<AbstractProperty<Makeup, ?>> properties =
        AbstractProperty.<Makeup> findAnnotations(Textured.class);

    private int x;

    private int y;

    private String poseName = null;

    private ImagePose pose;

    private int seq = 0;

    @Property(label = "X")
    public int getX()
    {
        return this.x;
    }

    public void setX( int x )
    {
        this.seq++;
        this.x = x;
    }

    @Property(label = "Y")
    public int getY()
    {
        return this.y;
    }

    public void setY( int y )
    {
        this.seq++;
        this.y = y;
    }

    @Property(label = "Pose Name", allowNull = false)
    public String getPoseName()
    {
        return this.poseName;
    }

    public void setPoseName( String poseName )
    {
        this.poseName = poseName;
        this.pose = Itchy.getGame().resources.getPose(poseName);
    }

    @Override
    public int getChangeId()
    {
        return this.seq;
    }

    @Override
    public OffsetSurface apply( OffsetSurface src )
    {
        if (this.pose == null) {
            return src;
        }
        
        Surface texture = this.pose.getSurface();
        int textureWidth = texture.getWidth();
        int textureHeight = texture.getHeight();

        int bottom = src.getSurface().getHeight();
        int right = src.getSurface().getWidth();

        Surface tiledSurface = new Surface( right, bottom, true );

        int y = this.y > 0 ? (this.y % textureHeight) - textureHeight : 0;

        while (y < bottom) {
            int x = this.x > 0 ? (this.x % textureWidth) - textureWidth : 0;
            while (x < right) {

                texture.blit(tiledSurface, x, y);

                x += textureWidth;
            }
            y += textureHeight;
        }

        Surface result = src.getSurface().copy();
        tiledSurface.blit(result);
        tiledSurface.free();

        return new SimpleOffsetSurface(result, src.getOffsetX(), src.getOffsetY());
    }

    @Override
    public void applyGeometry( TransformationData src )
    {
    }

    @Override
    public List<AbstractProperty<Makeup, ?>> getProperties()
    {
        return properties;
    }

}
