/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

//TODO Is this right???
public interface MouseListenerView extends View, MouseListener
{
    /**
     * Actors who's Roles are MouseListeners can receive mouse events. The events that they
     * will receive have been adjusted, so that the x and y coordinates are their world coordinates.
     * 
     * @param game
     */
    public void enableMouseListener( Game game );

    /**
     * No actors within the layer will receive mouse events.
     * 
     * @param game
     */
    public void disableMouseListener( Game game );

    /**
     * One of the actors within the layer can capture the mouse, so that no other listeners will
     * hear the mouse events, until {@link #releaseMouse(MouseListener)} is called. This is useful
     * when a mouse drag is initiated.
     * 
     * @param owner
     *        The only object to receive the mouse events. Note, that this is usually an Actor, and
     *        the coordinates of the mouse event will be adjusted so that they are in world
     *        coordinates, i.e. the Actor's x and y and the event's x and y are comparable.
     */
    public void captureMouse( ViewMouseListener owner );

    /**
     * Called after {@link #captureMouse(MouseListener)} to allow other to hear mouse events once
     * again.
     * 
     * @param owner
     *        The owner that was earlier passed to captureMouse. The mouse might not be released if
     *        the owner is not the one that currently captured the mouse (implementation dependent).
     * 
     */
    public void releaseMouse( ViewMouseListener owner );

}
