/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.List;

import uk.co.nickthecoder.jame.Rect;

/**
 * A Layer, which can be a parent to other layers.
 */
public interface ParentView<V extends View> extends View
{
    public void add( V view );

    public void add( int index, V view );

    public void remove( V view );

    public List<V> getChildren();
    
    /**
     * Adjusts the given rectangle, so that 'rect' in the child layer's coordinates now becomes
     * valid in this layer's coordinates.
     * @param rect
     */
    public void adjustChildRect( Rect rect );

}
