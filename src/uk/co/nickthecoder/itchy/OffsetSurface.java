/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.itchy.util.Property;
import uk.co.nickthecoder.jame.Surface;

public interface OffsetSurface
{
    /**
     * Along with getOffsetY(), defines the key coordinate of the pose. When a Pose is drawn on
     * screen at x,y, the top left of the Pose will be drawn at ( x-offsetX, y-offsetY )
     */
    @Property(label = "Offset X")
    public int getOffsetX();

    @Property(label = "Offset Y")
    public int getOffsetY();

    public Surface getSurface();

}
