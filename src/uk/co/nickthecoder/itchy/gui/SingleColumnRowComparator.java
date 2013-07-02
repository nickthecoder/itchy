package uk.co.nickthecoder.itchy.gui;

import java.util.Comparator;

public class SingleColumnRowComparator<T> implements Comparator<TableModelRow>
{
    private final int columnIndex;

    public SingleColumnRowComparator( int columnIndex )
    {
        this.columnIndex = columnIndex;
    }

    @Override
    public int compare( TableModelRow a, TableModelRow b )
    {
        @SuppressWarnings("unchecked")
        Comparable<T> ca = (Comparable<T>) a.getData(this.columnIndex);

        @SuppressWarnings("unchecked")
        T cb = (T) b.getData(this.columnIndex);

        if (ca == null) {
            if (cb == null) {
                return 0;
            } else {
                return 1;
            }
        } else {
            if (cb == null) {
                return -1;
            }
            return ca.compareTo(cb);
        }
    }

}
