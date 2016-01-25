package uk.co.nickthecoder.itchy.gui;

import java.util.HashMap;

import uk.co.nickthecoder.itchy.AnimationResource;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Resources;

public abstract class AnimationPicker extends Picker<AnimationResource>
{

    public static HashMap<String, AnimationResource> createAnimationsHashMap()
    {
        Resources resources = Itchy.getGame().resources;
        HashMap<String, AnimationResource> animations = new HashMap<String, AnimationResource>();
        for (String name : resources.animationNames()) {
            AnimationResource animationResource = resources.getAnimationResource(name);
            animations.put(name, animationResource);
        }
        return animations;
    }

    public AnimationPicker()
    {
        super( "Pick Animation", createAnimationsHashMap() );
    }

    public AnimationPicker(AnimationResource selected)
    {
        super( "Pick Animation", createAnimationsHashMap(), selected );
    }
}
