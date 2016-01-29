package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;

public class MouseInput extends AbstractInput
{
    public static final String[] buttonCodes = new String[]
    { "NONE", "M_LEFT", "M_MIDDLE", "M_RIGHT", "M_WHEELUP", "M_WHEELDOWN" };

    public static final String[] buttonLabels = new String[]
    { "NONE", "Left", "Middle", "Right", "Wheel Up", "Wheel Down" };

    public int button;

    public MouseInput()
    {
        
    }
    
    public MouseInput( int buttonNumber )
    {
        button = buttonNumber;
    }
    
    @Override
    public boolean pressed()
    {
        if (Itchy.isMouseButtonDown(this.button)) {
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
        if ((mbe.button == this.button) && (mbe.state == MouseButtonEvent.STATE_PRESSED)) {
            return super.matches(mbe);
        }
        return false;
    }

    @Override
    public String toString()
    {
        return super.toString() + buttonCodes[button];
    }
}
