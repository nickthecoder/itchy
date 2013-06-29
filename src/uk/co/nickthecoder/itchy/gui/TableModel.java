package uk.co.nickthecoder.itchy.gui;

import java.util.Comparator;

public interface TableModel
{
    public int getRowCount();

    public TableModelRow getRow( int i );

    public void sort( Comparator<TableModelRow> c );
}
