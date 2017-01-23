package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.util.ModifierKeyFilter;

/* TODO Use new filters */
public class AbstractInput implements InputInterface
{
    public boolean ctrlModifier;
    public boolean shiftModifier;
    public boolean altModifier;

    
    /**
     * If true, then only match when the key/button is pressed, not continuously while it is held down.
     */
    public boolean click;

    protected boolean previouslyUp = true;


    @Override
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        if (this.click) {
            buffer.append("click+");
        }
        if (this.shiftModifier) {
            buffer.append("shift+");
        }
        if (this.ctrlModifier) {
            buffer.append("ctrl+");
        }
        if (this.altModifier) {
            buffer.append("alt+");
        }

        return buffer.toString();
    }

    public boolean matches(KeyboardEvent ke)
    {
        if (this.ctrlModifier != ModifierKeyFilter.CTRL.accept(ke)) {
            return false;
        }
        if (this.shiftModifier != ModifierKeyFilter.SHIFT.accept(ke)) {
            return false;
        }
        if (this.altModifier != ModifierKeyFilter.ALT.accept(ke)) {
            return false;
        }
        return true;
    }

    public boolean matches(MouseButtonEvent mbe)
    {
        return checkModifiers();
    }

    public boolean pressed()
    {
        return checkModifiers();
    }
    
    protected boolean checkModifiers()
    {
        if (this.ctrlModifier && !Itchy.isCtrlDown()) {
            return false;
        }
        if (this.shiftModifier && !Itchy.isShiftDown()) {
            return false;
        }
        if (this.altModifier && !Itchy.isAltDown()) {
            return false;
        }

        return true;
    }
}
