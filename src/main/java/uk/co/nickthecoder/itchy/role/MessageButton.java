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
 * Sends a message to the Game when the button is clicked.
 */
public class MessageButton extends ButtonRole
{
    protected static final List<Property<Role, ?>> properties = new ArrayList<Property<Role, ?>>();

    static {
        properties.add(new StringProperty<Role>("message"));
    }

    /**
     * The message that is sent to the Game when the button is clicked. This is set from the "Role" tab of the Scene
     * Designer.
     */
    public String message = "none";

    @Override
    public List<Property<Role, ?>> getProperties()
    {
        return properties;
    }

    @Override
    protected void onClick()
    {
        Itchy.getGame().getDirector().onMessage(this.message);
    }

}
