/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.Preferences;

import uk.co.nickthecoder.itchy.editor.Editor;
import uk.co.nickthecoder.itchy.extras.Pause;
import uk.co.nickthecoder.itchy.gui.GuiPose;
import uk.co.nickthecoder.itchy.gui.Rules;
import uk.co.nickthecoder.itchy.util.AutoFlushPreferences;
import uk.co.nickthecoder.jame.Keys;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.event.Event;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;
import uk.co.nickthecoder.jame.event.QuitEvent;

public abstract class Game implements EventListener, MessageListener
{
    public Resources resources = new Resources();

    private AutoFlushPreferences preferences;

    protected CompoundLayer layers;

    protected ActorsLayer popupLayer;

    protected String title;

    protected Rect screenRect;

    protected List<EventListener> eventListeners;

    protected List<MouseListener> mouseListeners;

    protected List<KeyListener> keyListeners;

    private MouseListener mouseOwner;

    private EventListener modalListener;

    private Focusable keyboardFocus;

    private final List<GuiPose> windows;

    public SceneBehaviour currentSceneBehaviour = new NullSceneBehaviour();

    private String sceneName;

    protected boolean testing;

    private boolean initialised = false;

    private String preTestSceneName;

    public Pause pause;

    private Rules rules;
    
    public Game( String title, int width, int height ) throws Exception
    {
        this.pause = new Pause(this);
        this.title = title;
        this.screenRect = new Rect(0, 0, width, height);

        this.layers = new CompoundLayer("game", this.screenRect);

        this.popupLayer = new ScrollableLayer("popup", this.screenRect);
        this.popupLayer.setYAxisPointsDown(true);
        this.popupLayer.setVisible(true);

        this.eventListeners = new LinkedList<EventListener>();
        this.mouseListeners = new LinkedList<MouseListener>();
        this.keyListeners = new LinkedList<KeyListener>();

        this.windows = new ArrayList<GuiPose>();

        this.addEventListener(this);

        Itchy.init(this);
    }

    /**
     * Typically, this is called immediately after you have created your Game object, usually in the
     * "main" method.
     * 
     * Do NOT override this method, if you need to do one-time initialisation, then do it in the
     * init method. This will ensure that everything gets initialised in the correct order.
     */
    public void start()
    {
        start(getInitialSceneName());
    }

    public void start( String sceneName )
    {
        Itchy.startGame(this);
        if (!this.initialised) {
            init();
            this.initialised = true;
        }

        if (sceneName != null) {
            this.layers.clear();
            this.layers.reset();
            loadScene(sceneName);
        }

        Itchy.mainLoop();
    }

    public abstract void init();

    public abstract String getInitialSceneName();

    public void testScene( String sceneName )
    {
        try {
            this.preTestSceneName = this.sceneName;
            this.testing = true;

            start(sceneName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void endTest()
    {
        System.out.println("Ending the test");
        this.testing = false;
        this.layers.clear();
        this.layers.reset();
        if (this.preTestSceneName != null) {
            loadScene(this.preTestSceneName);
        }
        Itchy.endGame();
    }

    public AutoFlushPreferences getPreferences()
    {
        if (this.preferences == null) {
            this.preferences = new AutoFlushPreferences(Preferences.userNodeForPackage(this
                .getClass()));
        }
        return this.preferences;
    }

    public CompoundLayer getLayers()
    {
        return this.layers;
    }

    public ActorsLayer getPopupLayer()
    {
        return this.popupLayer;
    }

    public void render( Surface screen )
    {
        this.layers.render(this.screenRect, screen);
        this.popupLayer.render(this.screenRect, screen);
    }

    /**
     * Use this to time actual game play, which will exclude time while the game is paused. A single
     * value from gameTimeMillis is meaningless, it only has meaning when one value is subtracted
     * from a later value (which will give the number of milliseconds of game time.
     */
    public long gameTimeMillis()
    {
        if (this.pause.isPaused()) {
            return this.pause.pauseTimeMillis();
        } else {
            return System.currentTimeMillis() - this.pause.totalTimePausedMillis();
        }
    }

    public void processEvent( Event event )
    {
        if (event instanceof QuitEvent) {
            for (EventListener el : this.eventListeners) {
                if (el.onQuit()) {
                    return;
                }
            }
            Itchy.terminate();
        }

        if (event instanceof KeyboardEvent) {
            KeyboardEvent ke = (KeyboardEvent) event;

            if (ke.isPressed()) {

                if (this.testing) {
                    if ((ke.symbol == Keys.ESCAPE) || (ke.symbol == Keys.F12)) {
                        endTest();
                        return;
                    }
                }

                if (this.keyboardFocus != null) {
                    if (this.keyboardFocus.onKeyDown(ke)) {
                        return;
                    }
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

    public void setModalListener( EventListener listener )
    {
        this.modalListener = listener;
    }

    /**
     * Used when a single entity believes it deserves first priority to all key strokes. In
     * particular, this is used by GUI components, which accept keyboard input when they have the
     * focus.
     */
    public void setFocus( Focusable focus )
    {
        this.keyboardFocus = focus;
    }

    public int getWidth()
    {
        return this.screenRect.width;
    }

    public int getHeight()
    {
        return this.screenRect.height;
    }

    public String getTitle()
    {
        return this.title;
    }

    public String getIconFilename()
    {
        return this.resources.resolveFilename("icon.bmp");
    }

    @Override
    public boolean onQuit()
    {
        Itchy.terminate();
        return true;
    }

    /**
     * Called when a button is pressed. Most games don't use onKeyDown or onKeyUp during game play,
     * instead, each Actor uses : Itchy.isKeyDown( ... ). onKeyDown and onKeyUp are useful
     * for typing.
     */
    @Override
    public boolean onKeyDown( KeyboardEvent ke )
    {
        return this.currentSceneBehaviour.onKeyDown(ke);
    }

    /**
     * Called when a button is pressed. Most games don't use onKeyDown or onKeyUp during game play,
     * instead, each Actor uses : Itchy.isKeyDown( ... ). onKeyDown and onKeyUp are useful
     * for typing.
     */
    @Override
    public boolean onKeyUp( KeyboardEvent ke )
    {
        return this.currentSceneBehaviour.onKeyUp(ke);
    }

    @Override
    public boolean onMouseDown( MouseButtonEvent mbe )
    {
        return this.currentSceneBehaviour.onMouseDown(mbe);
    }

    @Override
    public boolean onMouseUp( MouseButtonEvent mbe )
    {
        return this.currentSceneBehaviour.onMouseDown(mbe);
    }

    @Override
    public boolean onMouseMove( MouseMotionEvent mbe )
    {
        return this.currentSceneBehaviour.onMouseMove(mbe);
    }

    /**
     * Override this method to run code once per frame.
     */
    public void tick()
    {
        this.currentSceneBehaviour.tick();
    }

    @Override
    public void onMessage( String message )
    {
        this.currentSceneBehaviour.onMessage(message);
    }

    public boolean loadScene( String sceneName )
    {
        try {
            Scene scene = this.resources.getScene(sceneName);
            if (scene == null) {
                System.err.println("Scene not found : " + sceneName);
                return false;
            }

            this.currentSceneBehaviour = scene.createSceneBehaviour();
            scene.create(this.layers, false);
            Itchy.showMousePointer(scene.showMouse);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        this.sceneName = sceneName;
        return true;
    }

    public String getSceneName()
    {
        return this.sceneName;
    }

    /**
     * Called to end the game, which will usually end the whole program. The program will not end,
     * if one Game creates and runs another Game, and then this second Game ends, in this case, the
     * first Game will take back control, and the program will continue. Note that the Editor is a
     * Game, so when the Editor ends, this is exactly what is happens.
     * 
     * You may override end, as long as you call super.end() at the end of your overridden method.
     */
    public void end()
    {
        Itchy.endGame();
    }

    public void setRules( Rules rules )
    {
        this.rules = rules;
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
        window.focus();
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

    public void startEditor()
    {
        try {
            Editor editor = new Editor(this);
            editor.start(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startEditor( String designSceneName )
    {
        try {
            Editor editor = new Editor(this);
            editor.designScene(designSceneName);
            editor.start(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void runFromMain( String[] argv ) throws Exception
    {
        if ((argv.length == 1) && ("--editor".equals(argv[0]))) {

            Editor editor = new Editor(this);
            editor.start(null);

        } else {
            this.start(getInitialSceneName());
        }
    }
}
