/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.drunkinvaders;

import java.text.DecimalFormat;

import uk.co.nickthecoder.itchy.AbstractBehaviour;
import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.ActorCollisionStrategy;
import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.Game;
import uk.co.nickthecoder.itchy.GameManager;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Launcher;
import uk.co.nickthecoder.itchy.MultiLineTextPose;
import uk.co.nickthecoder.itchy.StageView;
import uk.co.nickthecoder.itchy.WorldRectangle;
import uk.co.nickthecoder.itchy.ZOrderStage;
import uk.co.nickthecoder.itchy.animation.Animation;
import uk.co.nickthecoder.itchy.animation.CompoundAnimation;
import uk.co.nickthecoder.itchy.extras.SceneTransition;
import uk.co.nickthecoder.itchy.neighbourhood.Neighbourhood;
import uk.co.nickthecoder.itchy.neighbourhood.NeighbourhoodCollisionStrategy;
import uk.co.nickthecoder.itchy.neighbourhood.StandardNeighbourhood;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.Keys;

public class DrunkInvaders extends Game
{
    public static final int NEIGHBOURHOOD_SQUARE_SIZE = 60;

    public static DrunkInvaders game;

    public ZOrderStage backgroundStage;

    public ZOrderStage glassStage;

    public ZOrderStage fadeStage;

    public StageView backgroundView;

    public StageView glassView;

    public StageView fadeView;

    public int metronome;

    public int metronomeCountdown;

    private int aliensRemaining;

    private int levelNumber = 1;

    private Neighbourhood neighbourhood;

    private Behaviour info;

    public boolean transitioning = false;
    
    public WorldRectangle worldBounds;

    public DrunkInvaders( GameManager gameManager )
        throws Exception
    {
        super(gameManager);

        game = this;
        this.neighbourhood = new StandardNeighbourhood(NEIGHBOURHOOD_SQUARE_SIZE);

    }

    @Override
    public void createStagesAndViews()
    {
        this.worldBounds = new WorldRectangle( 0,0, this.getWidth(), this.getHeight());

        Rect screenRect = new Rect(0, 0, getWidth(), getHeight());

        this.mainStage = new ZOrderStage("main");
        this.backgroundStage = new ZOrderStage("background");
        this.glassStage = new ZOrderStage("glass");
        this.fadeStage = new ZOrderStage("fade");

        this.mainView = new StageView(screenRect, this.mainStage);
        this.mainView.centerOn(320, 240);
        this.mainView.enableMouseListener(this);

        this.backgroundView = new StageView(screenRect, this.backgroundStage);
        this.backgroundView.centerOn(320, 240);

        this.glassView = new StageView(screenRect, this.glassStage);
        this.fadeView = new StageView(screenRect, this.fadeStage);

        this.gameViews.add(this.backgroundView);
        this.gameViews.add(this.mainView);
        this.gameViews.add(this.glassView);
        this.gameViews.add(this.fadeView);

        this.stages.add(this.backgroundStage);
        this.stages.add(this.mainStage);

        this.glassStage.locked = true;
        this.fadeStage.locked = true;

    }

    @Override
    public void onActivate()
    {
        super.onActivate();
        game = this;

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
        super.tick();

        if (this.metronomeCountdown == 0) {
            this.metronomeCountdown = this.metronome;
        }

        this.metronomeCountdown--;
    }

    public void toggleInfo()
    {
        if (this.info == null) {

            final MultiLineTextPose pose = new MultiLineTextPose(this.resources.getFont("vera"), 16);

            this.info = new AbstractBehaviour()
            {
                @Override
                public void tick()
                {
                    pose.setText(
                        "Aliens Remaining : " + DrunkInvaders.this.aliensRemaining + "\n" +
                            "Dropped Frames   : " + Itchy.frameRate.getDroppedFrames()
                        );
                }
            };
            pose.setAlignment(0, 0);
            Actor actor = new Actor(pose);
            actor.setBehaviour(this.info);
            actor.moveTo(40, 460);
            this.glassStage.addTop(actor);

        } else {
            this.info.getActor().kill();
            this.info = null;
        }
    }

    public ActorCollisionStrategy createCollisionStrategy( Actor actor )
    {
        return new NeighbourhoodCollisionStrategy(actor, this.neighbourhood);
    }

    @Override
    public void startScene( String sceneName )
    {
        this.neighbourhood.clear();

        this.transitioning = true;
        Animation transition = new CompoundAnimation(false)
            .add(SceneTransition.slideUp())
            .add(SceneTransition.fade());

        if ("about".equals(sceneName)) {
            transition = new CompoundAnimation(false)
                .add(SceneTransition.slideRight())
                .add(SceneTransition.fade());

        } else if ("levels".equals(sceneName)) {
            transition = new CompoundAnimation(false)
                .add(SceneTransition.slideRight())
                .add(SceneTransition.fade());

        } else if ("menu".equals(sceneName)) {
            transition = new CompoundAnimation(false)
                .add(SceneTransition.slideLeft())
                .add(SceneTransition.fade());
        }
        new SceneTransition(transition).transition(sceneName);
    }

    @Override
    public void onMessage( String message )
    {
        if ("editor".equals(message)) {
            startEditor();

        } else if ("quit".equals(message)) {
            end();

        } else if ("reset".equals(message)) {
            this.getPreferences().clear();

        } else if (message == SceneTransition.COMPLETE) {
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
        Launcher.main(new String[] { "drunkInvaders" });
    }

}
