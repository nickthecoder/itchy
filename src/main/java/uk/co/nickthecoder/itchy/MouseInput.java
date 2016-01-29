package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;

public class MouseInput extends AbstractInput
{
    public int button;
    
    @Override
    public boolean pressed()
    {
        if (Itchy.isMouseButtonDown(this.button)) {
            if ( (click && previouslyUp) || (!click) ) {
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
        if ((mbe.button == this.button)&& (mbe.state == MouseButtonEvent.STATE_PRESSED)) {
            return super.matches(mbe);
        }
        return false;
    }

}
