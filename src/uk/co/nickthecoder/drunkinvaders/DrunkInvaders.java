/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.drunkinvaders;

import java.io.File;
import java.text.DecimalFormat;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.ActorCollisionStrategy;
import uk.co.nickthecoder.itchy.Game;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.MultiLineTextPose;
import uk.co.nickthecoder.itchy.ScrollableLayer;
import uk.co.nickthecoder.itchy.animation.Animation;
import uk.co.nickthecoder.itchy.extras.FilmTransition;
import uk.co.nickthecoder.itchy.neighbourhood.Neighbourhood;
import uk.co.nickthecoder.itchy.neighbourhood.NeighbourhoodCollisionStrategy;
import uk.co.nickthecoder.itchy.neighbourhood.StandardNeighbourhood;
import uk.co.nickthecoder.itchy.util.TextBehaviour;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.Keys;

public class DrunkInvaders extends Game
{
    public static final File RESOURCES = new File("resources/drunkInvaders/drunkInvaders.xml");

    public static final int NEIGHBOURHOOD_SQUARE_SIZE = 60;

    public static DrunkInvaders game;

    public ScrollableLayer backgroundLayer;

    public ScrollableLayer mainLayer;

    public ScrollableLayer glassLayer;

    public ScrollableLayer fadeLayer;

    public int metronome;

    public int metronomeCountdown;

    private int aliensRemaining;

    private int levelNumber = 1;

    private Neighbourhood neighbourhood;

    private TextBehaviour info;

    public boolean transitioning = false;

    public DrunkInvaders() throws Exception
    {
        super("Drunk Invaders", 640, 480);
        this.resources.load(RESOURCES);

        this.neighbourhood = new StandardNeighbourhood(NEIGHBOURHOOD_SQUARE_SIZE);

        this.mainLayer = new ScrollableLayer("main", this.screenRect);
        this.mainLayer.centerOn(320, 240);

        this.backgroundLayer = new ScrollableLayer("background", this.screenRect);
        this.backgroundLayer.centerOn(320, 240);

        this.glassLayer = new ScrollableLayer("glass", this.screenRect);
        this.fadeLayer = new ScrollableLayer("fade", this.screenRect);

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
        this.mainLayer.enableMouseListener();

        this.metronomeCountdown = 0;
        this.metronome = 20;
    }

    @Override
    public boolean onKeyDown( KeyboardEvent ke )
    {
        if (ke.symbol == Keys.F1) {
            debug();
        }

        if (ke.symbol == Keys.F2) {
            toggleInfo();
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
                            "Dropped Frames   : " + Itchy.frameRate.getDroppedFrames()
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
        if (this.pause.isPaused()) {
            this.pause.unpause();
        }

        this.neighbourhood.clear();

        this.transitioning = true;
        Animation transition = FilmTransition.slideUp();
        
        if ("about".equals(sceneName)) {
            transition = FilmTransition.slideRight();
            
        } else if ("levels".equals(sceneName)) {
                transition = FilmTransition.slideRight();
                
        } else if ("menu".equals(sceneName)) {
            transition = FilmTransition.slideLeft();
        }
        new FilmTransition().animation(transition).transition(sceneName);
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
            end();
            
        } else if ( message == FilmTransition.COMPLETE) {
            this.transitioning = false;
        }
    }

    public void addAliens( int n )
    {
        this.aliensRemaining += n;

        // We only care when the last alien was killed during play, not when fading the scene out.
        if (this.transitioning) {
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

    public void debug()
    {
        this.neighbourhood.debug();
    }

    public static void main( String argv[] ) throws Exception
    {
        DrunkInvaders.game = new DrunkInvaders();
        DrunkInvaders.game.runFromMain(argv);
    }

    @Override
    public String getInitialSceneName()
    {
        return "menu";
    }

}
