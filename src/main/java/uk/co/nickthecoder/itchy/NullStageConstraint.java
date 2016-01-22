/*******************************************************************************
 * Copyright (c) 2014 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.property.Property;

public class NullStageConstraint implements StageConstraint
{
    private static final List<Property<StageConstraint,?>> properties = new ArrayList<Property<StageConstraint,?>>();

    @Override
    public List<Property<StageConstraint,?>> getProperties()
    {
        return properties;
    }
    
    @Override
    public double constrainX( double requestedX, double requestedY )
    {
        return requestedX;
    }

    @Override
    public double constrainY( double requestedX, double requestedY )
    {
        return requestedY;
    }

    @Override
    public void added( Actor actor )
    {
        // Do nothing
    }
}
