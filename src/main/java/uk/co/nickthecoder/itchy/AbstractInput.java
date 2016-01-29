package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.ModifierKey;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;

public class AbstractInput implements InputInterface
{
    public boolean ctrlModifier;
    public boolean shiftModifier;
    public boolean metaModifier;
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
        if (this.metaModifier) {
            buffer.append("meta+");
        }
        if (this.altModifier) {
            buffer.append("alt+");
        }

        return buffer.toString();
    }

    public boolean matches(KeyboardEvent ke)
    {
        if (this.ctrlModifier != (ke.modifier(ModifierKey.LCTRL) || ke.modifier(ModifierKey.RCTRL))) {
            return false;
        }
        if (this.shiftModifier != (ke.modifier(ModifierKey.LSHIFT) || ke.modifier(ModifierKey.RSHIFT))) {
            return false;
        }
        if (this.metaModifier != (ke.modifier(ModifierKey.LMETA) || ke.modifier(ModifierKey.RMETA))) {
            return false;
        }
        if (this.altModifier != (ke.modifier(ModifierKey.LALT) || ke.modifier(ModifierKey.RALT))) {
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
        if (this.metaModifier && !Itchy.isMetaDown()) {
            return false;
        }

        return true;
    }
}
