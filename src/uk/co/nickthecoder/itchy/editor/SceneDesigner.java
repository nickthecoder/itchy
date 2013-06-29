package uk.co.nickthecoder.itchy.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.ActorsLayer;
import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.Costume;
import uk.co.nickthecoder.itchy.CostumeResource;
import uk.co.nickthecoder.itchy.Font;
import uk.co.nickthecoder.itchy.ImagePose;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.KeyListener;
import uk.co.nickthecoder.itchy.MouseListener;
import uk.co.nickthecoder.itchy.Scene;
import uk.co.nickthecoder.itchy.SceneActor;
import uk.co.nickthecoder.itchy.SceneResource;
import uk.co.nickthecoder.itchy.ScrollableLayer;
import uk.co.nickthecoder.itchy.TextPose;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.ButtonGroup;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.FlowLayout;
import uk.co.nickthecoder.itchy.gui.GridLayout;
import uk.co.nickthecoder.itchy.gui.GuiPose;
import uk.co.nickthecoder.itchy.gui.ImageComponent;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.MessageBox;
import uk.co.nickthecoder.itchy.gui.Notebook;
import uk.co.nickthecoder.itchy.gui.TextBox;
import uk.co.nickthecoder.itchy.gui.ToggleButton;
import uk.co.nickthecoder.itchy.gui.VerticalScroll;
import uk.co.nickthecoder.itchy.util.FollowBehaviour;
import uk.co.nickthecoder.itchy.util.NinePatch;
import uk.co.nickthecoder.itchy.util.NullBehaviour;
import uk.co.nickthecoder.itchy.util.Property;
import uk.co.nickthecoder.itchy.util.Reversed;
import uk.co.nickthecoder.itchy.util.StringProperty;
import uk.co.nickthecoder.jame.Keys;
import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

public class SceneDesigner implements MouseListener, KeyListener
{
    private static final int MODE_SELECT = 1;
    private static final int MODE_STAMP_COSTUME = 2;
    private static final int MODE_DRAG_SCROLL = 3;
    private static final int MODE_DRAG_ACTOR = 4;
    private static final int MODE_DRAG_HANDLE = 5;

    private final Editor editor;

    private final SceneResource sceneResource;

    private Scene scene;

    private ScrollableLayer actorsLayer;

    private ScrollableLayer glassLayer;

    private ActorsLayer guiLayer;

    private final Rect sceneRect;

    private final RGBA sceneBackground = new RGBA( 0, 0, 0 );

    private final RGBA guiBackground = null; // new RGBA( 255, 255, 255 );

    private GuiPose toolboxPose;

    private GuiPose toolbarPose;

    private ButtonGroup costumeButtonGroup;

    private ToggleButton selectButton;

    private Notebook toolboxNotebook;

    private Container propertiesContainer;

    private Container behaviourContainer;

    private int mode = MODE_SELECT;

    private int dragStartX;
    private int dragStartY;

    private Actor currentActor;
    private Actor highlightActor;
    private Costume currentCostume;

    private Actor stampActor;

    private RotateHandleBehaviour rotateHandle;
    private final List<ScaleHandleBehaviour> scaleHandles = new ArrayList<ScaleHandleBehaviour>();
    private final List<HandleBehaviour> handles = new ArrayList<HandleBehaviour>();

    private HandleBehaviour currentHandleBehaviour;

    public SceneDesigner( Editor editor, SceneResource sceneResource )
    {
        this.editor = editor;
        this.sceneRect = new Rect( 0, 0, editor.game.getWidth(), editor.game.getHeight() );
        this.sceneResource = sceneResource;
        try {
            this.scene = this.sceneResource.getScene();
        } catch ( Exception e ) {
            e.printStackTrace();
            this.onDone();
            return;
        }

        this.costumeButtonGroup = new ButtonGroup();

    }

    public void go()
    {
        this.createToolbar();
        this.createToolbox();

        this.actorsLayer = new ScrollableLayer( this.editor.size, this.sceneBackground, false );
        this.glassLayer = new ScrollableLayer( this.editor.size, null, false );
        this.guiLayer = new ScrollableLayer( this.editor.size, this.guiBackground, true );

        this.actorsLayer.setVisible( true );
        this.glassLayer.setVisible( true );
        this.guiLayer.setVisible( true );

        Itchy.singleton.getGameLayer().add( this.actorsLayer );
        Itchy.singleton.getGameLayer().add( this.glassLayer );
        Itchy.singleton.getGameLayer().add( this.guiLayer );

        Itchy.singleton.addKeyListener( this );

        Actor toolboxActor = this.toolboxPose.getActor();
        this.guiLayer.add( toolboxActor );

        Actor toolbarActor = this.toolbarPose.getActor();
        this.guiLayer.add( toolbarActor );

        this.actorsLayer.addMouseListener( this );

        this.scene.create( this.actorsLayer, true );

        this.createPageBorder();
        this.createHandles();

        this.setMode( MODE_SELECT );
        toolboxActor.moveTo( 0, this.editor.size.height - this.toolboxPose.getHeight() );

        this.onHome();
    }

    private void createPageBorder()
    {
        int margin = 0;
        NinePatch ninePatch = this.editor.rules.resources.getNinePatch( "pageBorder" );
        Surface newSurface = ninePatch.createSurface( this.sceneRect.width + margin * 2, this.sceneRect.height
                + margin * 2 );

        ImagePose newPose = new ImagePose( newSurface );
        newPose.setOffsetX( margin );
        newPose.setOffsetY( margin );

        Actor actor = new Actor( newPose );
        this.glassLayer.add( actor );
        if ( actor.getYAxisPointsDown() ) {
            actor.moveTo( margin, margin );
        } else {
            actor.moveTo( margin, this.sceneRect.height - margin );
        }

    }

    private void createToolbox()
    {
        this.toolboxPose = new GuiPose();
        this.toolboxPose.addStyle( "toolbox" );
        this.toolboxPose.draggable = true;

        this.toolboxPose.setRules( this.editor.rules );
        this.toolboxPose.reStyle();
        this.toolboxPose.forceLayout();
        this.toolboxPose.setPosition( 0, 0, this.editor.size.width, 200 );
        this.toolboxPose.addStyle( "semi" );

        Container costumes = new Container();
        costumes.addStyle( "costumes" );
        costumes.setLayout( new FlowLayout() );

        for ( String name : this.editor.resources.costumeNames() ) {
            CostumeResource costumeResource = this.editor.resources.getCostumeResource( name );
            this.addCostumeButton( costumes, costumeResource );
        }
        VerticalScroll costumesScroll = new VerticalScroll( costumes );

        this.propertiesContainer = new Container();
        VerticalScroll propertiesScroll = new VerticalScroll( this.propertiesContainer );

        this.behaviourContainer = new Container();
        VerticalScroll behaviourScroll = new VerticalScroll( this.behaviourContainer );

        this.toolboxNotebook = new Notebook();
        this.toolboxNotebook.addPage( new Label( "C" ), costumesScroll );
        this.toolboxNotebook.addPage( new Label( "P" ), propertiesScroll );
        this.toolboxNotebook.addPage( new Label( "B" ), behaviourScroll );

        this.toolboxPose.setFill( true, true );
        this.toolboxNotebook.setFill( true, true );
        this.toolboxNotebook.setExpansion( 1 );
        costumes.setExpansion( 1 );
        costumes.setFill( true, true );
        costumesScroll.setExpansion( 1 );
        costumesScroll.setFill( true, true );

        costumesScroll.setClientHeight( 700 );

        this.toolboxPose.addChild( this.toolboxNotebook );
    }

    private void createToolbar()
    {
        this.toolbarPose = new GuiPose();
        this.toolbarPose.addStyle( "toolbar" );

        this.toolbarPose.setRules( this.editor.rules );
        this.toolbarPose.reStyle();
        this.toolbarPose.forceLayout();

        this.addToolbarButtons( this.toolbarPose );
        // this.toolbarPose.setFill( true, true );

        this.toolbarPose.setPosition( 0, 0, this.editor.size.width, this.toolbarPose.getRequiredHeight() );
    }

    private void addToolbarButtons( Container toolbar )
    {
        Button done = new Button( "Done" );
        done.addActionListener( new ActionListener()
        {
            @Override
            public void action()
            {
                SceneDesigner.this.onDone();
            }
        } );
        toolbar.addChild( done );

        Button home = new Button( "Home" );
        home.addActionListener( new ActionListener()
        {
            @Override
            public void action()
            {
                SceneDesigner.this.onHome();
            }
        } );
        toolbar.addChild( home );

        Button actorUp = new Button( "Up" );
        actorUp.addActionListener( new ActionListener()
        {
            @Override
            public void action()
            {
                SceneDesigner.this.onActorUp();
            }
        } );
        toolbar.addChild( actorUp );

        Button actorDown = new Button( "Down" );
        actorDown.addActionListener( new ActionListener()
        {
            @Override
            public void action()
            {
                SceneDesigner.this.onActorDown();
            }
        } );
        toolbar.addChild( actorDown );

        Button actorTop = new Button( "Top" );
        actorTop.addActionListener( new ActionListener()
        {
            @Override
            public void action()
            {
                SceneDesigner.this.onActorTop();
            }
        } );
        toolbar.addChild( actorTop );

        Button actorBottom = new Button( "Bottom" );
        actorBottom.addActionListener( new ActionListener()
        {
            @Override
            public void action()
            {
                SceneDesigner.this.onActorBottom();
            }
        } );
        toolbar.addChild( actorBottom );

        Button actorDelete = new Button( "Delete" );
        actorDelete.addActionListener( new ActionListener()
        {
            @Override
            public void action()
            {
                SceneDesigner.this.onActorDelete();
            }
        } );
        toolbar.addChild( actorDelete );

        Button save = new Button( "Save" );
        save.addActionListener( new ActionListener()
        {
            @Override
            public void action()
            {
                SceneDesigner.this.onSave();
            }
        } );
        toolbar.addChild( save );

        this.selectButton = new ToggleButton( "Select" );
        this.costumeButtonGroup.add( this.selectButton );
        this.costumeButtonGroup.defaultButton = this.selectButton;
        this.selectButton.addActionListener( new ActionListener()
        {
            @Override
            public void action()
            {
                SceneDesigner.this.onSelect();
            }
        } );
        toolbar.addChild( this.selectButton );

        Button textButton = new Button( "Text" );
        textButton.addActionListener( new ActionListener()
        {
            @Override
            public void action()
            {
                SceneDesigner.this.onText();
            }
        } );
        toolbar.addChild( textButton );
    }

    private void createProperties()
    {
        GridLayout grid = new GridLayout( this.propertiesContainer, 2 );
        this.propertiesContainer.setLayout( grid );
        grid.clear();

        for ( Property<Actor, ?> property : this.currentActor.getProperties() ) {
            try {
                Component component = property.createComponent( this.currentActor, true );
                grid.addRow( property.label, component );

            } catch ( Exception e ) {
                e.printStackTrace();
            }
        }
        this.createBehaviourProperties();
    }

    private void createBehaviourProperties()
    {
        GridLayout grid = new GridLayout( this.behaviourContainer, 2 );
        this.behaviourContainer.setLayout( grid );
        grid.clear();

        StringProperty<Actor> beProp = new StringProperty<Actor>( "Behaviour", "behaviour.behaviourClassName" );
        ComponentChangeListener ccl = new ComponentChangeListener()
        {
            @Override
            public void changed()
            {
                SceneDesigner.this.createBehaviourProperties();
            }
        };

        try {
            TextBox txtBehaviourClassName = (TextBox) beProp.createComponent( this.currentActor, true, ccl );
            grid.addRow( beProp.label, txtBehaviourClassName );
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        SceneDesignerBehaviour behaviour = (SceneDesignerBehaviour) this.currentActor.getBehaviour();
        for ( Property<Behaviour, ?> property : behaviour.actualBehaviour.getProperties() ) {
            try {
                Component component = property.createComponent( behaviour.actualBehaviour, true );
                grid.addRow( property.label, component );

            } catch ( Exception e ) {
                e.printStackTrace();
            }
        }

    }

    private void deleteProperties()
    {
        this.propertiesContainer.clear();
        this.behaviourContainer.clear();
    }

    private void addCostumeButton( Container container, final CostumeResource costumeResource )
    {
        Surface surface = costumeResource.getThumbnail();
        if ( surface != null ) {
            ImageComponent img = new ImageComponent( surface );

            ToggleButton button = new ToggleButton( img );
            button.addActionListener( new ActionListener()
            {
                @Override
                public void action()
                {
                    SceneDesigner.this.onSelectCostume( costumeResource );
                }
            } );
            this.costumeButtonGroup.add( button );
            container.addChild( button );
        }
    }

    private void setMode( int mode )
    {
        if ( mode == this.mode ) {
            return;
        }

        this.mode = mode;
        this.deleteStampActor();

        if ( this.mode == MODE_SELECT ) {
            this.costumeButtonGroup.select( this.selectButton );
        }
    }

    private void onSelectCostume( CostumeResource costumeResource )
    {
        this.deleteStampActor();
        this.selectActor( null );
        this.currentCostume = costumeResource.costume;
        this.setMode( MODE_STAMP_COSTUME );
        this.createStampActor();
    }

    private void onSelect()
    {
        this.setMode( MODE_SELECT );
    }

    private void onDone()
    {
        this.actorsLayer.removeMouseListener( this );
        this.toolboxPose.destroy();
        this.toolbarPose.destroy();
        this.editor.mainGuiPose.show();
    }

    @Override
    public boolean onKeyDown( KeyboardEvent event )
    {

        if ( Itchy.singleton.isCtrlDown() ) {

            int scrollAmount = Itchy.singleton.isShiftDown() ? 100 : 10;

            if ( event.symbol == Keys.s ) {
                this.onSave();
            } else if ( event.symbol == Keys.LEFT ) {
                this.actorsLayer.scrollBy( -scrollAmount, 0 );
                this.glassLayer.scrollBy( -scrollAmount, 0 );
            } else if ( event.symbol == Keys.RIGHT ) {
                this.actorsLayer.scrollBy( scrollAmount, 0 );
                this.glassLayer.scrollBy( scrollAmount, 0 );
            } else if ( event.symbol == Keys.UP ) {
                this.actorsLayer.scrollBy( 0, scrollAmount );
                this.glassLayer.scrollBy( 0, scrollAmount );
            } else if ( event.symbol == Keys.DOWN ) {
                this.actorsLayer.scrollBy( 0, -scrollAmount );
                this.glassLayer.scrollBy( 0, -scrollAmount );
            }

        } else {

            int moveAmount = Itchy.singleton.isShiftDown() ? 10 : 1;

            if ( event.symbol == Keys.DELETE ) {
                this.onActorDelete();
                return true;
            } else if ( event.symbol == Keys.KEY_1 ) {
                this.toolboxNotebook.selectPage( 0 );
            } else if ( event.symbol == Keys.KEY_2 ) {
                this.toolboxNotebook.selectPage( 1 );
            } else if ( event.symbol == Keys.KEY_3 ) {
                this.toolboxNotebook.selectPage( 2 );
            } else if ( event.symbol == Keys.PAGEUP ) {
                this.onActorUp();
            } else if ( event.symbol == Keys.PAGEDOWN ) {
                this.onActorDown();
            } else if ( event.symbol == Keys.HOME ) {
                this.onActorTop();
            } else if ( event.symbol == Keys.END ) {
                this.onActorBottom();
            } else if ( event.symbol == Keys.o ) {
                this.onHome();
            } else if ( event.symbol == Keys.LEFT ) {
                this.moveActor( -moveAmount, 0 );
            } else if ( event.symbol == Keys.RIGHT ) {
                this.moveActor( moveAmount, 0 );
            } else if ( event.symbol == Keys.UP ) {
                this.moveActor( 0, moveAmount );
            } else if ( event.symbol == Keys.DOWN ) {
                this.moveActor( 0, -moveAmount );
            }

        }

        return false;
    }

    private void moveActor( int dx, int dy )
    {
        if ( this.currentActor != null ) {
            this.currentActor.moveBy(  dx, dy );
        }
    }

    @Override
    public boolean onKeyUp( KeyboardEvent event )
    {
        return false;
    }

    @Override
    public boolean onMouseDown( MouseButtonEvent event )
    {
        if ( ( event.button == 2 ) || ( ( event.button == 1 ) && Itchy.singleton.isCtrlDown() ) ) {
            this.setMode( MODE_DRAG_SCROLL );
            this.beginDrag( event );
            return true;
        }

        if ( event.button != 1 ) {
            return false;
        }

        if ( this.mode == MODE_SELECT ) {

            for ( HandleBehaviour handleBehaviour : this.handles ) {
                Actor actor = handleBehaviour.getActor();

                if ( actor.touching( event.x, event.y ) ) {
                    this.beginDrag( event );
                    handleBehaviour.dragStart();
                    this.currentHandleBehaviour = handleBehaviour;
                    this.setMode( MODE_DRAG_HANDLE );
                    this.hideHighlightActor();
                    return true;
                }
            }

            boolean fromBottom = Itchy.singleton.isShiftDown();
            for ( Actor actor : fromBottom ? this.actorsLayer.getActors() : Reversed
                    .list( this.actorsLayer.getActors() ) ) {
                if ( actor.touching( event.x, event.y ) ) {
                    this.selectActor( actor );
                    this.setMode( MODE_DRAG_ACTOR );
                    this.beginDrag( event );
                    return true;
                }
            }
            this.selectActor( null );
            return true;

        }

        if ( this.mode == MODE_STAMP_COSTUME ) {
            Actor actor;

            SceneDesignerBehaviour behaviour = new SceneDesignerBehaviour();
            String behaviourClassName;
            if ( this.stampActor.getAppearance().getPose() instanceof TextPose ) {
                actor = new Actor( this.stampActor.getAppearance().getPose() );
                behaviourClassName = NullBehaviour.class.getName();

            } else {
                actor = new Actor( this.currentCostume );
                behaviourClassName = this.stampActor.getCostume().behaviourClassName;
            }
            try {
                behaviour.setBehaviourClassName( behaviourClassName );
            } catch ( Exception e ) {
                e.printStackTrace();
            }

            actor.moveTo( event.x, event.y );
            actor.setBehaviour( behaviour );
            this.actorsLayer.add( actor );

            if ( !Itchy.singleton.isShiftDown() ) {
                this.setMode( MODE_SELECT );
                this.selectActor( actor );
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean onMouseUp( MouseButtonEvent event )
    {
        if ( ( this.mode == MODE_DRAG_HANDLE ) || ( this.mode == MODE_DRAG_ACTOR ) ) {
            this.createProperties();
        }

        if ( this.mode == MODE_DRAG_HANDLE ) {
            this.currentHandleBehaviour.dragEnd();
            this.currentHandleBehaviour = null;
            this.setMode( MODE_SELECT );
            this.selectActor( this.currentActor );
        }

        if ( ( this.mode == MODE_DRAG_SCROLL ) || ( this.mode == MODE_DRAG_ACTOR ) ) {
            this.setMode( MODE_SELECT );
        }

        return false;
    }

    @Override
    public boolean onMouseMove( MouseMotionEvent event )
    {
        int dx = event.x - this.dragStartX;
        int dy = event.y - this.dragStartY;

        if ( this.mode == MODE_STAMP_COSTUME ) {
            this.stampActor.moveTo( event.x, event.y );

            return true;

        } else if ( this.mode == MODE_DRAG_SCROLL ) {

            this.actorsLayer.scrollBy( -dx, -dy );
            this.glassLayer.scrollBy( -dx, -dy );
            // this.beginDrag( event );

            return true;

        } else if ( this.mode == MODE_DRAG_ACTOR ) {
            this.currentActor.moveBy( dx, dy );
            this.beginDrag( event );

        } else if ( this.mode == MODE_DRAG_HANDLE ) {
            this.currentHandleBehaviour.moveBy( dx, dy );
            this.beginDrag( event );

        }

        return false;
    }

    private void beginDrag( MouseEvent event )
    {
        this.dragStartX = event.x;
        this.dragStartY = event.y;
    }

    private void selectActor( Actor actor )
    {
        this.currentActor = actor;
        this.deleteHighlightActor();
        this.deleteProperties();

        if ( actor != null ) {

            this.createProperties();
            this.createHightlightActor();
        }
    }

    private void createStampActor()
    {
        assert ( this.stampActor == null );
        this.stampActor = new Actor( this.currentCostume );
        this.stampActor.moveTo( -10000, -10000 ); // Anywhere off screen
        this.stampActor.getAppearance().setAlpha( 128 );
        this.glassLayer.add( this.stampActor );
    }

    private void deleteStampActor()
    {
        if ( this.stampActor != null ) {
            this.glassLayer.remove( this.stampActor );
            this.stampActor = null;
        }
    }

    private void onHome()
    {
        int x = 0;
        int y = this.sceneRect.height - this.editor.size.height + this.toolbarPose.getHeight();
        this.actorsLayer.scrollTo( x, y );
        this.glassLayer.scrollTo( x, y );
    }

    private void onActorUp()
    {
        if ( ( this.mode == MODE_SELECT ) && ( this.currentActor != null ) ) {
            this.currentActor.zOrderUp();
        }
    }

    private void onActorDown()
    {
        if ( ( this.mode == MODE_SELECT ) && ( this.currentActor != null ) ) {
            this.currentActor.zOrderDown();
        }
    }

    private void onActorTop()
    {
        if ( ( this.mode == MODE_SELECT ) && ( this.currentActor != null ) ) {
            this.currentActor.zOrderTop();
        }
    }

    private void onActorBottom()
    {
        if ( ( this.mode == MODE_SELECT ) && ( this.currentActor != null ) ) {
            this.currentActor.zOrderBottom();
        }
    }

    private void onActorDelete()
    {
        if ( ( this.mode == MODE_SELECT ) && ( this.currentActor != null ) ) {
            this.currentActor.kill();
            this.selectActor( null );
        }
    }

    private void onSave()
    {
        Scene scene = new Scene();

        for ( Actor actor : this.actorsLayer.getActors() ) {
            SceneActor sceneActor = SceneActor.createSceneActor( actor );
            scene.sceneActors.add( sceneActor );
        }

        this.sceneResource.setScene( scene );
        try {
            this.sceneResource.save();
        } catch ( Exception e ) {
            e.printStackTrace();
            MessageBox.show( "Error", "Save failed." );
        }
    }

    private void onText()
    {
        Set<String> names = this.editor.resources.fontNames();
        if ( names.size() == 0 ) {
            return;
        }
        for ( String name : names ) {
            this.selectActor( null );
            this.setMode( MODE_STAMP_COSTUME );

            Font font = this.editor.resources.getFont( name );
            String text = "newText";

            TextPose pose = new TextPose( text, font, 22 );
            this.stampActor = new Actor( pose );

            this.stampActor.moveTo( -10000, -10000 ); // Anywhere off screen
            this.glassLayer.add( this.stampActor );
            break;
        }

    }

    private void createHightlightActor()
    {
        Surface actorSurface = this.currentActor.getAppearance().getSurface();

        int margin = 10;
        NinePatch ninePatch = this.editor.rules.resources.getNinePatch( "highlight" );
        Surface newSurface = ninePatch.createSurface( actorSurface.getWidth() + margin * 2, actorSurface.getHeight()
                + margin * 2 );

        ImagePose newPose = new ImagePose( newSurface );
        newPose.setOffsetX( this.currentActor.getAppearance().getOffsetX() + margin );
        newPose.setOffsetY( this.currentActor.getAppearance().getOffsetY() + margin );

        this.highlightActor = new Actor( newPose );
        this.highlightActor.moveTo( this.currentActor );
        this.glassLayer.add( this.highlightActor );
        this.highlightActor.setBehaviour( new FollowBehaviour( this.currentActor ) );
        this.highlightActor.activate();

        for ( ScaleHandleBehaviour be : this.scaleHandles ) {
            be.setTarget( this.currentActor );
            be.getActor().getAppearance().setAlpha( 255 );
        }
        this.rotateHandle.setTarget( this.currentActor );
        this.rotateHandle.getActor().getAppearance().setAlpha( 255 );

    }

    private void hideHighlightActor()
    {
        for ( ScaleHandleBehaviour be : this.scaleHandles ) {
            be.getActor().getAppearance().setAlpha( 0 );
        }
        this.rotateHandle.getActor().getAppearance().setAlpha( 0 );
        this.highlightActor.getAppearance().setAlpha( 0 );
    }

    private void deleteHighlightActor()
    {
        if ( this.highlightActor != null ) {
            this.highlightActor.kill();
            this.highlightActor = null;
        }
        for ( ScaleHandleBehaviour be : this.scaleHandles ) {
            be.setTarget( null );
        }
        this.rotateHandle.setTarget( null );
    }

    private void createHandles()
    {

        ImagePose pose = this.editor.rules.resources.getPose( "rotateHandle" );
        Actor rotateActor = new Actor( pose );
        this.rotateHandle = new RotateHandleBehaviour();
        rotateActor.setBehaviour( this.rotateHandle );
        rotateActor.getAppearance().setAlpha( 0 );
        rotateActor.activate();
        this.glassLayer.add( rotateActor );
        this.handles.add( this.rotateHandle );

        pose = this.editor.rules.resources.getPose( "scaleHandle" );
        for ( int dx = -1; dx < 2; dx += 2 ) {
            for ( int dy = -1; dy < 2; dy += 2 ) {
                Actor scaleHandle = new Actor( pose );
                ScaleHandleBehaviour behaviour = new ScaleHandleBehaviour( dx, dy );
                scaleHandle.setBehaviour( behaviour );
                scaleHandle.getAppearance().setAlpha( 0 );
                scaleHandle.activate();

                this.scaleHandles.add( behaviour );
                this.handles.add( behaviour );
                this.glassLayer.add( scaleHandle );
            }
        }
        this.scaleHandles.get( 0 ).opposite = this.scaleHandles.get( 3 );
        this.scaleHandles.get( 3 ).opposite = this.scaleHandles.get( 0 );
        this.scaleHandles.get( 1 ).opposite = this.scaleHandles.get( 2 );
        this.scaleHandles.get( 2 ).opposite = this.scaleHandles.get( 1 );

    }

    abstract class HandleBehaviour extends Behaviour
    {
        Actor target;

        int startX;
        int startY;

        boolean dragging = false;

        public void setTarget( Actor target )
        {
            this.target = target;
            if ( target == null ) {
                this.getActor().getAppearance().setAlpha( 0 );
            } else {
                this.getActor().getAppearance().setAlpha( 255 );
            }
        }

        public void dragStart()
        {
            this.startX = (int) this.actor.getX();
            this.startY = (int) this.actor.getY();
            this.dragging = true;
        }

        public void dragEnd()
        {
            this.dragging = false;
        }

        public void moveBy( int dx, int dy )
        {
            this.actor.moveBy( dx, dy );
        }
    }

    class ScaleHandleBehaviour extends HandleBehaviour
    {
        int cornerX;
        int cornerY;
        double startScale;
        double startTargetX;
        double startTargetY;
        ScaleHandleBehaviour opposite;

        public ScaleHandleBehaviour( int dx, int dy )
        {
            this.cornerX = dx;
            this.cornerY = dy;
        }

        @Override
        public void dragStart()
        {
            super.dragStart();
            if ( this.target.getAppearance().getPose() instanceof TextPose ) {
                this.startScale = ( (TextPose) ( this.target.getAppearance().getPose() ) ).getFontSize();
            } else {
                this.startScale = this.target.getAppearance().getScale();
            }
            this.startTargetX = this.target.getX();
            this.startTargetY = this.target.getY();
        }

        @Override
        public void tick()
        {
            if ( SceneDesigner.this.mode == MODE_DRAG_HANDLE ) {
                return;
            }

            if ( this.target != null ) {
                double x = this.target.getCornerX();
                double y = this.target.getCornerY();
                if ( this.cornerX > 0 ) {
                    x += this.target.getAppearance().getWidth();
                }
                if ( this.cornerY > 0 ) {
                    y += this.target.getAppearance().getHeight();
                }
                this.getActor().moveTo( x, y );
            }
        }

        @Override
        public void moveBy( int dx, int dy )
        {
            assert ( this.target != null );

            double ratioX = ( this.target.getX() - this.opposite.actor.getX() )
                    / ( this.actor.getX() - this.opposite.actor.getX() );
            double ratioY = ( this.target.getY() - this.opposite.actor.getY() )
                    / ( this.actor.getY() - this.opposite.actor.getY() );

            super.moveBy( dx, dy );

            Actor other = Itchy.singleton.isShiftDown() ? this.target : this.opposite.getActor();

            double scaleX = ( other.getX() - this.actor.getX() ) / ( other.getX() - this.startX );
            double scaleY = ( other.getY() - this.actor.getY() ) / ( other.getY() - this.startY );
            double scale = Math.min( scaleX, scaleY );

            if ( !Itchy.singleton.isShiftDown() ) {
                this.target.moveBy( dx * ratioX, dy * ratioY );
            }

            if ( this.target.getAppearance().getPose() instanceof TextPose ) {
                TextPose pose = (TextPose) this.target.getAppearance().getPose();
                pose.setFontSize( this.startScale * scale );
            } else {
                this.target.getAppearance().setScale( this.startScale * scale );
            }
        }

    }

    class RotateHandleBehaviour extends HandleBehaviour
    {
        @Override
        public void moveBy( int dx, int dy )
        {
            super.moveBy( dx, dy );

            double tx = this.actor.getX() - this.target.getX();
            double ty = this.actor.getY() - this.target.getY();

            double angle = Math.atan2( ty, tx );
            this.actor.getAppearance().setDirectionRadians( angle );
            this.target.getAppearance().setDirectionRadians( angle );

        }

        @Override
        public void tick()
        {
            if ( this.dragging ) {
                return;
            }

            if ( this.target != null ) {
                this.actor.moveTo( this.target );
                this.actor.getAppearance().setDirection( this.target.getAppearance().getDirection() );
                this.actor.moveForward( 30 );
            }
        }

    }

}
