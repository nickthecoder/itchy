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
import uk.co.nickthecoder.jame.event.MouseEvent;
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

    private int oldX;
    private int oldY;

    protected boolean adjustMouse( MouseEvent event )
    {
        this.oldX = event.x;
        this.oldY = event.y;
        Rect rect = getAbsolutePosition();
        event.x -= rect.x;
        event.y -= rect.y;
        return ((event.x >= 0) && (event.x < rect.width) && (event.y >= 0) && (event.y < rect.height));
    }

    protected void unadjustMouse( MouseEvent event )
    {
        event.x = this.oldX;
        event.y = this.oldY;
    }

    @Override
    public boolean onMouseDown( MouseButtonEvent event )
    {
        try {
            if (!adjustMouse(event)) {
                return false;
            }
            return this.rootContainer.mouseDown(event);

        } finally {
            unadjustMouse(event);
        }

    }

    @Override
    public boolean onMouseUp( MouseButtonEvent event )
    {
        try {
            if (!adjustMouse(event)) {
                return false;
            }
            return this.rootContainer.mouseUp(event);

        } finally {
            unadjustMouse(event);
        }
    }

    @Override
    public boolean onMouseMove( MouseMotionEvent event )
    {
        try {
            if (!adjustMouse(event)) {
                return false;
            }
            return this.rootContainer.mouseMove(event);

        } finally {
            unadjustMouse(event);
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
        // TODO Why does the GUI not have key up events?
        return false;
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
