/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.drunkinvaders;

import java.text.DecimalFormat;

import uk.co.nickthecoder.itchy.AbstractDirector;
import uk.co.nickthecoder.itchy.AbstractRole;
import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Input;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Launcher;
import uk.co.nickthecoder.itchy.MultiLineTextPose;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.StageView;
import uk.co.nickthecoder.itchy.WorldRectangle;
import uk.co.nickthecoder.itchy.ZOrderStage;
import uk.co.nickthecoder.itchy.animation.Animation;
import uk.co.nickthecoder.itchy.animation.CompoundAnimation;
import uk.co.nickthecoder.itchy.collision.ActorCollisionStrategy;
import uk.co.nickthecoder.itchy.collision.Neighbourhood;
import uk.co.nickthecoder.itchy.collision.NeighbourhoodCollisionStrategy;
import uk.co.nickthecoder.itchy.collision.StandardNeighbourhood;
import uk.co.nickthecoder.itchy.extras.AnimationSceneTransition;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.event.KeyboardEvent;

public class DrunkInvaders extends AbstractDirector
{
    public static final int NEIGHBOURHOOD_SQUARE_SIZE = 60;

    public static DrunkInvaders director;

    public ZOrderStage backgroundStage;

    public StageView backgroundView;

    public int metronome;

    public int metronomeCountdown;

    private int levelNumber = 1;

    Neighbourhood neighbourhood;

    private Role info;

    public WorldRectangle worldBounds;

    protected Input inputDebug;

    protected Input inputToggleInfo;

    @Override
    public void onStarted()
    {
        director = this;

        this.neighbourhood = new StandardNeighbourhood(NEIGHBOURHOOD_SQUARE_SIZE);

        this.worldBounds = new WorldRectangle(0, 0, this.game.getWidth(), this.game.getHeight());
    }

    @Override
    public void onActivate()
    {
        super.onActivate();
        director = this;

        this.metronomeCountdown = 0;
        this.metronome = 20;

        this.inputDebug = Input.find("debug");
        this.inputToggleInfo = Input.find("toggleInfo");
    }

    @Override
    public void onKeyDown(KeyboardEvent ke)
    {
        if (this.inputDebug.matches(ke)) {
            debug();
            ke.stopPropagation();
        }

        if (this.inputToggleInfo.matches(ke)) {
            toggleInfo();
            ke.stopPropagation();
        }

        super.onKeyDown(ke);
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
                    int aliensRemaining = 0;
                    if (DrunkInvaders.this.game.getSceneDirector() instanceof Level) {
                        aliensRemaining = ((Level) DrunkInvaders.this.game.getSceneDirector()).getAliensRemaining();
                    }
                    pose.setText(
                        "Aliens Remaining     : " + aliensRemaining + "\n" +
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

    public ActorCollisionStrategy createCollisionStrategy(Actor actor)
    {
        return new NeighbourhoodCollisionStrategy(actor, this.neighbourhood);
    }

    public void nextLevel()
    {
        this.game.getPreferences().putBoolean("completedLevel" + this.levelNumber, true);

        this.levelNumber += 1;
        play();
    }

    @Override
    public void onStartingScene(String sceneName)
    {
        Animation transition = new CompoundAnimation(false)
            .add(AnimationSceneTransition.slideUp())
            .add(AnimationSceneTransition.fade());

        if ("about".equals(sceneName)) {
            transition = new CompoundAnimation(false)
                .add(AnimationSceneTransition.slideRight())
                .add(AnimationSceneTransition.fade());

        } else if ("levels".equals(sceneName)) {
            transition = new CompoundAnimation(false)
                .add(AnimationSceneTransition.slideRight())
                .add(AnimationSceneTransition.fade());

        } else if ("menu".equals(sceneName)) {
            transition = new CompoundAnimation(false)
                .add(AnimationSceneTransition.slideLeft())
                .add(AnimationSceneTransition.fade());
        }

        this.setSceneTransition( new AnimationSceneTransition(transition) );

        super.onStartingScene(sceneName);
        this.neighbourhood.clear();

    }

    @Override
    public void onMessage(String message)
    {
        if ("editor".equals(message)) {
            this.game.startEditor();

        } else if ("quit".equals(message)) {
            this.game.end();

        } else if ("reset".equals(message)) {
            this.game.getPreferences().clear();
        }
    }

    public void addAliens(int n)
    {
        if (this.game.getSceneDirector() instanceof Level) {
            ((Level) (this.game.getSceneDirector())).addAliens(n);
        }
    }

    public boolean completedLevel(int level)
    {
        return this.game.getPreferences().getBoolean("completedLevel" + level, false);
    }

    public void play(int levelNumber)
    {
        this.levelNumber = levelNumber;
        this.play();
    }

    public void play()
    {
        DecimalFormat df = new DecimalFormat("00");
        getGame().startScene("level" + df.format(this.levelNumber));
    }

    public void debug()
    {
        this.neighbourhood.debug();
    }

    public static void main(String argv[]) throws Exception
    {
        Launcher.main(new String[] { "Drunk Invaders" });
    }

}
