package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButton;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;

public class MouseInput extends AbstractInput
{
    public MouseButton mouseButton;

    public MouseInput()
    {
        
    }
    
    public MouseInput( MouseButton mouseButton )
    {
        this.mouseButton = mouseButton;
    }
    
    @Override
    public boolean pressed()
    {
        if (Itchy.isMouseButtonDown(this.mouseButton.value)) {
            if ((click && previouslyUp) || (!click)) {
                previouslyUp = false;
                return super.pressed();
            }
        } else {
            previouslyUp = true;
        }
        return false;
    }

    @Override
    public boolean matches(KeyboardEvent ke)
    {
        return false;
    }

    @Override
    public boolean matches(MouseButtonEvent mbe)
    {
        if ((mbe.button == this.mouseButton.value) && (mbe.state == MouseButtonEvent.STATE_PRESSED)) {
            return super.matches(mbe);
        }
        return false;
    }

    @Override
    public String toString()
    {
        return super.toString() + "M_" + mouseButton.name();
    }
}
