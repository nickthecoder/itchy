package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.itchy.animation.Animation;

public class AnimationResource extends NamedResource
{
    public Animation animation;

    public AnimationResource( Resources resources, String name, Animation animation )
    {
        super( resources, name );
        this.animation = animation;
    }

}
