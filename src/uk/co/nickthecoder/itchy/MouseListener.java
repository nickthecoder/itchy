/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

public interface MouseListener
{
    public boolean onMouseDown( MouseButtonEvent mbe );

    public boolean onMouseUp( MouseButtonEvent mbe );

    public boolean onMouseMove( MouseMotionEvent mme );

}
