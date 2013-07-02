package uk.co.nickthecoder.itchy.gui;

import uk.co.nickthecoder.jame.Keys;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

public abstract class ClickableContainer extends Container
{
    protected long clickTimeMillis;

    public ClickableContainer()
    {
        super();
        this.type = "clickable";
    }

    @Override
    public boolean onKeyDown( KeyboardEvent ke )
    {
        if (this.hasFocus && ((ke.symbol == Keys.SPACE) || (ke.symbol == Keys.RETURN))) {
            this.onClick(null);
            return true;
        }
        return super.onKeyDown(ke);
    }

    @Override
    public boolean mouseDown( MouseButtonEvent mbe )
    {
        if (super.mouseDown(mbe)) {
            return true;
        }
        if (mbe.button == 1) {
            this.getRoot().captureMouse(this);

            this.addStyle("down");

            if (this.focusable) {
                this.focus();
            }
            return true;
        }
        return false;
    }

    @Override
    public void mouseUp( MouseButtonEvent mbe )
    {
        this.getRoot().releaseMouse(this);

        this.removeStyle("down");
        if (this.contains(mbe)) {
            long now = System.currentTimeMillis();
            if (now - this.clickTimeMillis < 500) {
                this.onDoubleClick(mbe);
            } else {
                this.onClick(mbe);
            }
            this.clickTimeMillis = now;
        }
    }

    @Override
    public void mouseMove( MouseMotionEvent mme )
    {
        if (this.contains(mme)) {
            this.addStyle("down");
        } else {
            this.removeStyle("down");
        }
    }

    /**
     * 
     * @param mbe
     *        The mouse button event which caused this onClick, or null if the onClick was called
     *        via other means, such as a keyboard shortcut.
     */
    public abstract void onClick( MouseButtonEvent mbe );

    public void onDoubleClick( MouseButtonEvent mbe )
    {
        this.onClick(mbe);
    }

}
