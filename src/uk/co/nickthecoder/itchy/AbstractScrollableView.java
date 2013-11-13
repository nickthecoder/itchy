/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.jame.Rect;

public abstract class AbstractScrollableView extends AbstractView
{
    /**
     * The area of the world currently visible within this layer.
     */
    protected final WorldRectangle worldRect;

    public AbstractScrollableView( Rect position )
    {
        super(position);
        this.worldRect = new WorldRectangle(0, 0, position.width, position.height);
    }

    // TODO is this needed?
    public WorldRectangle getWorldRectangle()
    {
        return this.worldRect;
    }

    public void ceterOn( Actor actor )
    {
        this.centerOn(actor.getX(), actor.getY());
    }

    public void centerOn( double x, double y )
    {
        this.worldRect.x = x - this.worldRect.width / 2;
        this.worldRect.y = y - this.worldRect.height / 2;
    }

    public void scrollTo( double x, double y )
    {
        this.worldRect.x = x;
        this.worldRect.y = y;
    }

    public void scrollBy( double dx, double dy )
    {
        this.worldRect.x += dx;
        this.worldRect.y += dy;
    }

    @Override
    public boolean contains( int x, int y )
    {
        Rect position = this.getAbsolutePosition();
        if ((x < position.x) || (y < position.y) || (x > position.x + position.width) ||
            (y > position.y + position.height)) {
            return false;
        }
        return true;
    }

    @Override
    public void reset()
    {
        scrollTo(0, 0);
    }

}
