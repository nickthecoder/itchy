package uk.co.nickthecoder.itchy;

import java.util.prefs.Preferences;

import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;
import uk.co.nickthecoder.jame.event.QuitEvent;
import uk.co.nickthecoder.jame.event.ResizeEvent;
import uk.co.nickthecoder.jame.event.WindowEvent;

/**
 * Implements all of Director's methods in the shortest way possible,
 * usually by doing nothing at all.
 */
public class NullDirector implements Director
{

    @Override
    public void onMouseDown(MouseButtonEvent event)
    {        
    }

    @Override
    public void onMouseUp(MouseButtonEvent event)
    {
    }

    @Override
    public void onMouseMove(MouseMotionEvent event)
    {
    }

    @Override
    public void onKeyDown(KeyboardEvent ke)
    {
    }

    @Override
    public void onKeyUp(KeyboardEvent ke)
    {
    }

    @Override
    public void onQuit(QuitEvent e)
    {
    }

    @Override
    public void onMessage(String message)
    {
    }

    @Override
    public boolean onWindowEvent(WindowEvent we)
    {
        return false;
    }

    @Override
    public void attach(Game game)
    {
    }

    @Override
    public void onStarted()
    {        
    }

    @Override
    public void onActivate()
    {
    }

    @Override
    public void onDeactivate()
    {        
    }

    @Override
    public void onResize(ResizeEvent e)
    {
    }

    @Override
    public boolean startScene(String sceneName)
    {
        return false;
    }

    @Override
    public void tick()
    {        
    }

    @Override
    public Preferences getPreferenceNode()
    {
        return Preferences.userNodeForPackage(this.getClass());
    }

}
