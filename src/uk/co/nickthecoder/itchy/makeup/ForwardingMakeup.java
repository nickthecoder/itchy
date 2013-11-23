/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.makeup;

import java.util.Collections;
import java.util.List;

import uk.co.nickthecoder.itchy.OffsetSurface;
import uk.co.nickthecoder.itchy.property.AbstractProperty;

public class ForwardingMakeup implements Makeup
{
    private int changeId = 0;
    
    private Makeup makeup;
    
    public ForwardingMakeup( Makeup makeup )
    {
        this.makeup = makeup;
        this.changeId = 0;
    }
    
    public void setMakeup( Makeup makeup )
    {
        if ( this.makeup != null) {
            this.changeId += this.makeup.getChangeId();
        }
        this.makeup = makeup;
    }
    
    public Makeup getMakeup()
    {
        return this.makeup;
    }
    
    @Override
    public OffsetSurface apply( OffsetSurface os )
    {
        return this.makeup.apply(os);
    }

    @Override
    public int getChangeId()
    {
        return this.changeId + this.makeup.getChangeId();
    }

    @Override
    public List<AbstractProperty<Makeup, ?>> getProperties()
    {
        return Collections.emptyList();
    }

    @Override
    public void applyGeometry( TransformationData src )
    {
        this.makeup.applyGeometry(src);
    }
};