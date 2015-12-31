/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import uk.co.nickthecoder.jame.event.MouseButtonEvent;

public class TableRow extends ClickableContainer
{
    private final Table table;

    final TableModelRow row;

    private final int rowIndex;

    public TableRow( Table table, TableModelRow row, int rowIndex )
    {
        this.table = table;
        this.row = row;
        this.rowIndex = rowIndex;
    }

    public TableModelRow getTableModelRow()
    {
        return this.row;
    }

    public int getRowIndex()
    {
        return this.rowIndex;
    }

    @Override
    public void onClick( MouseButtonEvent e )
    {
        this.table.focus();
        if (this.row != this.table.getCurrentTableModelRow()) {
            this.table.selectRow(this);
        }
    }

    @Override
    public void onDoubleClick( MouseButtonEvent e )
    {
        this.table.pickRow(this);
    }

}
