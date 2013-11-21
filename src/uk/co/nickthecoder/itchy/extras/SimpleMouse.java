/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.extras;

import uk.co.nickthecoder.itchy.Mouse;
import uk.co.nickthecoder.itchy.MousePointer;
import uk.co.nickthecoder.jame.Video;

/**
 * Responsible for how the mouse is displayed. There are three basic states : No mouse, Regular Mouse, Graphical mouse.
 */
public class SimpleMouse implements Mouse
{
    private MousePointer mousePointer;

    /**
     * Keeps a record of if the current scene should show the mouse pointer. This is needed so that the mouse can be shown/hidden when one
     * Game ends and the previous one is re-activated.
     */
    private boolean regularMousePointer = true;

    @Override
    public void setMousePointer( MousePointer mousePointer )
    {
        showRegularMousePointer(false);
        this.mousePointer = mousePointer;
    }

    public MousePointer getMousePointer()
    {
        return this.mousePointer;
    }

    @Override
    public boolean isRegularMousePointer()
    {
        return this.regularMousePointer;
    }

    public void showRegularMousePointer( boolean value )
    {
        this.regularMousePointer = value;
        Video.showMousePointer(value);
    }
    
    public void onActivate()
    {
        Video.showMousePointer(this.regularMousePointer);
    }
}
