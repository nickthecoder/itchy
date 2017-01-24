/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.io.File;
import java.util.Date;
import java.util.Stack;

import uk.co.nickthecoder.itchy.editor.Editor;
import uk.co.nickthecoder.jame.Audio;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.event.Event;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButton;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;
import uk.co.nickthecoder.jame.event.ResizeEvent;
import uk.co.nickthecoder.jame.event.ScanCode;

/**
 * The top-level manager of the Itchy game engine. It only has static methods.
 */
public final class Itchy
{

    /**
     * This is the highest SDL key sym which can be checked using isKeyDown(). The highest key sym is currently 321, and
     * I'm using 400, which leaves plenty of room for additional keys to be added in the future.
     */
    private static int KEYBOARD_STATE_SIZE = 400;

    /**
     * Number of mouse buttons + 1
     */
    private static int MOUSE_STATE_SIZE = MouseButton.values().length + 1;

    /**
     * Holds a boolean for each key indicating if it is currently up (false) or down (true). On key pressed events sets
     * the appropriate boolean, and key released events reset
     * the boolean. Uses {@link Keys} values to index the array.
     */
    private static boolean[] keyboardState;

    /**
     * Holds a boolean for each Mouse Button indicating if it is currently up (false) or down (true).
     */
    private static boolean[] mouseState;

    /**
     * Holds the position of the mouse between {@link MouseMotionEvent}s.
     */
    private static int mouseX;

    /**
     * Holds the position of the mouse between {@link MouseMotionEvent}s.
     */
    private static int mouseY;

    private static boolean running;

    private static Game currentGame;

    private static Game loadingGame;

    /**
     * If one game calls another game, which then exists, this is how we return to the first game.
     * Note that the {@link Launcher} is a game, so this stack is used often.
     */
    private static Stack<Game> gameStack = new Stack<Game>();

    /**
     * We only want to initialise Jame once.
     */
    private static boolean initialised = false;

    /**
     * Used to find the "resources" directory.
     */
    private static File baseDirectory;

    /**
     * Used in a bodge attempting to fix a bug in SDL, where the incorrect window size is returned when dragging a
     * window's border.
     */
    private static long lastWindowResizeTime = 0;

    /**
     * A registry of standard classes/scripts (Roles, SceneDirectors, Directors) as well as Animations, Makeup and
     * Eases.
     * Used by the {@link Editor}
     * 
     * @priority 4
     */
    static final Registry registry = new Registry();

    /**
     * This is called automatically when the game's ".itchy" file is being loaded (because it gets the game's width and
     * height from this file). As this is called automatically, you don't need to worry about it.
     * 
     * @param resources
     *            Uses the resource's GameInfo to set the screen size.
     * @throws Exception
     * @priority 5
     */
    public static void init() throws Exception
    {
        if (initialised) {
            return;
        }

        System.out.println("Itchy.init");

        Audio.init();
        Audio.open();

        keyboardState = new boolean[KEYBOARD_STATE_SIZE];
        mouseState = new boolean[MOUSE_STATE_SIZE];
        // TODO Remove?
        // setScreenMode(resources);
        initialised = true;
    }

    /**
     * Most games windows cannot be resized, so this method isn't used much. Note, this only changes the size of the
     * screen, it does not change the sizes of the {@link View}s.
     * 
     * @param width
     *            The new width of the screen in pixels.
     * @param height
     *            The new height of the screen in pixels.
     * @priority 3
     */
    public static void resizeScreen(int width, int height, boolean resizable)
    {
        // TODO Handle window resizing
        //Game game = currentGame;

        // setScreenMode(game.getTitle(), game.resources, width, height, resizable);
        // TODO Remove bodge
        lastWindowResizeTime = new Date().getTime();
    }

    /**
     * Gets Itchy's base directory - it is the directory which has the "resources" sub-directory, and its used to find
     * all of the games' resources. Most of the time this will be the current directory, but if you set the system
     * property "itchy.base", then it will use that instead.
     * 
     * @return The system property "itchy.base" if it is set, otherwise the current directory (".").
     * @priority 3
     */
    public static File getBaseDirectory()
    {
        if (baseDirectory == null) {
            String basePath = System.getProperty("itchy.base");
            if (basePath == null) {
                System.err.println("itchy.base not set. Defaulting to the current directory.");
                basePath = ".";
            }
            baseDirectory = new File(basePath);
        }
        return baseDirectory;
    }

    /**
     * @return The "resources" directory.
     * @priority 3
     */
    public static File getResourcesDirectory()
    {
        return new File(getBaseDirectory(), "resources");
    }

    /**
     * Itchy has a stack of games, game A can start another game, (game B). While game B is loading, and while game B
     * is active, then getGame will return game B. Once game B ends, then game A will become the active game again,
     * and getGame will return game A.
     * <p>
     * Note that while a game is loading, then getGame will return it, even though it isn't yet active.
     * 
     * @return The currently active game.
     */
    public static Game getGame()
    {
        if (loadingGame != null) {
            return loadingGame;
        } else {
            return currentGame;
        }
    }

    /*
     *  TODO Remove setScreenMode
    private static void setScreenMode(Resources resources)
    {
        GameInfo gameInfo = resources.getGameInfo();
        setScreenMode(gameInfo.title, resources, gameInfo.width, gameInfo.height, gameInfo.resizable);
    }
     */
    
    /* 
     * TODO Remove setScreenMode
    private static void setScreenMode(Game game)
    {
        setScreenMode(game.getTitle(), game.resources, game.getWidth(), game.getHeight(),
            game.getDirector().isResizable());
    }
    */
    
    /* 
     * TODO Remove setScreenMode
    private static void setScreenMode(String title, Resources resources, int width, int height, boolean resizable)
    {
        // TODO VIDEO
        //Video.setWindowTitle(title);

        // According to the SDL1.2 docs, windows MUST be given a 32x32 image. How very quaint (windows is shite).
        // For good looking OSes, we should try to use a bigger icon if there is one.
        // Note that these must be bmp (yuck!), and not png, because it uses SDL_LoadBMP. Annoying!
        String filename32 = resources.resolveFilename("icon32.bmp");
        String filename = resources.resolveFilename("icon.bmp");

        if (System.getProperty("os.name").startsWith("Windows")) {
            filename = filename32;
        }

        try {
            if (new File(filename).exists()) {
                // TODO VIDEO
                //Video.setWindowIcon(filename);
            } else if (new File(filename32).exists()) {
                // TODO VIDEO
                //Video.setWindowIcon(filename32);
            }
        } catch (Exception e) {
            // Do nothing
        }

        try {
            int flags = Video.SWSURFACE | Video.DOUBLEBUF;
            if (resizable) {
                flags = flags | Video.RESIZABLE;
            }
            Video.setMode(width, height, 32, flags);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */

    /**
     * Used internally by Itchy.
     * 
     * @param game
     * @priority 5
     */
    public static void startGame(Game game)
    {
        loadingGame = null;
        if (currentGame != null) {
            currentGame.onDeactivate();
            gameStack.push(currentGame);
        }
        currentGame = game;
        currentGame.onActivate();
    }

    /**
     * Used internally by Itchy.
     * 
     * @param game
     * @priority 5
     */
    static void loadingGame(Game game)
    {
        loadingGame = game;
    }

    /**
     * Should be called once from the program's entry point (static void main method; usually from {@link Launcher}). To
     * exit from the mainLoop, call {@link #terminate()}.
     * 
     * @priority 5
     */
    public static void mainLoop()
    {
        try {
            if (!running) {
                running = true;
                while (running) {
                    getGame().getFrameRate().loop();
                }
            }
        } catch (Exception e) {
            System.err.println("Mainloop Failed");
            handleException(e);
        }
    }

    /**
     * Called when a game script throws an Exception. The current behaviour just prints a stack trace to stderr,
     * but later versions of Itchy may report the error more gracefully. For example, it may stop reporting errors
     * if too many are thrown, as they are likely all from the same bug.
     * 
     * @param e
     * @priority 3
     */
    public static void handleException(Exception e)
    {
        e.printStackTrace();
        if (e instanceof org.python.core.PySyntaxError) {
            for (StackTraceElement ste : e.getStackTrace()) {
                if (ste.getClassName().startsWith("org.python")) {
                    // Ignored
                } else {
                    System.err.println(ste);
                }
            }
            System.err.println("Python script exception!");
        }
    }

    /**
     * Called when a {@link Game} ends. If there is another game on the stack, then continues to run, otherwise
     * terminates.
     * 
     * Itchy.
     * 
     * @priority 5
     */
    public static void endGame()
    {
        currentGame.onDeactivate();

        if (gameStack.isEmpty()) {
            terminate();
        } else {
            currentGame = gameStack.pop();
            try {
                // TODO Bring back the previous game window.
                // setScreenMode(currentGame);
            } catch (Exception e) {
                e.printStackTrace();
            }
            currentGame.onActivate();
            mainLoop();
        }
    }

    /**
     * Indicates that the main loop should end. Note the game does not end immediately, it only sets a flag, which will
     * cause the main loop to end after the current frame has been processed.
     * 
     * @priority 3
     */
    public static void terminate()
    {
        System.out.println("Terminating itchy");
        running = false;
        getGame().getFrameRate().end();
    }

    /**
     * Gets the surface the game is being drawn to. This will not have an alpha channel.
     * 
     * @return The display's surface.
     */
    public static Surface getDisplaySurface()
    {
        // TODO VIDEO
        //return Video.getDisplaySurface();
        return getGame().gameWindow.surface;
    }

    /**
     * Renders (draws) the whole screen, and then flips the double buffer, so that the newly rendered screen is visible.
     * 
     * @priority 3
     */
    public static void render()
    {
        GameWindow gameWindow = currentGame.gameWindow;
        
        try {
            gameWindow.render();
        } catch (Exception e) {
            handleException(e);
        }
    }

    /**
     * Is itchy still running?
     * 
     * @return False if Itchy should exit at the end of the current frame. True otherwise.
     * @priority 3
     */
    public static boolean isRunning()
    {
        return running;
    }

    /**
     * It is usually better to test for key presses via an {@link Input}, because this given more flexibility, and your
     * game code looks cleaner. It is also easier for players to change keys to suit themselves.
     * 
     * @param keySym
     * @return
     * @priority 3
     */
    public static boolean isKeyDown(int keySym)
    {
        return keyboardState[keySym];
    }

    /**
     * Tests state of either shift keys. A convenience method, the same as
     * <code>isKeyDown( Keys.LSHIFT ) || isKeyDown( Keys.RSHIFT )</code>
     * 
     * @priority 3
     */
    public static boolean isShiftDown()
    {
        /* TODO Use Event.getModifiers */
        return keyboardState[ScanCode.LSHIFT.value] || keyboardState[ScanCode.RSHIFT.value];
    }

    /**
     * Tests state of either control keys. A convenience method, the same as
     * <code>isKeyDown( Keys.LCTRL ) || isKeyDown( Keys.RCTRL )</code>
     * 
     * @priority 3
     */
    public static boolean isCtrlDown()
    {
        /* TODO Use Event.getModifiers */
        return keyboardState[ScanCode.LCTRL.value] || keyboardState[ScanCode.RCTRL.value];
    }

    /**
     * Tests state of either control keys. A convenience method, the same as
     * <code>isKeyDown( Keys.LALT ) || isKeyDown( Keys.RALT )</code>
     * 
     * @priority 3
     */
    public static boolean isAltDown()
    {
        /* TODO Use Event.getModifiers */
        return keyboardState[ScanCode.LALT.value] || keyboardState[ScanCode.RALT.value];
    }

    /**
     * Processes a single event. Called from {@link #processEvents}.
     * 
     * @param event
     * @priority 5
     */
    public static void processEvent(Event event)
    {
        if (event instanceof ResizeEvent) {

            // Using Gnome 3.14.1, when a window border is dragged, the correct resize event is sent, but
            // then the window is resized, and ANOTHER resize event is fired which includes the
            // size of the chrome (title bar and borders). This bodge stops a runaway, where the window
            // is made higher and higher when dragging sideways. It does not completely fix the problem, but
            // makes it bearable.
            long diff = new Date().getTime() - lastWindowResizeTime;
            if (diff < 500) {
                // Ignore the resize event which happens within 0.5 seconds of the window being resized.
                lastWindowResizeTime = 0;
                return;
            }
        }

        if (event instanceof KeyboardEvent) {
            KeyboardEvent ke = (KeyboardEvent) event;

            int key = ke.scanCodeValue;
            if ((key > 0) && (key < keyboardState.length)) {
                keyboardState[key] = ke.pressed;
            }

        } else if (event instanceof MouseEvent) {
            MouseEvent me = (MouseEvent) event;

            mouseX = me.x;
            mouseY = me.y;

            if (event instanceof MouseButtonEvent) {
                MouseButtonEvent mbe = (MouseButtonEvent) event;
                if (mbe.button < mouseState.length) {
                    mouseState[mbe.button] = mbe.pressed;
                }
            }

        }

        currentGame.processEvent(event);
    }

    /**
     * Taken from the last {@link MouseMotionEvent}.
     * 0 is the left edge of the screen.
     * <p>
     * To convert this to world coordinates use {@link View#getWorldX(int)}.
     * 
     * @return The x position of the mouse in pixels.
     */
    public static int getMouseX()
    {
        return mouseX;
    }

    /**
     * Taken from the last {@link MouseMotionEvent}.
     * 0 is the top edge of the screen, and the Y axis points downwards.
     * <p>
     * To convert this to world coordinates use {@link View#getWorldY(int)}.
     * 
     * @return The y position of the mouse in pixels.
     */
    public static int getMouseY()
    {
        return mouseY;
    }

    /**
     * Tests if the one of the mouse buttons is currently held down.
     * <p>
     * Consider using {@link Input} rather than testing mouse buttons directly. This will make you code more readable
     * and flexible. Player can change the inputs to suit themselves without editing code.
     * 
     * @param mouseButton
     *            The number of the mouse button. See MouseButtonEvent for button numbers.
     * @return true iff the button is down.
     * @priority 3
     */
    public static boolean isMouseButtonDown(int mouseButton)
    {
        if (mouseButton >= mouseState.length) {
            return false;
        } else {
            return mouseState[mouseButton];
        }
    }

    /**
     * Prevent people creating instances of Itchy - it only has static methods.
     */
    private Itchy()
    {
    }

}
