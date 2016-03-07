/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.jame.Rect;

public abstract class AbstractScrollableView extends AbstractView implements ScrollableView
{
    /**
     * The area of the world currently visible within this layer.
     */
    protected final WorldRectangle worldRect;

    public AbstractScrollableView()
    {
        this(new Rect(0, 0, 640, 480));
    }

    public AbstractScrollableView(Rect position)
    {
        super(position);
        this.worldRect = new WorldRectangle(0, 0, position.width, position.height);
    }

    @Override
    public WorldRectangle getVisibleRectangle()
    {
        return this.worldRect;
    }

    @Override
    public void setPosition(Rect rect)
    {
        super.setPosition(rect);
        this.worldRect.width = rect.width;
        this.worldRect.height = rect.height;
    }

    @Override
    public void centerOn(Actor actor)
    {
        this.centerOn(actor.getX(), actor.getY());
    }

    @Override
    public void centerOn(double x, double y)
    {
        this.worldRect.x = x - this.worldRect.width / 2;
        this.worldRect.y = y - this.worldRect.height / 2;
    }

    @Override
    public void scrollTo(double x, double y)
    {
        this.worldRect.x = x;
        this.worldRect.y = y;
    }

    @Override
    public void scrollBy(double dx, double dy)
    {
        this.worldRect.x += dx;
        this.worldRect.y += dy;
    }

    // TODO Is this different from the parent implementation?
    @Override
    public boolean contains(int x, int y)
    {
        if ((x < position.x) || (y < position.y) || (x > position.x + position.width) ||
            (y > position.y + position.height)) {
            return false;
        }
        return true;
    }

    @Override
    public double getWorldX(int screenX)
    {
        return this.worldRect.x + super.getWorldX(screenX);
    }

    @Override
    public double getWorldY(int screenY)
    {
        return this.worldRect.y + super.getWorldY(screenY);

    }

    /**
     * Returns the x coordinate of the mouse pointer in this view's coordinate system.
     * The position of the view on the screen, and also the scroll offset is taken into account.
     */
    public double getMouseX()
    {
        return getWorldX(Itchy.getMouseX());
    }

    /**
     * Returns the y coordinate of the mouse pointer in this view's coordinate system.
     * The position of the view on the screen, and also the scroll offset is taken into account.
     *
     * The direction of the Y axis is also taken care of. Stage views have the Y axis pointing upwards,
     * whereas raw mouse events have (0,0) in the top left, with the y axis pointing downwards.
     */
    public double getMouseY()
    {
        return getWorldY(Itchy.getMouseY());
    }

    @Override
    public void reset()
    {
        scrollTo(0, 0);
    }

}
