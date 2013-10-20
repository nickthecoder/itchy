/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.jame.Surface;

public class CostumeResource extends NamedResource
{
    // TODO All other XXXResources have getXXX, instead of a public property.
    public Costume costume;

    public CostumeResource( Resources resources, String name, Costume costume )
    {
        super(resources, name);
        this.costume = costume;
    }

    public String getExtendedFromName()
    {
        Costume base = this.costume.getExtendedFrom();
        if (base == null) {
            return null;
        } else {
            return this.resources.getCostumeName(base);
        }
    }

    public Surface getThumbnail()
    {
        return this.resources.getThumbnail(this);
    }
}
