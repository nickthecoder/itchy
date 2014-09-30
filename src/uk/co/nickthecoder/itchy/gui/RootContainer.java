/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.Keys;
import uk.co.nickthecoder.jame.event.ModifierKey;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

public class RootContainer extends PlainContainer
{
    protected Stylesheet stylesheet;

    protected static AbstractComponent focus;

    protected GuiView view;

    public boolean modal = false;

    private AbstractComponent mouseOwner;

    public boolean draggable = false;

    public RootContainer()
    {
        super();
        this.type = "root";
        this.stylesheet = Itchy.getGame().getStylesheet();
    }

    public void style( AbstractComponent component )
    {
        if (this.stylesheet != null) {
            this.stylesheet.style(component);
        }
    }

    public void setStylesheet( Stylesheet stylesheet )
    {
        this.stylesheet = stylesheet;
        this.reStyle();
    }

    public Stylesheet getStylesheet()
    {
        return this.stylesheet;
    }

    @Override
    public RootContainer getRoot()
    {
        return this;
    }
    
    public GuiView getView()
    {
    	return this.view;
    }

    private boolean dragging;
    private int dragStartX;
    private int dragStartY;

    @Override
    public boolean onMouseDown( MouseButtonEvent event )
    {
        if (this.mouseOwner == null) {
            if (super.onMouseDown(event)) {
                return true;
            }

            if (this.draggable && contains(event)) {
                if ((event.button == 2) || ((event.button == 1) && Itchy.isAltDown())) {
                    captureMouse(this);
                    this.dragging = true;
                    this.dragStartX = event.x;
                    this.dragStartY = event.y;
                    return true;
                }
            }

        } else if (this.mouseOwner != this) {
            int dx = 0;
            int dy = 0;
            try {
                for (Component component = this.mouseOwner; component != this; component = component.getParent()) {
                    dx -= component.getX();
                    dy -= component.getY();
                }
                event.x += dx;
                event.y += dy;
                if (this.mouseOwner.onMouseDown(event)) {
                    return true;
                }

            } finally {
                event.x -= dx;
                event.y -= dy;
            }
        }

        return contains(event);
    }

    @Override
    public boolean onMouseUp( MouseButtonEvent event )
    {
        if (this.dragging) {
            int dx = event.x - this.dragStartX;
            int dy = event.y - this.dragStartY;

            this.setPosition(this.x + dx, this.y + dy, this.width, this.height);
            this.dragging = false;
            this.releaseMouse(this);
            return true;
        }

        if (this.mouseOwner == null) {
            if (super.onMouseUp(event)) {
                return true;
            }

        } else {
            int dx = 0;
            int dy = 0;
            try {
                for (Component component = this.mouseOwner; component != this; component = component.getParent()) {
                    dx -= component.getX();
                    dy -= component.getY();
                }
                event.x += dx;
                event.y += dy;
                if (this.mouseOwner.onMouseUp(event)) {
                    return true;
                }

            } finally {
                event.x -= dx;
                event.y -= dy;
            }
        }

        return contains(event);
    }

    @Override
    public boolean onMouseMove( MouseMotionEvent event )
    {
        if (this.dragging) {
            int dx = event.x - this.dragStartX;
            int dy = event.y - this.dragStartY;

            // Do we adjust the RootContainer's position, or the GuiView's position?
            // Change the view if the root container fits exactly within the view, otherwise change the root container.
            Rect viewRect = this.view.getPosition();
            if ((this.width == viewRect.width) && (this.height == viewRect.height)) {
                viewRect.x += dx;
                viewRect.y += dy;
            } else {
                this.setPosition(this.x + dx, this.y + dy, this.width, this.height);
            }
            return true;
        }

        if (this.mouseOwner == null) {
            if (super.onMouseMove(event)) {
                return true;
            }

        } else {
            int dx = 0;
            int dy = 0;
            try {
                for (Component component = this.mouseOwner; component != this; component = component.getParent()) {
                    dx -= component.getX();
                    dy -= component.getY();
                }
                event.x += dx;
                event.y += dy;
                if (this.mouseOwner.onMouseMove(event)) {
                    return true;
                }

            } finally {
                event.x -= dx;
                event.y -= dy;
            }
        }

        return contains(event);
    }

    public boolean keyDown( KeyboardEvent ke )
    {
        if (ke.symbol == Keys.TAB) {

            if (ke.modifier(ModifierKey.SHIFT)) {

                if (RootContainer.focus == null) {
                    this.previousFocus(null, this);
                } else {
                    RootContainer.focus.parent.previousFocus(RootContainer.focus, RootContainer.focus);
                }

            } else {

                if (RootContainer.focus == null) {
                    this.nextFocus(null, this);
                } else {
                    RootContainer.focus.parent.nextFocus(RootContainer.focus, RootContainer.focus);
                }

            }
            return true;
        }

        return false;
    }

    @Override
    public void focus()
    {
        this.nextFocus(null, this);
    }

    public void setFocus( AbstractComponent component )
    {
        Itchy.getGame().setFocus(component);

        if (RootContainer.focus == component) {
            return;
        }
        if (RootContainer.focus != null) {
            RootContainer.focus.hasFocus = false;
            RootContainer.focus.removeStyle("focus");
            PlainContainer oldRoot = RootContainer.focus.getRoot();
            if (oldRoot != null) {
                oldRoot.removeStyle("focus");
            }
            RootContainer.focus.onFocus(false);
        }
        RootContainer.focus = component;

        if (RootContainer.focus != null) {
            RootContainer.focus.hasFocus = true;
            RootContainer.focus.addStyle("focus");
            this.addStyle("focus");
            RootContainer.focus.onFocus(true);

            // Ensure that the newly focused component is visible, scrollable will scroll,
            // and notebooks will select the appropriate tab.
            Container parent = component.getParent();
            while (parent != null) {
                parent.ensureVisible(component);
                parent = parent.getParent();
            }
        }

    }

    @Override
    public void invalidate()
    {
        if (this.view != null) {
            this.view.invalidate();
        }
    }

    public void show()
    {
        this.view = Itchy.getGame().show(this);
        center();
        this.focus();
    }

    public void showNow()
    {
        show();
        Itchy.render();
    }

    public void hide()
    {
        Itchy.getGame().hide(this.view);
    }

    public void center()
    {
        int width = this.getRequiredWidth();
        int height = this.getRequiredHeight();
        Rect position = this.view.getPosition();
        setPosition((position.width - width) / 2, (position.height - height) / 2, width, height);
    }
    
    public void resizeView()
    {
    	Rect viewRect = this.view.getPosition();
    	this.view.setPosition( new Rect( viewRect.x, viewRect.y, this.getWidth(), this.getHeight() ) );
    }

    public void captureMouse( AbstractComponent component )
    {
        if (this.view != null) {
            Itchy.getGame().captureMouse(this.view);
        }
        this.mouseOwner = component;
    }

    public void releaseMouse( Component component )
    {
        if (this.view != null) {
            Itchy.getGame().releaseMouse(this.view);
        }
        this.mouseOwner = null;
    }

}
