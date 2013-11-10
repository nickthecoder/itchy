/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.prefs.Preferences;

import uk.co.nickthecoder.itchy.editor.Editor;
import uk.co.nickthecoder.itchy.editor.SceneDesigner;
import uk.co.nickthecoder.itchy.gui.GuiPose;
import uk.co.nickthecoder.itchy.gui.Stylesheet;
import uk.co.nickthecoder.itchy.script.ScriptManager;
import uk.co.nickthecoder.itchy.util.AutoFlushPreferences;
import uk.co.nickthecoder.itchy.util.StringUtils;
import uk.co.nickthecoder.itchy.util.TagCollection;
import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.event.Event;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.Keys;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;
import uk.co.nickthecoder.jame.event.QuitEvent;

public class Game implements InputListener, QuitListener, MessageListener
{
    /**
     * Used internally when creating the Game object, and when using scripted game code, rather than
     * Java code.
     */
    public final GameManager gameManager;

    /**
     * Holds all of the images, sounds, fonts, animations costumes etc used by this game.
     */
    public final Resources resources;

    /**
     * Holds the tag information for all of the Actors used within this game. This isn't typically
     * used directly, but instead used via {@link Actor#allByTag(String)},
     * {@link Actor#hadTag(String)}, {@link Actor#addTag(String)} and
     * {@link Actor#removeTag(String)}.
     */
    public final TagCollection<Behaviour> behaviourTags = new TagCollection<Behaviour>();

    public List<Actor> actors = new LinkedList<Actor>();

    /**
     * Helps to implement a pause feature within your game. Typically, you will pause/unpause from
     * your game's {@link SceneBehaviour#onKeyDown(KeyboardEvent)} :
     * <code>myGame.pause.togglePause();</code>
     */
    public Pause pause;

    /**
     * Holds data about this game that is stored for use then next time the game is run. For
     * example, it can be used to store high scores.
     */
    private AutoFlushPreferences preferences;

    /**
     * A Game has a set of Layers stacked one on top of another. Actors (the characters within you
     * game), live on a layer. A simple game may have just one layer, but a more complex game may
     * have more.
     */
    protected CompoundLayer layers;

    /**
     * A special layer which appears above the normal layers, and is used to show pop-up windows.
     */
    protected ActorsLayer popupLayer;

    /**
     * A list of all the objects that need to know when a mouse is clicked or moved.
     */
    protected List<MouseListener> mouseListeners;

    /**
     * A list of all the objects that need to know when a key is pressed.
     */
    protected List<KeyListener> keyListeners;

    /**
     * The mouse can be captured for a period of time, which ensures mouse events are only sent to
     * itself. For example, if you are dragging an object, you may not want any other objects from
     * receiving the mouse-move event.
     */
    private MouseListener mouseOwner;

    /**
     * The modalListener is give all mouse and keyboard events, and no other listeners hear about
     * them. This is used for a "modal" popup window. i.e. a window which takes over the whole game,
     * and doesn't let the user do anything else until it is dismissed.
     */
    private InputListener modalListener;

    /**
     * Which component has the focus - used within the GUI subsystem to highlight a single gui
     * component.
     */
    private Focusable keyboardFocus;

    /**
     * The list of all of the windows current shown.
     */
    private final List<GuiPose> windows;

    /**
     * The current SceneBehaviour, which was created during {@link #loadScene(String)}
     */
    private SceneBehaviour sceneBehaviour;

    /**
     * The name of the current scene, i.e. the last one started using {@link #startScene(String)}
     */
    private String sceneName;

    /**
     * Set when the game is run from the {@link SceneDesigner}.
     */
    protected boolean testing;

    /**
     * Defines how GUI components look, such as the images/textures used for the controls, their
     * margins etc.
     */
    private Stylesheet stylesheet;

    /**
     * Keeps a record of if the current scene should show the mouse pointer. This is needed so that
     * the moue can be shown/hidden when one Game ends and the previous one is re-activated.
     */
    private boolean showMousePointer = true;

    /**
     * Game should have only this constructor, it is called dynamically after the Game's resources
     * have been loaded. The class name determining which sub-class of Game should be created is
     * included in the resources file.
     * 
     * @param gameManager
     */
    public Game( GameManager gameManager )
    {
        this.gameManager = gameManager;
        this.resources = gameManager.resources;

        this.sceneBehaviour = new PlainSceneBehaviour();
        this.pause = new Pause(this);

        this.mouseListeners = new LinkedList<MouseListener>();
        this.keyListeners = new LinkedList<KeyListener>();

        this.windows = new ArrayList<GuiPose>();

        this.addMouseListener(this);
        this.addKeyListener(this);

        Rect screenRect = new Rect(0, 0, getWidth(), getHeight());

        this.layers = new CompoundLayer("game", screenRect);

        this.popupLayer = new ScrollableLayer("popup", screenRect);
        this.popupLayer.setYAxisPointsDown(true);
        this.popupLayer.setVisible(true);

        createLayers();
    }

    public ScriptManager getScriptManager()
    {
        return this.gameManager.scriptManager;
    }

    /**
     * Called soon after a Game is created and also when a Game creates another Game, and the second
     * Game ends (reactivating the first one).
     */
    public void onActivate()
    {
        showMousePointer(this.showMousePointer);
    }

    /**
     * Called when a Game gives starts another Game.
     */
    public void onDeactivate()
    {
    }

    /**
     * Overridden by sub classes of Game, when they want more than one {@link Layer}. Called from
     * the end of Game's constructor.
     */
    protected void createLayers()
    {
        Rect screenRect = new Rect(0, 0, getWidth(), getHeight());

        ScrollableLayer mainLayer = new ScrollableLayer("main", screenRect, RGBA.BLACK);
        this.layers.add(mainLayer);

        mainLayer.enableMouseListener(this);
    }

    /**
     * Removes all actors from all layers, and resets the layers to the origin.
     */
    public void clear()
    {
        this.layers.clear();
        this.popupLayer.clear();
        this.layers.reset();
    }

    /**
     * Typically, this is called immediately the Game object is created, usually from the "main"
     * method.
     * 
     * Do NOT override this method.
     */
    public void start()
    {
        Itchy.startGame(this);
        if (!StringUtils.isBlank(this.resources.gameInfo.initialScene)) {
            startScene(this.resources.gameInfo.initialScene);
        }
        Itchy.mainLoop();
    }

    /**
     * The same as {@link #start()}, but named scene is started instead of the default scene. This
     * can be useful during development to jump to a particular scene.
     * 
     * @param sceneName
     */
    public void start( String sceneName )
    {
        Itchy.startGame(this);

        if (!StringUtils.isBlank(sceneName)) {
            clear();
            startScene(sceneName);
        }

        Itchy.mainLoop();
    }

    /**
     * Called by the SceneDesigner, to test a single scene. Essentially the same as
     * {@link #start(String)}, but also sets the boolean <code>testing</code>, so that the game
     * knows its in testing mode.
     * 
     * @param sceneName
     *        The name of the scene to play.
     */
    public void testScene( String sceneName )
    {
        try {
            System.err.println("Starting Test");
            this.testing = true;
            start(sceneName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * The flip side of {@link testScene(String)}, will return to the scene designer.
     * 
     * Very similar to {@link #end}.
     */
    public void endTest()
    {
        clear();
        end();
        this.testing = false;
        System.err.println("Ended Test");
    }

    /**
     * Preferences contain permanently stored data (i.e. its still there when you wuit the game and
     * restart it days later). The data is stored in a hierarchy of nodes. Each node has a set of
     * name/value pairs, just like HashMap. Each node can also have named sub-nodes (which gives it
     * the hierarchical structure).
     * 
     * @return The top level node preferences for this game. The path of this node is determined by
     *         {@link #getPreferenceNode()}.
     */
    public AutoFlushPreferences getPreferences()
    {
        if (this.preferences == null) {
            this.preferences = new AutoFlushPreferences(getPreferenceNode());
        }
        return this.preferences;
    }

    /**
     * Gets the root node for this game. The default implementation uses the path based on the
     * Game's class name, and the game's ID. Note, unlike {@link #getPreferences()}, the return
     * Preferences are not AutoFlushPreferences, you will need to call 'flush' to commit each of the
     * changes.
     * 
     * @return The top level preferences node for this game.
     */
    protected Preferences getPreferenceNode()
    {
        return Preferences.userNodeForPackage(this.getClass()).node(this.resources.getId());
    }

    /**
     * Returns a lists of all the Behaviours tagged with a given tag.
     * 
     * @param tag
     *        The tag to search for.
     * @return A set of Behaviours meeting the criteria. An empty set if no behaviours meet the criteria.
     * @See {@link Behaviour#addTag(String)}
     */
    public Set<Behaviour> findBehaviourByTag( String tag )
    {
        return this.behaviourTags.getTagMembers(tag);
    }

    /**
     * Get the top-level CompoundLayer, which holds all of the sub-layers. The sub-layers are the
     * ones that Actors will be added to. Note that the pop-up layer is NOT part of the hierarchy,
     * it is above all these.
     * 
     * @return
     */
    public CompoundLayer getLayers()
    {
        return this.layers;
    }

    /**
     * The popup layer is a layer draw above all of the game's regular layers. It is useful for
     * displaying dialog boxes, or other critical information.
     * 
     * @return The popup layer.
     */
    public ActorsLayer getPopupLayer()
    {
        return this.popupLayer;
    }

    /**
     * Renders all of the layers (including the popup layer) to the display surface.
     * 
     * Itchy will call this from inside the game loop, so there is normally no need for you to call
     * it explicitly. You may call this with a surface of your creation to obtain a screenshot of
     * the game. {@link uk.co.nikthecoder.extras.SceneTransition} takes screenshots in this manner,
     * which are then slid, or faded from view.
     * 
     * @param display
     *        The surface to draw onto. This will normally be the display surface, but can be any
     *        surface.
     */
    public void render( Surface display )
    {
        Rect screenRect = new Rect(0, 0, getWidth(), getHeight());

        this.layers.render(screenRect, display);
        this.popupLayer.render(screenRect, display);
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

    /**
     * Itchy passes every event through the current game's processEvent method. You shouldn't call
     * this method directly, or override it. If you need to handle events at the Game level, then
     * override onKeyDown, onKeyUp, onMouseMove, onMouseDown, onMouseUp etc, and leave this method
     * well alone!
     * 
     * @param event
     *        The event that needs to be handled.
     */
    void processEvent( Event event )
    {
        if (event instanceof QuitEvent) {
            if (this.onQuit()) {
                return;
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

    /**
     * Prevents mouse events going to any other MouseListeners, other than the one specified, until
     * {@link #releaseMouse(EventListener)} is called.
     * 
     */
    public void captureMouse( MouseListener owner )
    {
        assert (this.mouseOwner == null);
        this.mouseOwner = owner;
    }

    /**
     * The flip side of {@link #captureMouse(InputListener)}. MouseListeners will receive mouse
     * events as normal.
     * 
     * @param owner
     */
    public void releaseMouse( MouseListener owner )
    {
        assert (this.mouseOwner == owner);
        this.mouseOwner = null;
    }

    public void setModalListener( InputListener listener )
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

    /**
     * @return The width of the Game's display in pixels, taken from the {@link GameInfo}, which is
     *         stored in the game's {@link Resources} file.
     */
    public int getWidth()
    {
        return this.resources.gameInfo.width;
    }

    /**
     * @return The height of the Game's display in pixels, taken from the {@link GameInfo}, which is
     *         stored in the game's {@link Resources} file.
     */
    public int getHeight()
    {
        return this.resources.gameInfo.height;
    }

    /**
     * @return The title of the Game, as it should appear in the window's title bar. This is taken
     *         from the {@link GameInfo}, which is stored in the game's {@link Resources} file.
     */
    public String getTitle()
    {
        return this.resources.gameInfo.title;
    }

    /**
     * Called when the application has been asked to quit, such as when Alt-F4 is pressed, or the
     * window's close button is pressed. The default behaviour is to terminate the application.
     */
    @Override
    public boolean onQuit()
    {
        Itchy.terminate();
        return true;
    }

    /**
     * Called when a button is pressed. Most games don't use onKeyDown or onKeyUp during game play,
     * instead, each Actor uses : Itchy.isKeyDown( ... ). onKeyDown and onKeyUp are useful for
     * typing, not for game play.
     */
    @Override
    public boolean onKeyDown( KeyboardEvent ke )
    {
        return this.getSceneBehaviour().onKeyDown(ke);
    }

    /**
     * Called when a button is pressed. Most games don't use onKeyDown or onKeyUp during game play,
     * instead, each Actor uses : Itchy.isKeyDown( ... ). onKeyDown and onKeyUp are useful for
     * typing.
     * 
     * The default behaviour is to do nothing more than forward the event to the current scene's
     * {@link SceneBehaviour}.
     */
    @Override
    public boolean onKeyUp( KeyboardEvent ke )
    {
        return this.getSceneBehaviour().onKeyUp(ke);
    }

    /**
     * The default behaviour is to do nothing more than forward the event to the current scene's
     * {@link SceneBehaviour}.
     */
    @Override
    public boolean onMouseDown( MouseButtonEvent mbe )
    {
        return this.getSceneBehaviour().onMouseDown(mbe);
    }

    /**
     * The default behaviour is to do nothing more than forward the event to the current scene's
     * {@link SceneBehaviour}.
     */
    @Override
    public boolean onMouseUp( MouseButtonEvent mbe )
    {
        return this.getSceneBehaviour().onMouseDown(mbe);
    }

    /**
     * The default behaviour is to do nothing more than forward the event to the current scene's
     * {@link SceneBehaviour}.
     */
    @Override
    public boolean onMouseMove( MouseMotionEvent mbe )
    {
        return this.getSceneBehaviour().onMouseMove(mbe);
    }

    public List<Actor> getActors()
    {
        return this.actors;
    }
    
    private List<Actor> newActors = new ArrayList<Actor>();
    
    void add( Actor actor )
    {
        this.newActors.add(actor);
    }
    
    /**
     * Override this method to run code once per frame. The default behaviour is to call the current
     * scene's {@link SceneBehaviour}'s tick method and then all of the game's active Actor's tick
     * methods.
     */
    public void tick()
    {
        this.getSceneBehaviour().tick();
        for (Iterator<Actor> i = this.actors.iterator(); i.hasNext();) {
            Actor actor = i.next();
            if (actor.isDead()) {
                i.remove();
            } else {
                actor.tick();
            }
        }
        this.actors.addAll(this.newActors);
        this.newActors.clear();
    }

    /**
     * The default behaviour is to do nothing more than forward the message to the current scene's
     * {@link SceneBehaviour}.
     */
    @Override
    public void onMessage( String message )
    {
        this.getSceneBehaviour().onMessage(message);
    }

    public SceneBehaviour getSceneBehaviour()
    {
        return this.sceneBehaviour;
    }

    public void showMousePointer( boolean value )
    {
        this.showMousePointer = value;
        uk.co.nickthecoder.jame.Video.showMousePointer(value);
    }

    /**
     * Clears and resets the layers, and then loads the specified scene.
     * 
     * @param sceneName
     *        The name of the scene to load.
     */
    public void startScene( String sceneName )
    {
        if (this.pause.isPaused()) {
            this.pause.unpause();
        }

        this.layers.clear();
        this.layers.reset();
        this.loadScene(sceneName);
    }

    public boolean loadScene( String sceneName )
    {
        try {
            Scene scene = this.resources.getScene(sceneName);
            if (scene == null) {
                System.err.println("Scene not found : " + sceneName);
                try {
                    throw new Exception();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }

            this.sceneBehaviour.onDeactivate();

            this.sceneName = sceneName;

            this.sceneBehaviour = scene.createSceneBehaviour(this.resources);
            this.sceneBehaviour.onActivate();
            scene.create(this.layers, this.resources, false);
            showMousePointer(scene.showMouse);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        // System.out.println( "Resetting frame rate" );
        Itchy.frameRate.reset();
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
        clear();
        Itchy.endGame();
    }

    public void setStylesheet( Stylesheet rules )
    {
        this.stylesheet = rules;
    }

    public Stylesheet getStylesheet()
    {
        return this.stylesheet;
    }

    public void showWindow( GuiPose window )
    {
        this.windows.add(window);

        if (window.getStylesheet() == null) {
            window.setStylesheet(this.stylesheet);
        }
        window.reStyle();
        window.forceLayout();
        window.setPosition(0, 0, window.getRequiredWidth(), window.getRequiredHeight());

        Actor actor = window.getActor();

        actor.moveTo(Math.max(0, (this.popupLayer.position.width - window.getRequiredWidth()) / 2),
            Math.max(0, (this.popupLayer.position.height - window.getRequiredHeight()) / 2));

        this.popupLayer.addTop(actor);

        if (window.modal) {
            this.setModalListener(window);
        }
        this.addMouseListener(window);
        this.addKeyListener(window);

    }

    public void hideWindow( GuiPose window )
    {
        this.removeMouseListener(window);
        this.removeKeyListener(window);
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
            editor.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startEditor( String designSceneName )
    {
        try {
            Editor editor = new Editor(this);
            editor.start(designSceneName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString()
    {
        return this.getClass().getName() + " Resources " + this.resources;
    }

}
