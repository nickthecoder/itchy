/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import java.util.ArrayList;
import java.util.List;

public class SimpleTableModelRow implements TableModelRow
{
    private List<Object> data = new ArrayList<Object>();

    @Override
    public Object getData( int index )
    {
        return this.data.get(index);
    }

    public void add( Object obj )
    {
        this.data.add(obj);
    }

}
