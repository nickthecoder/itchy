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
    public boolean mouseDown( MouseButtonEvent mbe )
    {
        if (mbe.button == 1) {
            this.dragging = this.acceptDrag(mbe);

            if (this.dragging) {
                this.startX = mbe.x;
                this.startY = mbe.y;
                this.getRoot().captureMouse(this);
                return true;
            }
        }
        return super.mouseDown(mbe);

    }

    @Override
    public void mouseMove( MouseMotionEvent mme )
    {
        if (this.dragging) {
            this.drag(mme, mme.x - this.startX, mme.y - this.startY);
        }
    }

    @Override
    public void mouseUp( MouseButtonEvent mbe )
    {
        if (this.dragging) {
            this.getRoot().releaseMouse(this);
            this.drag(mbe, mbe.x - this.startX, mbe.y - this.startY);
            this.endDrag(mbe, mbe.x - this.startX, mbe.y - this.startY);
        } else {
            super.mouseUp(mbe);
        }
    }

    public abstract boolean acceptDrag( MouseButtonEvent e );

    public abstract void drag( MouseEvent mme, int dx, int dy );

    public void endDrag( MouseButtonEvent e, int dx, int dy )
    {
    }

    @Override
    public void onClick( MouseButtonEvent e )
    {
    }

}
