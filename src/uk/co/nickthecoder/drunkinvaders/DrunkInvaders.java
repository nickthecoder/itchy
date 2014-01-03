/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.drunkinvaders;

import java.text.DecimalFormat;
import java.util.List;

import uk.co.nickthecoder.itchy.AbstractDirector;
import uk.co.nickthecoder.itchy.AbstractRole;
import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.ActorCollisionStrategy;
import uk.co.nickthecoder.itchy.GenericCompoundView;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Launcher;
import uk.co.nickthecoder.itchy.MultiLineTextPose;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.Stage;
import uk.co.nickthecoder.itchy.StageView;
import uk.co.nickthecoder.itchy.View;
import uk.co.nickthecoder.itchy.WorldRectangle;
import uk.co.nickthecoder.itchy.ZOrderStage;
import uk.co.nickthecoder.itchy.animation.Animation;
import uk.co.nickthecoder.itchy.animation.CompoundAnimation;
import uk.co.nickthecoder.itchy.extras.SceneTransition;
import uk.co.nickthecoder.itchy.neighbourhood.Neighbourhood;
import uk.co.nickthecoder.itchy.neighbourhood.NeighbourhoodCollisionStrategy;
import uk.co.nickthecoder.itchy.neighbourhood.StandardNeighbourhood;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.Keys;

public class DrunkInvaders extends AbstractDirector
{
    public static final int NEIGHBOURHOOD_SQUARE_SIZE = 60;

    public static DrunkInvaders director;

    public ZOrderStage backgroundStage;

    public StageView backgroundView;

    public int metronome;

    public int metronomeCountdown;

    private int aliensRemaining;

    private int levelNumber = 1;

    private Neighbourhood neighbourhood;

    private Role info;

    public boolean transitioning = false;

    public WorldRectangle worldBounds;

    @Override
    public void onStarted()
    {
        director = this;

        this.neighbourhood = new StandardNeighbourhood(NEIGHBOURHOOD_SQUARE_SIZE);

        this.worldBounds = new WorldRectangle(0, 0, this.game.getWidth(), this.game.getHeight());

        Rect screenRect = new Rect(0, 0, this.game.getWidth(), this.game.getHeight());

        this.mainStage = new ZOrderStage("main");
        this.backgroundStage = new ZOrderStage("background");

        this.mainView = new StageView(screenRect, this.mainStage);
        this.mainView.centerOn(320, 240);
        this.mainView.enableMouseListener(this.game);

        this.backgroundView = new StageView(screenRect, this.backgroundStage);
        this.backgroundView.centerOn(320, 240);

        GenericCompoundView<View> views = this.game.getGameViews();
        views.add(this.backgroundView);
        views.add(this.mainView);

        List<Stage> stages = this.game.getStages();
        stages.add(this.backgroundStage);
        stages.add(this.mainStage);

        // this.glassStage.locked = true;
        // this.fadeStage.locked = true;

    }

    @Override
    public void onActivate()
    {
        super.onActivate();
        director = this;

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

            final MultiLineTextPose pose = new MultiLineTextPose(this.game.resources.getDefaultFont(), 16);

            this.info = new AbstractRole()
            {
                @Override
                public void tick()
                {
                    pose.setText(
                        "Aliens Remaining     : " + DrunkInvaders.this.aliensRemaining + "\n" +
                            "Dropped Frames       : " + Itchy.frameRate.getDroppedFrames() + "\n" +
                            "Surfaces Created     : " + Surface.totalCreated() + "\n" +
                            "Surfaces Existing    : " + Surface.totalExisting() + "\n" +
                            "Surfaces Freed by GC : " + Surface.totalFreedByGC()
                        );
                }
            };
            pose.setAlignment(0, 0);
            Actor actor = new Actor(pose);
            actor.setRole(this.info);
            actor.moveTo(40, 460);
            this.getGame().getGlassStage().addTop(actor);

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
    public boolean startScene( String sceneName )
    {
        this.neighbourhood.clear();

        System.out.println("Transition start for " + sceneName );
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
        this.transitioning = true;
        System.out.println( "Alien count pre  trans : " + this.aliensRemaining );
        boolean result = new SceneTransition(transition).transition(sceneName);
        System.out.println( "Alien count post trans : " + this.aliensRemaining );
        this.transitioning = true;
        return result;
    }

    @Override
    public void onMessage( String message )
    {
        if ("editor".equals(message)) {
            this.game.startEditor();

        } else if ("quit".equals(message)) {
            this.game.end();

        } else if ("reset".equals(message)) {
            this.game.getPreferences().clear();

        } else if (message == SceneTransition.COMPLETE) {
            this.transitioning = false;
            System.out.println("Transition end" );

        }
    }

    public void addAliens( int n )
    {
        if (this.game.getSceneDirector() instanceof PlaySceneDirector ) {
            ((PlaySceneDirector) (this.game.getSceneDirector())).addAliens( n );
        }
    }

    public boolean completedLevel( int level )
    {
        return this.game.getPreferences().getBoolean("completedLevel" + level, false);
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
        Launcher.main(new String[] { "Drunk Invaders" });
    }

}
