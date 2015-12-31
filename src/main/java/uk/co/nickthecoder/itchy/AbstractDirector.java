/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.prefs.Preferences;

import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;
import uk.co.nickthecoder.jame.event.WindowEvent;

public class AbstractDirector implements Director
{
    protected Game game;

    public ZOrderStage mainStage;

    public StageView mainView;

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
        Rect screenRect = new Rect(0, 0, this.game.getWidth(), this.game.getHeight());

        this.mainStage = new ZOrderStage("main");
        this.game.getStages().add(this.mainStage);

        this.mainView = new StageView(screenRect, this.mainStage);
        this.game.getGameViews().add(this.mainView);

        this.mainView.enableMouseListener(this.game);
    }

    /**
     * The default role is to do nothing more than forward the event to the current scene's {@link SceneDirector}.
     */
    @Override
    public boolean onMouseDown( MouseButtonEvent event )
    {
        return this.game.getSceneDirector().onMouseDown(event);
    }

    /**
     * The default role is to do nothing more than forward the event to the current scene's {@link SceneDirector}.
     */
    @Override
    public boolean onMouseUp( MouseButtonEvent event )
    {
        return this.game.getSceneDirector().onMouseUp(event);
    }

    /**
     * The default role is to do nothing more than forward the event to the current scene's {@link SceneDirector}.
     */
    @Override
    public boolean onMouseMove( MouseMotionEvent event )
    {
        return this.game.getSceneDirector().onMouseMove(event);
    }

    /**
     * Called when a button is pressed. Most games don't use onKeyDown or onKeyUp during game play, instead, each Actor uses :
     * Itchy.isKeyDown( ... ). onKeyDown and onKeyUp are useful for typing, not for game play.
     */
    @Override
    public boolean onKeyDown( KeyboardEvent event )
    {
        return this.game.getSceneDirector().onKeyDown(event);
    }

    /**
     * Called when a button is pressed. Most games don't use onKeyDown or onKeyUp during game play, instead, each Actor uses :
     * Itchy.isKeyDown( ... ). onKeyDown and onKeyUp are useful for typing.
     * 
     * The default role is to do nothing more than forward the event to the current scene's {@link SceneDirector}.
     */
    @Override
    public boolean onKeyUp( KeyboardEvent ke )
    {
        return this.game.getSceneDirector().onKeyUp(ke);
    }

    /**
     * Called when the application has been asked to quit, such as when Alt-F4 is pressed, or the window's close button is pressed. The
     * default role is to terminate the application.
     */
    @Override
    public boolean onQuit()
    {
        Itchy.terminate();
        return true;
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
	public void onResize( int width, int height )
    {
    	try {
    		this.game.resize( width, height );
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    @Override
    public boolean startScene( String sceneName )
    {
        if (this.game.pause.isPaused()) {
            this.game.pause.unpause();
        }
        this.game.clear();
        return this.game.loadScene(sceneName);
    }

    /**
     * Gets the root node for this game. The default implementation uses the path based on the Directors class name, and the game's ID.
     * Note, unlike {@link Game#getPreferences()}, the returned Preferences are not AutoFlushPreferences, you will need to call 'flush' to
     * commit each of the changes.
     * 
     * @return The top level preferences node for this game.
     */
    @Override
    public Preferences getPreferenceNode()
    {
        return Preferences.userNodeForPackage(this.getClass()).node(this.game.resources.getId());
    }

    /**
     * The default role is to do nothing more than forward the message to the current scene's {@link SceneDirector}.
     */
    @Override
    public void onMessage( String message )
    {
        this.game.getSceneDirector().onMessage(message);
    }

    @Override
    public void tick()
    {
        // Do nothing.
    }
}
