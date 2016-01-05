/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.makeup;

import java.util.Collections;
import java.util.List;

import uk.co.nickthecoder.itchy.Appearance;
import uk.co.nickthecoder.itchy.property.Property;

/**
 * Used internally by Appearance, has no properties, and always returns the same change id.
 */
public abstract class BuiltInMakeup implements Makeup
{
    protected int changeId = 1;

    protected Appearance appearance;

    public BuiltInMakeup( Appearance appearance )
    {
        this.appearance = appearance;
    }

    @Override
    public List<Property<Makeup, ?>> getProperties()
    {
        return Collections.emptyList();
    }

    @Override
    public int getChangeId()
    {
        return this.changeId;
    }

    public void changed()
    {
        this.changeId++;
    }
}
