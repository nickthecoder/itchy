/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.io.File;
import java.util.Stack;

import uk.co.nickthecoder.itchy.animation.Animations;
import uk.co.nickthecoder.itchy.animation.Eases;
import uk.co.nickthecoder.itchy.makeup.Makeup;
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

    private static Game loadingGame;
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
        registry.add(new ClassName(Director.class, PlainDirector.class.getName()));

        registry.add(new ClassName(SceneDirector.class, PlainSceneDirector.class.getName()));

        registry.add(new ClassName(Role.class, uk.co.nickthecoder.itchy.role.PlainRole.class.getName()));
        registry.add(new ClassName(Role.class, uk.co.nickthecoder.itchy.role.EditorButton.class.getName()));
        registry.add(new ClassName(Role.class, uk.co.nickthecoder.itchy.role.LinkButton.class.getName()));
        registry.add(new ClassName(Role.class, uk.co.nickthecoder.itchy.role.MessageButton.class.getName()));
        registry.add(new ClassName(Role.class, uk.co.nickthecoder.itchy.role.NumberValue.class.getName()));
        registry.add(new ClassName(Role.class, uk.co.nickthecoder.itchy.role.TextValue.class.getName()));
        registry.add(new ClassName(Role.class, uk.co.nickthecoder.itchy.role.QuitButton.class.getName()));
        registry.add(new ClassName(Role.class, uk.co.nickthecoder.itchy.role.ProgressBar.class.getName()));
        registry.add(new ClassName(Role.class, uk.co.nickthecoder.itchy.role.SceneButton.class.getName()));

        registry.add(new ClassName(CostumeProperties.class, CostumeProperties.class.getName()));

        registry.add(new ClassName(Makeup.class, uk.co.nickthecoder.itchy.NullMakeup.class.getName()));
        registry.add(new ClassName(Makeup.class, uk.co.nickthecoder.itchy.makeup.Shadow.class.getName()));
        registry.add(new ClassName(Makeup.class, uk.co.nickthecoder.itchy.makeup.Scale.class.getName()));
        registry.add(new ClassName(Makeup.class, uk.co.nickthecoder.itchy.makeup.Textured.class.getName()));
        registry.add(new ClassName(Makeup.class, uk.co.nickthecoder.itchy.makeup.Frame.class.getName()));
        registry.add(new ClassName(Makeup.class, uk.co.nickthecoder.itchy.makeup.SimpleFrame.class.getName()));
        registry.add(new ClassName(Makeup.class, uk.co.nickthecoder.itchy.makeup.ScaledBackground.class.getName()));

        Eases.registerEases();

        Animations.registerAnimations();
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
        if ( loadingGame != null ) {
            return loadingGame;
        } else {
            return currentGame;
        }
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
                Video.setWindowIcon(filename);
            } else if (new File(filename32).exists()) {
                Video.setWindowIcon(filename32);
            }
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
        loadingGame = null;
        if (currentGame != null) {
            currentGame.onDeactivate();
            gameStack.push(currentGame);
        }
        currentGame = game;
        currentGame.onActivate();
        setScreenMode(currentGame);
    }

    public static void loadingGame( Game game )
    {
        loadingGame = game;
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
            if (e instanceof org.python.core.PySyntaxError) {
                for (StackTraceElement ste : e.getStackTrace()) {
                    if (ste.getClassName().startsWith("org.python")) {
                        // Ignored
                    } else {
                        System.err.println( ste );
                    }
                }
                System.err.println( "Python script exception!" );
            }
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

    public static void doRedraw()
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
