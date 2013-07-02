package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import uk.co.nickthecoder.itchy.gui.GuiPose;
import uk.co.nickthecoder.itchy.gui.Rules;
import uk.co.nickthecoder.jame.Audio;
import uk.co.nickthecoder.jame.Events;
import uk.co.nickthecoder.jame.JameException;
import uk.co.nickthecoder.jame.Keys;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.Video;
import uk.co.nickthecoder.jame.event.Event;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;
import uk.co.nickthecoder.jame.event.QuitEvent;

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

    /**
     * As sdljava hasn't implemented GetKeystate, I've had to implement it myself. This holds a
     * boolean for each key. On key pressed events sets the appropriate boolean, and key released
     * events reset the boolean. Uses the SDL keysym values to index the array.
     */
    private boolean[] keyboardState;

    /**
     * This is the highest SDL key sym which can be checked using isKeyDown(). The highest key sym
     * is currently 321, and I'm using 400, which leaves plenty of room for additional keys to be
     * added in the future.
     */
    private static int KEYBOARD_STATE_SIZE = 400;

    public Surface screen;

    private CompoundLayer rootLayer;

    private CompoundLayer gameLayer;

    private Rect rootRect;

    private boolean running;

    public static Itchy singleton = new Itchy();

    // private long defaultSurfaceFlags = SDLVideo.SDL_SWSURFACE; //
    // SDLVideo.SDL_HWSURFACE;

    private List<EventListener> eventListeners;

    private List<MouseListener> mouseListeners;

    private List<KeyListener> keyListeners;

    private MouseListener mouseOwner;

    private EventListener modalListener;

    private Game game;

    private ActorsLayer popupLayer;

    private final List<GuiPose> windows;

    private Rules rules;

    public int keyboardRepeatDelay = Events.DEFAULT_REPEAT_DELAY;

    public int keyboardRepeatInterval = Events.DEFAULT_REPEAT_INTERVAL;

    private int frames;

    private long framesStart;

    GameLoopJob gameLoopJob = new GameLoopJob();

    private Itchy()
    {
        this.windows = new ArrayList<GuiPose>();
    }

    public void init( Game game ) throws JameException
    {
        this.init(game, game.getWidth(), game.getHeight(), 32);
    }

    public void init( Game game, int width, int height ) throws JameException
    {
        this.init(game, width, height, 32);
    }

    public void init( Game game, int width, int height, int bpp ) throws JameException
    {
        while (this.windows.size() > 0) {
            this.windows.get(0).destroy();
        }
        this.eventListeners = new LinkedList<EventListener>();
        this.mouseListeners = new LinkedList<MouseListener>();
        this.keyListeners = new LinkedList<KeyListener>();

        if (this.rootLayer != null) {
            this.rootLayer.destroy();
        }

        this.game = game;
        this.keyboardState = new boolean[KEYBOARD_STATE_SIZE];

        Video.init();
        Audio.init();
        Audio.open();
        Events.enableKeyTranslation(true);

        if (game.getTitle() == null) {
            Video.setWindowTitle("Itchy");
        } else {
            Video.setWindowTitle(game.getTitle());
        }
        if (game.getIconFilename() != null) {
            Video.setWindowIcon(game.getIconFilename());
        }

        System.out.println("Itchy initialising screen " + width + "," + height);
        this.screen = Video.setMode(width, height);

        this.rootRect = new Rect(0, 0, width, height);
        this.rootLayer = new CompoundLayer(this.rootRect);
        this.gameLayer = new CompoundLayer(this.rootRect);
        this.rootLayer.add(this.gameLayer);

        this.popupLayer = new ScrollableLayer(this.rootRect, null, true);
        this.popupLayer.setVisible(true);
        this.rootLayer.add(this.popupLayer);
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

    public CompoundLayer getGameLayer()
    {
        return this.gameLayer;
    }

    /**
     * Removes all layers and kills all Actors on those layers
     */
    public void clear()
    {
        for (Actor actor : Actor.allByTag("active")) {
            actor.kill();
        }

        this.rootLayer.clear();

    }

    public void resetFrameRate()
    {
        this.frames = 0;
        this.framesStart = System.currentTimeMillis();
    }

    public void loop()
    {
        resetFrameRate();
        this.running = true;

        while (this.running) {

            try {
                long frameStart = System.currentTimeMillis();

                while (true) {
                    Event event = Events.poll();
                    if (event == null) {
                        break;
                    } else {
                        this.processEvent(event); // TODO make this a task.
                    }
                }

                this.gameLoopJob.start();

                if ( ! this.gameLoopJob.finished() ) {
                    System.err.println( "Itchy gameloopjob not finished" );
                }
                
                this.gameLoopJob.lock();
                try {
                    this.game.tick(); // TODO make this a task
                    this.rootLayer.render(this.rootRect, this.screen);
                } finally {
                    this.gameLoopJob.unlock();
                }

                this.screen.flip();
                this.frames++;

                long delay = 20 - (System.currentTimeMillis() - frameStart);
                if (delay > 1) {
                    Thread.sleep(delay);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } // loop
    }

    public void completeTasks()
    {
        while ( this.gameLoopJob.doTask() ) {
            // Do nothing
        }
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

    public double getFrameRate()
    {
        long now = System.currentTimeMillis();
        return ((this.frames)) / ((now - this.framesStart) / 1000.0);
    }

    private void processEvent( Event event )
    {
        if (event instanceof QuitEvent) {
            for (EventListener el : this.eventListeners) {
                if (el.onQuit()) {
                    return;
                }
            }
            this.terminate();
        }

        if (event instanceof KeyboardEvent) {
            KeyboardEvent ke = (KeyboardEvent) event;

            if (ke.isPressed()) {
                int key = ke.symbol;
                if ((key > 0) && (key < this.keyboardState.length)) {
                    this.keyboardState[key] = true;
                }

                if (this.modalListener == null) {

                    for (KeyListener listener : this.keyListeners) {
                        if (listener.onKeyDown(ke)) {
                            return;
                        }
                    }

                    for (EventListener el : this.eventListeners) {
                        if (el.onKeyDown(ke)) {
                            return;
                        }
                    }
                } else {
                    this.modalListener.onKeyDown(ke);
                    return;
                }

            } else if (ke.isReleased()) {

                int key = ke.symbol;
                if ((key > 0) && (key < this.keyboardState.length)) {
                    this.keyboardState[key] = false;
                }

                if (this.modalListener == null) {

                    for (KeyListener listener : this.keyListeners) {
                        if (listener.onKeyUp(ke)) {
                            return;
                        }
                    }

                    for (EventListener el : this.eventListeners) {
                        if (el.onKeyUp(ke)) {
                            return;
                        }
                    }
                } else {
                    this.modalListener.onKeyUp(ke);
                    return;
                }

            }
        }

        if (event instanceof MouseButtonEvent) {
            MouseButtonEvent mbe = (MouseButtonEvent) event;

            if (mbe.isPressed()) {

                if (this.modalListener == null) {
                    for (EventListener el : this.eventListeners) {
                        if (el.onMouseDown(mbe)) {
                            return;
                        }
                    }
                    for (MouseListener el : this.mouseListeners) {
                        if (el.onMouseDown(mbe)) {
                            return;
                        }
                    }
                } else {
                    this.modalListener.onMouseDown(mbe);
                    return;
                }

            }

            if (mbe.isReleased()) {

                if (this.mouseOwner == null) {

                    if (this.modalListener == null) {
                        for (EventListener el : this.eventListeners) {
                            if (el.onMouseUp(mbe)) {
                                return;
                            }
                        }
                        for (MouseListener el : this.mouseListeners) {
                            if (el.onMouseUp(mbe)) {
                                return;
                            }
                        }
                    } else {
                        this.modalListener.onMouseUp(mbe);
                        return;
                    }

                } else {
                    this.mouseOwner.onMouseUp(mbe);
                    return;
                }
            }

        }

        if (event instanceof MouseMotionEvent) {
            MouseMotionEvent mme = (MouseMotionEvent) event;
            if (this.mouseOwner == null) {

                if (this.modalListener == null) {
                    for (EventListener el : this.eventListeners) {
                        if (el.onMouseMove(mme)) {
                            return;
                        }
                    }
                    for (MouseListener el : this.mouseListeners) {
                        if (el.onMouseMove(mme)) {
                            return;
                        }
                    }
                } else {
                    this.modalListener.onMouseMove(mme);
                    return;
                }

            } else {
                this.mouseOwner.onMouseMove(mme);
                return;
            }

        }
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

    /**
     * Indicates that the main loop should end. Note the game does not end immediately, it only sets
     * a flag, which will cause the main loop to end after the current frame has been processed.
     */
    public void terminate()
    {
        this.running = false;
    }

    public void setModalListener( EventListener listener )
    {
        this.modalListener = listener;
    }

    public void addEventListener( EventListener listener )
    {
        this.eventListeners.add(listener);
    }

    public void removeEventListener( EventListener listener )
    {
        this.eventListeners.remove(listener);
    }

    public void addMouseListener( MouseListener listener )
    {
        this.mouseListeners.add(listener);
    }

    public void removeMouseListener( MouseListener listener )
    {
        this.mouseListeners.remove(listener);
    }

    public void addKeyListener( KeyListener listener )
    {
        this.keyListeners.add(listener);
    }

    public void removeKeyListener( KeyListener listener )
    {
        this.keyListeners.remove(listener);
    }

    public void captureMouse( EventListener owner )
    {
        assert (this.mouseOwner == null);
        this.mouseOwner = owner;
    }

    public void releaseMouse( EventListener owner )
    {
        assert (this.mouseOwner == owner);
        this.mouseOwner = null;
    }

    public Rules getRules()
    {
        return this.rules;
    }

    public void showWindow( GuiPose window )
    {
        this.windows.add(window);

        if (window.getRules() == null) {
            window.setRules(this.rules);
        }
        window.reStyle();
        window.forceLayout();
        window.setPosition(0, 0, window.getRequiredWidth(), window.getRequiredHeight());

        Actor actor = window.getActor();

        actor.moveTo(Math.max(0, (this.popupLayer.position.width - window.getRequiredWidth()) / 2),
                Math.max(0, (this.popupLayer.position.height - window.getRequiredHeight()) / 2));

        this.popupLayer.add(actor);

        if (window.modal) {
            this.setModalListener(window);
        }
        this.addEventListener(window);

    }

    public void hideWindow( GuiPose window )
    {
        this.removeEventListener(window);
        this.popupLayer.remove(window.getActor());

        this.windows.remove(window);

        if (window.modal) {
            if (this.windows.size() > 0) {
                GuiPose topWindow = this.windows.get(this.windows.size() - 1);
                if (topWindow.modal) {
                    this.setModalListener(topWindow);

                } else {
                    this.setModalListener(null);
                }
            } else {
                this.setModalListener(null);
            }
        }

    }

}
