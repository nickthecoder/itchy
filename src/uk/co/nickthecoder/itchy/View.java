/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;

/**
 * Think of Layers as transparent pieces of plastic, which can be stacked stacked together to form
 * the image on the screen.
 * <p>
 * Layers are stacked together by using a {@link CompoundView} (Game's {@link Game#gameViews}
 * attribute). A layer is rendered (drawn), by placing {@link Actor} objects onto a
 * {@link ActorLayer}. A simple game will use just one {@link ScrollableView}, in which case, the
 * creating of the layer is done automatically by Game. However, if you want to use more than one
 * layer, then override Game's {@link createLayers()} method.
 */
public interface View
{
    /**
     * Layers can be stacked in a hierarchical manner.
     * 
     * @return The parent layer, or null, if this is the root layer, and has no parent.
     */
    public ParentView<?> getParent();

    /**
     * Called by ParentLayer's when added or removed from a layer.
     * 
     * @param parent
     *        The new parent, or null if this layer is being removed from a ParentLayer.
     */
    public void setParent( ParentView<?> parent );

    /**
     * Draws this layer to the given Surface. This is how layers are drawn to the display, but can
     * also be used to draw to non-display surfaces. For example, if you want to take a snapshot of
     * the game, you could render to a surface which you created, and then save that surface to
     * disk.
     * 
     * @param within
     *        The clip rectangle of the surface on which to draw. For the top-level
     * @param destSurface
     *        The surface to draw on
     */
    public void render( Surface destSurface, Rect clip, int offsetX, int offsetY );

    /**
     * @return The position of this View in display device coordinates, i.e. relative to the top
     *         left of the display.
     */
    public Rect getAbsolutePosition();

    public Rect getRelativeRect();
    
    /**
     * Does this layer contain the point (which is in device coordinates). This is useful to
     * determine if a mouse click if within a given layer.
     * 
     * @param x
     *        The x coordinate from the left of the display.
     * @param y
     *        The y coordinate fomr the top of the display.
     */
    public boolean contains( int x, int y );

    /**
     * Resets this layer, and all children layers if it has them. For ScrollableViews, this means
     * scrolling them back to (0,0).
     */
    public void reset();

    /**
     * @return True iff the view should be rendered.
     */
    public boolean isVisible();

}
