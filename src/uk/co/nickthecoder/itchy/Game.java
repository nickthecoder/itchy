package uk.co.nickthecoder.itchy;

import java.util.prefs.Preferences;

import uk.co.nickthecoder.itchy.util.AutoFlushPreferences;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

public abstract class Game extends Task implements EventListener, MessageListener
{
    public final Resources resources = new Resources();

    private boolean ticking = false;

    private AutoFlushPreferences preferences;
    
    protected CompoundLayer layers =
        new CompoundLayer("game", new Rect(0,0,this.getWidth(), this.getHeight()));

    public AutoFlushPreferences getPreferences()
    {
        if (this.preferences == null) {
            this.preferences = new AutoFlushPreferences(Preferences.userNodeForPackage(this
                .getClass()));
        }
        return this.preferences;
    }

    public CompoundLayer getLayers()
    {
        return layers;
    }
    
    public int getWidth()
    {
        return 640;
    }

    public int getHeight()
    {
        return 480;
    }

    public String getTitle()
    {
        return this.getClass().getName();
    }

    public String getIconFilename()
    {
        return "icon.bmp";
    }

    @Override
    public boolean onQuit()
    {
        Itchy.singleton.terminate();
        return true;
    }
    
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

    @Override
    public void onMessage( String message )
    {
    }

    public abstract void init();
    
    public void start()
    {
        Itchy.singleton.startGame(this);
    }

    public void stop()
    {
        Itchy.singleton.endGame();
    }

    /**
     * Part of the Task interface, and simply calls 'tick'. Your subclass should override tick, and
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
