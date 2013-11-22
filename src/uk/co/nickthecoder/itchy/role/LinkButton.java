/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.role;

import java.awt.Desktop;
import java.net.URI;

import uk.co.nickthecoder.itchy.property.Property;

/**
 * When clicked, this button will use the default web browser to display a web page.
 * The URL (web address) can be editted within the SceneDesigner.
 */
public class LinkButton extends Button
{
    /**
     * The URL to launch when the button is clicked.
     */
    @Property(label = "URL")
    public String url = "";

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
