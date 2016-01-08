/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.BooleanProperty;
import uk.co.nickthecoder.itchy.property.ClassNameProperty;
import uk.co.nickthecoder.itchy.property.IntegerProperty;
import uk.co.nickthecoder.itchy.property.PropertySubject;
import uk.co.nickthecoder.itchy.property.StringProperty;
import uk.co.nickthecoder.jame.Surface;

public class CostumeResource extends NamedResource implements PropertySubject<CostumeResource>
{
    protected static final List<Property<CostumeResource, ?>> properties = new ArrayList<Property<CostumeResource, ?>>();

    static {
        properties.add( new StringProperty<CostumeResource>( "name" ) );
        properties.add( new ClassNameProperty<CostumeResource>( Role.class, "costume.roleClassName" ) );
        properties.add( new IntegerProperty<CostumeResource>( "costume.defaultZOrder" ) );
        properties.add( new BooleanProperty<CostumeResource>( "costume.showInDesigner" ) );
        properties.add( new IntegerProperty<CostumeResource>( "order" ).hint("(within scene designer's toolbox)") );
    }

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

    @Override
    public List<Property<CostumeResource, ?>> getProperties()
    {
        return properties;
    }
    
    public Costume getCostume()
    {
        return this.costume;
    }

    public void setCostume( Costume costume )
    {
        this.costume = costume;
    }

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
