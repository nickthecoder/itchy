/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.itchy.util.Named;

public class NamedResource implements Named
{
    protected Resources resources;

    protected String name;

    public static final String ANONYMOUS = "anonymous";

    /**
     * Create an anonymous resource, which isn't part of the Resources. This is useful for dynamically created resources, such as by the
     * class Fragment. Anonymous resources are not saved, or displayed in the editor.
     */
    public NamedResource()
    {
        this.name = ANONYMOUS;
        this.resources = null;
    }

    public NamedResource( Resources resources, String name )
    {
        this.resources = resources;
        this.name = name;
    }

    public boolean isAnonymous()
    {
        return this.resources == null;
    }

    public void setName( String newName )
    {
        if (this.resources.has(this)) {
            this.resources.renameResource(this, newName);
        }
        this.name = newName;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

}
