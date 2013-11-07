/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.InputListener;
import uk.co.nickthecoder.itchy.GraphicsContext;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Pose;
import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

/**
 * GuiPose is an intermediary between the world of Actor/Layers and and the world of
 * Components/Containers. A GuiPose is both a Container (a RootContainer in fact), and a Pose (which
 * is part of an Actor's Appearance).
 * 
 * A GuiPose can be included within a game, just like any other Actor. See GuiPose.getActor.
 * 
 * If you wish to use the gui pacakage without using Actors and Layers etc, then use RootContainer
 * instead of GuiPose. However, you would then need to deal with how RootContainer is drawn on the
 * screen, and how it receives keyboard and mouse events.
 */
public class GuiPose extends RootContainer implements Pose, InputListener
{
    private Actor actor;

    /**
     * Used to implement Pose's changedSinceLastUsed method. When a component is invalidated, it
     * bubbles up to this root component, and sets changed to true. If appearance is doing effects
     * on this Pose (such as a rotation), then it will clear its cached image, and start again.
     */
    private boolean changed;

    private Component mouseOwner;

    private boolean invalid = true;

    public boolean modal = false;

    public boolean draggable = false;

    private boolean dragging;

    private int dragStartX;

    private int dragStartY;

    public GuiPose()
    {
        super();

        Itchy.getGame().addMouseListener(this);
        Itchy.getGame().addKeyListener(this);
    }

    @Override
    public Surface getSurface()
    {
        this.ensureLayedOut();
        if (this.surface == null) {
            this.surface = new Surface(this.width, this.height, true);
        }
        if (this.invalid) {
            this.surface.fill(new RGBA(0, 0, 0, 0));
            GraphicsContext gc = new GraphicsContext(this.surface);
            this.render(gc);

        }
        this.invalid = false;
        return this.surface;
    }

    @Override
    public void setPosition( int x, int y, int width, int height )
    {
        if ((this.surface == null) || (this.surface.getWidth() != width) ||
            (this.surface.getHeight() != height)) {
            super.setPosition(x, y, width, height);

            if (this.surface != null) {
                this.surface.free();

                this.surface = null;
            }
            this.invalidate();
        }

    }

    @Override
    public void invalidate()
    {
        this.invalid = true;
        this.changed = true;
    }

    /**
     * Returns the Actor associated with this GuiPose. Note you should NOT create an Actor manually,
     * always call getActor instead.
     * 
     * @return The Actor associated with this GuiPose.
     */
    public Actor getActor()
    {
        if (this.actor == null) {
            this.actor = new Actor(this);
        }
        return this.actor;
    }

    @Override
    public int getOffsetX()
    {
        return 0;
    }

    @Override
    public int getOffsetY()
    {
        return 0;
    }

    @Override
    public double getDirection()
    {
        return 0;
    }

    @Override
    public boolean onMouseDown( MouseButtonEvent event )
    {
        if (this.actor.getLayer() == null) {
            return false;
        }

        // Calculate the position of this gui on the screen.
        Rect offsetPos = this.actor.getLayer().getAbsolutePosition();
        offsetPos.x += this.actor.getX();
        offsetPos.y += this.actor.getY();

        event.x -= offsetPos.x;
        event.y -= offsetPos.y;
        try {

            return this.testMouseDown(event);

        } finally {
            event.x += offsetPos.x;
            event.y += offsetPos.y;
        }
    }

    @Override
    public boolean onMouseUp( MouseButtonEvent event )
    {
        if (this.actor.getLayer() == null) {
            return false;
        }

        if (this.mouseOwner != null) {

            // Calculate the position of this gui on the screen, and the
            // component within the gui.
            Rect offsetPos = this.actor.getLayer().getAbsolutePosition();
            offsetPos.x += this.actor.getX();
            offsetPos.y += this.actor.getY();

            Rect position = this.mouseOwner.getAbsolutePosition();
            offsetPos.x += position.x;
            offsetPos.y += position.y;

            event.x -= offsetPos.x;
            event.y -= offsetPos.y;
            try {

                this.mouseOwner.mouseUp(event);

            } finally {
                event.x += offsetPos.x;
                event.y += offsetPos.y;
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean onMouseMove( MouseMotionEvent mbe )
    {
        if (this.actor.getLayer() == null) {
            return false;
        }

        if (this.mouseOwner != null) {

            // Calculate the position of this gui on the screen, and the
            // component within the gui.
            Rect offsetPos = this.actor.getLayer().getAbsolutePosition();
            offsetPos.x += this.actor.getX();
            offsetPos.y += this.actor.getY();

            Rect position = this.mouseOwner.getAbsolutePosition();
            offsetPos.x += position.x;
            offsetPos.y += position.y;

            mbe.x -= offsetPos.x;
            mbe.y -= offsetPos.y;
            try {

                this.mouseOwner.mouseMove(mbe);

            } finally {
                mbe.x += offsetPos.x;
                mbe.y += offsetPos.y;
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean onKeyDown( KeyboardEvent ke )
    {
        if (this.actor.getLayer() == null) {
            return false;
        }
        return this.keyDown(ke);

    }

    @Override
    public boolean onKeyUp( KeyboardEvent ke )
    {
        return false;
    }

    @Override
    public void captureMouse( Component component )
    {
        assert (this.mouseOwner == null);

        this.mouseOwner = component;
        Itchy.getGame().captureMouse(this);
    }

    @Override
    public void releaseMouse( Component component )
    {
        assert (this.mouseOwner == component);

        this.mouseOwner = null;
        Itchy.getGame().releaseMouse(this);
    }

    @Override
    public boolean changedSinceLastUsed()
    {
        return this.changed;
    }

    @Override
    public void used()
    {
        this.changed = false;
    }

    private Component localFocus;
    
    public void show()
    {
        Itchy.getGame().showWindow(this);
        if ((localFocus != null) && (localFocus.getRoot() == this)) {
            localFocus.focus();
        } else {
            this.focus();
        }
    }

    public void hide()
    {
        Itchy.getGame().hideWindow(this);
    }

    @Override
    public void setFocus( Component component )
    {
        this.localFocus = component;
        super.setFocus(component);
    }

    public void destroy()
    {
        this.hide();
        this.getActor().kill();
    }

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

            this.getActor().moveBy(dx, dy);
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

}
