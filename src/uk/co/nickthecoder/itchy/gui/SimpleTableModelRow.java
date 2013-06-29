package uk.co.nickthecoder.itchy.gui;

import java.util.List;
import java.util.ArrayList;

public class SimpleTableModelRow implements TableModelRow
{
    private List<Object> data = new ArrayList<Object>();

    public Object getData( int index )
    {
        return this.data.get( index );
    }

    public void add( Object obj )
    {
        this.data.add( obj );
    }

}
