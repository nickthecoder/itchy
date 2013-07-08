package uk.co.nickthecoder.itchy.neighbourhood;

import java.util.Iterator;

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

    /**
     * Iterates over the set of squares contained by the rectangle defined by the top left and
     * bottom right squares.
     * @param topLeft
     * @param bottomRight Note, the iteration INCLUDES this square, and others in its row and column.
     *      This is different to most range tests, where the "to" is usually exclusive.
     */
    public abstract Iterator<Square> squareIterator( Square topLeft, Square bottomRight );

    /**
     * Looks for a NeighbourhoodSquare within this neighbourhood. If a square at the given
     * coordinates hasn't been created yet, then null is returned.
     */
    public abstract Square getExistingSquare( double x, double y );

    public abstract void debug();

}