/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.Collections;
import java.util.List;

import uk.co.nickthecoder.itchy.util.AbstractProperty;

public class NullMakeup implements Makeup
{

    @Override
    public List<AbstractProperty<Makeup, ?>> getProperties()
    {
        return Collections.emptyList();
    }

    @Override
    public OffsetSurface apply( OffsetSurface src )
    {
        return src;
    }

    @Override
    public int getChangeId()
    {
        return 0;
    }

}
