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

/**
 * A Director is a place where some of your game's logic lives.
 * The Director is long lived (exists for the entire life of the game).
 * <p>
 * It is rare for Director to do very much; it is usually better to place game logic into {@link Role}s and
 * {@link SceneDirector}s.
 */
public interface Director extends InputListener, QuitListener, MessageListener, WindowListener
{
    // Messages sent to the Director while loading the game

    /**
     * GameInfoLoaded
     * 
     * @priority 2
     */
    public static final String GAME_INFO_LOADED = "GameInfoLoaded";
    /**
     * InputsLoaded
     * 
     * @priority 2
     */
    public static final String INPUTS_LOADED = "InputsLoaded";
    /**
     * FontsLoaded
     * 
     * @priority 2
     */
    public static final String FONTS_LOADED = "FontsLoaded";
    /**
     * SoundsLoaded
     * 
     * @priority 2
     */
    public static final String SOUNDS_LOADED = "SoundsLoaded";
    /**
     * NinePatchesLoaded
     * 
     * @priority 2
     */
    public static final String NINE_PATCHES_LOADED = "NinePatchesLoaded";
    /**
     * SpriteSheetsLoaded
     * 
     * @priority 2
     */
    public static final String SPRITE_SHEETS_LOADED = "SpriteSheetsLoaded";
    /**
     * PosesLoaded
     * 
     * @priority 2
     */
    public static final String POSES_LOADED = "PosesLoaded";
    /**
     * AnimationsLoaded
     * 
     * @priority 2
     */
    public static final String ANIMIATIONS_LOADED = "AnimationsLoaded";
    /**
     * CostumesLoaded
     * 
     * @priority 2
     */
    public static final String COSTUMES_LOADED = "CostumesLoaded";
    /**
     * LayoutsLoaded
     * 
     * @priority 2
     */
    public static final String LAYOUTS_LOADED = "LayoutsLoaded";
    /**
     * ScenesLoaded
     * 
     * @priority 2
     */
    public static final String SCENES_LOADED = "ScenesLoaded";
    /**
     * Loaded
     * 
     * @priority 2
     */
    public static final String LOADED = "Loaded";

    public void attach(Game game);

    /**
     * Called after the resources are loaded, and the game is ready to begin.
     * Perform one-time initialisation here.
     * <p>
     * Note, The constructor do all but the most trivial of initialisation, because the resources have not yet been
     * fully loaded at that point.
     * 
     */
    public void onStarted();

    /**
     * Itchy allows one game to call another, the first game is put to sleep while the second game is being played.
     * Called when the game is first started, and also whenever a 'child' game exits.
     * <p>
     * Used very rarely.
     * 
     * @priority 3
     */
    public void onActivate();

    /**
     * The opposite of {@link #onActivate()}.
     * <p>
     * Used very rarely.
     * 
     * @priority 3
     */
    public void onDeactivate();

    /**
     * Called while a {@link Scene} is being loaded.
     * 
     * @param sceneName
     */
    public void onStartingScene(String sceneName);

    /**
     * Called soon after a {@link Scene} has been loaded.
     */
    public void onStartedScene();

    /**
     * Director can be sent messages from various sources.
     * While the {@link Resources} are being loaded, various messages are sent : {@link #GAME_INFO_LOADED},
     * {@link #INPUTS_LOADED}, {@link #FONTS_LOADED}, {@link #SPRITE_SHEETS_LOADED}, {@link #POSES_LOADED},
     * {@link #ANIMIATIONS_LOADED}, {@link #COSTUMES_LOADED}, {@link #LAYOUTS_LOADED}, {@link #SCENES_LOADED}.
     * The order of these cannot be guarenteed, however the last message is : {@link #LOADED}.
     */
    @Override
    public void onMessage(String message);

    @Override
    public void onQuit(QuitEvent e);

    @Override
    public void onKeyDown( KeyboardEvent ke );

    @Override
    public void onKeyUp( KeyboardEvent ke );

    @Override
    public void onMouseDown( MouseButtonEvent event );

    @Override
    public void onMouseUp( MouseButtonEvent event );

    @Override
    public void onMouseMove( MouseMotionEvent event );
    
    /**
     * Called when the game's window is resized. Most games cannot be resized, and therefore this isn't used.
     * 
     * @param e
     * @priority 2
     */
    public void onResize(ResizeEvent e);

    public Scene loadScene(String sceneName);

    /**
     * Called once per frame (60 times per second).
     */
    public void tick();

    /**
     * Gets the root node for this game.
     * 
     * @return The top level preferences node for this game.
     */
    public Preferences getPreferenceNode();

}
