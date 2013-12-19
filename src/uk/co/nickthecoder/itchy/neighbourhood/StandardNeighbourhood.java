/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.neighbourhood;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uk.co.nickthecoder.itchy.Actor;

public class StandardNeighbourhood implements Neighbourhood
{
    private final double squareSize;

    private double oy;

    private final List<NeighbourhoodRow> rows;

    public StandardNeighbourhood( double squareSize )
    {
        this.squareSize = squareSize;
        this.rows = new ArrayList<NeighbourhoodRow>();
        this.oy = 0;
    }

    @Override
    public void clear()
    {
        this.rows.clear();
        this.oy = 0;
    }

    @Override
    public double getSquareSize()
    {
        return this.squareSize;
    }

    @Override
    public Square getSquare( double x, double y )
    {
        NeighbourhoodRow row = this.getExistingRow(y);
        if (row == null) {
            row = this.createRow(y);
        }
        return row.getSquare(x);
    }

    @Override
    public Iterator<Square> squareIterator( final Square topLeft, final Square bottomRight )
    {
        return new Iterator<Square>() {

            private double x = topLeft.getX();
            private double y = topLeft.getY();

            @Override
            public boolean hasNext()
            {
                return (this.y <= bottomRight.getY());
            }

            @Override
            public Square next()
            {
                Square square = StandardNeighbourhood.this.getSquare(this.x, this.y);
                this.x += StandardNeighbourhood.this.squareSize;
                if (this.x > bottomRight.getX()) {
                    this.y += StandardNeighbourhood.this.squareSize;
                    this.x = topLeft.getX();
                }
                return square;
            }

            @Override
            public void remove()
            {
                throw new RuntimeException("Cannot remove using this Neighbourhood.squareIterator");
            }

        };
    }

    @Override
    public Square getExistingSquare( double x, double y )
    {
        NeighbourhoodRow row = this.getExistingRow(y);
        if (row == null) {
            return null;
        }
        Square result = row.getExistingSquare(x);
        if (result == null) {
            return null;
        }
        return result;
    }

    private NeighbourhoodRow getExistingRow( double y )
    {
        int iy = (int) Math.floor((y - this.oy) / this.squareSize);
        if ((iy < 0) || (iy >= this.rows.size())) {
            return null;
        }
        return this.rows.get(iy);
    }

    private NeighbourhoodRow createRow( double y )
    {
        int iy = (int) Math.floor((y - this.oy) / StandardNeighbourhood.this.squareSize);

        if (iy < 0) {

            List<NeighbourhoodRow> newRows = new ArrayList<NeighbourhoodRow>(-iy);
            this.oy += iy * StandardNeighbourhood.this.squareSize;
            for (int i = 0; i <= -iy - 1; i++) {
                newRows.add(new NeighbourhoodRow(this.oy + i * this.squareSize));
            }
            this.rows.addAll(0, newRows);

        } else if (iy >= this.rows.size()) {

            int extra = iy - this.rows.size() + 1;
            for (int i = 0; i < extra; i++) {
                NeighbourhoodRow row = new NeighbourhoodRow(this.oy + this.rows.size() *
                    this.squareSize);
                this.rows.add(row);
            }

        } else {
            throw new RuntimeException("Attempt to recreate an existing row " + y);
        }

        return this.getExistingRow(y);
    }

    /* ---------------------------------------------- */
    /* ------------- NeighbourhoodRow --------------- */
    /* ---------------------------------------------- */

    public class NeighbourhoodRow
    {
        private double ox;

        private final double y;

        private final List<Square> row;

        public NeighbourhoodRow( double y )
        {
            this.y = y;
            this.ox = 0;
            this.row = new ArrayList<Square>();
        }

        public Square getSquare( double x )
        {
            Square result = this.getExistingSquare(x);
            if (result == null) {
                result = this.createSquare(x);
            }
            return result;

        }

        private Square getExistingSquare( double x )
        {
            int ix = (int) Math.floor((x - this.ox) / StandardNeighbourhood.this.squareSize);
            if ((ix < 0) || (ix >= this.row.size())) {
                return null;
            }
            return this.row.get(ix);
        }

        private Square createSquare( double x )
        {
            int ix = (int) Math.floor((x - this.ox) / StandardNeighbourhood.this.squareSize);

            if (ix < 0) {
                List<Square> newSquares = new ArrayList<Square>(-ix);
                this.ox += ix * StandardNeighbourhood.this.squareSize;
                for (int i = 0; i < -ix; i++) {
                    newSquares.add(new Square(StandardNeighbourhood.this, this.ox + i *
                        StandardNeighbourhood.this.squareSize, this.y));
                }
                this.row.addAll(0, newSquares);
                for (int i = 0; i < -ix; i++) {
                    this.row.get(i).initialise();
                }
            } else {
                int extra = ix - this.row.size() + 1;
                for (int i = 0; i < extra; i++) {
                    Square square = new Square(StandardNeighbourhood.this, this.ox +
                        (this.row.size()) *
                        StandardNeighbourhood.this.squareSize, this.y);
                    this.row.add(square);
                    square.initialise();
                }

            }

            return this.getExistingSquare(x);
        }

    }

    @Override
    public void debug()
    {
        System.err.println("Neighbourhood : " + this.getSquareSize() + " oy=" + this.oy);
        double y = this.oy;
        for (NeighbourhoodRow row : this.rows) {
            System.err.println("\nRow : " + y + " ... " + row.y);

            double x = row.ox;
            for (Square sq : row.row) {
                System.err.println("\n" + sq + " : expected : " + x + "," + y + "\n ");
                for (Actor actor : sq.getOccupants()) {
                    System.err.println(actor);
                }
                x += getSquareSize();
            }

            y += getSquareSize();
        }
    }

}
