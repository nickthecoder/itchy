/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;

public interface Layer
{

    public String getName();

    /**
     * It it the norm in mathematics, for the Y axis to point upwards, but display devices have the
     * Y axis pointing downwards. This boolean lets you choose which of these two conventions you
     * want to use for the world coordinates (i.e. values of Actor.y). Set in the constructor.
     */
    public boolean getYAxisPointsDown();

    public WorldRectangle getWorldRectangle();

    public void adjustPosition( Rect rect );

    public Layer getParent();

    public void setParent( Layer parent );
    
    public boolean isVisible();

    public void render( Rect within, Surface destSurface );

    public void clear();

    public boolean contains( int x, int y );

    public void destroy();

    public void reset();
}
