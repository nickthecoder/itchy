/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.prefs.Preferences;

import uk.co.nickthecoder.jame.event.ResizeEvent;

public interface Director extends InputListener, QuitListener, MessageListener, WindowListener
{
    // Messages sent to the Director while loading the game
    public static final String GAME_INFO_LOADED = "GameInfoLoaded";
    public static final String INPUTS_LOADED = "InputsLoaded";
    public static final String FONTS_LOADED = "FontsLoaded";
    public static final String SOUNDS_LOADED = "SoundsLoaded";
    public static final String NINE_PATCHES_LOADED = "NinePatchesLoaded";
    public static final String POSES_LOADED = "PosesLoaded";
    public static final String ANIMIATIONS_LOADED = "AnimationsLoaded";
    public static final String COSTUMES_LOADED = "CostumesLoaded";
    public static final String SCENES_LOADED = "ScenesLoaded";
    public static final String LOADED = "Loaded";

    public void attach( Game game );

    public void onStarted();

    public void onActivate();

    public void onDeactivate();

    public void onResize( ResizeEvent e );

    public boolean startScene( String sceneName );

    public void tick();

    /**
     * Gets the root node for this game.
     * 
     * @return The top level preferences node for this game.
     */
    public Preferences getPreferenceNode();

}
