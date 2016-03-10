/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.role;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.StringProperty;

/**
 * When clicked, this button will start a given scene. The scene is given by the sceneName property, which can be
 * edited within the Scene Designer.
 */
public class SceneButton extends ButtonRole
{
    protected static final List<Property<Role, ?>> properties = new ArrayList<Property<Role, ?>>();

    static {
        properties.add(new StringProperty<Role>("sceneName"));
    }

    public String sceneName;

    @Override
    public List<Property<Role, ?>> getProperties()
    {
        return properties;
    }

    @Override
    protected void onClick()
    {
        super.onClick();
        Itchy.getGame().startScene(this.sceneName);
    }

}
