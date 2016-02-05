/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
 * Game is one of the core classes within Itchy, it is the central point around which all the other pieces work. As a games designer, you do
 * not create a Game object directly, it is created automatically when the {@link Resources} are loaded.
 * <p>
 * A game consists of a set of {@link Stage} objects (typically {@link ZOrderStage}), and on each Stage, there live a set of {@link Actor}
 * objects. The Actors are things like spaceships, bullets and even the scenery. How the actors move and interact is defined by their
 * {@link Role}. Most of your game's code will probably be a type of Role.
 * <p>
 * A game needs to display the stage and its actors on the screen, and that is done via a {@link View} (a {@link StageView} in fact). Each
 * stage normally has one view, but there could be more. For example, you could split the screen down the middle for a two player game, and
 * there would be one Stage with two Views of it (one for each player).
 * <p>
 * The Game, in partnership with its {@link Director}, is responsible for coordinating all of these pieces. Each frame (sixty times a
 * second), the game's {@link #tick()} method is called. This will call every Actor's Role's {@link Role#animateAndTick()} method, and it is the
 * Role's tick method that moves actors. The {@link SceneDesigner}'s tick method is also called once every frame.
 * <p>
 * After all the ticking has taken place, its time to draw to the screen. Every View is rendered (drawn), and what you see on the screen is
 * the combination of all of the views. You can think of views as a stack of transparent plastic sheets stacked one on top of another. Each
 * view may have only a small part of the final picture.
 * <p>
 */
public class Game
{
    /**
     * Holds all of the images, sounds, fonts, animations costumes etc used by this game.
     */
    public final Resources resources;

    /**
     * The overall controller of the game, but ironically, it often has very little to do, as most of the game logic is inside each Actor's
     * {@link Role}. Also, each scene can have its own {@link SceneDirector} which controls a single scene. This leaves very little for
     * Director to do in most cases.
     * The director is set from the "Info" tab in the {@link Editor}.
     */
    private Director director;

    /**
     * Holds the tag information for all of the Actors used within this game. This isn't typically used directly, but instead used via
     * {@link AbstractRole#allByTag(String)}, {@link AbstractRole#hasTag(String)}, {@link AbstractRole#addTag(String)} and
     * {@link AbstractRole#removeTag(String)}.
     */
    public final TagCollection<Role> roleTags = new TagCollection<Role>();

    /**
     * Helps to implement a pause feature within your game. Typically, you will pause/unpause from your game's
     * {@link SceneDirector#onKeyDown(KeyboardEvent)}
     */
    public Pause pause;

    /**
     * Holds data about this game that is stored for use then next time the game is run. For example, it can be used to store high scores.
     */
    private AutoFlushPreferences preferences;

    /**
     * A tree structure of all the views.
     */
    private CompoundView allViews;

    /**
     * A sub-tree of {@link #allViews}, which excludes some special views which a game should not tamper with.
     */
    private CompoundView gameViews;

    /**
     * A list of all the stages used by this game.
     */
    private List<Stage> stages;

    /**
     * A special stage which is at the front of the z-order (i.e. always visible).
     * It can be used for generic tools, not specific to a game, for example, it can be use to display a "Pause" message.
     */
    private ZOrderStage glassStage;

    /**
     * The view for the {@link #glassStage}
     */
    private StageView glassView;

    /**
     * The list of all of the windows currently shown.
     */
    private GenericCompoundView<GuiView> windows;

    /**
     * A list of all the objects that need to know when a mouse is clicked or moved.
     */
    private List<MouseListener> mouseListeners;

    /**
     * A list of all the objects that need to know when a key is pressed.
     */
    private List<KeyListener> keyListeners;

    /**
     * The mouse can be captured for a period of time, which ensures mouse events are only sent to itself. For example, if you are dragging
     * an object, you may not want any other objects from receiving the mouse-move event.
     */
    private MouseListener mouseOwner;

    /**
     * The modalListener is give all mouse and keyboard events, and no other listeners hear about them. This is used for a "modal" popup
     * window. i.e. a window which takes over the whole game, and doesn't let the user do anything else until it is dismissed.
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
     * As a game designer, you don't need to know HOW your scripts are compiled and executed, ScriptManager takes care of that for
     * you. Game writers can safely ignore this.
     */
    public final ScriptManager scriptManager;

    /**
     * Controls how the mouse pointer appears while inside the game window. It could be hidden (useful for games which don't use a mouse),
     * appear as a regular mouse pointer, or look a bit more flashy. The mouse is given special status, as its MousePointer always receives
     * mouse events, even if something else has captured the mouse.
     */
    public Mouse mouse = new SimpleMouse();

    /**
     * Game constructor called when the resources are being loaded. Note that not much initialisation can take place yet, as vital
     * information is missing. For example, the games width and height are unknown. The {@link #init()} method will be called later, to
     * complete the initialisation.
     * 
     * @param resources The resources being loaded. Note, the resources will NOT be fully loaded yet.
     */
    public Game( Resources resources )
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
        this.pause = new SimplePause(this);
    }

    /**
     * Called just once, soon after the Game is created. Its called as soon as the GameInfo has been read from the resources file. This is
     * where most of Game's initialisation occurs; it couldn't take place in Game's constructor, because vital information wasn't available
     * at that time (e.g. the game's width and height weren't known then.
     */
    public void init()
    {
        Rect displayRect = new Rect(0, 0, getWidth(), getHeight());

        this.allViews = new CompoundView("allViews", displayRect);
        this.stages = new LinkedList<Stage>();

        // Covered by game views
        this.gameViews = new CompoundView("gameViews", displayRect);
        this.allViews.add(this.gameViews);

        // Covered by GUI Windows
        this.windows = new GenericCompoundView<GuiView>("windows", displayRect);
        this.allViews.add(this.windows);

        // Covered by the glass view
        this.glassStage = new ZOrderStage();
        this.glassView = new StageView(displayRect, this.glassStage);
        this.glassView.enableMouseListener(this);
        this.allViews.add(this.glassView);

        this.addMouseListener(this.allViews);
    }

    public FrameRate getFrameRate()
    {
        return Itchy.frameRate;
    }

    /**
     * Called only once, after the resources have been loaded. Do not change directors during the game. Instead, if you want different
     * behaviour for different parts of the game, then use different SceneDirectors to code each part of the game.
     * 
     * @param director
     */
    public void setDirector( Director director )
    {
        this.director = director;
        this.director.attach(this);

        this.addMouseListener(this.director);
        this.addKeyListener(this.director);
    }

    public Director getDirector()
    {
        return this.director;
    }

    public CompoundView getGameViews()
    {
        return this.gameViews;
    }

    public List<Stage> getStages()
    {
        return this.stages;
    }

    public ScriptManager getScriptManager()
    {
        return this.scriptManager;
    }

    /**
     * Called soon after a Game is created and also when a Game creates another Game, and the second Game ends (reactivating the first one).
     */
    public void onActivate()
    {
        this.mouse.onActivate();
        this.director.onActivate();
    }

    /**
     * Called when a Game gives starts another Game.
     */
    public void onDeactivate()
    {
        this.director.onDeactivate();
    }
    
    public void resize( int width, int height )
    {
		Itchy.resizeScreen( width, height );
		Rect rect = new Rect( 0,0, width, height ); 
		this.allViews.setPosition( rect );
    	this.gameViews.setPosition( rect );
		this.windows.setPosition( rect );
    }

    /**
     * Kills all Actors and resets the layers to the origin.
     * 
     */
    public void clear()
    {
        this.allViews.reset();
        for (Stage stage : this.stages) {
            stage.clear();
        }
        this.gameViews.clear();
        this.stages.clear();
        
        this.glassStage.clear();
    }

    /**
     * Typically, this is called immediately the Game object is created, usually from the "main" method.
     */
    public void start()
    {
        Itchy.startGame(this);
        this.director.onStarted();
        if (!StringUtils.isBlank(this.resources.getGameInfo().initialScene)) {
            this.director.startScene(this.resources.getGameInfo().initialScene);
        }
        running = true;
        Itchy.mainLoop();
    }
    
    /**
     * The same as {@link #start()}, but named scene is started instead of the default scene. This can be useful during development to jump
     * to a particular scene.
     * 
     * @param sceneName
     */
    public void start( String sceneName )
    {
        Itchy.startGame(this);
        this.director.onStarted();

        if (!StringUtils.isBlank(sceneName)) {
            clear();
            this.director.startScene(sceneName);
        }
        running = true;
        Itchy.mainLoop();
    }

    public boolean isRunning()
    {
    	return this.running;
    }
    
    public void startEditor()
    {
        // If the editor has been started without the game being started (i.e. directly from the
        // launcher) then we need to start the game, so that it creates its layers and views.
        // For the scene designer to copy.
        if ((this.stages == null) || (this.stages.size() == 0)) {
            this.director.onStarted();
        }
        
        try {
            Editor editor = new Editor(this);
            editor.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startEditor( String designSceneName )
    {
        // If the editor has been started without the game being started (i.e. directly from the
        // launcher) then we need to start the game, so that it creates its layers and views.
        // For the scene designer to copy.
        if ((this.stages == null) || (this.stages.size() == 0)) {
            this.director.onStarted();
        }

        try {
            Editor editor = new Editor(this);
            editor.start(designSceneName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Called by the SceneDesigner, to test a single scene. Essentially the same as {@link #start(String)}, but also sets the boolean
     * <code>testing</code>, so that the game knows its in testing mode.
     * 
     * @param sceneName
     *        The name of the scene to play.
     */
    public void testScene( String sceneName )
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
     */
    public void endTest()
    {
        clear();
        end();
        this.testing = false;
        System.err.println("Ended Test");
    }

    /**
     * Preferences contain permanently stored data (i.e. its still there when you quit the game and restart it days later). The data is
     * stored in a hierarchy of nodes. Each node has a set of name/value pairs, just like HashMap. Each node can also have named sub-nodes
     * (which gives it the hierarchical structure).
     * 
     * @return The top level node preferences for this game. The path of this node is determined by {@link Director#getPreferenceNode()}.
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
     *        The tag to search for.
     * @return A set of Roles meeting the criteria. An empty set if no roles meet the criteria.
     * @see AbstractRole#addTag(String)
     */
    public Set<Role> findRoleByTag( String tag )
    {
        return this.roleTags.getTagMembers(tag);
    }

    public Role findRoleById( String id )
    {
        if (id == null) {
            return null;
        }

        for (Stage stage : this.getStages()) {
            for (Actor actor : stage.getActors()) {
                Role role = actor.getRole();
                if (id.equals(role.getId())) {
                    return role;
                }
            }
        }
        return null;
    }

    /**
     * Returns the views that are under your control. Add to this compound view from your Director's onStarted method. This CompoundView
     * does not include the background view ({@link #getBackground()}), nor any pop-up windows, nor the glass view ({@link #getGlassView()}
     * ), which is the top most view.
     */
    public CompoundView getViews()
    {
        return this.gameViews;
    }

    /**
     * The glass stage is used when you want something that absolutely must be above the regular game views. For example, you could use this
     * to add some debugging information, such as the frames per second. You shouldn't use it for regular actors.
     * 
     * @return The glass stage, who's view is draw above all of the game's regular views.
     */
    public ZOrderStage getGlassStage()
    {
        return this.glassStage;
    }

    /**
     * The glass view is drawn above all of the game's regular views. It is a view of the stage returned by {@link #getGlassStage()}.
     */
    public StageView getGlassView()
    {
        return this.glassView;
    }

    /**
     * Renders all of the views to the display surface.
     * 
     * Itchy will call this from inside the game loop, so there is normally no need for you to call it explicitly. However, you may call
     * this with a surface of your creation to obtain a screenshot of the game. {@link SceneTransition} takes screenshots in this manner,
     * which are then slid, or faded from view.
     * 
     * @param display
     *        The surface to draw onto. This will normally be the display surface, but can be any surface.
     */
    public void render( Surface display )
    {
        Rect screenRect = new Rect(0, 0, display.getWidth(), display.getHeight());
        this.allViews.render(display, screenRect, 0, 0);
    }

    /**
     * Use this to time actual game play, which will exclude time while the game is paused. A single value from gameTimeMillis is
     * meaningless, it only has meaning when one value is subtracted from a later value (which will give the number of milliseconds of game
     * elapse time.
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
     * Itchy passes every event through the current game's processEvent method. You should NOT call this method directly.
     * 
     * @param event
     *        The event that needs to be handled.
     */
    void processEvent( Event event )
    {
        if (event instanceof QuitEvent) {
            this.director.onQuit( (QuitEvent) event );
            // Itchy won't terminate if the director calls event.stopPropagation().
            Itchy.terminate();

        } else if (event instanceof ResizeEvent) {
        	ResizeEvent re = (ResizeEvent) event;
			this.director.onResize( re );
        	
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

                        for (MouseListener ml : this.mouseListeners) {
                            ml.onMouseDown(mbe);
                        }
                    } else {
                        this.modalListener.onMouseDown(mbe);
                        return;
                    }
                } else {
                    this.mouseOwner.onMouseUp(mbe);
                    return;
                }
            }

            if (mbe.isReleased()) {

                if (this.mouse.getMousePointer() != null) {
                    this.mouse.getMousePointer().onMouseUp(mbe);
                }

                if (this.mouseOwner == null) {

                    if (this.modalListener == null) {

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
     * Prevents mouse events going to any other MouseListeners, other than the one specified, until {@link #releaseMouse(MouseListener)} is
     * called.
     * 
     */
    public void captureMouse( MouseListener owner )
    {
        assert (this.mouseOwner == null);
        this.mouseOwner = owner;
    }

    /**
     * The flip side of {@link #captureMouse(MouseListener)}. MouseListeners will receive mouse events as normal.
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
     * Used when a single entity believes it deserves first priority to all key strokes. In particular, this is used by GUI components,
     * which accept keyboard input when they have the focus.
     */
    public void setFocus( Focusable focus )
    {
        this.keyboardFocus = focus;
    }

    /**
     * @return The width of the Game's display in pixels, taken from the {@link GameInfo}, which is stored in the game's {@link Resources}
     *         file.
     */
    public int getWidth()
    {
        return this.resources.getGameInfo().width;
    }

    /**
     * @return The height of the Game's display in pixels, taken from the {@link GameInfo}, which is stored in the game's {@link Resources}
     *         file.
     */
    public int getHeight()
    {
        return this.resources.getGameInfo().height;
    }

    /**
     * @return The title of the Game, as it should appear in the window's title bar. This is taken from the {@link GameInfo}, which is
     *         stored in the game's {@link Resources} file.
     */
    public String getTitle()
    {
        return this.resources.getGameInfo().title;
    }
    
    public boolean isResizable()
    {
    	return this.resources.getGameInfo().resizable;
    }

    private boolean abortTicks;
    
    public void tick()
    {
        this.abortTicks = false;
        this.director.tick();

        if (this.abortTicks ) {
            return;
        }
        
        this.getSceneDirector().tick();
        for (Stage stage : this.getStages()) {
            stage.tick();
            if (this.abortTicks ) {
                return;
            }
        }
        
        this.glassStage.tick();
    }

    public SceneDirector getSceneDirector()
    {
        return this.sceneDirector;
    }

    public boolean hasScene( String sceneName )
    {
        try {
            return this.resources.getScene(sceneName) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public Layout layout;
    
    public boolean mergeScene( String additionalSceneName )
    {
        Scene scene = this.loadScene(additionalSceneName);
        if (scene == null) {
            return false;
        }
        // Prevent a concurrent modification exception by aborting the ticks after the current stage as ticked.
        this.abortTicks = true;
        
        this.layout.merge(scene.layout);
        return true;
    }
    
    public Layout getLayout()
    {
        return this.layout;
    }
    
    public boolean startScene( String sceneName )
    {
        this.abortTicks = true;

        if (this.pause.isPaused()) {
            this.pause.unpause();
        }
        this.clear();
        Scene scene = this.director.loadScene(sceneName);
        if ( scene== null) {
            return false;
        }

        this.sceneDirector.onDeactivate();

        this.sceneName = sceneName;
        this.sceneDirector = scene.getSceneDirector();
        this.layout = scene.layout;

        // fire loading
        scene.getSceneDirector().loading(scene);

        // Convert all of the DelayedActivation roles into the actual roles.
        // This will fire each role's onBirth and onAttach
        for (Stage stage : this.getStages()) {
            for (Actor actor : stage.getActors()) {
                actor.getRole().tick();
            }
        }

        this.mouse.showRegularMousePointer(scene.showMouse);

        // fire events
        this.getSceneDirector().onLoaded();

        // Fire each role's "onSceneCreated"
        for (Stage stage : this.getStages()) {
            for (Actor actor : stage.getActors()) {
                actor.getRole().sceneCreated();
            }
        }

        // Fire sceneDirector's onActivate
        this.getSceneDirector().onActivate();

        Itchy.frameRate.reset();
        
        return true;
    }

    public Scene loadScene( String sceneName )
    {
        SceneStub sceneStub = this.resources.getScene(sceneName);
        if (sceneStub == null) {
            System.err.println("Scene not found : " + sceneName);
            return null;
        }
        try {
            Scene scene = sceneStub.load( false );
            if (scene == null) {
                System.err.println("Error loading scene : " + sceneName);
                try {
                    throw new Exception();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            for (Layer layer: scene.layout.getLayersByZOrder()) {
                this.gameViews.add( layer.getView() );

                Stage stage = layer.getStage();
                if (stage != null) {
                    this.stages.add(stage);
                }
            }
            
            return scene;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getSceneName()
    {
        return this.sceneName;
    }

    /**
     * Called to end the game, which will usually end the whole program. The program will not end, if one Game creates and runs another
     * Game, and then this second Game ends, in this case, the first Game will take back control, and the program will continue. Note that
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
    
    public void setStylesheet( Stylesheet rules )
    {
        this.stylesheet = rules;
    }

    public Stylesheet getStylesheet()
    {
        return this.stylesheet;
    }

    public GuiView show( RootContainer rootContainer )
    {
        GuiView view = new GuiView(new Rect(0, 0, this.getWidth(), this.getHeight()), rootContainer);
        this.show( view );
      
        return view;
    }
    
    public void show( GuiView view )
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

    public void hide( GuiView view )
    {
        view.setVisible(false);
        
        this.removeKeyListener(view);

        this.windows.remove(view);

        if (view.rootContainer.modal) {
            if (this.windows.getChildren().size() > 0) {
                GuiView topWindow = this.windows.getChildren().get(this.windows.getChildren().size() - 1);
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
     */
    public Iterator<Actor> getActors()
    {
        return new AllActorsIterator();
    }

    
    class AllActorsIterator implements Iterator<Actor>
    {
        private Actor next;

        public Iterator<Stage> stageIterator;

        public Iterator<Actor> actorIterator;

        public AllActorsIterator()
        {
            this.stageIterator = Game.this.stages.iterator();
            if (this.stageIterator.hasNext()) {
                this.actorIterator = this.stageIterator.next().iterator();
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
            }
            this.next = null;
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
