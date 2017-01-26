/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.itchy.property.PropertySubject;
import uk.co.nickthecoder.jame.Rect;

/**
 * A View is is a rectangular area responsible for drawing part of the screen.
 * <p>
 * The most common View is {@link StageView}, which is how all of the {@link Actor}s are drawn. The second most common
 * View is {@link RGBAView}, which draws a solid colour, and typically has a Z-Order of zero, placing it at the bottom
 * of the set of {@link Layer}s in the {@link Layout}.
 */
public interface View extends PropertySubject<View>
{
    /**
     * Return a GraphicsContext, clipped to this view's position, and taking care of any scrolling.
     * 
     * @priority 3
     */
    public GraphicsContext adjustGraphicsContext(GraphicsContext gc);

    /**
     * Return a GraphicsContext, clipped to this view's position, and taking care of any scrolling.
     * 
     * @priority 3
     */
    public NewGraphicsContext adjustGraphicsContext(NewGraphicsContext gc);

    /**
     * Draws the view. This is how layers are drawn to the display, but can also be used to draw
     * to non-display surfaces.
     * For example, if you want to take a snapshot of the game, you could render to a surface which you
     * created, and then save that surface to disk.
     * 
     */
    public void render(GraphicsContext gc);

    public void render(NewGraphicsContext gc);

    /**
     * @return The position of the view relative to the parent view
     */
    public Rect getPosition();

    /**
     * A simple setter.
     * 
     * @param rect
     *            The area of the screen that this View covers. (0,0) is at the top left, with the Y-axis pointing down.
     */
    public void setPosition(Rect rect);

    /**
     * @priority 3
     */
    public Rect getRelativeRect();

    /**
     * Does this layer contain the point (which is in device coordinates). This is useful to determine if a mouse click
     * is within a given view.
     * 
     * @param x
     *            The x coordinate from the left of the screen.
     * @param y
     *            The y coordinate from the top of the screen, with the Y-axis pointing down.
     */
    public boolean contains(int x, int y);

    /**
     * Converts a screen coordinate to a world coordinate, taking into account the position of the view on the screen,
     * and
     * also any scroll offset (if the View is a {@link ScrollableView}.
     * 
     * @param screenX
     * @return
     */
    public double getWorldX(int screenX);

    /**
     * Converts a screen coordinate to a world coordinate, taking into account the position of the view on the screen,
     * and
     * also any scroll offset (if the View is a {@link ScrollableView}.
     * The direction of the Y axis is flipped (screen coorindates point down, whereas world coordinates point up).
     * 
     * @param screenY
     * @return
     */
    public double getWorldY(int screenY);

    /**
     * Resets this view. For ScrollableViews, this means scrolling them back to (0,0).
     */
    public void reset();

    /**
     * @return True iff the view should be rendered.
     */
    public boolean isVisible();

}
