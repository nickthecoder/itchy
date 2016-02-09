package uk.co.nickthecoder.itchy.util;

public interface Filter<M>
{
    public boolean accept( M subject );
}
