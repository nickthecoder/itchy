/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;

public class GraphicsContext
{
    private Surface destination;

    /**
     * The position on the destination surface which corresponds to the world's origin. For example,
     * if ox is 10, and an image is blitted at x=30, then the image will be blitted at x=40 on the
     * destination surface.
     */
    private int ox;

    private int oy;

    /**
     * The clipping rectangle of the destination surface (not in world coordinates).
     */
    private Rect clip;

    public GraphicsContext( Surface destination )
    {
        this.destination = destination;
        this.clip = new Rect(0, 0, this.destination.getWidth(), this.destination.getHeight());
    }

    public void blit( Surface surface, int x, int y )
    {
        this.blit(surface, new Rect(0, 0, surface.getWidth(), surface.getHeight()), x, y,
                Surface.BlendMode.NONE);
    }

    public void blit( Surface surface, int x, int y, Surface.BlendMode blendMode )
    {
        this.blit(surface, new Rect(0, 0, surface.getWidth(), surface.getHeight()), x, y, blendMode);
    }

    public void blit( Surface surface, Rect origSrcRect, int x, int y, Surface.BlendMode blendMode )
    {
        x += this.ox;
        y += this.oy;

        Rect srcRect = new Rect(origSrcRect);

        int clipLeft = this.clip.x - x;
        if (clipLeft > 0) {
            x += clipLeft;
            srcRect.x += clipLeft;
            srcRect.width -= clipLeft;
        }

        int clipRight = (x + srcRect.width) - (this.clip.x + this.clip.width);
        if (clipRight > 0) {
            srcRect.width -= clipRight;
        }

        if (srcRect.width < 0) {
            return;
        }

        int clipBottom = (y + srcRect.height) - (this.clip.y + this.clip.height);
        if (clipBottom > 0) {
            srcRect.height -= clipBottom;
        }

        int clipTop = this.clip.y - y;
        if (clipTop > 0) {
            y += clipTop;
            srcRect.y += clipTop;
            srcRect.height -= clipTop;
        }

        if (srcRect.height < 0) {
            return;
        }

        surface.blit(srcRect, this.destination, x, y, blendMode);
    }

    public void fill( Rect rect, RGBA color )
    {
        Rect destRect = this.adjustRect(rect);

        if (destRect != null) {
            this.destination.fill(destRect, color);
        }
    }

    private Rect adjustRect( Rect rect )
    {
        Rect result = new Rect(rect.x + this.ox, rect.y + this.oy, rect.width, rect.height);
        return result;
    }

    public void scroll( int dx, int dy )
    {
        this.ox -= dx;
        this.oy -= dy;
    }

    public boolean empty()
    {
        return (this.clip.width <= 0) || (this.clip.height <= 0);
    }

    public GraphicsContext window( Rect rect )
    {
        GraphicsContext result = new GraphicsContext(this.destination);
        result.ox = this.ox + rect.x;
        result.oy = this.oy + rect.y;

        result.clip = new Rect(result.ox, result.oy, rect.width, rect.height);

        int dx = (result.clip.x + result.clip.width) - (this.clip.x + this.clip.width);
        int dy = (result.clip.y + result.clip.height) - (this.clip.y + this.clip.height);

        if (dx > 0) {
            result.clip.width -= dx;
        }
        if (dy > 0) {
            result.clip.height -= dy;
        }

        dx = this.clip.x - result.clip.x;
        if (dx > 0) {
            result.clip.x += dx;
            result.clip.width -= dx;
        }
        dy = this.clip.y - result.clip.y;
        if (dy > 0) {
            result.clip.y += dy;
            result.clip.height -= dy;
        }

        return result;
    }

    @Override
    public String toString()
    {
        return "GraphicsContext @ " + this.ox + "," + this.oy + " clip : " + this.clip;
    }

}
