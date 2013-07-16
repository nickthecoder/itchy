/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SimpleTableModel implements TableModel
{
    private List<TableModelRow> rows = new ArrayList<TableModelRow>();

    @Override
    public TableModelRow getRow( int i )
    {
        return this.rows.get(i);
    }

    @Override
    public int getRowCount()
    {
        return this.rows.size();
    }

    public void addRow( TableModelRow row )
    {
        this.rows.add(row);
    }

    @Override
    public void sort( Comparator<TableModelRow> comparator )
    {
        Collections.sort(this.rows, comparator);
    }

    public void clear()
    {
        this.rows.clear();
    }
}
