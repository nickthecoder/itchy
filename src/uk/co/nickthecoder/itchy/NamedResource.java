/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

public class NamedResource
{
    protected Resources resources;

    protected String name;

    public NamedResource( String name )
    {
        this.name = name;
    }
    
    public NamedResource( Resources resources, String name )
    {
        this.resources = resources;
        this.name = name;
    }

    public void rename( String newName )
    {
        this.resources.rename(this, newName);
        this.name = newName;
    }

    public String getName()
    {
        return this.name;
    }
}
