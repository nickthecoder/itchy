/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.extras;

import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.MouseListener;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

/**
 * Detects when the mouse is clicked on this behaviour's Actor, and sends a message to the Game.
 * <p>
 * The abstract onClick method is called after the mouseUp event.  
 * <p>
 * The button will fire three events, "down", "up" and "click". "down" is fired when the mouse is clicked
 * within the actor. The "up" is fired when the mouse moves outside of the actor while the button is still down.
 * Finally, "up" and "click" are fired when the mouse button is released within the button.
 * <p>
 * For the button to work, the layer that the actor is drawn on must have had its mouse listener
 * enabled. i.e. call {@link uk.co.nickthecoder.itchy.ActorsLayer#enableMouseListener} when
 * initialising your Game object.
 */
public abstract class Button extends Behaviour implements MouseListener
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
    public boolean onMouseDown( MouseButtonEvent event )
    {
        if (this.getActor().hitting(event.x, event.y)) {
            onDown();
            this.down = true;
            this.inside = true;
            getActor().getLayer().captureMouse(this);
            return true;
        }
        return false;
    }

    @Override
    public boolean onMouseUp( MouseButtonEvent event )
    {
        if (this.down) {
            getActor().getLayer().releaseMouse(this);

            if (this.inside) {
                onUp();
            }
            if (this.getActor().hitting(event.x, event.y)) {
                onClick();
            }
            this.down = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean onMouseMove( MouseMotionEvent event )
    {
        boolean nowInside = this.getActor().hitting(event.x, event.y);

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

            return true;
        }
        
        return false;
    }

    /**
     * Fires the "down" event
     */
    protected void onDown()
    {
        this.event("down");
    }
    
    /**
     * Fires the "up" event
     */
    protected void onUp()
    {
        this.event("up");
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
