/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.extras;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.ImagePose;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.animation.AlphaAnimation;
import uk.co.nickthecoder.itchy.animation.Animation;
import uk.co.nickthecoder.itchy.animation.AnimationListener;
import uk.co.nickthecoder.itchy.animation.MoveAnimation;
import uk.co.nickthecoder.itchy.animation.NumericAnimation;
import uk.co.nickthecoder.jame.Surface;

/**
 * Transitions from one scene to another.
 * 
 * It works by taking a snapshot of the current screen, then loading the new
 * {@link uk.co.nickthecoder.itchy.Scene} (which will kill all Actors from the previous scene). It
 * then places the snapshot above the new scene, so the new scene can't be seen. Finally the
 * snapshot is animated, such that it gradually reveals the new scene.
 * 
 * The default animation is a fade (changing the snapshot's alpha from 255 to 0).
 * 
 * To use this class, instead of calling Game.startScene directly :
 * 
 * <pre>
 * <code>
 * new SceneTransition.transition(&quot;theNextSceneName&quot;);
 * </code>
 * </pre>
 * 
 * And if you want to customise the animation :
 * 
 * <pre>
 * <code>
 * new SceneTransition.animation(myAnimation).transition(&quot;theNextSceneName&quot;);
 * </code>
 * </pre>
 * 
 * The {@link uk.co.nickthecoder.itchy.Game} object will receive a message :
 * <code>SceneTransition.COMPLETE</code> when the transition has finished.
 */
public class SceneTransition
{
    public static final String COMPLETE = "SceneTransition.COMPLETE";

    /**
     * The default number of frames for the transitions.
     */
    public static final int TRANSITION_FRAMES = 30;

    /**
     * Slides the old scene left.
     * 
     * @return An animation suitable for the {@link #SceneTransition(Animation)}
     */
    public static Animation slideLeft()
    {
        return new MoveAnimation(TRANSITION_FRAMES, NumericAnimation.linear, -Itchy.getGame()
            .getWidth(), 0);
    }

    /**
     * Slides the old scene right.
     * 
     * @return An animation suitable for the {@link #SceneTransition(Animation)}
     */
    public static Animation slideRight()
    {
        return new MoveAnimation(
            TRANSITION_FRAMES, NumericAnimation.linear, Itchy.getGame().getWidth(), 0);
    }

    /**
     * Slides the old scene up.
     * 
     * @return An animation suitable for the {@link #SceneTransition(Animation)}
     */
    public static Animation slideUp()
    {
        return new MoveAnimation(
            TRANSITION_FRAMES, NumericAnimation.linear, 0, Itchy.getGame().getHeight());
    }

    /**
     * Slides the old scene up.
     * 
     * @return An animation suitable for the {@link #SceneTransition(Animation)}
     */
    public static Animation slideDown()
    {
        return new MoveAnimation(
            TRANSITION_FRAMES, NumericAnimation.linear, 0, -Itchy.getGame().getHeight());
    }

    /**
     * This is the default animation. It fades the old scene, gradually revealing the new scene.
     * 
     * @return An animation suitable for the {@link #SceneTransition(Animation)}
     */
    public static Animation fade()
    {
        return new AlphaAnimation(TRANSITION_FRAMES, NumericAnimation.easeInCirc, 0);
    }

    private static SceneTransition currentSceneTransition = null;

    private Animation animation;

    private Actor actor;

    private boolean pause = false;

    public SceneTransition()
    {
        // Default transition is a fade out over 100 frames.
        this.animation = fade();
    }

    public SceneTransition( Animation animation )
    {
        this.animation = animation.copy();

    }

    /**
     * When value = true, the game will be paused while the scene transition takes place. This may
     * cause problems if any of your game code relies on the elapsed time since the start of the
     * scene.
     * 
     * @param value
     */
    public void setPause( boolean value )
    {
        this.pause = value;
    }

    /**
     * The game will be paused while the scene transition takes place. This may cause problems if
     * any of your game code relies on the elapsed time since the start of the scene.
     * 
     * @return this
     */
    public SceneTransition pause()
    {
        setPause(true);
        return this;
    }

    public Animation getAnimation()
    {
        return this.animation;
    }

    protected void takeSnapshot()
    {
        Surface oldImage = Itchy.getDisplaySurface().copy();
        ImagePose pose = new ImagePose(oldImage, 0, 0);

        this.actor = new Actor(pose);
    }

    /**
     * Calls Game.clear to immediately kill the actors on all of the layers.
     * You can override this method if you need to keep some of the actors alive.
     */
    protected void clear()
    {
        Itchy.getGame().clear();
    }

    /**
     * Begins the transition by creating an Actor with the snapshot of the old scene, and then
     * setting its animation to that used in the SceneTransition constructor.
     * {@link #onAnimationComplete} will be called when the animation finishes.
     */
    protected void begin()
    {
        this.actor.moveTo(0, 0);
        Itchy.getGame().getPopupLayer().addTop(this.actor);
        this.animation.addAnimationListener(new AnimationListener() {
            @Override
            public void finished()
            {
                onAnimationComplete();
            }
        });
        this.actor.setAnimation(this.animation);
    }

    /**
     * Performs the transition to the named scene.
     * 
     * @param sceneName
     *        The name of the scene to transition to.
     */
    public void transition( String sceneName )
    {
        if (currentSceneTransition != null) {
            // We are already in the middle of a different transition. Lets kill that one,
            // and redraw the screen before taking the snapshot.
            currentSceneTransition.onAnimationComplete();
            Itchy.getGame().render(Itchy.getDisplaySurface());
        }

        currentSceneTransition = this;
        takeSnapshot();
        clear();

        Itchy.getGame().loadScene(sceneName);
        if (this.pause) {
            Itchy.getGame().pause.pause(false);
        }
        begin();

    }

    /**
     * Kills the snapshot actor and sends the message <code>FilmTransition.COMPLETE</code> to the
     * Game.
     */
    protected void onAnimationComplete()
    {
        currentSceneTransition = null;

        this.actor.kill();
        if (this.pause) {
            Itchy.getGame().pause.unpause();
        }
        Itchy.getGame().onMessage(COMPLETE);
    }

}
