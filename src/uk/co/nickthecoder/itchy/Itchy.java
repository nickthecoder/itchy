/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.io.File;
import java.util.Stack;

import uk.co.nickthecoder.itchy.animation.Eases;
import uk.co.nickthecoder.itchy.util.ClassName;
import uk.co.nickthecoder.jame.Audio;
import uk.co.nickthecoder.jame.Events;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.Video;
import uk.co.nickthecoder.jame.event.Event;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.Keys;
import uk.co.nickthecoder.jame.event.MouseEvent;

/**
 * The is the overall manager of the Itchy game engine. There is only one instance (Itchy.singleton).
 * 
 * The init method should be called as soon as possible, ideally right at the top of your program's main method.
 * 
 * General notes about Itchy :
 * 
 * Itchy works with two types of coordinates; pixel coordinates, and world coordinates. World coordinates are used to keep track of Actors'
 * positions, and are stored in doubles. Pixel coordinates are stored as integers and are used when dealing with the low-level processing of
 * images.
 */
public class Itchy
{

    /**
     * This is the highest SDL key sym which can be checked using isKeyDown(). The highest key sym is currently 321, and I'm using 400,
     * which leaves plenty of room for additional keys to be added in the future.
     */
    private static int KEYBOARD_STATE_SIZE = 400;

    /**
     * Holds a boolean for each key. On key pressed events sets the appropriate boolean, and key released events reset the boolean. Uses the
     * Keys values to index the array.
     */
    private static boolean[] keyboardState;

    private static int mouseX;

    private static int mouseY;

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

    private static boolean initialised = false;

    private static File baseDirectory;

    public static final Registry registry = new Registry();

    static {
        System.out.println( "Registering standard ClassNames");
        
        registry.add(new ClassName(Director.class, PlainDirector.class.getName()));

        registry.add(new ClassName(SceneDirector.class, PlainSceneDirector.class.getName()));

        registry.add(new ClassName(Role.class, uk.co.nickthecoder.itchy.NullRole.class.getName()));
        registry.add(new ClassName(Role.class, uk.co.nickthecoder.itchy.extras.EditorButton.class.getName()));
        registry.add(new ClassName(Role.class, uk.co.nickthecoder.itchy.extras.LinkButton.class.getName()));
        registry.add(new ClassName(Role.class, uk.co.nickthecoder.itchy.extras.MessageButton.class.getName()));
        registry.add(new ClassName(Role.class, uk.co.nickthecoder.itchy.extras.NumberValue.class.getName()));
        registry.add(new ClassName(Role.class, uk.co.nickthecoder.itchy.extras.TextValue.class.getName()));
        registry.add(new ClassName(Role.class, uk.co.nickthecoder.itchy.extras.QuitButton.class.getName()));
        registry.add(new ClassName(Role.class, uk.co.nickthecoder.itchy.extras.ProgressBar.class.getName()));

        registry.add(new ClassName(CostumeProperties.class, CostumeProperties.class.getName()));

        System.out.println( "Registering Eases");
        
        Eases.registerEases();
    }

    public static void init( Resources resources ) throws Exception
    {
        if (initialised) {
            return;
        }
        Video.init();
        Audio.init();
        Audio.open();
        Events.enableKeyTranslation(true);

        keyboardState = new boolean[KEYBOARD_STATE_SIZE];
        soundManager = new SoundManager();
        setScreenMode(resources);
        initialised = true;
    }

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

    public static File getResourcesDirectory()
    {
        return new File(getBaseDirectory(), "resources");
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
        setScreenMode(game.getTitle(), game.resources, game.getWidth(), game.getHeight());
    }

    private static void setScreenMode( Resources resources )
    {
        setScreenMode(resources.getGameInfo().title, resources,
            resources.getGameInfo().width, resources.getGameInfo().height);
    }

    private static void setScreenMode( String title, Resources resources, int width, int height )
    {
        Video.setWindowTitle(title);

        // Load the 32x32 image, and then try to load the higher res image - not caring if it fails.
        // According to the SDL1.2 docs, windows MUST be given a 32x32 image.
        String filename32 = resources.resolveFilename("icon32.bmp");
        String filename = resources.resolveFilename("icon.bmp");

        try {
            Video.setWindowIcon(filename32);
            Video.setWindowIcon(filename);
        } catch (Exception e) {
            // Do nothing
        }

        try {
            Video.setMode(width, height);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startGame( Game game )
    {
        if (currentGame != null) {
            currentGame.onDeactivate();
            gameStack.push(currentGame);
        }
        currentGame = game;
        currentGame.onActivate();
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
            System.err.println("Mainloop Failed");
            e.printStackTrace();
        }
    }

    public static void endGame()
    {
        currentGame.onDeactivate();
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
            currentGame.onActivate();
            mainLoop();
        }
    }

    /**
     * Indicates that the main loop should end. Note the game does not end immediately, it only sets a flag, which will cause the main loop
     * to end after the current frame has been processed.
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
    }

    public static Surface getDisplaySurface()
    {
        return Video.getDisplaySurface();
    }

    private static void doRedraw()
    {
        currentGame.render(Video.getDisplaySurface());
        Video.flip();
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
     * Tests state of either shift keys A convenience method, the same as isKeyDown( Keys.LSHIFT ) || isKeyDown( Keys.RSHIFT )
     */
    public static boolean isShiftDown()
    {
        return keyboardState[Keys.LSHIFT] || keyboardState[Keys.RSHIFT];
    }

    /**
     * Tests state of either control keys A convenience method, the same as isKeyDown( Keys.LCTRL ) || isKeyDown( Keys.RCTRL )
     */
    public static boolean isCtrlDown()
    {
        return keyboardState[Keys.LCTRL] || keyboardState[Keys.RCTRL];
    }

    /**
     * Tests state of either control keys A convenience method, the same as isKeyDown( Keys.LALT ) || isKeyDown( Keys.RALT )
     */
    public static boolean isAltDown()
    {
        return keyboardState[Keys.LALT] || keyboardState[Keys.RALT];
    }

    /**
     * Tests state of either meta keys A convenience method, the same as isKeyDown( Keys.LMETA ) || isKeyDown( Keys.RMETA )
     */
    public static boolean isMetaDown()
    {
        return keyboardState[Keys.LMETA] || keyboardState[Keys.RMETA];
    }

    /**
     * Tests state of either super keys A convenience method, the same as isKeyDown( Keys.LSUPER ) || isKeyDown( Keys.RSUPER )
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
