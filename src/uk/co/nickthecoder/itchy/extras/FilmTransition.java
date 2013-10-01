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
 * It works by taking a snapshot of the current screen, then loading the new scene (which will kill
 * all Actors from the previous scene). It then places the snapshot above the new scene, so the new
 * scene can't be seen. Finally the snapshot is animated, such that it gently reveals the new scene.
 * 
 * The default animation is a fade (changing the snapshot's alpha from 255 to 0).
 * 
 * To use this class, instead of calling Game.loadScene directly :
 * 
 * <pre>
 * new FilmTransition.transition(&quot;theNextSceneName&quot;);
 * </pre>
 * 
 * And if you want to customise the animation :
 * 
 * <pre>
 * new FilmTransition.animation(myAnimation).transition(&quot;theNextSceneName&quot;);
 * </pre>
 */
public class FilmTransition
{
    public static final String COMPLETE = "FilmTransition.Complete";

    public static Animation slideLeft()
    {
        return new MoveAnimation(30, NumericAnimation.linear, -Itchy.getGame().getWidth(), 0);
    }

    public static Animation slideRight()
    {
        return new MoveAnimation(30, NumericAnimation.linear, Itchy.getGame().getWidth(), 0);
    }

    public static Animation slideUp()
    {
        return new MoveAnimation(30, NumericAnimation.linear, 0, Itchy.getGame().getHeight());
    }

    public static Animation slideDown()
    {
        return new MoveAnimation(30, NumericAnimation.linear, 0, -Itchy.getGame().getHeight());
    }

    public static Animation fade()
    {
        return new AlphaAnimation(30, NumericAnimation.linear, 0);
    }

    private static FilmTransition currentFilmTransition = null;

    private Animation animation;

    private Actor actor;

    private boolean pause = false;

    public FilmTransition()
    {
        // Default transition is a fade out over 100 frames.
        this.animation = fade();
    }

    public void setAnimation( Animation animation )
    {
        this.animation = animation.copy();
    }

    public void setPause( boolean value )
    {
        this.pause = value;
    }

    /**
     * Causes all actors to be paused while the transition takes place.
     */
    public FilmTransition pause()
    {
        setPause(true);
        return this;
    }

    public Animation getAnimation()
    {
        return this.animation;
    }

    public FilmTransition animation( Animation animation )
    {
        this.setAnimation(animation);
        return this;
    }

    protected void takeSnapshot()
    {
        Surface oldImage = Itchy.screen.copy();
        ImagePose pose = new ImagePose(oldImage, 0, 0);

        this.actor = new Actor(pose);
    }

    protected void clear()
    {
        Itchy.getGame().getLayers().clear();
    }

    protected void begin()
    {
        this.actor.moveTo(0, 0);
        Itchy.getGame().getPopupLayer().add(this.actor);
        this.animation.addAnimationListener(new AnimationListener() {
            @Override
            public void finished()
            {
                onAnimationComplete();
            }
        });
        this.actor.setAnimation(this.animation);
        this.actor.activate();
    }

    public void transition( String sceneName )
    {
        if (currentFilmTransition != null) {
            // We are already in the middle of a different transition. Lets kill that one,
            // and redraw the screen before taking the snapshot.
            currentFilmTransition.end();
            Itchy.getGame().render(Itchy.screen);
        }

        currentFilmTransition = this;
        takeSnapshot();
        clear();

        // TODO Allow the scene to delay activating the actors till the animation has finished
        // or another arbitrary delay.
        Itchy.getGame().loadScene(sceneName);
        if (this.pause) {
            Itchy.getGame().pause.pause(false);
        }
        begin();

    }

    protected void end()
    {
        onAnimationComplete();
    }

    /**
     * Kills the snapshot actor and sends the message <code>FilmTransition.COMPLETE</code> to the
     * Game.
     */
    protected void onAnimationComplete()
    {
        currentFilmTransition = null;

        this.actor.kill();
        if (this.pause) {
            Itchy.getGame().pause.unpause();
        }
        Itchy.getGame().onMessage(COMPLETE);
    }

}