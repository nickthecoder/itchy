/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.itchy.property.PropertySubject;
import uk.co.nickthecoder.jame.Rect;

public interface View extends PropertySubject<View>
{
    /**
     * Return a GraphicsContext, clipped to this view's position, and taking care of any scrolling. 
     */
    public GraphicsContext adjustGraphicsContext( GraphicsContext gc );

    /**
     * Draws the view. This is how layers are drawn to the display, but can also be used to draw
     * to non-display surfaces.
     * For example, if you want to take a snapshot of the game, you could render to a surface which you
     * created, and then save that surface to disk.
     * 
     */
    public void render( GraphicsContext gc );

    /**
     * @return The position of the view relative to the parent view
     */
    public Rect getPosition();

    public void setPosition(Rect rect);

    public Rect getRelativeRect();

    /**
     * Does this layer contain the point (which is in device coordinates). This is useful to determine if a mouse click
     * is within a given view.
     * 
     * @param x
     *            The x coordinate from the left of the display.
     * @param y
     *            The y coordinate from the top of the display.
     */
    public boolean contains(int x, int y);

    public double getWorldX( int screenX );

    public double getWorldY( int screenY );
    
    /**
     * Resets this view, and all children view if it has them. For ScrollableViews, this means scrolling them back to
     * (0,0).
     */
    public void reset();

    /**
     * @return True iff the view should be rendered.
     */
    public boolean isVisible();

}
