/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.extras;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.NullSceneBehaviour;
import uk.co.nickthecoder.itchy.Pose;
import uk.co.nickthecoder.itchy.animation.AlphaAnimation;
import uk.co.nickthecoder.itchy.animation.AnimationListener;
import uk.co.nickthecoder.itchy.animation.CompoundAnimation;
import uk.co.nickthecoder.itchy.animation.NumericAnimation;

public class FadeTransition extends NullSceneBehaviour
{
    private Actor fadeActor;

    public String sceneName;
    
    public int fadeOutDuration = 20;

    public int fadeInDuration = 20;
    

    public FadeTransition( String sceneName, Pose pose )
    {
        this.fadeActor = new Actor(pose);
        this.fadeActor.moveTo(0, 0);
        this.fadeActor.getAppearance().setAlpha(0);
        this.fadeActor.activate();

        Itchy.singleton.getGame().getPopupLayer().add(this.fadeActor);

        this.sceneName = sceneName;
        Itchy.singleton.getGame().getLayers().deactivateAll();

        AlphaAnimation fadeOut = new AlphaAnimation(fadeOutDuration, NumericAnimation.linear, 255);
        AlphaAnimation fadeIn = new AlphaAnimation(fadeInDuration, NumericAnimation.linear, 0);
        CompoundAnimation animation = new CompoundAnimation(true);
        animation.addAnimation(fadeOut);
        animation.addAnimation(fadeIn);

        fadeOut.addAnimationListener(new AnimationListener() {
            @Override
            public void finished()
            {
                Itchy.singleton.getGame().getLayers().clear();
                Itchy.singleton.getGame().getLayers().reset();

                Itchy.singleton.getGame().loadScene(FadeTransition.this.sceneName);
            }
        });

        this.fadeActor.setAnimation(animation);

        fadeOut.addAnimationListener(new AnimationListener() {
            @Override
            public void finished()
            {
                FadeTransition.this.fadeActor.kill();
            }
        });
    }

}
