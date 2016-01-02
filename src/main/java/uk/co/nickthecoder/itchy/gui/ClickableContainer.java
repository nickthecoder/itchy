/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.Keys;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

public abstract class ClickableContainer extends PlainContainer
{
    protected long clickTimeMillis;

    private boolean dragging = false;

    public ClickableContainer()
    {
        super();
        this.type = "clickable";
    }

    @Override
    public void onKeyDown( KeyboardEvent ke )
    {
        if (this.hasFocus && ((ke.symbol == Keys.SPACE) || (ke.symbol == Keys.RETURN))) {
            this.onClick(null);
            ke.stopPropagation();
        }
        super.onKeyDown(ke);
    }

    @Override
    public boolean onMouseDown( MouseButtonEvent event )
    {
        if (super.onMouseDown(event)) {
            return true;
        }
        if (event.button == 1) {
            this.getRoot().captureMouse(this);

            this.addStyle("down");
            this.dragging = true;

            if (this.focusable) {
                this.focus();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onMouseUp( MouseButtonEvent event )
    {
        if (this.dragging) {
            this.dragging = false;
            this.getRoot().releaseMouse(this);

            this.removeStyle("down");
            if (this.contains(event)) {
                long now = System.currentTimeMillis();
                if (now - this.clickTimeMillis < 500) {
                    this.onDoubleClick(event);
                } else {
                    this.onClick(event);
                }
                this.clickTimeMillis = now;
            }

            return true;
        }
        return false;
    }

    @Override
    public boolean onMouseMove( MouseMotionEvent event )
    {
        if (this.dragging) {

            if (this.contains(event)) {
                this.addStyle("down");
            } else {
                this.removeStyle("down");
            }
            return true;
        }
        return false;
    }

    /**
     * 
     * @param mbe
     *        The mouse button event which caused this onClick, or null if the onClick was called via other means, such as a keyboard
     *        shortcut.
     */
    public abstract void onClick( MouseButtonEvent mbe );

    public void onDoubleClick( MouseButtonEvent mbe )
    {
        this.onClick(mbe);
    }

}
