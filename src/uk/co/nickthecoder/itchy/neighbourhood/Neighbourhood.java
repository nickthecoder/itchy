package uk.co.nickthecoder.itchy.neighbourhood;

import java.util.Iterator;

import uk.co.nickthecoder.itchy.util.WorldRectangle;

public interface Neighbourhood
{

    public abstract void clear();

    public abstract double getSquareSize();

    /**
     * Looks for a NeighbourhoodSquare within the neighbourhood. If a square at the given
     * coordinates hasn't been created yet, then that square is created.
     * 
     * @return The square at the given coordinate
     */
    public abstract Square getSquare( double x, double y );

    public abstract Iterator<Square> squareIterator( WorldRectangle area );

    /**
     * Looks for a NeighbourhoodSquare within this neighbourhood. If a square at the given
     * coordinates hasn't been created yet, then null is returned.
     */
    public abstract Square getExistingSquare( double x, double y );

    public abstract void debug();

}