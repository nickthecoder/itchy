package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;

public interface InputInterface
{
    public boolean pressed();
    
    public boolean matches(KeyboardEvent ke);
    
    public boolean matches(MouseButtonEvent mbe);


}
