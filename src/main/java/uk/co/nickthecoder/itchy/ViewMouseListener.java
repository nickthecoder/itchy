/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

public interface ViewMouseListener
{
    public boolean onMouseDown( MouseListenerView view, MouseButtonEvent event );

    public boolean onMouseUp( MouseListenerView view, MouseButtonEvent event );

    public boolean onMouseMove( MouseListenerView view, MouseMotionEvent event );

    /**
     * We can't tell if scripted objects are mouse listeners until they have been created. All regular java classes are expected to return
     * true.
     * 
     * @return True iff this really does listen for mouse events.
     */
    public boolean isMouseListener();
}
