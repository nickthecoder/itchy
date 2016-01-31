/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.jame.JameException;
import uk.co.nickthecoder.jame.Surface;

public class ImagePose implements Pose
{
    private static ImagePose dummyPose;

    public static ImagePose superimpose( OffsetSurface below, OffsetSurface above, int dx, int dy )
    {
        int left = Math.max(above.getOffsetX() - below.getOffsetX() + dx, 0);
        int top = Math.max(above.getOffsetY() - below.getOffsetX() + dy, 0);

        int right = Math.max(above.getSurface().getWidth() - above.getOffsetX() -
            (below.getSurface().getWidth() - below.getOffsetX()) + dx, 0);

        int bottom = Math.max(above.getSurface().getHeight() - above.getOffsetY() -
            (below.getSurface().getHeight() - below.getOffsetY()) + dy, 0);

        int width = below.getSurface().getWidth() + left + right;
        int height = below.getSurface().getHeight() + top + bottom;

        Surface surface = new Surface(width, height, true);

        below.getSurface().blit(surface, left, top, Surface.BlendMode.COMPOSITE);

        above.getSurface().blit(surface, left + below.getOffsetX() - above.getOffsetX() + dx,
            top + below.getOffsetY() - above.getOffsetY() + dy, Surface.BlendMode.COMPOSITE);

        ImagePose pose = new ImagePose(surface, left + below.getOffsetX(), top + below.getOffsetY());

        return pose;
    }

    private int offsetX;
    private int offsetY;
    private double direction;

    private Surface surface;

    private static Surface loadSurface( String filename ) throws JameException
    {
        Surface loaded = new Surface(filename);
        Surface result = loaded.convert();
        loaded.free();

        return result;
    }
    
    // TODO What's this for? Surely it shouldn't be public, even if it is still required.
    public static ImagePose getDummyPose()
    {
        if (dummyPose == null) {
            dummyPose = new ImagePose(new Surface(1, 1, true));
        }
        return dummyPose;
    }

    public ImagePose( String filename ) throws JameException
    {
        this(loadSurface(filename));
    }

    public ImagePose( Surface surface )
    {
        this(surface, surface.getWidth() / 2, surface.getHeight() / 2);
    }

    public ImagePose( Surface surface, int offsetX, int offsetY )
    {
        this.surface = surface;

        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.direction = 0;
    }

    void load( String filename )
        throws JameException
    {
        this.surface = new Surface(filename);
    }
    
    public void setDirection( double direction )
    {
        this.direction = direction;
    }

    @Override
    public int getOffsetX()
    {
        return this.offsetX;
    }

    @Override
    public int getOffsetY()
    {
        return this.offsetY;
    }

    public void setOffsetX( int value )
    {
        this.offsetX = value;
    }

    public void setOffsetY( int value )
    {
        this.offsetY = value;
    }

    @Override
    public double getDirection()
    {
        return this.direction;
    }

    @Override
    public Surface getSurface()
    {
        return this.surface;
    }

    @Override
    public boolean isShared()
    {
        return true;
    }

    @Override
    public int getChangeId()
    {
        // As image poses don't change, we can return a constant.
        return 0;
    }

    @Override
    public void attach( Appearance appearance )
    {
    }

    public String toString()
    {
        return "ImagePose: (" + surface.getWidth() + "x" + surface.getHeight() + ")"
            + " offset(" + getOffsetX() + "," + getOffsetY() +")"
            + " direction: " + direction;
    }
}
