/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.role;

import uk.co.nickthecoder.itchy.AbstractRole;
import uk.co.nickthecoder.itchy.MouseListenerView;
import uk.co.nickthecoder.itchy.ViewMouseListener;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

/**
 * Detects when the mouse is clicked on this role's Actor, and sends a message to the Game.
 * <p>
 * The abstract onClick method is called after the mouseUp event.
 * <p>
 * The button will fire three events, "mouseDown", "mouseUp" and "click". "mouseDown" is fired when the mouse is pressed
 * within the actor. The "mouseUp" is fired when the mouse moves outside of the actor while the button is still down.
 * "mouseUp" and "click" are fired when the mouse button is released within the button. In addition, events "hoverOver"
 * and "hoverOut" are also fired when the mouse moves over the button without being clicked.
 * <p>
 * For the button to work, the layer that the actor is drawn on must have had its mouse listener enabled. i.e. call
 * {@link uk.co.nickthecoder.itchy.StageView#enableMouseListener(uk.co.nickthecoder.itchy.Game)} when initialising your
 * Game object.
 * 
 * This is called ButtonRole, rather than just Button to avoid confusion with the Button class in the gui package.
 */
public abstract class ButtonRole extends AbstractRole implements ViewMouseListener
{

    private boolean down = false;
    private boolean inside;

    /**
     * Does nothing
     */
    @Override
    public void tick()
    {
    }

    @Override
    public void onAttach()
    {
        super.onAttach();
    }

    @Override
    public void onBirth()
    {
        super.onBirth();
    }

    @Override
    public void onMouseDown(MouseListenerView view, MouseButtonEvent event)
    {
        if (getActor().hitting(event.x, event.y)) {
            onDown();
            this.down = true;
            this.inside = true;
            view.captureMouse(this);
            event.stopPropagation();
        }
    }

    @Override
    public void onMouseUp(MouseListenerView view, MouseButtonEvent event)
    {
        if (this.down) {
            view.releaseMouse(this);

            if (this.inside) {
                onUp();
            }
            if (getActor().hitting(event.x, event.y)) {
                onClick();
            }
            this.down = false;
            event.stopPropagation();
        }
    }

    @Override
    public void onMouseMove(MouseListenerView view, MouseMotionEvent event)
    {
        boolean nowInside = getActor().hitting(event.x, event.y);

        if (nowInside != this.inside) {
            this.inside = nowInside;

            if (this.down) {
                if (nowInside) {
                    this.onDown();
                } else {
                    this.onUp();
                }
            } else {
                if (nowInside) {
                    this.onHoverOver();
                } else {
                    this.onHoverOut();
                }
            }

            event.stopPropagation();
        }
    }

    /**
     * Fires the "mouseDown" event
     */
    protected void onDown()
    {
        this.event("mouseDown");
    }

    /**
     * Fires the "up" event
     */
    protected void onUp()
    {
        this.event("mouseUp");
    }

    @Override
    public boolean isMouseListener()
    {
        return true;
    }

    /**
     * Fires the "click" event. Override this method, to perform your button's action.
     */
    protected void onClick()
    {
        this.event("click");
    }

    protected void onHoverOver()
    {
        this.event("hoverOver");
    }

    protected void onHoverOut()
    {
        this.event("hoverOut");
    }

}
