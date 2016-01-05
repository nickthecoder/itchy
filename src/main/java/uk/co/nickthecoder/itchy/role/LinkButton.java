/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.role;

import java.awt.Desktop;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.itchy.property.StringProperty;

/**
 * When clicked, this button will use the default web browser to display a web page. The URL (web address) can be edited
 * within the SceneDesigner.
 */
public class LinkButton extends Button
{
    protected static final List<AbstractProperty<Role, ?>> properties = new ArrayList<AbstractProperty<Role, ?>>();

    static {
        properties.add(new StringProperty<Role>("url"));
    }

    /**
     * The URL to launch when the button is clicked.
     */
    public String url = "";

    @Override
    public List<AbstractProperty<Role, ?>> getProperties()
    {
        return properties;
    }

    @Override
    protected void onClick()
    {
        Desktop desktop = Desktop.getDesktop();

        try {
            desktop.browse(new URI(this.url));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
