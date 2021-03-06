/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.makeup;

import java.util.Collections;
import java.util.List;

import uk.co.nickthecoder.itchy.OffsetSurface;
import uk.co.nickthecoder.itchy.property.Property;

public class NullMakeup extends AbstractMakeup
{

    @Override
    public List<Property<Makeup, ?>> getProperties()
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

    @Override
    public void applyGeometry( TransformationData src )
    {
    }

}
