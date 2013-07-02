package uk.co.nickthecoder.itchy.neighbourhood;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.co.nickthecoder.itchy.Actor;

public class Square
{

    private List<Square> neighbours; // Includes this!

    private Set<Actor> occupants;

    private Neighbourhood neighbourhood;

    private double ox = -1;

    private double oy = -1;

    public Square( Neighbourhood nbh, double x, double y )
    {
        this.neighbourhood = nbh;
        this.occupants = new HashSet<Actor>();
        this.ox = x;
        this.oy = y;
    }

    void initialise()
    {

        this.neighbours = new ArrayList<Square>(9);

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {

                if ((dx == 0) && (dy == 0)) {
                    this.neighbours.add(this);

                    if (this.neighbourhood.getExistingSquare(this.ox, this.oy) != this) {
                        throw new RuntimeException("Square is in the wrong place");
                    }

                } else {
                    Square neighbour = this.neighbourhood.getExistingSquare(this.ox + dx *
                            this.neighbourhood.getSquareSize(),
                            this.oy + dy * this.neighbourhood.getSquareSize());

                    if (neighbour == this) {
                        System.err.println("2 Places " + this + " dx,dy : " + dx + "," + dy);
                        this.neighbourhood.debug();
                        throw new RuntimeException("Square In two places at once! ");
                    }

                    if (neighbour != null) {

                        if (this.ox != neighbour.ox - dx * this.neighbourhood.getSquareSize()) {
                            System.err.println("Incorrect neightbour");
                            System.err.println(this + "->" + dx + "," + dy + " -> " + neighbour);
                            System.err.println(this.ox + " vs " +
                                    (neighbour.ox - dx * this.neighbourhood.getSquareSize()));
                            throw new RuntimeException("Incorrect x neighbour");
                        }

                        if (this.oy != neighbour.oy - dy * this.neighbourhood.getSquareSize()) {
                            System.err.println(this + "->" + dx + "," + dy + " -> " + neighbour);
                            throw new RuntimeException("Incorrect y neighbour");
                        }

                        if (neighbour.neighbours != null) {
                            this.neighbours.add(neighbour);
                            neighbour.neighbours.add(this);
                        }
                    }

                }
            }
        }

    }

    public void add( Actor actor )
    {
        this.occupants.add(actor);
    }

    public void remove( Actor actor )
    {
        this.occupants.remove(actor);
    }

    public List<Square> getNeighbouringSquares()
    {
        return Collections.unmodifiableList(this.neighbours);
    }

    public Set<Actor> getOccupants()
    {
        return Collections.unmodifiableSet(this.occupants);
    }

    public void debug()
    {
        System.err.println("Debugging Square : " + this + "( " + this.ox + "," + this.oy + ")");
        System.err.println("Occupants : " + this.occupants);

        System.err.println();

        for (Square nb : this.neighbours) {
            System.err.println("Neighbour : " + nb);
            System.err.println("  Occupants : " + nb.occupants);
            System.err.println("  mutual neighbours : " + nb.neighbours.contains(this));
        }
    }

    @Override
    public String toString()
    {
        return "Square (" + this.ox + "," + this.oy + ")";
    }
}
