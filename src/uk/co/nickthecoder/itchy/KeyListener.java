package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.jame.event.KeyboardEvent;

public interface KeyListener
{
    public boolean onKeyDown( KeyboardEvent ke );

    public boolean onKeyUp( KeyboardEvent ke );

}
