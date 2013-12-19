/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import java.util.Comparator;

public class TableModelColumn
{
    protected String title;

    protected int index;

    protected int width;

    public Comparator<TableModelRow> rowComparator = null;

    public TableModelColumn( String title, int index, int width )
    {
        this.title = title;
        this.index = index;
        this.width = width;
    }

    public String getTitle()
    {
        return this.title;
    }

    public void setWidth( int value )
    {
        this.width = value;
    }

    public int getWidth()
    {
        return this.width;
    }

    public Component createCell( TableModelRow row )
    {
        Object data = row.getData(this.index);
        if (data == null) {
            return new Label("");
        } else {
            return new Label(data.toString());
        }
    }

    public void updateComponent( Component component, TableModelRow row )
    {
        if (component instanceof Label) {
            Label label = (Label) component;
            Object data = row.getData(this.index);
            label.setText(data == null ? "" : data.toString());
        }
    }

}
