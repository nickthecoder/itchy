/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

public abstract class Layer implements MouseListener
{
    private String name;
    
    public final Rect position;

    protected WorldRectangle worldRect;

    /**
     * It it the norm in mathematics, for the Y axis to point upwards, but display devices have the
     * Y axis pointing downwards. This boolean lets you choose which of these two conventions you
     * want to use for the world coordinates (i.e. values of Actor.y).
     */
    protected boolean yAxisPointsDown = false;

    private boolean visible = true;
    private boolean removePending = false;
    protected Layer parent;
    protected List<MouseListener> mouseListeners;
    
    /**
     * Used by the editor.
     */
    public boolean locked = false;

    public Layer( String name, Rect position )
    {
        assert (position != null);
        this.name = name;
        this.position = position;
        this.worldRect = new WorldRectangle(0, 0, position.width, position.height);
    }

    public String getName()
    {
        return this.name;
    }
    
    /**
     * It it the norm in mathematics, for the Y axis to point upwards, but display devices have the
     * Y axis pointing downwards. This boolean lets you choose which of these two conventions you
     * want to use for the world coordinates (i.e. values of Actor.y). Set in the constructor.
     */
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

    public WorldRectangle getWorldRectangle()
    {
        return this.worldRect;
    }

    public void remove()
    {
        this.removePending = true;
        this.visible = false;
    }

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
            parent = parent.parent;
        }
        return rect;
    }

    protected void adjustPosition( Rect rect )
    {
        rect.x += this.position.x;
        rect.y += this.position.y;
    }

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

    protected abstract void clear();

    public void addMouseListener( MouseListener listener )
    {
        if (this.mouseListeners == null) {
            this.mouseListeners = new ArrayList<MouseListener>();
            Itchy.singleton.getGame().addMouseListener(this);
        }
        this.mouseListeners.add(listener);
    }

    public void removeMouseListener( MouseListener listener )
    {
        this.mouseListeners.remove(listener);
        if (this.mouseListeners.size() == 0) {
            this.mouseListeners = null;
            Itchy.singleton.getGame().removeMouseListener(this);
        }
    }

    protected void adjustMouse( MouseEvent event )
    {
        Rect position = this.getAbsolutePosition();
        event.x -= position.x;

        if (this.yAxisPointsDown) {
            event.y -= this.position.y;
        } else {
            event.y = position.y + this.position.height - event.y;
        }
    }

    protected boolean contains( int x, int y )
    {
        Rect position = this.getAbsolutePosition();
        if ((x < position.x) || (y < position.y) || (x > position.x + position.width) ||
            (y > position.y + position.height)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean onMouseDown( MouseButtonEvent event )
    {
        if (!this.contains(event.x, event.y)) {
            return false;
        }

        int tx = event.x;
        int ty = event.y;

        this.adjustMouse(event);

        try {
            for (MouseListener listener : this.mouseListeners) {
                if (listener.onMouseDown(event)) {
                    return true;
                }
            }
        } finally {
            event.x = tx;
            event.y = ty;
        }

        return false;
    }

    @Override
    public boolean onMouseUp( MouseButtonEvent event )
    {
        if (!this.contains(event.x, event.y)) {
            return false;
        }

        int tx = event.x;
        int ty = event.y;

        this.adjustMouse(event);

        try {
            for (MouseListener listener : this.mouseListeners) {
                if (listener.onMouseUp(event)) {
                    return true;
                }
            }
        } finally {
            event.x = tx;
            event.y = ty;
        }

        return false;
    }

    @Override
    public boolean onMouseMove( MouseMotionEvent event )
    {

        if (!this.contains(event.x, event.y)) {
            return false;
        }

        int tx = event.x;
        int ty = event.y;

        this.adjustMouse(event);

        try {
            for (MouseListener listener : this.mouseListeners) {
                if (listener.onMouseMove(event)) {
                    return true;
                }
            }
        } finally {
            event.x = tx;
            event.y = ty;
        }

        return false;
    }

    public abstract void destroy();
    
    public String toString()
    {
        return this.getClass().getName() + " (" + this.name + ")";
    }
}
