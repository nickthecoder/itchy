/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;

public abstract class AbstractLayer implements Layer
{
    private String name;

    public final Rect position;

    protected WorldRectangle worldRect;


    /**
     * It it the norm in mathematics, for the Y axis to point upwards, but display devices have the
     * Y axis pointing downwards. This boolean lets you choose which of these two conventions you
     * want to use for the world coordinates (i.e. values of {@link Actor.getY()}).
     */
    protected boolean yAxisPointsDown = false;

    private boolean visible = true;
    private boolean removePending = false;
    protected Layer parent;

    public AbstractLayer( String name, Rect position )
    {
        assert (position != null);
        this.name = name;
        this.position = position;
        this.worldRect = new WorldRectangle(0, 0, position.width, position.height);
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public Layer getParent()
    {
        return this.parent;
    }

    @Override
    public void setParent( Layer parent )
    {
        this.parent = parent;
    }

    /**
     * It it the norm in mathematics, for the Y axis to point upwards, but display devices have the
     * Y axis pointing downwards. This boolean lets you choose which of these two conventions you
     * want to use for the world coordinates (i.e. values of Actor.y). Set in the constructor.
     */
    @Override
    public boolean getYAxisPointsDown()
    {
        return this.yAxisPointsDown;
    }

    public void setYAxisPointsDown( boolean value )
    {
        this.yAxisPointsDown = value;
    }

    public boolean isRemovePending()
    {
        return this.removePending;
    }

    @Override
    public WorldRectangle getWorldRectangle()
    {
        return this.worldRect;
    }

    public void remove()
    {
        this.removePending = true;
        this.visible = false;
    }

    @Override
    public boolean isVisible()
    {
        return this.visible;
    }

    public void setVisible( boolean visible )
    {
        this.visible = visible;
    }

    public Rect getAbsolutePosition()
    {
        Rect rect = new Rect(this.position);
        Layer parent = this.parent;
        while (parent != null) {
            parent.adjustPosition(rect);
            parent = parent.getParent();
        }
        return rect;
    }

    @Override
    public void adjustPosition( Rect rect )
    {
        rect.x += this.position.x;
        rect.y += this.position.y;
    }

    @Override
    public void render( Rect within, Surface destSurface )
    {
        int clipLeft = within.x + this.position.x;
        int clipTop = within.y + this.position.y;
        int clipWidth = this.position.width;
        int clipHeight = this.position.height;

        if (clipLeft + clipWidth > within.x + within.width) {
            clipWidth = within.x + within.width - clipLeft;
        }
        if (clipTop + clipHeight > within.y + within.height) {
            clipHeight = within.y + within.height - clipTop;
        }
        Rect clip = new Rect(clipLeft, clipTop, clipWidth, clipHeight);
        this.render2(clip, destSurface);
    }

    protected abstract void render2( Rect clip, Surface destSurface );

    @Override
    public abstract void clear();

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
    public abstract void destroy();

    @Override
    public abstract void reset();

    @Override
    public String toString()
    {
        return this.getClass().getName() + " (" + this.name + ")";
    }
}
