/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.List;

import uk.co.nickthecoder.itchy.animation.Animation;
import uk.co.nickthecoder.itchy.property.AbstractProperty;

public class AnimationResource extends NamedResource
{

    public static List<AbstractProperty<AnimationResource, ?>> properties = AbstractProperty.findAnnotations(AnimationResource.class);

    public Animation animation;

    public AnimationResource( Resources resources, String name, Animation animation )
    {
        super(resources, name);
        this.animation = animation;
    }

}
