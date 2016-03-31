/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import uk.co.nickthecoder.itchy.editor.Editor;
import uk.co.nickthecoder.itchy.editor.SceneDesigner;
import uk.co.nickthecoder.itchy.extras.SceneTransition;
import uk.co.nickthecoder.itchy.extras.SimpleMouse;
import uk.co.nickthecoder.itchy.gui.GuiView;
import uk.co.nickthecoder.itchy.gui.RootContainer;
import uk.co.nickthecoder.itchy.gui.Stylesheet;
import uk.co.nickthecoder.itchy.script.ScriptManager;
import uk.co.nickthecoder.itchy.util.AutoFlushPreferences;
import uk.co.nickthecoder.itchy.util.StringUtils;
import uk.co.nickthecoder.itchy.util.TagCollection;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.event.Event;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.Keys;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;
import uk.co.nickthecoder.jame.event.QuitEvent;
import uk.co.nickthecoder.jame.event.ResizeEvent;
import uk.co.nickthecoder.jame.event.WindowEvent;

/**
 * Game is the core class, from which everything else hangs. You can access the currently running game at any time by
 * calling {@link Itchy#getGame()}.
 * As a games designer, you do not create a Game object directly, it is created automatically when the Game's
 * {@link Resources} are loaded. Loading the Resources also creates a {@link Director}, which is where some of your
 * game's code sits.
 * <p>
 * It isn't until a {@link Scene} is loaded, that the Game is ready for action. Loading a Scene, creates a
 * {@link Layout}, which is made up of {@link Layers}, and some Layers will have {@link Stage}s, and on these Stages are
 * {@link Actor}s. Each Actor has a {@link Role}, and Roles are were the vast majority of your game should be written.
 * <p>
 * Loading the Scene will also set up a {@link SceneDirector}, another place for your game code.
 * <p>
 * Once the Scene is loaded, the main "game loop" runs. Each time round the loop is a single frame which is 1/60th of a
 * second, also known as one "tick". Each tick calls {@link Director#tick()}, {@link SceneDirector#tick()} and for every
 * Actor, {@link Role#tick()}.
 * <p>
 * It now time to redraw the whole screen. This is done through {@link View}s. You can think of Views as a stack of
 * transparent plastic sheets stacked one on top of another. Each view may have only a small part of the final picture.
 * You should not be able to see straight through any part of this stack of Views, and the easiest way to do this is for
 * the bottom most layer to be fully opaque. It can either be a single solid colour (using the {@link RGBAView}), or be
 * an image as big as the screen.
 * <p>
 * The game loop, the ticks and the rendering (drawing) or the Views are all done automatically (so you don't need to
 * write any code to do these things).
 */
public class Game
{
    /**
     * Holds all of the images, sounds, fonts, animations costumes etc used by this game.
     */
    public final Resources resources;

    // NOTE, this can be private if/when Editor is no longer a subclass of Game.
    /**
     * The Layout for the current Scene. Set each time a Scene is loaded.
     */
    protected Layout layout;

    /**
     * Holds the tag information for all of the Actors used within this game. This isn't typically used directly, but
     * instead used via {@link AbstractRole#findRolesByTag(String)}, {@link AbstractRole#hasTag(String)},
     * {@link AbstractRole#addTag(String)} and {@link AbstractRole#removeTag(String)}.
     * 
     * @priority 5
     */
    public final TagCollection<Role> roleTags = new TagCollection<Role>();

    /**
     * Helps to implement a pause feature within your game. Typically, you will pause/un-pause from your game's
     * {@link SceneDirector#onKeyDown(KeyboardEvent)}
     */
    public Pause pause;

    /**
     * Controls how the mouse pointer appears while inside the game window. It could be hidden (useful for games which
     * don't use a mouse),
     * appear as a regular mouse pointer, or look a bit more flashy. The mouse is given special status, as it always
     * receives mouse events, even if something else has 'captured' the mouse.
     */
    public Mouse mouse = new SimpleMouse();

    /**
     * The overall controller of the game, but ironically, it often has very little to do, as most of the game logic is
     * inside each Actor's {@link Role}. Also, each scene can have its own {@link SceneDirector} which controls a single
     * scene. This leaves very little for Director to do in most cases.
     * <p>
     * The director is set from the "Info" tab in the {@link Editor}.
     */
    private Director director;

    /**
     * Holds data about this game that is stored for use the next time the game is run. For example, it can be used to
     * store high scores.
     */
    private AutoFlushPreferences preferences;

    /**
     * A special stage which is at the front of the z-order (i.e. always visible).
     * It can be used for generic tools, not specific to a game, for example, it can be use to display a "Pause"
     * message.
     */
    private ZOrderStage glassStage;

    /**
     * The view for the {@link #glassStage}
     */
    private StageView glassView;

    /**
     * The list of all of the windows currently shown.
     */
    private List<GuiView> windows;

    /**
     * A list of all the objects that need to know when a mouse is clicked or moved.
     */
    private List<MouseListener> mouseListeners;

    /**
     * A list of all the objects that need to know when a key is pressed.
     */
    private List<KeyListener> keyListeners;

    /**
     * The mouse can be captured for a period of time, which ensures mouse events are only sent to itself. For example,
     * if you are dragging
     * an object, you may not want any other objects from receiving the mouse-move event.
     */
    private MouseListener mouseOwner;

    /**
     * The modalListener is give all mouse and keyboard events, and no other listeners hear about them. This is used for
     * a "modal" popup
     * window. i.e. a window which takes over the whole game, and doesn't let the user do anything else until it is
     * dismissed.
     */
    private InputListener modalListener;

    /**
     * Which component has the focus - used within the GUI subsystem to highlight a single gui component.
     */
    private Focusable keyboardFocus;

    /**
     * The current SceneDirector, which was created during {@link #loadScene(String)}
     */
    private SceneDirector sceneDirector;

    /**
     * The name of the current scene, i.e. the last one started using {@link #startScene(String)}
     */
    private String sceneName;

    /**
     * Set when the game is run from the {@link SceneDesigner}.
     */
    private boolean testing;

    /**
     * Set when the game is running, both normally and when testing a scene from the scene designer.
     */
    protected boolean running = false;

    /**
     * Defines how GUI components look, such as the images/textures used for the controls, their margins etc.
     */
    private Stylesheet stylesheet;

    /**
     * As a game designer, you don't need to know HOW your scripts are compiled and executed, ScriptManager takes care
     * of that for
     * you. Game writers can safely ignore this.
     */
    public final ScriptManager scriptManager;

    /**
     * Game constructor called when the resources are being loaded. Note that not much initialisation can take place
     * yet, as vital
     * information is missing. For example, the games width and height are unknown. The {@link #init()} method will be
     * called later, to
     * complete the initialisation.
     * 
     * @param resources
     *            The resources being loaded. Note, the resources will NOT be fully loaded yet.
     * @priority 3
     */
    public Game(Resources resources)
    {
        if ((!(this instanceof Editor)) && (resources.game != null)) {
            throw new RuntimeException("Attempted to create more than one game sharing a single resources file");
        }
        this.resources = resources;
        this.scriptManager = resources.scriptManager;

        this.mouseListeners = new LinkedList<MouseListener>();
        this.keyListeners = new LinkedList<KeyListener>();

        // Sensible defaults, which can be replaced in the init() method.
        this.sceneDirector = new PlainSceneDirector();
        this.pause = new SimplePause();
    }

    /**
     * Called just once, soon after the Game is created. Its called as soon as the GameInfo has been read from the
     * resources file. This is
     * where most of Game's initialisation occurs; it couldn't take place in Game's constructor, because vital
     * information wasn't available
     * at that time (e.g. the game's width and height weren't known then.
     * 
     * @priority 3
     */
    public void init()
    {
        Rect displayRect = new Rect(0, 0, getWidth(), getHeight());

        this.windows = new ArrayList<GuiView>();

        this.glassStage = new ZOrderStage();
        this.glassView = new StageView(displayRect, this.glassStage);
        this.glassView.enableMouseListener(this);
    }

    /**
     * 
     * @return
     * @priority 3
     */
    public FrameRate getFrameRate()
    {
        return Itchy.getFrameRate();
    }

    /**
     * Called only once, after the resources have been loaded. Do not change directors during the game. Instead, if you
     * want different
     * behaviour for different parts of the game, then use different SceneDirectors to code each part of the game.
     * 
     * @param director
     * @priority 3
     */
    public void setDirector(Director director)
    {
        this.director = director;
        this.director.attach(this);

        this.addMouseListener(this.director);
        this.addKeyListener(this.director);
    }

    /**
     * A simple getter.
     * 
     * @return
     */
    public Director getDirector()
    {
        return this.director;
    }

    /**
     * 
     * @return
     * @priority 5
     */
    public List<Stage> getStages()
    {
        List<Stage> result = new ArrayList<Stage>();
        if (this.layout != null) {
            for (Layer layer : this.layout.getLayersByZOrder()) {
                Stage stage = layer.getStage();
                if (stage != null) {
                    if (!result.contains(stage)) {
                        result.add(stage);
                    }
                }
            }
        }

        return result;
    }

    /**
     * Used internally by Itchy.
     * 
     * @return
     * @priority 5
     */
    public ScriptManager getScriptManager()
    {
        return this.scriptManager;
    }

    /**
     * Used internally by Itchy.
     * Called soon after a Game is created and also when a Game creates another Game, and the second Game ends
     * (reactivating the first one).
     * 
     * @priority 5
     */
    public void onActivate()
    {
        this.mouse.onActivate();
        this.director.onActivate();
    }

    /**
     * Called when a Game gives starts another Game.
     * 
     * @priority 5
     */
    public void onDeactivate()
    {
        this.director.onDeactivate();
    }

    /**
     * Called when the game window is resized. {@link AbstractDirector#onResize(ResizeEvent)} take care of resizable
     * windows, so there is no need for your code to call this.
     * 
     * @param width
     *            The new width of the window's client area (excluding the windows borders).
     * @param height
     *            The new height of the window's client area (excluding the window boarders and title bar).
     * @priority 3
     */
    public void resize(int width, int height)
    {
        Itchy.resizeScreen(width, height);
    }

    /**
     * Kills all Actors on all Stages. It is unlikely you will need this, because loading a new Scene calls clear
     * automatically.
     * 
     * @priority 5
     */
    public void clear()
    {
        for (Stage stage : this.getStages()) {
            stage.clear();
        }

        this.glassStage.clear();
    }

    /**
     * Typically, this is called just after the Game object is created, usually from the "main" method.
     * 
     * @priority 5
     */
    public void start()
    {
        Itchy.startGame(this);
        this.director.onStarted();
        if (!StringUtils.isBlank(this.resources.getGameInfo().initialScene)) {
            this.startScene(this.resources.getGameInfo().initialScene);
        }
        running = true;
        Itchy.mainLoop();
    }

    /**
     * The same as {@link #start()}, but named scene is started instead of the default scene. This can be useful during
     * development to jump
     * to a particular scene.
     * 
     * @param sceneName
     * @priority 5
     */
    public void start(String sceneName)
    {
        Itchy.startGame(this);
        this.director.onStarted();

        if (!StringUtils.isBlank(sceneName)) {
            clear();
            this.startScene(sceneName);
        }
        running = true;
        Itchy.mainLoop();
    }

    /**
     * @return false if this game has ended, or if another game has been started.
     * @priority 3
     */
    public boolean isRunning()
    {
        return this.running;
    }

    /**
     * While it is possible to start the editor while your game is running, it is currently not recommended, because
     * weird things can happen if you edit the game while it is still running.
     * 
     * @priority 3
     */
    public void startEditor()
    {
        // TODO, check if director.onStarted has been called?
        try {
            Editor editor = new Editor(this);
            editor.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * While it is possible to start the editor while your game is running, it is currently not recommended, because
     * weird things can happen if you edit the game while it is still running.
     * 
     * @priority 3
     */
    public void startEditor(String designSceneName)
    {
        // TODO, check if director.onStarted has been called?
        try {
            Editor editor = new Editor(this);
            editor.start(designSceneName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Called by the SceneDesigner, to test a single scene. Essentially the same as {@link #start(String)}, but also
     * sets the boolean <code>testing</code>, so that the game knows its in testing mode.
     * 
     * @param sceneName
     *            The name of the scene to play.
     * @priority 5
     */
    public void testScene(String sceneName)
    {
        try {
            System.err.println("Starting Test " + this + " scene " + sceneName);
            this.testing = true;
            start(sceneName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * The flip side of {@link #testScene(String)}, will return to the scene designer.
     * 
     * Very similar to {@link #end()}.
     * 
     * @priority 5
     */
    public void endTest()
    {
        clear();
        end();
        this.testing = false;
        System.err.println("Ended Test");
    }

    /**
     * Preferences contain permanently stored data (i.e. its still there when you quit the game and restart it days
     * later). The data is
     * stored in a hierarchy of nodes. Each node has a set of name/value pairs, just like HashMap. Each node can also
     * have named sub-nodes
     * (which gives it the hierarchical structure).
     * 
     * @return The top level node preferences for this game. The path of this node is determined by
     *         {@link Director#getPreferenceNode()}.
     */
    public AutoFlushPreferences getPreferences()
    {
        if (this.preferences == null) {
            this.preferences = new AutoFlushPreferences(this.director.getPreferenceNode());
        }
        return this.preferences;
    }

    /**
     * Returns a lists of all the Roles tagged with a given tag.
     * 
     * @param tag
     *            The tag to search for.
     * @return A set of Roles meeting the criteria. An empty set if no roles meet the criteria.
     * @see AbstractRole#addTag(String)
     */
    public Set<Role> findRolesByTag(String tag)
    {
        return this.roleTags.getTagMembers(tag);
    }

    Map<String, Actor> actorsById = new WeakHashMap<String, Actor>();

    /**
     * Looks for an Actor with the given ID. Note that there is nothing to enforce Actor's IDs to be unique. Looking for
     * a non-unique ID will return just one Actor (which one is not defined). If you want to find a set of Actors, use
     * tags and {@link #findRolesByTag(String)}.
     * 
     * @param id
     * @return An Actor
     */
    public Actor findActorById(String id)
    {
        return actorsById.get(id);
    }

    /**
     * The glass stage is used when you want something that absolutely must be above the regular game views. For
     * example, you could use this
     * to add some debugging information, such as the frames per second. You shouldn't use it for regular actors.
     * 
     * @return The glass stage, who's view is draw above all of the game's regular views.
     * @priority 3
     */
    public ZOrderStage getGlassStage()
    {
        return this.glassStage;
    }

    /**
     * The glass view is drawn above all of the game's regular views. It is a view of the stage returned by
     * {@link #getGlassStage()}.
     * 
     * @priority 3
     */
    public StageView getGlassView()
    {
        return this.glassView;
    }

    /**
     * Most games have no use for {@link GuiView}s.
     * 
     * @return
     * @priority 2
     */
    public List<GuiView> getGUIViews()
    {
        return this.windows;
    }

    /**
     * Renders all of the views to the display surface.
     * 
     * Itchy will call this from inside the game loop, so there is normally no need for you to call it explicitly.
     * However, you may call
     * this with a surface of your own creation to obtain a screenshot of the game. {@link SceneTransition} takes
     * screenshots in this manner,
     * which are then slid, or faded from view.
     * 
     * @param display
     *            The surface to draw onto. This will normally be the display surface, but can be any surface WITHOUT an
     *            alpha channel.
     * @priority 3
     */
    public void render(Surface display)
    {
        GraphicsContext gc = new SurfaceGraphicsContext(display);
        if (this.layout != null) {
            for (Layer layer : this.layout.getLayersByZOrder()) {
                View view = layer.getView();
                view.render(view.adjustGraphicsContext(gc));
            }
        }
        for (GuiView window : this.windows) {
            window.render(window.adjustGraphicsContext(gc));
        }
        this.glassView.render(this.glassView.adjustGraphicsContext(gc));
    }

    /**
     * Use this to time actual game play, which will exclude time while the game is paused. A single value from
     * gameTimeMillis is meaningless, it only has meaning when one value is subtracted from a later value (which will
     * give the number of milliseconds of game elapse time.
     * <p>
     * To time events, it is usually easier to use {@link uk.co.nickthecoder.itchy.extras.Timer}, rather than using this
     * method directly.
     * 
     * @priority 3
     */
    public long gameTimeMillis()
    {
        if (this.pause.isPaused()) {
            return this.pause.pauseTimeMillis();
        } else {
            return System.currentTimeMillis() - this.pause.totalTimePausedMillis();
        }
    }

    /**
     * Itchy passes every event through the current game's processEvent method. You should NOT call this method
     * directly.
     * 
     * @param event
     *            The event that needs to be handled.
     * @priority 5
     */
    void processEvent(Event event)
    {
        if (event instanceof QuitEvent) {
            this.director.onQuit((QuitEvent) event);
            // Itchy won't terminate if the director calls event.stopPropagation().
            Itchy.terminate();

        } else if (event instanceof ResizeEvent) {
            ResizeEvent re = (ResizeEvent) event;
            this.director.onResize(re);

        } else if (event instanceof KeyboardEvent) {
            KeyboardEvent ke = (KeyboardEvent) event;

            if (ke.isPressed()) {

                if (this.testing) {
                    if ((ke.symbol == Keys.ESCAPE) || (ke.symbol == Keys.F12)) {
                        endTest();
                        event.stopPropagation();
                    }
                }

                if (this.keyboardFocus != null) {
                    this.keyboardFocus.onKeyDown(ke);
                }

                if (this.modalListener == null) {

                    for (KeyListener listener : this.keyListeners) {
                        listener.onKeyDown(ke);
                    }

                } else {
                    this.modalListener.onKeyDown(ke);
                    return;
                }

            } else if (ke.isReleased()) {

                if (this.modalListener == null) {

                    for (KeyListener listener : this.keyListeners) {
                        listener.onKeyUp(ke);
                    }

                } else {
                    this.modalListener.onKeyUp(ke);
                    return;
                }
            }

        } else if (event instanceof MouseButtonEvent) {

            MouseButtonEvent mbe = (MouseButtonEvent) event;

            if (mbe.isPressed()) {
                if (this.mouse.getMousePointer() != null) {
                    this.mouse.getMousePointer().onMouseDown(mbe);
                }

                if (this.mouseOwner == null) {

                    if (this.modalListener == null) {

                        for (ListIterator<GuiView> i = this.windows.listIterator(this.windows.size()); i.hasPrevious();) {
                            GuiView window = i.previous();
                            window.onMouseDown(mbe);
                        }
                        if (this.layout != null) {
                            for (Layer layer : this.layout.getLayers()) {
                                View view = layer.getView();
                                if (view instanceof MouseListenerView) {
                                    MouseListenerView mlv = (MouseListenerView) view;
                                    mlv.onMouseDown(mbe);
                                }
                            }
                        }
                        for (MouseListener ml : this.mouseListeners) {
                            ml.onMouseDown(mbe);
                        }

                    } else {
                        this.modalListener.onMouseDown(mbe);
                        return;
                    }
                } else {
                    this.mouseOwner.onMouseDown(mbe);
                    return;
                }
            }

            if (mbe.isReleased()) {

                if (this.mouse.getMousePointer() != null) {
                    this.mouse.getMousePointer().onMouseUp(mbe);
                }

                if (this.mouseOwner == null) {

                    if (this.modalListener == null) {

                        for (ListIterator<GuiView> i = this.windows.listIterator(this.windows.size()); i.hasPrevious();) {
                            GuiView window = i.previous();
                            window.onMouseUp(mbe);
                        }
                        if (this.layout != null) {
                            for (Layer layer : this.layout.getLayers()) {
                                View view = layer.getView();
                                if (view instanceof MouseListenerView) {
                                    MouseListenerView mlv = (MouseListenerView) view;
                                    mlv.onMouseUp(mbe);
                                }
                            }
                        }
                        for (MouseListener ml : this.mouseListeners) {
                            ml.onMouseUp(mbe);
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

        } else if (event instanceof MouseMotionEvent) {

            MouseMotionEvent mme = (MouseMotionEvent) event;

            if (this.mouse.getMousePointer() != null) {
                this.mouse.getMousePointer().onMouseMove(mme);
            }

            if (this.mouseOwner == null) {

                if (this.modalListener == null) {

                    for (ListIterator<GuiView> i = this.windows.listIterator(this.windows.size()); i.hasPrevious();) {
                        GuiView window = i.previous();
                        window.onMouseMove(mme);
                    }
                    if (this.layout != null) {
                        for (Layer layer : this.layout.getLayers()) {
                            View view = layer.getView();
                            if (view instanceof MouseListenerView) {
                                MouseListenerView mlv = (MouseListenerView) view;
                                mlv.onMouseMove(mme);
                            }
                        }
                    }
                    for (MouseListener el : this.mouseListeners) {
                        el.onMouseMove(mme);
                    }

                } else {
                    this.modalListener.onMouseMove(mme);
                    return;
                }

            } else {
                this.mouseOwner.onMouseMove(mme);
                return;
            }
        } else if (event instanceof WindowEvent) {

            WindowEvent we = (WindowEvent) event;
            if (this.director.onWindowEvent(we)) {
                return;
            }
            // If the director hasn't handled mouse focus, then hide fancy mouse pointers when the
            // mouse leaves the window, and show it when it returns.
            if (we.lostMouseFocus()) {
                this.mouse.onLostMouseFocus();
            } else if (we.gainedMouseFocus()) {
                this.mouse.onGainedMouseFocus();
            }
            return;
        }
    }

    /**
     * 
     * @param listener
     * @priority 3
     */
    public void addMouseListener(MouseListener listener)
    {
        this.mouseListeners.add(listener);
    }

    /**
     * 
     * @param listener
     * @priority 3
     */
    public void removeMouseListener(MouseListener listener)
    {
        this.mouseListeners.remove(listener);
    }

    /**
     * 
     * @param listener
     * @priority 3
     */
    public void addKeyListener(KeyListener listener)
    {
        this.keyListeners.add(listener);
    }

    /**
     * 
     * @param listener
     * @priority 3
     */
    public void removeKeyListener(KeyListener listener)
    {
        this.keyListeners.remove(listener);
    }

    /**
     * Prevents mouse events going to any other MouseListeners, other than the one specified, until
     * {@link #releaseMouse(MouseListener)} is
     * called.
     * 
     * @priority 3
     */
    public void captureMouse(MouseListener owner)
    {
        assert (this.mouseOwner == null);
        this.mouseOwner = owner;
    }

    /**
     * The flip side of {@link #captureMouse(MouseListener)}. MouseListeners will receive mouse events as normal.
     * 
     * @param owner
     * @priority 3
     */
    public void releaseMouse(MouseListener owner)
    {
        assert (this.mouseOwner == owner);
        this.mouseOwner = null;
    }

    /**
     * @priority 3
     */
    public void setModalListener(InputListener listener)
    {
        this.modalListener = listener;
    }

    /**
     * Used when a single entity believes it deserves first priority to all key strokes. In particular, this is used by
     * GUI components,
     * which accept keyboard input when they have the focus.
     * 
     * @priority 3
     */
    public void setFocus(Focusable focus)
    {
        this.keyboardFocus = focus;
    }

    /**
     * @return The width of the Game's display in pixels, taken from the {@link GameInfo}, which is stored in the game's
     *         {@link Resources} file.
     */
    public int getWidth()
    {
        return this.resources.getGameInfo().width;
    }

    /**
     * @return The height of the Game's display in pixels, taken from the {@link GameInfo}, which is stored in the
     *         game's {@link Resources} file.
     */
    public int getHeight()
    {
        return this.resources.getGameInfo().height;
    }

    /**
     * @return The title of the Game, as it should appear in the window's title bar. This is taken from the
     *         {@link GameInfo}, which is stored in the game's {@link Resources} file.
     * @priority 3
     */
    public String getTitle()
    {
        return this.resources.getGameInfo().title;
    }

    /**
     * 
     * @return
     * @priority 3
     */
    public boolean isResizable()
    {
        return this.resources.getGameInfo().resizable;
    }

    /**
     * If no further tick methods should be called for this frame, then
     * this flag is set. For example, loading a new Scene will set this flag.
     * The {@link #tick()} method checks this, and returns whenever it is set.
     * 
     */
    private boolean abortTicks;

    /**
     * Called once per frame (usually 60 times per second).
     * Calls {@link Director#tick()}, {@link SceneDirector#tick()} and the {@link Stage#tick()} for each Stage, which
     * will
     * in turn, call all of the {@link Role#tick()}.
     * 
     * @priority 3
     */
    public void tick()
    {
        this.abortTicks = false;
        this.director.tick();

        if (this.abortTicks) {
            return;
        }

        this.getSceneDirector().tick();

        if (this.abortTicks) {
            return;
        }

        for (Stage stage : this.getStages()) {
            stage.tick();
            if (this.abortTicks) {
                return;
            }
        }

        this.glassStage.tick();
    }

    /**
     * A simple getter for the current {@link SceneDirector}. The SceneDirector is set automatically when a
     * {@link Scene} is loaded.
     * 
     * @return
     */
    public SceneDirector getSceneDirector()
    {
        return this.sceneDirector;
    }

    /**
     * Automatically called when a {@link Scene} is loaded.
     * 
     * @param sceneDirector
     * @priority 3
     */
    public void setSceneDirector(SceneDirector sceneDirector)
    {
        if (this.sceneDirector != null) {
            this.sceneDirector.onDeactivate();
        }
        this.sceneDirector = sceneDirector;

        this.sceneDirector.onActivate();
    }

    /**
     * @param sceneName
     * @return true if the named scene exists.
     */
    public boolean hasScene(String sceneName)
    {
        try {
            return this.resources.getScene(sceneName) != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Loads the named {@link Scene}, and merges it with the current one. The Game's layout will be a combination of the
     * two Scene's Layouts.
     * <p>
     * Often called from {@link SceneDirector#loading(Scene)} when common Actors are needed by many scenes. For example
     * the score is an Actor on a glass layer, which is needed for every level of a game. Rather than add the score's
     * Actor to every Scene, create a Scene containing just the score, and merge it with the real game scenes.
     * 
     * @param additionalSceneName
     * @return
     */
    public Scene mergeScene(String additionalSceneName)
    {
        Scene scene = this.loadScene(additionalSceneName);
        if (scene == null) {
            return null;
        }
        // Prevent a concurrent modification exception by aborting the ticks after the current stage has ticked.
        this.abortTicks = true;

        this.layout.merge(scene.layout);

        // Convert all of the DelayedActivation roles into the actual roles.
        // This will fire each role's onBirth and onAttach
        for (Stage stage : this.getStages()) {
            for (Actor actor : stage.getActors()) {
                Role role = actor.getRole();
                if (role.getClass() == DelayedActivation.class) {
                    role.tick();
                }
            }
        }

        return scene;
    }

    /**
     * 
     * @param scene
     * @priority 3
     */
    public void unmergeScene(Scene scene)
    {
        // Prevent a concurrent modification exception by aborting the ticks after the current stage as ticked.
        this.abortTicks = true;

        for (Layer layer : scene.layout.getLayers()) {
            Stage stage = layer.getStage();
            if (stage != null) {
                stage.clear();
            }
        }
    }

    /**
     * 
     * @return
     */
    public Layout getLayout()
    {
        return this.layout;
    }

    /**
     * Clears all Stages and Actors, and then loads the named {@link Scene}.
     * <p>
     * Many events are fired when loading the scene, and the order is very important.
     * <ul>
     * <li>{@link Director#onStartingScene(String)}</li>
     * <li>{@link Director#loadScene(String)}</li>
     * <li>{@link SceneDirector#onDeactivate()}
     * <li>the actors are created</li>
     * <li>new SceneDirector is created</li>
     * <li>Each Actor's {@link AbstractRole#onAttach()}</li>
     * <li>Each Actor's {@link AbstractRole#onBirth()}</li>
     * <li>{@link SceneDirector#onLoaded()}</li>
     * <li>Each Actor's {@link AbstractRole#onSceneCreated()}</li>
     * <li>{@link SceneDirector#onActivate()}</li>
     * <li>{@link Director#onStartedScene()}</li>
     * </ul>
     * 
     * @param sceneName
     * @return
     */
    public boolean startScene(String sceneName)
    {
        this.abortTicks = true;
        this.director.onStartingScene(sceneName);

        if (this.pause.isPaused()) {
            this.pause.unpause();
        }

        this.sceneDirector.onDeactivate();

        this.clear();
        Scene scene = this.director.loadScene(sceneName);
        if (scene == null) {
            return false;
        }

        this.sceneName = sceneName;
        this.sceneDirector = scene.getSceneDirector();
        this.layout = scene.layout;

        // fire loading
        scene.getSceneDirector().loading(scene);

        // Convert all of the DelayedActivation roles into the actual roles.
        // This will fire each role's onBirth and onAttach
        for (Stage stage : this.getStages()) {
            for (Actor actor : stage.getActors()) {
                Role role = actor.getRole();
                if ((role != null) && (role.getClass() == DelayedActivation.class)) {
                    role.tick();
                }
            }
        }

        this.mouse.showRegularMousePointer(scene.showMouse);

        // fire events
        this.getSceneDirector().onLoaded();

        // Fire each role's "onSceneCreated"
        for (Stage stage : this.getStages()) {
            for (Actor actor : stage.getActors()) {
                Role role = actor.getRole();
                if (role != null) {
                    role.sceneCreated();
                }
            }
        }

        // Fire sceneDirector's onActivate
        this.sceneDirector.onActivate();

        Itchy.getFrameRate().reset();

        this.director.onStartedScene();
        return true;
    }

    /**
     * Loads a scene. Should not be called directly. Use {@link #startScene(String)} or {@link #mergeScene(String)}
     * instead.
     * 
     * @param sceneName
     * @return
     * @priority 5
     */
    Scene loadScene(String sceneName)
    {
        SceneStub sceneStub = this.resources.getScene(sceneName);
        if (sceneStub == null) {
            System.err.println("Scene not found : " + sceneName);
            return null;
        }
        try {
            Scene scene = sceneStub.load(false);
            if (scene == null) {
                System.err.println("Error loading scene : " + sceneName);
                try {
                    throw new Exception();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            return scene;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 
     * @return The current Scene's name.
     */
    public String getSceneName()
    {
        return this.sceneName;
    }

    /**
     * Called to end the game, which will usually end the whole program. The program will not end, if one Game creates
     * and runs another
     * Game, and then this second Game ends, in this case, the first Game will take back control, and the program will
     * continue. Note that
     * the Editor is a Game, so when the Editor ends, this is exactly what is happens.
     * 
     * You may override end, as long as you call super.end() at the end of your overridden method.
     */
    public void end()
    {
        clear();
        running = false;
        Itchy.endGame();
    }

    /**
     * Sets the {@link Stylesheet} used by all {@link GuiView}s.
     * 
     * @param rules
     * @priority 3
     */
    public void setStylesheet(Stylesheet rules)
    {
        this.stylesheet = rules;
    }

    /**
     * A simple getter.
     * 
     * @return
     * @priority 3
     */
    public Stylesheet getStylesheet()
    {
        return this.stylesheet;
    }

    /**
     * 
     * @param rootContainer
     * @return
     * @priority 3
     */
    public GuiView show(RootContainer rootContainer)
    {
        GuiView view = new GuiView(new Rect(0, 0, this.getWidth(), this.getHeight()), rootContainer);
        this.show(view);

        return view;
    }

    /**
     * 
     * @param view
     * @priority 3
     */
    public void show(GuiView view)
    {
        view.setVisible(true);

        this.windows.add(view);

        if (view.rootContainer.getStylesheet() == null) {
            view.rootContainer.setStylesheet(this.stylesheet);
        }
        view.rootContainer.reStyle();
        view.rootContainer.forceLayout();
        // rootContainer.setPosition(0, 0, rootContainer.getRequiredWidth(), rootContainer.getRequiredHeight());

        if (view.rootContainer.modal) {
            this.setModalListener(view);
        }
        this.addKeyListener(view);

    }

    /**
     * 
     * @param view
     * @priority 3
     */
    public void hide(GuiView view)
    {
        view.setVisible(false);

        this.removeKeyListener(view);

        this.windows.remove(view);

        if (view.rootContainer.modal) {
            if (this.windows.size() > 0) {
                GuiView topWindow = this.windows.get(this.windows.size() - 1);
                if (topWindow.rootContainer.modal) {

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
     * Iterator of all actors on all stages.
     * The order is defined by the order of the stages in the game's list of stages ({@link #stages}), and by the
     * order of the actors with each stage.
     * 
     * @return An iterator of all actors on all stages
     * @priority 3
     */
    public Iterator<Actor> getActors()
    {
        return new AllActorsIterator();
    }

    /**
     * 
     * @priority 5
     */
    class AllActorsIterator implements Iterator<Actor>
    {
        private Actor next;

        public Iterator<Stage> stageIterator;

        public Iterator<Actor> actorIterator;

        public AllActorsIterator()
        {
            this.stageIterator = Game.this.getStages().iterator();
            if (this.stageIterator.hasNext()) {
                Stage stage = this.stageIterator.next();
                this.actorIterator = stage.iterator();
            }
            advance();
        }

        private void advance()
        {
            if (this.actorIterator.hasNext()) {
                this.next = this.actorIterator.next();
            } else {
                while (this.stageIterator.hasNext()) {
                    this.actorIterator = this.stageIterator.next().iterator();
                    if (this.actorIterator.hasNext()) {
                        this.next = this.actorIterator.next();
                        return;
                    }
                }
                this.next = null;
            }
        }

        @Override
        public boolean hasNext()
        {
            return this.next != null;
        }

        @Override
        public Actor next()
        {
            Actor result = this.next;
            advance();
            return result;
        }

        @Override
        public void remove()
        {
            this.actorIterator.remove();
        }

    }

}
