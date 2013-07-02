package uk.co.nickthecoder.itchy.neighbourhood;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Actor;

public class Neighbourhood
{
    private final double squareSize;

    private double oy;

    private final List<NeighbourhoodRow> rows;

    public Neighbourhood( double squareSize )
    {
        this.squareSize = squareSize;
        this.rows = new ArrayList<NeighbourhoodRow>();
        this.oy = 0;
    }

    public void clear()
    {
        this.rows.clear();
        this.oy = 0;
    }

    public double getSquareSize()
    {
        return this.squareSize;
    }

    /**
     * Looks for a NeighbourhoodSquare within the neighbourhood. If a square at the given
     * coordinates hasn't been created yet, then that square is created.
     * 
     * @return The square at the given coordinate
     */
    public Square getSquare( double x, double y )
    {
        NeighbourhoodRow row = this.getExistingRow(y);
        if (row == null) {
            row = this.createRow(y);
        }
        return row.getSquare(x);
    }

    /**
     * Looks for a NeighbourhoodSquare within this neighbourhood. If a square at the given
     * coordinates hasn't been created yet, then null is returned.
     */
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
        int iy = (int) Math.floor((y - this.oy) / Neighbourhood.this.squareSize);

        // System.out.println( "Creating row " + y + " ... " + iy );

        if (iy < 0) {

            // System.out.println( "Creating rows at head for " + y + " iy=" + iy );
            List<NeighbourhoodRow> newRows = new ArrayList<NeighbourhoodRow>(-iy);
            this.oy += iy * Neighbourhood.this.squareSize;
            for (int i = 0; i <= -iy - 1; i++) {
                // System.out.println( "Creating row at head " + ( this.oy + i * this.squareSize )
                // );
                newRows.add(new NeighbourhoodRow(this.oy + i * this.squareSize));
            }
            this.rows.addAll(0, newRows);

        } else if (iy >= this.rows.size()) {

            int extra = iy - this.rows.size() + 1;
            // System.out.println( "Creating rows at tail " + extra );
            for (int i = 0; i < extra; i++) {
                // System.out.println( "Creating row at tail " + ( this.oy + this.rows.size() *
                // this.squareSize ) );
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
            int ix = (int) Math.floor((x - this.ox) / Neighbourhood.this.squareSize);
            if ((ix < 0) || (ix >= this.row.size())) {
                return null;
            }
            return this.row.get(ix);
        }

        private Square createSquare( double x )
        {
            int ix = (int) Math.floor((x - this.ox) / Neighbourhood.this.squareSize);
            // System.out.println( "Creating square @ " + x );

            if (ix < 0) {
                // System.out.println( "Creating Sq at head " + ix );
                List<Square> newSquares = new ArrayList<Square>(-ix);
                this.ox += ix * Neighbourhood.this.squareSize;
                for (int i = 0; i < -ix; i++) {
                    newSquares.add(new Square(Neighbourhood.this, this.ox + i *
                            Neighbourhood.this.squareSize, this.y));
                }
                this.row.addAll(0, newSquares);
                for (int i = 0; i < -ix; i++) {
                    // System.out.println( "Index " + i + " sizes " + newSquares.size() + " & " +
                    // this.row.size() );
                    // System.out.println( "   new square " + ( newSquares.get( i ) == this.row.get(
                    // i ) ) );
                    this.row.get(i).initialise();
                }
            } else {
                int extra = ix - this.row.size() + 1;
                // System.out.println( "Creating Squares at tail " + ix + " extras " + extra );
                for (int i = 0; i < extra; i++) {
                    Square square = new Square(Neighbourhood.this, this.ox + (this.row.size()) *
                            Neighbourhood.this.squareSize, this.y);
                    this.row.add(square);
                    square.initialise();
                }

            }

            return this.getExistingSquare(x);
        }

    }

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
