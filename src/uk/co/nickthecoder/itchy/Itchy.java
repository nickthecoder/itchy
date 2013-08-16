/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;


import java.util.Stack;

import uk.co.nickthecoder.itchy.gui.Rules;
import uk.co.nickthecoder.jame.Audio;
import uk.co.nickthecoder.jame.Events;
import uk.co.nickthecoder.jame.Keys;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.Video;
import uk.co.nickthecoder.jame.event.Event;
import uk.co.nickthecoder.jame.event.KeyboardEvent;

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
 * 
 * @author nick
 * 
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

    public static Itchy singleton = new Itchy();

    /**
     * Holds a boolean for each key.
     * On key pressed events sets the appropriate boolean, and key released
     * events reset the boolean. Uses the Keys values to index the array.
     */
    private boolean[] keyboardState;

    public Surface screen;
    
    private boolean running;

    private Game game;

    /**
     * If one game calls another game, and then exists, this is how we return to the previous game.
     */
    private Stack<Game> gameStack = new Stack<Game>();


    public Rules rules;

    public int keyboardRepeatDelay = Events.DEFAULT_REPEAT_DELAY;

    public int keyboardRepeatInterval = Events.DEFAULT_REPEAT_INTERVAL;

    public FrameRate frameRate = createFrameRate();

    public final SoundManager soundManager = new SoundManager();
    
    GameLoopJob gameLoopJob = new GameLoopJob();

    private Itchy()
    {
    }
    
    public void init(Game game) throws Exception
    {
        Video.init();
        Audio.init();
        Audio.open();
        Events.enableKeyTranslation(true);
        
        this.keyboardState = new boolean[KEYBOARD_STATE_SIZE];
        
        setScreenMode( game );
    }

    public Game getGame()
    {
        return this.game;
    }

    public Resources getResources()
    {
        return this.game.resources;
    }

    public void setGuiRules( Rules rules )
    {
        this.rules = rules;
    }

    private void setScreenMode(Game game)
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
            this.screen = Video.setMode(game.getWidth(), game.getHeight());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    public void startGame( Game game )
    {
        if (this.game!=null) {
            this.gameStack.push(this.game);
        }
        this.game = game;
        setScreenMode(game);
    }

    public void mainLoop()
    {
        try {
            if (!this.running) {
                this.running = true;
                this.frameRate.loop();
            }
        } catch (Exception e) {
            System.err.println("Failed to initialise game");
            e.printStackTrace();
        }
    }

    public void endGame()
    {
        soundManager.stopAll();
        
        if (this.gameStack.isEmpty()) {
            this.terminate();
        } else {
            this.game = this.gameStack.pop();
            try {
                setScreenMode(this.game);
            }  catch (Exception e) {
                e.printStackTrace();
            }
            mainLoop();
        }
    }

    /**
     * Indicates that the main loop should end. Note the game does not end immediately, it only sets
     * a flag, which will cause the main loop to end after the current frame has been processed.
     */
    public void terminate()
    {
        System.out.println("Terminating itchy");
        this.running = false;
    }

    private FrameRate createFrameRate()
    {
        return new FrameRate() {

            @Override
            public boolean isRunning()
            {
                return Itchy.this.running;
            }

            @Override
            public void doGameLogic()
            {
                Itchy.this.doGameLogic();
            }

            @Override
            public void doRedraw()
            {
                Itchy.this.doRedraw();
            }
        };
    }

    private void doGameLogic()
    {
        while (true) {
            Event event = Events.poll();
            if (event == null) {
                break;
            } else {
                this.queueEvent(event);
            }
        }

        this.soundManager.tick();
        this.gameLoopJob.start();

    }

    private void queueEvent( final Event event )
    {
        this.gameLoopJob.add(new Task() {

            @Override
            public void run()
            {
                Itchy.this.processEvent(event);
            }
        });
    }

    private void doRedraw()
    {
        this.gameLoopJob.lock();
        try {
            this.game.render( this.screen );
            this.screen.flip();
        } finally {
            this.gameLoopJob.unlock();
        }
    }

    public void completeTasks()
    {
        this.gameLoopJob.completeTasks();
    }

    public void addTask( Task task )
    {
        this.gameLoopJob.add(task);
    }

    public void endOfFrame()
    {
    }

    public boolean isRunning()
    {
        return this.running;
    }

    public void enableKeyboardRepeat( boolean value )
    {
        if (value) {
            Events.keyboardRepeat(this.keyboardRepeatDelay, this.keyboardRepeatInterval);
        } else {
            Events.keyboardRepeat(0, 0);
        }
    }

    public boolean isKeyDown( int keySym )
    {
        return this.keyboardState[keySym];
    }

    /**
     * Tests state of either shift keys A convenience method, the same as isKeyDown( Keys.LSHIFT )
     * || isKeyDown( Keys.RSHIFT )
     */
    public boolean isShiftDown()
    {
        return this.keyboardState[Keys.LSHIFT] || this.keyboardState[Keys.RSHIFT];
    }

    /**
     * Tests state of either control keys A convenience method, the same as isKeyDown( Keys.LCTRL )
     * || isKeyDown( Keys.RCTRL )
     */
    public boolean isCtrlDown()
    {
        return this.keyboardState[Keys.LCTRL] || this.keyboardState[Keys.RCTRL];
    }

    /**
     * Tests state of either meta keys A convenience method, the same as isKeyDown( Keys.LMETA ) ||
     * isKeyDown( Keys.RMETA )
     */
    public boolean isMetaDown()
    {
        return this.keyboardState[Keys.LMETA] || this.keyboardState[Keys.RMETA];
    }

    /**
     * Tests state of either super keys A convenience method, the same as isKeyDown( Keys.LSUPER )
     * || isKeyDown( Keys.RSUPER )
     */
    public boolean isSuperDown()
    {
        return this.keyboardState[Keys.LSUPER] || this.keyboardState[Keys.RSUPER];
    }


    private void processEvent( Event event )
    {
        this.game.processEvent( event );
        
        if (event instanceof KeyboardEvent) {
            KeyboardEvent ke = (KeyboardEvent) event;

            if (ke.isPressed()) {
                int key = ke.symbol;
                if ((key > 0) && (key < this.keyboardState.length)) {
                    this.keyboardState[key] = true;
                }
            
            } else if (ke.isReleased()) {
    
                int key = ke.symbol;
                if ((key > 0) && (key < this.keyboardState.length)) {
                    this.keyboardState[key] = false;
                }
            }
        }
    }
    

    public Rules getRules()
    {
        return this.rules;
    }



    public void debug()
    {
    }
}
