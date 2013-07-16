/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
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
import uk.co.nickthecoder.itchy.neighbourhood.Neighbourhood;
import uk.co.nickthecoder.itchy.neighbourhood.NeighbourhoodCollisionStrategy;
import uk.co.nickthecoder.itchy.neighbourhood.StandardNeighbourhood;
import uk.co.nickthecoder.itchy.util.TextBehaviour;
import uk.co.nickthecoder.jame.Keys;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.event.KeyboardEvent;

public class DrunkInvaders extends Game
{
    public static final String RESOURCES = "resources/drunkInvaders/drunkInvaders.xml";

    public static final int NEIGHBOURHOOD_SQUARE_SIZE = 60;

    public static DrunkInvaders game;

    
    
    public ScrollableLayer backgroundLayer;

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

    public DrunkInvaders() throws Exception
    {
        Itchy.singleton.init(this);
        this.resources.load(RESOURCES);

        this.neighbourhood = new StandardNeighbourhood(NEIGHBOURHOOD_SQUARE_SIZE);
        Rect screenSize = new Rect(0, 0, 640, 480);

        this.mainLayer = new ScrollableLayer("main", screenSize);
        this.mainLayer.centerOn(320, 240);

        this.backgroundLayer = new ScrollableLayer("background", screenSize);
        this.backgroundLayer.centerOn(320, 240);

        this.glassLayer = new ScrollableLayer("glass", screenSize);
        this.fadeLayer = new ScrollableLayer("fade", screenSize);

        this.fadeActor = new Actor(this.resources.getPose("white"));
        this.fadeActor.getAppearance().setAlpha(0);
        this.fadeActor.activate();
        this.fadeLayer.add(this.fadeActor);

        this.layers.add(this.backgroundLayer);
        this.layers.add(this.mainLayer);
        this.layers.add(this.glassLayer);
        this.layers.add(this.fadeLayer);

        this.glassLayer.locked = true;
        this.fadeLayer.locked = true;

    }

    @Override
    public void init()
    {
        this.metronomeCountdown = 0;
        this.metronome = 20;

        this.mainLayer.disableMouseListener();
        this.mainLayer.enableMouseListener();

        this.startScene("menu");
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

            if (ke.symbol == Keys.w) {
                this.startScene("completed");
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

            this.info = new TextBehaviour(new MultiLineTextPose(this.resources.getFont("vera"), 16))
            {
                @Override
                public void tick()
                {
                    this.setText(
                        "Aliens Remaining : " + DrunkInvaders.this.aliensRemaining + "\n" +
                            "Dropped Frames   : " + Itchy.singleton.frameRate.getDroppedFrames()
                        );
                }
            };
            this.info.textPose.setAlignment(0, 0);
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
        return new NeighbourhoodCollisionStrategy(actor, this.neighbourhood);
    }

    public void startScene( String sceneName )
    {
        System.out.println("Starting scene " + sceneName);

        if (this.fadingOut) {
            return;
        }

        this.neighbourhood.clear();

        this.sceneName = sceneName;

        AlphaAnimation fadeOut = new AlphaAnimation(15, NumericAnimation.linear, 255);
        AlphaAnimation fadeIn = new AlphaAnimation(15, NumericAnimation.linear, 0);
        CompoundAnimation animation = new CompoundAnimation(true);
        animation.addAnimation(fadeOut);
        animation.addAnimation(fadeIn);

        fadeOut.setFinishedMessage("fadedOut");
        fadeOut.addMessageListener(this);

        fadeIn.setFinishedMessage("fadedIn");
        fadeIn.addMessageListener(this);

        this.fadingOut = true;
        this.fadeActor.setAnimation(animation);
    }

    @Override
    public void onMessage( String message )
    {
        if ("play".equals(message)) {
            startScene("levels");

        } else if ("menu".equals(message)) {
            startScene("menu");

        } else if ("about".equals(message)) {
            startScene("about");

        } else if ("editor".equals(message)) {
            startEditor();

        } else if ("quit".equals(message)) {
            stop();

        } else if ("fadedIn".equals(message)) {
            DrunkInvaders.this.fadingOut = false;

        } else if ("fadedOut".equals(message)) {

            this.mainLayer.clear();
            Itchy.singleton.completeTasks();
            this.fadingOut = false;
            try {
                Scene scene = this.resources.getScene(this.sceneName);
                if (scene == null) {
                    this.sceneName = "completed";
                    this.levelNumber = 1;
                    scene = this.resources.getScene(this.sceneName);
                }
                scene.create(DrunkInvaders.this.mainLayer, false);
                Itchy.showMousePointer(scene.showMouse);

            } catch (Exception e) {
                throw new RuntimeException(e);
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

    private void startEditor()
    {
        Itchy.singleton.removeEventListener(this);

        try {
            Editor editor = new Editor(DrunkInvaders.game);
            editor.init();
            editor.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main( String argv[] )
    {
        System.out.println("Welcome to Drunk Invaders");

        try {
            DrunkInvaders.game = new DrunkInvaders();

            if ((argv.length == 1) && ("--editor".equals(argv[0]))) {

                Editor editor = new Editor(DrunkInvaders.game);
                editor.init();
                editor.start();

            } else {
                DrunkInvaders.game.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Goodbye from Drunk Invaders");
    }

}
