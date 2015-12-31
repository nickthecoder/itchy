/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.Comparator;
import java.util.List;

import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.jame.Surface;

public class CostumeResource extends NamedResource
{
    public static List<AbstractProperty<CostumeResource, ?>> properties = AbstractProperty.findAnnotations(CostumeResource.class);

    private Costume costume;

    private int order;
    
    public static final Comparator<CostumeResource> orderComparator = new Comparator<CostumeResource>() {

        @Override
        public int compare( CostumeResource a, CostumeResource b )
        {
            if (a.getOrder() != b.getOrder()) {
                return a.getOrder() - b.getOrder();
            } else {
                return a.getName().compareTo(b.getName());
            }
        }
    };
        
    public CostumeResource( Resources resources, String name, Costume costume )
    {
        super(resources, name);
        this.costume = costume;
        this.order = 10;
    }

    @Property(label = "Costume", recurse = true)
    public Costume getCostume()
    {
        return this.costume;
    }

    public void setCostume( Costume costume )
    {
        this.costume = costume;
    }

    @Property(label = "Order" )
    public int getOrder()
    {
        return this.order;
    }
    
    public void setOrder( int order )
    {
        this.order = order;
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
