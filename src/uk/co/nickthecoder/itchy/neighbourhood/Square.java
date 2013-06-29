package uk.co.nickthecoder.itchy.neighbourhood;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;


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
    	neighbourhood = nbh;
    	occupants = new HashSet<Actor>();
    	ox = x;
    	oy = y;
    }

    void initialise()
    {

        this.neighbours = new ArrayList<Square>( 9 );
        
        for ( int dx = -1; dx <= 1; dx++ ) {
            for ( int dy = -1; dy <= 1; dy++ ) {
            	
                if ( ( dx == 0 ) && ( dy == 0 ) ) {
                    this.neighbours.add( this );
                                        
                    if ( neighbourhood.getExistingSquare( ox, oy ) != this ) {
                    	throw new RuntimeException( "Square is in the wrong place" );
                    }
                    
                } else {
                    Square neighbour = neighbourhood.getExistingSquare(
                		ox + dx * neighbourhood.getSquareSize() ,
                        oy + dy * neighbourhood.getSquareSize() );

                	if ( neighbour == this ) {
                		System.err.println( "2 Places " + this + " dx,dy : " + dx + "," + dy );
                		neighbourhood.debug();
                		throw new RuntimeException( "Square In two places at once! " );
                	}
	                    
                	
                    if ( neighbour != null ) {

	                	if ( ox != neighbour.ox - dx * neighbourhood.getSquareSize() ) {
	                		System.err.println( "Incorrect neightbour" );
	                		System.err.println( this + "->" + dx + "," + dy + " -> " + neighbour );
	                		System.err.println( ox + " vs " + (neighbour.ox - dx * neighbourhood.getSquareSize() ) );
	                		throw new RuntimeException( "Incorrect x neighbour" );
	                	}
	                	
	                	if ( oy != neighbour.oy - dy * neighbourhood.getSquareSize() ) {
	                		System.err.println( this + "->" + dx + "," + dy + " -> " + neighbour );
	                		throw new RuntimeException( "Incorrect y neighbour" );
	                	}
                	

                    	if ( neighbour.neighbours != null ) {
                            this.neighbours.add( neighbour );
                            neighbour.neighbours.add( this );
                        }
                    }

                }
            }
        }
        
    }
    
    public void add( Actor actor )
    {
        this.occupants.add( actor );
    }
    
    public void remove( Actor actor )
    {
        this.occupants.remove( actor );
    }

    public List<Square> getNeighbouringSquares()
    {
    	return Collections.unmodifiableList( this.neighbours );
    }
    
    public Set<Actor> getOccupants()
    {
    	return Collections.unmodifiableSet( this.occupants );
    }

    public void debug()
    {
    	System.err.println( "Debugging Square : " + this + "( " + ox + "," + oy + ")" );
    	System.err.println( "Occupants : " + occupants );
    	
    	System.err.println();
    	
    	for ( Square nb : neighbours ) {
    		System.err.println( "Neighbour : " + nb );
    		System.err.println( "  Occupants : " + nb.occupants );
    		System.err.println( "  mutual neighbours : " + nb.neighbours.contains( this ) );
    	}
    }

    public String toString()
    {
    	return "Square (" + ox + "," + oy + ")";
    }
}


