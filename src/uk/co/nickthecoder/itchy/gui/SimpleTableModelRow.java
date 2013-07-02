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
