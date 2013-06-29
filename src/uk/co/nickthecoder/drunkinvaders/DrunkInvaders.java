package uk.co.nickthecoder.drunkinvaders;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Game;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Scene;
import uk.co.nickthecoder.itchy.ScrollableLayer;
import uk.co.nickthecoder.itchy.animation.AlphaAnimation;
import uk.co.nickthecoder.itchy.animation.AnimationListener;
import uk.co.nickthecoder.itchy.animation.CompoundAnimation;
import uk.co.nickthecoder.itchy.animation.NumericAnimation;
import uk.co.nickthecoder.itchy.editor.Editor;
import uk.co.nickthecoder.jame.Keys;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.event.KeyboardEvent;

import uk.co.nickthecoder.itchy.neighbourhood.ActorCollisionStrategy;
import uk.co.nickthecoder.itchy.neighbourhood.Neighbourhood;
import uk.co.nickthecoder.itchy.neighbourhood.SinglePointCollisionStrategy;
import uk.co.nickthecoder.itchy.util.FPSBehaviour;

public class DrunkInvaders extends Game
{
    public static final String RESOURCES = "resources/drunkInvaders/drunkInvaders.xml";

    public static final int NEIGHBOURHOOD_SQUARE_SIZE = 60;

    public static DrunkInvaders singleton = new DrunkInvaders();

    public ScrollableLayer mainLayer;

    public ScrollableLayer glassLayer;

    public ScrollableLayer fadeLayer;

    public int metronome;

    public int metronomeCountdown;

    private String sceneName = null;

    private Actor fadeActor;

    private int aliensRemaining;

    private int levelNumber;

    private final Set<Integer> completedLevels = new HashSet<Integer>();

    private Neighbourhood neighbourhood;

    private Actor fpsActor;
    
    public DrunkInvaders()
    {
        neighbourhood = new Neighbourhood( NEIGHBOURHOOD_SQUARE_SIZE );
    }

    private void go()
        throws Exception
    {
        Itchy.singleton.init( this );

        this.metronomeCountdown = 0;
        this.metronome = 20;

        this.resources.load( RESOURCES );

        this.mainLayer = new ScrollableLayer( new Rect( 0, 0, 640, 480 ), null, false );
        this.mainLayer.centerOn( 320, 240 );
        this.mainLayer.enableMouseListener();
        Itchy.singleton.getGameLayer().add( this.mainLayer );

        this.glassLayer = new ScrollableLayer( new Rect( 0, 0, 640, 480 ), null, false );
        Itchy.singleton.getGameLayer().add( this.glassLayer );

        this.fadeLayer = new ScrollableLayer( new Rect( 0, 0, 640, 480 ), null, false );
        Itchy.singleton.getGameLayer().add( this.fadeLayer );

        this.fadeActor = new Actor( this.resources.getPose( "white" ) );
        this.fadeActor.getAppearance().setAlpha( 0 );
        this.fadeActor.activate();
        this.fadeLayer.add( this.fadeActor );

        this.levelNumber = 1;
        
        Itchy.singleton.addEventListener( this );
        this.startScene( "menu" );
        Itchy.singleton.loop();

    }

    @Override
    public boolean onKeyDown( KeyboardEvent ke )
    {
        if ( ke.isReleased() ) {
            return false;
        }


    	if ( ke.symbol == Keys.F1 ) {
    		debug();
    	}
    	
    	if ( ke.symbol == Keys.F2 ) {
    		toggleFPS();
    	}
    	
        if ( "levels".equals( this.sceneName ) ) {
            if ( ke.symbol == Keys.RETURN ) {
                this.play();
                return true;
            }
        }

        if ( "menu".equals( this.sceneName ) ) {
            if ( ke.symbol == Keys.ESCAPE ) {
                Itchy.singleton.terminate();
                return true;
            }

            if ( (ke.symbol == Keys.p) || (ke.symbol == Keys.RETURN) ) {
                this.startScene( "levels" );
                return true;
            }

            if ( ke.symbol == Keys.a ) {
                this.startScene( "about" );
            }

        } else {
            if ( ke.symbol == Keys.ESCAPE ) {
                this.startScene( "menu" );
            }
            return true;
        }

        return super.onKeyDown( ke );
    }

    @Override
    public boolean onKeyUp( KeyboardEvent ke )
    {
        return false;
    }

    @Override
    public void tick()
    {
        if ( this.metronomeCountdown == 0 ) {
            this.metronomeCountdown = this.metronome;
        }

        this.metronomeCountdown--;
    }
    
    public void toggleFPS()
    {
    	if (fpsActor == null) {
    		System.out.println( "Showing fps" );
    		fpsActor = FPSBehaviour.createActor( resources.getFont("vera"),  16 );
    		fpsActor.moveTo( 60, 460 );
    		glassLayer.add( fpsActor );
    		fpsActor.activate();
    	} else {
    		System.out.println( "Hiding fps" );
    		fpsActor.kill();
    	}
    }
    
    public ActorCollisionStrategy createCollisionStrategy( Actor actor )
    {
    	SinglePointCollisionStrategy spcs = new SinglePointCollisionStrategy( actor, this.neighbourhood );
    	//BruteForceActorCollisionStrategy bfcs = new BruteForceActorCollisionStrategy( actor );
        //return new DebugCollisionStrategy( spcs,  bfcs ); 
    	return spcs;
    }

    public boolean startScene( String sceneName )
    {
        System.out.println( "Starting scene " + sceneName );

        this.neighbourhood.clear();
        
        this.sceneName = sceneName;
        this.aliensRemaining = 0;

        try {
            final Scene scene = this.resources.getScene( this.sceneName );
            if (scene == null)  {
                return false;
            }

            AlphaAnimation fadeOut = new AlphaAnimation( 15, NumericAnimation.linear, 0, 255 );
            AlphaAnimation fadeIn = new AlphaAnimation( 15, NumericAnimation.linear, 255, 0 );
            CompoundAnimation animation = new CompoundAnimation( true );
            animation.addAnimation( fadeOut );
            animation.addAnimation( fadeIn );

            fadeOut.addAnimationListener( new AnimationListener()
            {
                @Override
                public void finished()
                {
                    DrunkInvaders.this.mainLayer.clear();
                    scene.create( DrunkInvaders.this.mainLayer, false );

                }
            } );

            this.fadeActor.setAnimation( animation );

        } catch ( Exception e ) {
            e.printStackTrace();
        }

        return true;
    }

    public void addAliens( int n )
    {
        this.aliensRemaining += n;
        if ( this.aliensRemaining == 0 ) {
            this.completedLevels.add( this.levelNumber );
            this.levelNumber += 1;

            if ( ! this.play() ) {
                this.startScene( "completed" );
            }
        }
    }

    public boolean completedLevel( int level )
    {
        return this.completedLevels.contains( level );
    }

    public void play( int levelNumber )
    {
        this.levelNumber = levelNumber;
        this.play();
    }

    public boolean play()
    {
        DecimalFormat df = new DecimalFormat( "00" );
        return this.startScene(  "level" + df.format( this.levelNumber ) );
    }

    public void action( String action )
    {
        if ( "play".equals( action ) ) {
            this.startScene( "levels" );

        } else if ( "menu".equals( action ) ) {
            this.startScene( "menu" );

        } else if ( "about".equals( action ) ) {
            this.startScene( "about" );

        } else if ( "editor".equals( action ) ) {
            this.startEditor();

        } else if ( "quit".equals( action  ) ) {
            Itchy.singleton.terminate();
        }
    }

    private void startEditor()
    {
        try {
            Editor editor = new Editor( DrunkInvaders.singleton );
            editor.init();
            editor.go();
            this.go();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    @Override
    public int getWidth()
    {
        return 640;
    }

    @Override
    public int getHeight()
    {
        return 480;
    }

    @Override
    public String getTitle()
    {
        return "Drunk Invaders";
    }

    @Override
    public String getIconFilename()
    {
        return "resources/drunkInvaders/icon.bmp";
    }
    
    public void debug()
    {
    	neighbourhood.debug();
    }

    


    public static void main( String argv[] )
    {
        System.out.println( "Welcome to Drunk Invaders" );

        try {
            if ( (argv.length == 1) && ("--editor".equals( argv[0] )) ) {
            
                Editor editor = new Editor( DrunkInvaders.singleton );
                editor.init();
                DrunkInvaders.singleton.resources.load( RESOURCES );
                editor.go();
            

            } else {
                DrunkInvaders.singleton.go();

            }

        } catch ( Exception e ) {
            e.printStackTrace();
        }
        System.out.println( "Goodbye from Drunk Invaders" );
    }
}
