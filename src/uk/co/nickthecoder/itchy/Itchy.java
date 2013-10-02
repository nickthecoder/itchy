/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.Stack;

import uk.co.nickthecoder.jame.Audio;
import uk.co.nickthecoder.jame.Events;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.Video;
import uk.co.nickthecoder.jame.event.Event;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.Keys;
import uk.co.nickthecoder.jame.event.MouseEvent;

/**
 * The is the overall manager of the Itchy game engine. There is only one instance
 * (Itchy.singleton).
 * 
 * The init method should be called as soon as possible, ideally right at the top of your program's
 * main method.
 * 
 * General notes about Itchy :
 * 
 * Itchy works with two types of coordinates; pixel coordinates, and world coordinates. World
 * coordinates are used to keep track of Actors' positions, and are stored in doubles. Pixel
 * coordinates are stored as integers and are used when dealing with the low-level processing of
 * images.
 */
public class Itchy
{
    public static void showMousePointer( boolean value )
    {
        uk.co.nickthecoder.jame.Video.showMousePointer(value);
    }

    /**
     * This is the highest SDL key sym which can be checked using isKeyDown(). The highest key sym
     * is currently 321, and I'm using 400, which leaves plenty of room for additional keys to be
     * added in the future.
     */
    private static int KEYBOARD_STATE_SIZE = 400;

    /**
     * Holds a boolean for each key. On key pressed events sets the appropriate boolean, and key
     * released events reset the boolean. Uses the Keys values to index the array.
     */
    private static boolean[] keyboardState;

    private static int mouseX;
    
    private static int mouseY;

    public static Surface screen;

    private static boolean running;

    private static Game currentGame;

    /**
     * If one game calls another game, and then exists, this is how we return to the previous game.
     */
    private static Stack<Game> gameStack = new Stack<Game>();

    public static int keyboardRepeatDelay = Events.DEFAULT_REPEAT_DELAY;

    public static int keyboardRepeatInterval = Events.DEFAULT_REPEAT_INTERVAL;
    
    public static FrameRate frameRate = createFrameRate();

    public static SoundManager soundManager;

    public static void init( Game game ) throws Exception
    {
        Video.init();
        Audio.init();
        Audio.open();
        Events.enableKeyTranslation(true);

        keyboardState = new boolean[KEYBOARD_STATE_SIZE];
        soundManager = new SoundManager();
        setScreenMode(game);
    }

    public static Game getGame()
    {
        return currentGame;
    }

    public static Resources getResources()
    {
        return currentGame.resources;
    }

    private static void setScreenMode( Game game )
    {
        if (game.getTitle() == null) {
            Video.setWindowTitle("Itchy");
        } else {
            Video.setWindowTitle(game.getTitle());
        }
        if (game.getIconFilename() != null) {
            Video.setWindowIcon(game.getIconFilename());
        }

        try {
            screen = Video.setMode(game.getWidth(), game.getHeight());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void startGame( Game game )
    {
        if (currentGame != null) {
            gameStack.push(currentGame);
        }
        currentGame = game;
        setScreenMode(currentGame);
    }

    public static void mainLoop()
    {
        try {
            if (!running) {
                running = true;
                frameRate.loop();
            }
        } catch (Exception e) {
            System.err.println("Failed to initialise game");
            e.printStackTrace();
        }
    }

    public static void endGame()
    {
        soundManager.stopAll();

        if (gameStack.isEmpty()) {
            terminate();
        } else {
            currentGame = gameStack.pop();
            try {
                setScreenMode(currentGame);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mainLoop();
        }
    }

    /**
     * Indicates that the main loop should end. Note the game does not end immediately, it only sets
     * a flag, which will cause the main loop to end after the current frame has been processed.
     */
    public static void terminate()
    {
        System.out.println("Terminating itchy");
        running = false;
    }

    private static FrameRate createFrameRate()
    {
        return new FrameRate() {

            @Override
            public boolean isRunning()
            {
                return Itchy.running;
            }

            @Override
            public void doGameLogic()
            {
                Itchy.doGameLogic();
            }

            @Override
            public void doRedraw()
            {
                Itchy.doRedraw();
            }
        };
    }

    private static void doGameLogic()
    {
        while (true) {
            Event event = Events.poll();
            if (event == null) {
                break;
            } else {
                processEvent(event);
            }
        }

        soundManager.tick();

        currentGame.tick();
        for (Actor actor : Actor.allByTag("active")) {
            actor.tick();
        }
    }

    private static void doRedraw()
    {
        currentGame.render(screen);
        screen.flip();
    }

    public static void endOfFrame()
    {
    }

    public static boolean isRunning()
    {
        return running;
    }

    public static void enableKeyboardRepeat( boolean value )
    {
        if (value) {
            Events.keyboardRepeat(keyboardRepeatDelay, keyboardRepeatInterval);
        } else {
            Events.keyboardRepeat(0, 0);
        }
    }

    public static boolean isKeyDown( int keySym )
    {
        return keyboardState[keySym];
    }

    /**
     * Tests state of either shift keys A convenience method, the same as isKeyDown( Keys.LSHIFT )
     * || isKeyDown( Keys.RSHIFT )
     */
    public static boolean isShiftDown()
    {
        return keyboardState[Keys.LSHIFT] || keyboardState[Keys.RSHIFT];
    }

    /**
     * Tests state of either control keys A convenience method, the same as isKeyDown( Keys.LCTRL )
     * || isKeyDown( Keys.RCTRL )
     */
    public static boolean isCtrlDown()
    {
        return keyboardState[Keys.LCTRL] || keyboardState[Keys.RCTRL];
    }

    /**
     * Tests state of either meta keys A convenience method, the same as isKeyDown( Keys.LMETA ) ||
     * isKeyDown( Keys.RMETA )
     */
    public static boolean isMetaDown()
    {
        return keyboardState[Keys.LMETA] || keyboardState[Keys.RMETA];
    }

    /**
     * Tests state of either super keys A convenience method, the same as isKeyDown( Keys.LSUPER )
     * || isKeyDown( Keys.RSUPER )
     */
    public static boolean isSuperDown()
    {
        return keyboardState[Keys.LSUPER] || keyboardState[Keys.RSUPER];
    }

    private static void processEvent( Event event )
    {

        if (event instanceof KeyboardEvent) {
            KeyboardEvent ke = (KeyboardEvent) event;

            if (ke.isPressed()) {
                int key = ke.symbol;
                if ((key > 0) && (key < keyboardState.length)) {
                    keyboardState[key] = true;
                }

            } else if (ke.isReleased()) {

                int key = ke.symbol;
                if ((key > 0) && (key < keyboardState.length)) {
                    keyboardState[key] = false;
                }
            }
        } else if (event instanceof MouseEvent) {
            MouseEvent me = (MouseEvent) event;
            
            mouseX = me.x;
            mouseY = me.y;
        }

        
        currentGame.processEvent(event);
    }
    
    public static int getMouseX()
    {
        return mouseX;
    }
    
    public static int getMouseY()
    {
        return mouseY;
    }
}
