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
 * 
 * Note, the message is send when the mouse button is pressed down. This is in contrast to how
 * buttons such as {@link uk.co.nickthecoder.itchy.gui.Button} in the gui package, where the message
 * is sent when the mouse button is released.
 * 
 * For the button to work, the layer that the actor is drawn on must have had its mouse listener
 * enabled. i.e. call {@link uk.co.nickthecoder.itchy.ActorsLayer#enableMouseListener} when
 * initialising your Game object.
 */
public abstract class Button extends Behaviour implements MouseListener
{

    /**
     * Does nothing
     */
    @Override
    public void tick()
    {
    }

    /**
     * Tests if the mouse is inside the actor's bounding box, and if it is, it sends a message to
     * the {@link uk.co.nickthecoder.itchy.Game}. Called by the Itchy framework.
     * 
     * @return true if the message was sent, otherwise false.
     */
    @Override
    public boolean onMouseDown( MouseButtonEvent event )
    {
        if (this.getActor().contains(event.x, event.y)) {
            this.event("onClick");
            onClick();
            return true;
        }
        return false;
    }

    /**
     * Does nothing.
     * 
     * @return false
     */
    @Override
    public boolean onMouseUp( MouseButtonEvent event )
    {
        return false;
    }

    /**
     * Does nothing.
     * 
     * @return false
     */
    @Override
    public boolean onMouseMove( MouseMotionEvent event )
    {
        return false;
    }

    protected abstract void onClick();
}
