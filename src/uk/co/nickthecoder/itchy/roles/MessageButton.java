/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.roles;

import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.util.Property;

/**
 * Sends a message to the Game when the button is clicked.
 */
public class MessageButton extends Button
{
    /**
     * The message that is sent to the Game when the button is clicked. This is set from the
     * "Role" tab of the Scene Designer.
     */
    @Property(label = "Message")
    public String message = "none";

    @Override
    protected void onClick()
    {
        Itchy.getGame().getDirector().onMessage(this.message);
    }

}
