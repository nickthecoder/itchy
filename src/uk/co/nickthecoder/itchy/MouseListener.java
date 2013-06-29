package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

public interface MouseListener
{
    public boolean onMouseDown( MouseButtonEvent mbe );

    public boolean onMouseUp( MouseButtonEvent mbe );

    public boolean onMouseMove( MouseMotionEvent mme );

}
