package uk.co.nickthecoder.drunkinvaders;

import java.text.DecimalFormat;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.ActorCollisionStrategy;
import uk.co.nickthecoder.itchy.Game;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.MultiLineTextPose;
import uk.co.nickthecoder.itchy.Scene;
import uk.co.nickthecoder.itchy.ScrollableLayer;
import uk.co.nickthecoder.itchy.animation.AlphaAnimation;
import uk.co.nickthecoder.itchy.animation.CompoundAnimation;
import uk.co.nickthecoder.itchy.animation.NumericAnimation;
import uk.co.nickthecoder.itchy.editor.Editor;
import uk.co.nickthecoder.itchy.neighbourhood.NeighbourhoodCollisionStrategy;
import uk.co.nickthecoder.itchy.neighbourhood.Neighbourhood;
import uk.co.nickthecoder.itchy.neighbourhood.StandardNeighbourhood;
import uk.co.nickthecoder.itchy.util.TextBehaviour;
import uk.co.nickthecoder.jame.Keys;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.event.KeyboardEvent;

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

    private int levelNumber = 1;

    private Neighbourhood neighbourhood;

    private TextBehaviour info;

    public boolean fadingOut = false;

    public DrunkInvaders()
    {
        this.neighbourhood = new StandardNeighbourhood(NEIGHBOURHOOD_SQUARE_SIZE);
    }

    private void go() throws Exception
    {
        Itchy.singleton.init(this);

        this.metronomeCountdown = 0;
        this.metronome = 20;

        this.resources.load(RESOURCES);

        this.mainLayer = new ScrollableLayer(new Rect(0, 0, 640, 480), null, false);
        this.mainLayer.centerOn(320, 240);
        this.mainLayer.enableMouseListener();
        Itchy.singleton.getGameLayer().add(this.mainLayer);

        this.glassLayer = new ScrollableLayer(new Rect(0, 0, 640, 480), null, false);
        Itchy.singleton.getGameLayer().add(this.glassLayer);

        this.fadeLayer = new ScrollableLayer(new Rect(0, 0, 640, 480), null, false);
        Itchy.singleton.getGameLayer().add(this.fadeLayer);

        this.fadeActor = new Actor(this.resources.getPose("white"));
        this.fadeActor.getAppearance().setAlpha(0);
        this.fadeActor.activate();
        this.fadeLayer.add(this.fadeActor);

        this.startScene("menu");
        loop();

    }

    @Override
    public boolean onKeyDown( KeyboardEvent ke )
    {
        if (ke.isReleased()) {
            return false;
        }

        if (ke.symbol == Keys.F1) {
            debug();
        }

        if (ke.symbol == Keys.F2) {
            toggleInfo();
        }

        if ("levels".equals(this.sceneName)) {
            if (ke.symbol == Keys.RETURN) {
                this.play();
                return true;
            }
        }

        if ("menu".equals(this.sceneName)) {
            if (ke.symbol == Keys.ESCAPE) {
                Itchy.singleton.terminate();
                return true;
            }

            if ((ke.symbol == Keys.p) || (ke.symbol == Keys.RETURN)) {
                this.startScene("levels");
                return true;
            }

            if (ke.symbol == Keys.a) {
                this.startScene("about");
            }

        } else {
            if (ke.symbol == Keys.ESCAPE) {
                this.startScene("menu");
            }
            return true;
        }

        return super.onKeyDown(ke);
    }

    @Override
    public boolean onKeyUp( KeyboardEvent ke )
    {
        return false;
    }

    @Override
    public void tick()
    {
        if (this.metronomeCountdown == 0) {
            this.metronomeCountdown = this.metronome;
        }

        this.metronomeCountdown--;
    }

    public void toggleInfo()
    {
        if (this.info == null) {

            this.info = new TextBehaviour( new MultiLineTextPose( this.resources.getFont("vera"), 16 ) )
            {
                public void tick() {
                    this.setText(
                        "Aliens Remaining : " + DrunkInvaders.this.aliensRemaining + "\n" +
                        "Dropped Frames   : " + Itchy.singleton.frameRate.getDroppedFrames()
                    );
                }
            };
            this.info.textPose.setAlignment( 0,0 );
            this.info.createActor().moveTo(40, 460);
            this.glassLayer.add(this.info.getActor());
            this.info.getActor().activate();

        } else {
            this.info.getActor().kill();
            this.info = null;
        }
    }

    public ActorCollisionStrategy createCollisionStrategy( Actor actor )
    {
        //Appearance appearance = actor.getAppearance();
        //if ( appearance.getWidth() > this.neighbourhood.getSquareSize() )  {
            return new NeighbourhoodCollisionStrategy(actor, this.neighbourhood);
        //} else {
        //    return new SinglePointCollisionStrategy(actor, this.neighbourhood);
        //}
    }

    public void startScene( String sceneName )
    {
        System.out.println("Starting scene " + sceneName);

        if (this.fadingOut) {
            return;
        }

        this.neighbourhood.clear();

        this.sceneName = sceneName;

        AlphaAnimation fadeOut = new AlphaAnimation(15, NumericAnimation.linear, 0, 255);
        AlphaAnimation fadeIn = new AlphaAnimation(15, NumericAnimation.linear, 255, 0);
        CompoundAnimation animation = new CompoundAnimation(true);
        animation.addAnimation(fadeOut);
        animation.addAnimation(fadeIn);

        fadeOut.setFinishedMessage( "fadedOut" );
        fadeOut.addMessageListener( this );
        
        fadeIn.setFinishedMessage( "fadedIn" );
        fadeIn.addMessageListener( this );
        
        this.fadingOut = true;
        this.fadeActor.setAnimation(animation);
    }
    
    public void onMessage( String message )
    {
        if ( "fadedIn".equals( message ) ) {
            DrunkInvaders.this.fadingOut = false;

        } else if ( "fadedOut".equals( message ) ) {

            this.mainLayer.clear();
            Itchy.singleton.completeTasks();
            this.fadingOut = false;
            try {
                Scene scene = this.resources.getScene(this.sceneName);
                if ( scene == null) {
                    this.sceneName = "completed";
                    this.levelNumber = 1;
                    scene =  this.resources.getScene(this.sceneName);
                }
                scene.create(DrunkInvaders.this.mainLayer, false);
                Itchy.showMousePointer(scene.showMouse);

            } catch (Exception e) {
                throw new RuntimeException( e );
            }
            
        }
    }
    
    public void addAliens( int n )
    {
        this.aliensRemaining += n;

        // We only care when the last alien was kill during play, not when fading the scene out.
        if (this.fadingOut) {
            return;
        }

        if (this.aliensRemaining == 0) {
            getPreferences().putBoolean("completedLevel" + this.levelNumber, true);

            this.levelNumber += 1;
            this.play();

        }
    }

    public boolean completedLevel( int level )
    {
        return getPreferences().getBoolean("completedLevel" + level, false);
    }

    public void play( int levelNumber )
    {
        this.levelNumber = levelNumber;
        this.play();
    }

    public void play()
    {
        DecimalFormat df = new DecimalFormat("00");
        this.startScene("level" + df.format(this.levelNumber));
    }

    public void action( String action )
    {
        if ("play".equals(action)) {
            this.startScene("levels");

        } else if ("menu".equals(action)) {
            this.startScene("menu");

        } else if ("about".equals(action)) {
            this.startScene("about");

        } else if ("editor".equals(action)) {
            this.startEditor();

        } else if ("quit".equals(action)) {
            Itchy.singleton.terminate();
        }
    }

    private void startEditor()
    {
        try {
            Editor editor = new Editor(DrunkInvaders.singleton);
            editor.init();
            editor.go();
            this.go();
        } catch (Exception e) {
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
        this.neighbourhood.debug();
    }

    public static void main( String argv[] )
    {
        System.out.println("Welcome to Drunk Invaders");

        try {
            if ((argv.length == 1) && ("--editor".equals(argv[0]))) {

                Editor editor = new Editor(DrunkInvaders.singleton);
                editor.init();
                DrunkInvaders.singleton.resources.load(RESOURCES);
                editor.go();

            } else {
                DrunkInvaders.singleton.go();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Goodbye from Drunk Invaders");
    }

}
