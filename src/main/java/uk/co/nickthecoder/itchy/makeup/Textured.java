/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.makeup;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.OffsetSurface;
import uk.co.nickthecoder.itchy.Pose;
import uk.co.nickthecoder.itchy.PoseResource;
import uk.co.nickthecoder.itchy.SimpleOffsetSurface;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.IntegerProperty;
import uk.co.nickthecoder.itchy.property.PoseResourceProperty;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.Surface.BlendMode;

/**
 * Takes a pose as a texture, and draws it on top of the source image, such that the source image's alpha remains unchanged, but the RGB is
 * replaced by the texutre's.
 * 
 * Note, if the pose texture has an alpha channel, then the source image's RGB will show through wherever the texture is transparent.
 */
public class Textured extends AbstractMakeup
{
    protected static final List<Property<Makeup, ?>> properties = new ArrayList<Property<Makeup, ?>>();

    static {
        properties.add(new PoseResourceProperty<Makeup>("pose").access("poseResource").aliases("poseName"));
        properties.add(new IntegerProperty<Makeup>("x"));
        properties.add(new IntegerProperty<Makeup>("y"));
    }

    private int x;

    private int y;

    private PoseResource poseResource;

    private int seq = 0;

    public int getX()
    {
        return this.x;
    }

    public void setX( int x )
    {
        this.seq++;
        this.x = x;
    }

    public int getY()
    {
        return this.y;
    }

    public void setY( int y )
    {
        this.seq++;
        this.y = y;
    }

    public Pose getPose()
    {
        return this.poseResource.pose;
    }

    public PoseResource getPoseResource()
    {
        return this.poseResource;
    }
    
    public void setPoseResource( PoseResource poseResource )
    {
        this.poseResource = poseResource;
        this.seq ++;
    }

    @Override
    public int getChangeId()
    {
        return this.seq;
    }

    @Override
    public OffsetSurface apply( OffsetSurface src )
    {
        if (this.poseResource == null) {
            return src;
        }

        boolean rebuiltTexture = false;
        Surface texture = this.poseResource.pose.getSurface();

        int textureWidth = texture.getWidth();
        int textureHeight = texture.getHeight();

        int bottom = src.getSurface().getHeight();
        int right = src.getSurface().getWidth();

        Surface tiledSurface = new Surface(right, bottom, true);

        int y = this.y > 0 ? (this.y % textureHeight) - textureHeight : 0;

        while (y < bottom) {
            int x = this.x > 0 ? (this.x % textureWidth) - textureWidth : 0;
            while (x < right) {

                if (texture.hasAlphaChannel()) {
                    texture.blit(tiledSurface, x, y, BlendMode.COMPOSITE);
                } else {
                    texture.blit(tiledSurface, x, y);
                }

                x += textureWidth;
            }
            y += textureHeight;
        }

        if (rebuiltTexture) {
            texture.free();
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
    public List<Property<Makeup, ?>> getProperties()
    {
        return properties;
    }

}
