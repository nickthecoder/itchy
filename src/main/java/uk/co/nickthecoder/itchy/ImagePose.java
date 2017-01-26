/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.jame.JameException;
import uk.co.nickthecoder.jame.Renderer;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.Texture;

/**
 * An ImagePose uses a simple bitmap
 */
public class ImagePose implements Pose
{
    private static ImagePose dummyPose;

    /**
     * Creates a new ImagePose by drawing two images on top of each other.
     * 
     * @param below
     *            The lower image, which will be (partially) obscured by the other image.
     * @param above
     *            The upper image, which will (partially) obscure the other image.
     * @param dx
     *            The relative position of the two images. Their offsetXs are also taken into account.
     * @param dy
     *            The relative position of the two images. Their offsetYs are also taken into account.
     * @return A new ImagePose, whose offsets will be at the same place as the 'below' image.
     */
    public static ImagePose superimpose(OffsetSurface below, OffsetSurface above, int dx, int dy)
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

    private static Surface loadSurface(String filename) throws JameException
    {
        Surface loaded = new Surface(filename);
        Surface result = loaded.convert();
        loaded.free();

        return result;
    }

    /**
     * Creates a 1x1 transparent pixel/
     * 
     * @return
     * @priority 2
     */
    public static ImagePose getDummyPose()
    {
        if (dummyPose == null) {
            dummyPose = new ImagePose(new Surface(1, 1, true));
        }
        return dummyPose;
    }

    /**
     * Creates an ImagePose by loading it from a file.
     * The offsets will be at the centre of the image.
     * 
     * @param filename
     *            The absolute path of the image.
     * @throws JameException
     */
    public ImagePose(String filename) throws JameException
    {
        this(loadSurface(filename));
    }

    /**
     * Creates an ImagePose from an existing Surface.
     * The offsets will be at the centre of the image.
     * 
     * @param surface
     */
    public ImagePose(Surface surface)
    {
        this(surface, surface.getWidth() / 2, surface.getHeight() / 2);
    }

    /**
     * Creates an ImagePose from an existing Surface.
     * 
     * @param surface
     * @param offsetX
     * @param offsetY
     */
    public ImagePose(Surface surface, int offsetX, int offsetY)
    {
        this.surface = surface;

        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.direction = 0;
    }

    /**
     * Used internally by Itchy when reloading the resources.
     * 
     * @param filename
     * @throws JameException
     * @priority 5
     */
    void load(String filename)
        throws JameException
    {
        this.surface = new Surface(filename);
    }

    /**
     * Sets the direction the image is pointing in.
     * For example an picture of a car heading to the left will have a direction of 180 degrees;
     * A rocket pointing upwards will have a direction of 90 degrees.
     * 
     * @param direction
     * @priority 2
     */
    public void setDirection(double direction)
    {
        this.direction = direction;
    }

    /**
     * A simple getter. See {@link #setDirection(double)}.
     */
    @Override
    public double getDirection()
    {
        return this.direction;
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

    /**
     * A simple setter.
     * 
     * @param value
     */
    public void setOffsetX(int value)
    {
        this.offsetX = value;
    }

    /**
     * A simple setter.
     * 
     * @param value
     */
    public void setOffsetY(int value)
    {
        this.offsetY = value;
    }

    /**
     * The image
     * 
     * @return
     * @priority 3
     */
    @Override
    public Surface getSurface()
    {
        return this.surface;
    }

    private Texture texture;

    // TODO Currently every ImagePose will have its own Texture, even if they came from a SpriteSheet.
    // Should really use one big Texture, and keep track of the rectangle.
    public Texture getTexture(Renderer renderer)
    {
        if (texture == null) {
            texture = new Texture(renderer, getSurface());
        }
        return texture;
    }

    /**
     * Used internally by Itchy as part of the OffsetSurface interface.
     * 
     * @return true
     * @priority 5
     */
    @Override
    public boolean isShared()
    {
        return true;
    }

    /**
     * Use internally by Itchy.
     * 
     * @return 0
     * @priority 5
     */
    @Override
    public int getChangeId()
    {
        // As image poses don't change, we can return a constant.
        return 0;
    }

    /**
     * Does nothing.
     * 
     * @priority 5
     */
    @Override
    public void attach(Appearance appearance)
    {
    }

    /**
     * @priority 2
     */
    public String toString()
    {
        return "ImagePose: (" + surface.getWidth() + "x" + surface.getHeight() + ")"
            + " offset(" + getOffsetX() + "," + getOffsetY() + ")"
            + " direction: " + direction;
    }
}
