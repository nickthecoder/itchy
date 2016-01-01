/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.role;

import java.text.DecimalFormat;

import uk.co.nickthecoder.itchy.property.Property;

public class NumberValue extends TextValue
{
    @Property(label = "Format")
    public String formatPattern = "0";

    private DecimalFormat format = new DecimalFormat("0");

    @Override
    public void onBirth()
    {
        super.onBirth();
        this.format = new DecimalFormat(this.formatPattern);
    }

    @Override
    protected String formatValue( Object value )
    {
        return this.format.format(value);
    }
}