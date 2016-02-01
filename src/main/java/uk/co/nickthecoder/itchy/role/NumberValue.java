/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.role;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.StringProperty;

public class NumberValue extends TextValue
{
    protected static final List<Property<Role, ?>> properties = new ArrayList<Property<Role, ?>>();

    static {
        properties.addAll(TextValue.properties);
        properties.add(new StringProperty<Role>("formatPattern"));
    }

    @Override
    public List<Property<Role, ?>> getProperties()
    {
        return properties;
    }

    public String formatPattern = "0";

    private DecimalFormat format = new DecimalFormat("0");

    @Override
    public void onBirth()
    {
        super.onBirth();
        this.format = new DecimalFormat(this.formatPattern);
    }

    @Override
    protected String formatValue(Object value)
    {
        return this.format.format(value);
    }
}
