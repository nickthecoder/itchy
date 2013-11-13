/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import uk.co.nickthecoder.itchy.AbstractView;
import uk.co.nickthecoder.itchy.GraphicsContext;
import uk.co.nickthecoder.itchy.InputListener;
import uk.co.nickthecoder.itchy.View;
import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

/**
 */
public class GuiView extends AbstractView implements View, InputListener
{
    private boolean invalid = true;

    public final RootContainer rootContainer;

    private Surface surface;

    public GuiView( Rect position, RootContainer rootContainer )
    {
        super(position);
        this.rootContainer = rootContainer;
    }

    public Surface getSurface()
    {
        this.rootContainer.ensureLayedOut();
        if (this.surface == null) {
            this.surface = new Surface(this.rootContainer.width, this.rootContainer.height, true);
        }
        if (this.invalid) {
            this.surface.fill(new RGBA(0, 0, 0, 0));
            GraphicsContext gc = new GraphicsContext(this.surface);
            this.rootContainer.render(gc);

        }
        this.invalid = false;
        return this.surface;
    }

    public void invalidate()
    {
        this.invalid = true;
    }

    @Override
    public boolean onMouseDown( MouseButtonEvent event )
    {
        Rect rect = new Rect(getAbsolutePosition());

        event.x -= rect.x;
        event.y -= rect.y;
        try {
            // TODO use onMouseDown instead?
            return this.rootContainer.mouseDown(event);

        } finally {
            event.x += rect.x;
            event.y += rect.y;
        }
    }

    @Override
    public boolean onMouseUp( MouseButtonEvent event )
    {
        Rect rect = new Rect(getAbsolutePosition());

        event.x -= rect.x;
        event.y -= rect.y;
        try {

            return this.rootContainer.mouseUp(event);

        } finally {
            event.x += rect.x;
            event.y += rect.y;
        }
    }

    @Override
    public boolean onMouseMove( MouseMotionEvent event )
    {
        Rect rect = new Rect(getAbsolutePosition());

        event.x -= rect.x;
        event.y -= rect.y;
        try {

            return this.rootContainer.mouseMove(event);

        } finally {
            event.x += rect.x;
            event.y += rect.y;
        }
    }

    @Override
    public boolean onKeyDown( KeyboardEvent event )
    {
        return this.rootContainer.keyDown(event);
    }

    @Override
    public boolean onKeyUp( KeyboardEvent ke )
    {
        return false;
    }

    /*
    @Override
    public boolean mouseDown( MouseButtonEvent event )
    {
        if (this.draggable) {
            if ((event.button == 2) || ((event.button == 1) && Itchy.isShiftDown())) {
                this.captureMouse(this);
                this.dragging = true;
                this.dragStartX = event.x;
                this.dragStartY = event.y;
                return true;
            }
        }

        return super.mouseDown(event);
    }

    @Override
    public void mouseMove( MouseMotionEvent event )
    {
        if (this.dragging) {
            int dx = event.x - this.dragStartX;
            int dy = event.y - this.dragStartY;

            getActor().moveBy(dx, dy);
            // this.dragStartX = event.x;
            // this.dragStartY = event.y;
        }
    }

    @Override
    public void mouseUp( MouseButtonEvent event )
    {
        if (this.dragging) {
            this.releaseMouse(this);
            this.dragging = true;
        }
    }
    */

    @Override
    public boolean getYAxisPointsDown()
    {
        return true;
    }

    @Override
    public void render2( Surface destSurface, Rect clip, int offsetX, int offsetY )
    {
        Surface surface = getSurface();
        Rect srcRect = new Rect(0, 0, surface.getWidth(), surface.getHeight());
        surface.blit(srcRect, destSurface, offsetX + this.rootContainer.x, offsetY + this.rootContainer.y);
    }

    @Override
    public String toString()
    {
        return "GuiView : " + this.rootContainer;
    }
}
