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

import uk.co.nickthecoder.itchy.gui.GuiPose;
import uk.co.nickthecoder.itchy.util.AutoFlushPreferences;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.event.Event;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;
import uk.co.nickthecoder.jame.event.QuitEvent;

public abstract class Game extends Task implements EventListener, MessageListener
{
    public Resources resources = new Resources();

    private boolean ticking = false;

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

    public Game( String title, int width, int height ) throws Exception
    {
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

        Itchy.singleton.init(this);
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

    public void render( Surface screen )
    {
        this.layers.render(this.screenRect, screen);
        this.popupLayer.render(this.screenRect, screen);
    }

    public void processEvent( Event event )
    {
        if (event instanceof QuitEvent) {
            for (EventListener el : this.eventListeners) {
                if (el.onQuit()) {
                    return;
                }
            }
            Itchy.singleton.terminate();
        }

        if (event instanceof KeyboardEvent) {
            KeyboardEvent ke = (KeyboardEvent) event;

            if (ke.isPressed()) {

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
        Itchy.singleton.terminate();
        return true;
    }

    /**
     * Called when a button is pressed. Most games don't use onKeyDown or onKeyUp during game play,
     * instead, each Actor uses : Itchy.singleton.isKeyDown( ... ). onKeyDown and onKeyUp are useful
     * for typing.
     */
    @Override
    public boolean onKeyDown( KeyboardEvent ke )
    {
        return false;
    }

    /**
     * Called when a button is pressed. Most games don't use onKeyDown or onKeyUp during game play,
     * instead, each Actor uses : Itchy.singleton.isKeyDown( ... ). onKeyDown and onKeyUp are useful
     * for typing.
     */
    @Override
    public boolean onKeyUp( KeyboardEvent ke )
    {
        return false;
    }

    @Override
    public boolean onMouseDown( MouseButtonEvent mbe )
    {
        return false;
    }

    @Override
    public boolean onMouseUp( MouseButtonEvent mbe )
    {
        return false;
    }

    @Override
    public boolean onMouseMove( MouseMotionEvent mbe )
    {
        return false;
    }

    /**
     * Override this method to run code once per frame.
     */
    public void tick()
    {
    }

    @Override
    public void onMessage( String message )
    {
    }

    public abstract void init();

    /**
     * Typically, this is called immediately after you have created your Game object, usually in the
     * "main" method.
     * 
     * Do NOT override this method, if you need to do one-time initialisation, then do it in the
     * init method. This will ensure that everything gets initialised in the correct order.
     */
    public void start()
    {
        Itchy.singleton.startGame(this);
        this.init();
        Itchy.singleton.mainLoop();
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
        Itchy.singleton.endGame();
    }

    public void showWindow( GuiPose window )
    {
        this.windows.add(window);

        if (window.getRules() == null) {
            window.setRules(Itchy.singleton.rules);
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

    /**
     * Part of the Task interface, and simply calls 'tick'. Your subclass should override tick, and
     * do nothing with 'run'.
     */
    @Override
    public void run()
    {
        if (this.ticking) {
            return;
        }

        try {
            this.ticking = true;
            this.tick();
        } finally {
            this.ticking = false;
        }
    }

}
