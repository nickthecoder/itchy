/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

public abstract class DragableContainer extends ClickableContainer
{
    protected int startX;

    protected int startY;

    protected boolean dragging;

    @Override
    public void onMouseDown( MouseButtonEvent event )
    {
        if (event.button == 1) {
            this.dragging = this.acceptDrag(event);

            if (this.dragging) {
                this.startX = event.x;
                this.startY = event.y;
                this.getRoot().captureMouse(this);
                event.stopPropagation();
            }
        }
        super.onMouseDown(event);

    }

    @Override
    public void onMouseMove( MouseMotionEvent event )
    {
        if (this.dragging) {
            this.drag(event, event.x - this.startX, event.y - this.startY);
            event.stopPropagation();
        } else {
            super.onMouseMove(event);
        }
    }

    @Override
    public void onMouseUp( MouseButtonEvent event )
    {
        if (this.dragging) {
            this.dragging = false;
            this.getRoot().releaseMouse(this);
            this.drag(event, event.x - this.startX, event.y - this.startY);
            this.endDrag(event, event.x - this.startX, event.y - this.startY);
            event.stopPropagation();
        } else {
            super.onMouseUp(event);
        }
    }

    public abstract boolean acceptDrag( MouseButtonEvent event );

    public abstract void drag( MouseEvent event, int dx, int dy );

    public void endDrag( MouseButtonEvent e, int dx, int dy )
    {
    }

    @Override
    public void onClick( MouseButtonEvent event )
    {
    }

}
