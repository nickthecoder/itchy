/*******************************************************************************
 * Copyright (c) 2014 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.itchy.property.PropertySubject;
import uk.co.nickthecoder.itchy.property.StringProperty;

public class InputResource extends NamedResource implements PropertySubject<InputResource>
{
    protected static final List<AbstractProperty<InputResource, ?>> properties = new ArrayList<AbstractProperty<InputResource,?>>();

    static {
        properties.add(new StringProperty<InputResource>("name"));
    }

    private Input input;

    public InputResource( Resources resources, String name, Input input )
    {
        super(resources, name);
        this.input = input;
    }

    @Override
    public List<AbstractProperty<InputResource, ?>> getProperties()
    {
        return properties;
    }
    
    public Input getInput()
    {
        return this.input;
    }

    public void setInput( Input input )
    {
        this.input = input;
    }

}
