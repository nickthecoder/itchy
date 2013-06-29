package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

public abstract class Game implements EventListener
{
    public final Resources resources = new Resources();

    @Override
    public boolean onQuit()
    {
        Itchy.singleton.terminate();
        return true;
    }

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract String getTitle();

    public abstract String getIconFilename();

    @Override
    public boolean onKeyDown( KeyboardEvent ke )
    {
        return false;
    }

    @Override
    public boolean onKeyUp( KeyboardEvent ke )
    {
        return false;
    }

    @Override
    public boolean onMouseDown( MouseButtonEvent mbe )
    {
        return false;
    }

    @Override
    public boolean onMouseUp( MouseButtonEvent mbe )
    {
        return false;
    }

    @Override
    public boolean onMouseMove( MouseMotionEvent mbe )
    {
        return false;
    }

    public abstract void tick();

}

