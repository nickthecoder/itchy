/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.animation.Animation;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.PropertySubject;
import uk.co.nickthecoder.itchy.property.StringProperty;

public class AnimationResource extends NamedResource implements PropertySubject<AnimationResource>
{
    protected static final List<Property<AnimationResource, ?>> properties = new ArrayList<Property<AnimationResource, ?>>();

    static {
        properties.add(new StringProperty<AnimationResource>("name"));
    }

    public Animation animation;

    public AnimationResource(Resources resources, String name, Animation animation)
    {
        super(resources, name);
        this.animation = animation;
    }

    @Override
    public List<Property<AnimationResource, ?>> getProperties()
    {
        return properties;
    }

}
