package uk.co.nickthecoder.itchy.gui;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

public class SimpleTableModel implements TableModel
{
    private List<TableModelRow> rows = new ArrayList<TableModelRow>();

    public TableModelRow getRow( int i )
    {
        return this.rows.get( i );
    }

    public int getRowCount()
    {
        return this.rows.size();
    }

    public void addRow( TableModelRow row )
    {
        this.rows.add( row );
    }

    public void sort( Comparator<TableModelRow> comparator )
    {
        Collections.sort( this.rows, comparator );
    }

    public void clear()
    {
        this.rows.clear();
    }
}
