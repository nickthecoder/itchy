/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.util;

import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;

/**
 * Divide an image into a 3x3 grid, and you have nine patches, which can be used to draw a larger image. Imagine we have an image of a plain
 * button background, and we want to draw a much larger button.
 * 
 * Take the corners, and copy them into the corners of the destination image. Now take each side, and copy it multiple times so that it
 * reaches from one corner to the other. We are just left with the centre to complete the final image. We could tile the source's centre
 * into the destination centre, but for some images, a flat fill is all that's needed.
 */
public class NinePatch extends ImageRenderable
{
    /**
     * All of the ways that the center can be rendered.
     */
    public enum Middle
    {
        empty, tile, fill
    };

    public int marginTop;

    public int marginRight;

    public int marginBottom;

    public int marginLeft;

    /**
     * How should the middle of the destination image be drawn?
     */
    public Middle middle;

    /**
     * If {@link #middle} is <code>Middle.fill</code>, then this is the colour used to draw the middle.
     */
    public RGBA backgroundColor;

    /**
     * Creates a nine patch with the desired margins, with the default middle of <code>Middle.file</code>.
     * 
     * @param surface
     *        The source image for the NinePatch. The fill colour is taken from centre pixel of the source surfaces middle patch.
     * @param marginTop
     * @param marginRight
     * @param marginBottom
     * @param marginLeft
     */
    public NinePatch( Surface surface, int marginTop, int marginRight, int marginBottom, int marginLeft )
    {
        this(surface, marginTop, marginRight, marginBottom, marginLeft, Middle.tile);
    }

    /**
     * @param surface
     * @param marginTop
     * @param marginRight
     * @param marginBottom
     * @param marginLeft
     * @param middle
     *        If using <code>Middle.fill</code>, then the fill colour is taken from the centre of the source image's middle patch.
     */
    public NinePatch( Surface surface, int marginTop, int marginRight, int marginBottom,
        int marginLeft, Middle middle )
    {
        super(surface);

        this.middle = middle;

        this.marginTop = marginTop;
        this.marginRight = marginRight;
        this.marginBottom = marginBottom;
        this.marginLeft = marginLeft;

        int midY = (this.marginTop + surface.getHeight() - this.marginBottom) / 2;
        int midX = (this.marginLeft + surface.getWidth() - this.marginRight) / 2;
        this.backgroundColor = surface.getPixelRGBA(midX, midY);
    }

    public int getMinimumWidth()
    {
        return this.marginLeft + this.marginRight;
    }
    
    public int getMinimumHeight()
    {
        return this.marginTop + this.marginBottom;
    }
    
    public int getMarginTop()
    {
        return this.marginTop;
    }

    public int getMarginRight()
    {
        return this.marginRight;
    }

    public int getMarginBottom()
    {
        return this.marginBottom;
    }

    public int getMarginLeft()
    {
        return this.marginLeft;
    }

    /**
     * Creates a new Surface of the given size and renders this NinePatch onto it.
     * 
     * @param width
     *        The width of the new Surface.
     * @param height
     *        The height of the new Surface
     * @return A new Surface, of the given size, with an alpha channel.
     */
    public Surface createSurface( int width, int height )
    {
        Surface surface = new Surface(width, height, true);
        this.render(surface);
        return surface;
    }

    /**
     * Draws the nine patch onto <code>result</code>.
     * 
     * @param result
     *        The destination surface. It is assumed to be a blank image.
     */
    @Override
    public void render( Surface result )
    {
        Rect srcRect;
        Rect destRect;

        int blankHeight = this.surface.getHeight() - this.marginTop - this.marginBottom;
        int blankWidth = this.surface.getWidth() - this.marginLeft - this.marginRight;
        int midY = (this.marginTop + result.getHeight() - this.marginBottom) / 2;
        int midX = (this.marginLeft + result.getWidth() - this.marginRight) / 2;

        this.surface.setAlphaEnabled(false);

        if (this.middle == Middle.fill) {
            // flat fill the middle

            Rect rect = new Rect(this.marginLeft, this.marginTop, result.getWidth() -
                this.marginLeft - this.marginRight, result.getHeight() - this.marginTop -
                this.marginBottom);
            result.fill(rect, this.backgroundColor);

        } else if (this.middle == Middle.tile) {
            // tile the middle

            srcRect = new Rect(this.marginLeft, this.marginTop, blankWidth, blankHeight);
            destRect = new Rect(0, 0, this.marginLeft, blankHeight);
            for (int x = midX; x < result.getWidth(); x += blankWidth) {
                for (int y = midY; y < result.getHeight(); y += blankHeight) {
                    destRect.x = x;
                    destRect.y = y;
                    this.surface.blit(srcRect, result, destRect);
                    destRect.y = midY * 2 - y - blankHeight;
                    this.surface.blit(srcRect, result, destRect);

                    destRect.x = midX * 2 - x - blankWidth;
                    destRect.y = y;
                    this.surface.blit(srcRect, result, destRect);
                    destRect.y = midY * 2 - y - blankHeight;
                    this.surface.blit(srcRect, result, destRect);
                }
            }
        }

        // left edge
        srcRect = new Rect(0, this.marginTop, this.marginLeft, blankHeight);
        destRect = new Rect(0, 0, this.marginLeft, blankHeight);
        for (int y = midY; y < result.getHeight(); y += blankHeight) {
            destRect.y = y;
            this.surface.blit(srcRect, result, destRect);
            destRect.y = midY * 2 - y - blankHeight;
            this.surface.blit(srcRect, result, destRect);
        }

        // right edge
        srcRect = new Rect(this.surface.getWidth() - this.marginRight, this.marginTop,
            this.marginRight, blankHeight);
        destRect = new Rect(result.getWidth() - this.marginRight, 0, this.marginRight, blankHeight);
        for (int y = midY; y < result.getHeight(); y += blankHeight) {
            destRect.y = y;
            this.surface.blit(srcRect, result, destRect);
            destRect.y = midY * 2 - y - blankHeight;
            this.surface.blit(srcRect, result, destRect);
        }

        // top edge
        srcRect = new Rect(this.marginLeft, 0, blankWidth, this.marginTop);
        destRect = new Rect(0, 0, blankWidth, this.marginTop);
        for (int x = midX; x < result.getWidth(); x += blankWidth) {
            destRect.x = x;
            this.surface.blit(srcRect, result, destRect);
            destRect.x = midX * 2 - x - blankWidth;
            this.surface.blit(srcRect, result, destRect);
        }

        // bottom edge
        srcRect = new Rect(this.marginLeft, this.surface.getHeight() - this.marginBottom,
            blankWidth, this.marginBottom);
        destRect = new Rect(0, result.getHeight() - this.marginBottom, blankWidth,
            this.marginBottom);
        for (int x = midX; x < result.getWidth(); x += blankWidth) {
            destRect.x = x;
            this.surface.blit(srcRect, result, destRect);
            destRect.x = midX * 2 - x - blankWidth;
            this.surface.blit(srcRect, result, destRect);
        }

        // top left corner
        srcRect = new Rect(0, 0, this.marginLeft, this.marginTop);
        destRect = new Rect(0, 0, this.marginLeft, this.marginTop);
        this.surface.blit(srcRect, result, destRect);

        // top right corner
        srcRect = new Rect(this.surface.getWidth() - this.marginRight, 0, this.marginRight,
            this.marginTop);
        destRect = new Rect(result.getWidth() - this.marginRight, 0, this.marginRight,
            this.marginTop);
        this.surface.blit(srcRect, result, destRect);

        // bottom left corner
        srcRect = new Rect(0, this.surface.getHeight() - this.marginBottom, this.marginLeft,
            this.marginBottom);
        destRect = new Rect(0, result.getHeight() - this.marginBottom, this.marginLeft,
            this.marginBottom);
        this.surface.blit(srcRect, result, destRect);

        // bottom right corner
        srcRect = new Rect(this.surface.getWidth() - this.marginRight, this.surface.getHeight() -
            this.marginBottom, this.marginRight, this.marginBottom);
        destRect = new Rect(result.getWidth() - this.marginRight, result.getHeight() -
            this.marginBottom, this.marginRight, this.marginBottom);
        this.surface.blit(srcRect, result, destRect);

        this.surface.setAlphaEnabled(true);

    }

}
