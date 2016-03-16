/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.PropertySubject;

public class CostumeFeatures implements PropertySubject<CostumeFeatures>, Cloneable
{
    public Costume costume;

    public CostumeFeatures(Costume costume)
    {
        this.costume = costume;
    }

    private final static List<Property<CostumeFeatures, ?>> EMPTY_PROPERTIES =
        new ArrayList<Property<CostumeFeatures, ?>>();

    /**
     * Used internally by Itchy.
     * 
     * @priority 5
     */
    @Override
    public List<Property<CostumeFeatures, ?>> getProperties()
    {
        return EMPTY_PROPERTIES;
    }

}
