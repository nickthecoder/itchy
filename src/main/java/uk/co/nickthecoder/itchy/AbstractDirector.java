/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.prefs.Preferences;

import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;
import uk.co.nickthecoder.jame.event.QuitEvent;
import uk.co.nickthecoder.jame.event.ResizeEvent;
import uk.co.nickthecoder.jame.event.WindowEvent;

public class AbstractDirector implements Director
{
    protected Game game;
    
    public Game getGame()
    {
        return this.game;
    }

    @Override
    public void attach( Game game )
    {
        this.game = game;
    }

    @Override
    public void onStarted()
    {
    }

    @Override
    public void onStartingScene(String sceneName)
    {    
    }

    @Override
    public void onStartedScene()
    {    
    }
    
    /**
     * The default role is to do nothing more than forward the event to the current scene's {@link SceneDirector}.
     */
    @Override
    public void onMouseDown( MouseButtonEvent event )
    {
        this.game.getSceneDirector().onMouseDown(event);
    }

    /**
     * The default role is to do nothing more than forward the event to the current scene's {@link SceneDirector}.
     */
    @Override
    public void onMouseUp( MouseButtonEvent event )
    {
        this.game.getSceneDirector().onMouseUp(event);
    }

    /**
     * The default role is to do nothing more than forward the event to the current scene's {@link SceneDirector}.
     */
    @Override
    public void onMouseMove( MouseMotionEvent event )
    {
        this.game.getSceneDirector().onMouseMove(event);
    }

    /**
     * Called when a button is pressed. Most games don't use onKeyDown or onKeyUp during game play, instead, each Actor uses :
     * Itchy.isKeyDown( ... ). onKeyDown and onKeyUp are useful for typing, not for game play.
     */
    @Override
    public void onKeyDown( KeyboardEvent event )
    {
        this.game.getSceneDirector().onKeyDown(event);
    }

    /**
     * Called when a button is pressed. Most games don't use onKeyDown or onKeyUp during game play, instead, each Actor uses :
     * Itchy.isKeyDown( ... ). onKeyDown and onKeyUp are useful for typing.
     * 
     * The default role is to do nothing more than forward the event to the current scene's {@link SceneDirector}.
     */
    @Override
    public void onKeyUp( KeyboardEvent ke )
    {
        this.game.getSceneDirector().onKeyUp(ke);
    }

    /**
     * Called when the application has been asked to quit, such as when Alt-F4 is pressed, or the window's close button is pressed. The
     * default role is to terminate the application.
     */
    @Override
    public void onQuit(QuitEvent e)
    {
    }
    
    @Override
	public boolean onWindowEvent( WindowEvent event )
    {
        // Do nothing
        return false;
    }

    @Override
    public void onActivate()
    {
        // Do nothing
    }

    @Override
    public void onDeactivate()
    {
        // Does nothing
    }

    @Override
	public void onResize( ResizeEvent event )
    {
    	try {
    		this.game.resize( event.width, event.height );
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    /**
     * Gets the root node for this game. The default implementation uses the path based on Itchy's class name, and the game's ID.
     * Note, unlike {@link Game#getPreferences()}, the returned Preferences are not AutoFlushPreferences, you will need to call 'flush' to
     * commit each of the changes.
     * 
     * @return The top level preferences node for this game.
     */
    @Override
    public Preferences getPreferenceNode()
    {
        return Preferences.userNodeForPackage(Itchy.class).node(this.game.resources.getId());
    }

    /**
     * The default role is to do nothing more than forward the message to the current scene's {@link SceneDirector}.
     */
    @Override
    public void onMessage( String message )
    {
        if ((this.game != null) && (this.game.getSceneDirector() != null)) {
            this.game.getSceneDirector().onMessage(message);
        }
    }

    @Override
    public void tick()
    {
        // Do nothing.
    }

    @Override
    public Scene loadScene(String sceneName)
    {
        return this.game.loadScene(sceneName);
    }
}
