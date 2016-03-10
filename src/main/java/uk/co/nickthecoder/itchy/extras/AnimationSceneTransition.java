package uk.co.nickthecoder.itchy.extras;

import uk.co.nickthecoder.itchy.AbstractRole;
import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Actor.AnimationEvent;
import uk.co.nickthecoder.itchy.Game;
import uk.co.nickthecoder.itchy.ImagePose;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.animation.AlphaAnimation;
import uk.co.nickthecoder.itchy.animation.Animation;
import uk.co.nickthecoder.itchy.animation.Eases;
import uk.co.nickthecoder.itchy.animation.MoveAnimation;
import uk.co.nickthecoder.jame.Surface;

public class AnimationSceneTransition implements SceneTransition
{
    public static final String COMPLETE = "SceneTransition.COMPLETE";

    private Surface snapshot;

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
        return new MoveAnimation(TRANSITION_FRAMES, Eases.linear, -Itchy.getGame().getWidth(), 0);
    }

    /**
     * Slides the old scene right.
     * 
     * @return An animation suitable for the {@link #SceneTransition(Animation)}
     */
    public static Animation slideRight()
    {
        return new MoveAnimation(
            TRANSITION_FRAMES, Eases.linear, Itchy.getGame().getWidth(), 0);
    }

    /**
     * Slides the old scene up.
     * 
     * @return An animation suitable for the {@link #SceneTransition(Animation)}
     */
    public static Animation slideUp()
    {
        return new MoveAnimation(
            TRANSITION_FRAMES, Eases.linear, 0, Itchy.getGame().getHeight());
    }

    /**
     * Slides the old scene up.
     * 
     * @return An animation suitable for the {@link #SceneTransition(Animation)}
     */
    public static Animation slideDown()
    {
        return new MoveAnimation(
            TRANSITION_FRAMES, Eases.linear, 0, -Itchy.getGame().getHeight());
    }

    /**
     * This is the default animation. It fades the old scene, gradually revealing the new scene.
     * 
     * @return An animation suitable for the {@link #SceneTransition(Animation)}
     */
    public static Animation fade()
    {
        return new AlphaAnimation(TRANSITION_FRAMES, Eases.easeInCirc, 0);
    }

    private Animation animation;

    private Actor actor;

    public AnimationSceneTransition()
    {
        this( fade() );
    }

    public AnimationSceneTransition(String animationName)
    {
        this.setNamedAnimation(animationName);
    }

    public AnimationSceneTransition(Animation animation)
    {
        this.setAnimation(animation);
    }

    public final void setNamedAnimation( String animationName )
    {
        Animation animation = Itchy.getGame().resources.getAnimation(animationName);
        if (animation != null) {
            this.setAnimation(animation);
        }
    }

    public final void setAnimation( Animation animation )
    {
        this.animation = animation.copy();
        this.animation.setFinishedMessage(COMPLETE);
    }
    
    @Override
    public void prepare()
    {
        if (this.isActive()) {
            this.complete();
        }
        
        Game game = Itchy.getGame();
        snapshot = new Surface(game.getWidth(), game.getHeight(), false);
        game.render(snapshot);
    }

    @Override
    public boolean isActive()
    {
        return this.snapshot != null;
    }

    /**
     * Begins the transition by creating an Actor with the snapshot of the old scene, and then setting its animation to
     * that used in the
     * SceneTransition constructor. {@link #onAnimationComplete} will be called when the animation finishes.
     */
    @Override
    public void begin()
    {
        ImagePose pose = new ImagePose(this.snapshot, 0, this.snapshot.getHeight());
        this.actor = new Actor(pose);
        this.actor.setRole(new ASTRole());

        this.actor.moveTo(0, 0);
        Itchy.getGame().getGlassStage().addTop(this.actor);
        this.actor.setAnimation(this.animation);
    }

    @Override
    public void complete()
    {
        if (this.actor != null) {
            this.actor.setAnimation(null, AnimationEvent.FAST_FORWARD);
        }
    }

    class ASTRole extends AbstractRole
    {
        /**
         * The animation will send the message SceneTransition.COMPLETE, when it finishes.
         */
        public void onMessage(String message)
        {
            if (message.equals(COMPLETE)) {
                onAnimationComplete();
            }
        }
    }

    /**
     * Kills the snapshot actor and sends the message <code>AnimationSceneTransition.COMPLETE</code> to the Director
     * and SceneDirector.
     */
    protected void onAnimationComplete()
    {
        this.actor.kill();
        this.actor = null;
        this.snapshot = null;
        Itchy.getGame().getDirector().onMessage(COMPLETE);
        Itchy.getGame().getSceneDirector().onMessage(COMPLETE);
    }

}
