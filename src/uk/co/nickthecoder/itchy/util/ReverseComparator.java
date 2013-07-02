package uk.co.nickthecoder.itchy.util;

import java.util.Comparator;

public class ReverseComparator<T> implements Comparator<T>
{
    private Comparator<T> wrapped;

    public ReverseComparator( Comparator<T> wrapped )
    {
        this.wrapped = wrapped;
    }

    @Override
    public int compare( T o1, T o2 )
    {
        return -this.wrapped.compare(o1, o2);
    }
}
