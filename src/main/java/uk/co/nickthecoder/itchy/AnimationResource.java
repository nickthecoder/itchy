/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.animation.Animation;
import uk.co.nickthecoder.itchy.animation.CompoundAnimation;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.StringProperty;

public class AnimationResource implements NamedSubject<AnimationResource>
{
    protected static final List<Property<AnimationResource, ?>> properties = new ArrayList<Property<AnimationResource, ?>>();

    static {
        properties.add(new StringProperty<AnimationResource>("name"));
    }

    @Override
    public List<Property<AnimationResource, ?>> getProperties()
    {
        return properties;
    }
    
    public Animation animation;

    private String name;
    
    public AnimationResource()
    {
        animation = new CompoundAnimation(true);
    }


    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void setName(String name)
    {
        this.name = name;
    }

}
