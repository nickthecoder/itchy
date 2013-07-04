package uk.co.nickthecoder.itchy;

import java.util.prefs.Preferences;

import uk.co.nickthecoder.itchy.util.AutoFlushPreferences;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

public abstract class Game extends Task implements EventListener
{
    public final Resources resources = new Resources();

    private boolean ticking = false;

    private AutoFlushPreferences preferences;
    
    public AutoFlushPreferences getPreferences()
    {
        if ( preferences == null ) {
            preferences = new AutoFlushPreferences( Preferences.userNodeForPackage(this.getClass()) );
        }
        return preferences;
    }

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

    /**
     * Called when a button is pressed. Most games don't use onKeyDown or onKeyUp during game play,
     * instead, each Actor uses : Itchy.singleton.isKeyDown( ... ). onKeyDown and onKeyUp are useful
     * for typing.
     */
    @Override
    public boolean onKeyDown( KeyboardEvent ke )
    {
        return false;
    }

    /**
     * Called when a button is pressed. Most games don't use onKeyDown or onKeyUp during game play,
     * instead, each Actor uses : Itchy.singleton.isKeyDown( ... ). onKeyDown and onKeyUp are useful
     * for typing.
     */
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

    /**
     * Override this method to run code once per frame.
     */
    public void tick()
    {
    }

    /**
     * Part of the Task interface, and simply calls 'tick'. You subclass should override tick, and
     * do nothing with 'run'.
     */
    @Override
    public void run()
    {
        if (this.ticking) {
            return;
        }

        try {
            this.ticking = true;
            this.tick();
        } finally {
            this.ticking = false;
        }
    }

}
