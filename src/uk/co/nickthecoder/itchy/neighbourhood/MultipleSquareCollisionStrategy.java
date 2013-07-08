package uk.co.nickthecoder.itchy.neighbourhood;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.util.WorldRectangle;

/**
 * Uses a Neighbourhood to optimise Actor's overlapping and touching methods. This strategy uses a
 * grid based neighbourhood. It is designed to be used for objects larger than a single square. If
 * can be mixed and matched with SinglePointCollisionStrategy.
 * 
 * This class was primarily created for large static objects, such as scenery, though it should also
 * be usable with moving objects too.
 * 
 * Adds the actor to all of the neighbourhood squares within its bounding rectangle. To test for
 * collisions, iterate through all of these squares, comparing with each of the the squares
 * occupants. Note that if another actor shares two or more squares, then the collision test will be
 * made two or more times. This is an acceptable overhread, only because this strategy is expected
 * to be used rarely for moving objects. If this assumption isn't true, then its worth creating a
 * new strategy which keeps track of which actors have already been tested to prevent duplicate
 * collision tests.
 * 
 * Note. If combined with SinglePointCollisionStrategy, then actors using this strategy must be
 * passive (they never test for collision, only the other actor tests for collision). i.e.
 * MultiplsSquareCollisionStrategy.touching is never called. This is needed because SinglPoint only
 * track a single square, and tests all surrounding squares, whereas MultiplSquare tracks all
 * (partially) occupied squares, and only considers them. So if a MultiSquare actor tests for
 * collision with a SinglePoint, it may miss it if the single point is outside the square occupied
 * by the tester, but the actors nevertheless overlap.
 */

/*
 * Idealy, this class shouldn't make assumptions about the geometry of the neighbourhood. For
 * example, can it cope with the circular side scrollers, or the doughnut world of asteroids? To do
 * this we need to let neighbourhood handle the iteration from top left to bottom right.
 * Note, at the time of writing neither circular, nor doughnut shaped Neighbourhoods have been implemented. 
 */

public class MultipleSquareCollisionStrategy extends ActorCollisionStrategy
{
    private Neighbourhood neighbourhood;

    private WorldRectangle previousRect;

    public MultipleSquareCollisionStrategy( Actor actor, Neighbourhood neighbourhood )
    {
        super(actor);
        this.neighbourhood = neighbourhood;
        this.update();
    }

    @Override
    public void update()
    {
        WorldRectangle rect = this.actor.getAppearance().getWorldRectangle();
        if ((this.previousRect == null) || (this.previousRect.equals(rect))) {

            for ( Iterator<Square> i = this.neighbourhood.squareIterator( this.previousRect ); i.hasNext(); ) {
                Square square = i.next();
                square.remove(actor);
            }

            for ( Iterator<Square> i = this.neighbourhood.squareIterator( rect ); i.hasNext(); ) {
                Square square = i.next();
                square.add(actor);
            }

            this.previousRect = rect;
        }
    }

    @Override
    public void remove()
    {
        if (this.previousRect != null) {
            for ( Iterator<Square> i = this.neighbourhood.squareIterator( this.previousRect ); i.hasNext(); ) {
                Square square = i.next();
                square.remove(actor);
            }
        }
    }

    @Override
    public Set<Actor> overlapping( Actor source, String... tags )
    {
        Set<Actor> results = new HashSet<Actor>();

        // System.out.println( "overlapping..." );
        for ( Iterator<Square> i = this.neighbourhood.squareIterator( this.previousRect ); i.hasNext(); ) {
            Square square = i.next();

            System.out.println("sqaure : " + square);

            for (Actor actor : square.getOccupants()) {

                // System.out.println( "Actor " + actor );

                if ((actor != source) && (!results.contains(actor))) {
                    for (String tag : tags) {
                        if (actor.hasTag(tag)) {

                            // System.out.println( "Has tag" );

                            // System.out.println( "Checking " + source + " vs " + actor );
                            if (source.overlapping(actor)) {
                                // System.out.println( "is overlapping" );
                                results.add(actor);
                                break;
                            }
                        }
                    }
                }
            }
        }

        return results;
    }

    @Override
    public Set<Actor> touching( Actor source, String... tags )
    {
        Set<Actor> results = new HashSet<Actor>();

        // System.out.println( "overlapping..." );
        for ( Iterator<Square> i = this.neighbourhood.squareIterator( this.previousRect ); i.hasNext(); ) {
            Square square = i.next();

            System.out.println("sqaure : " + square);

            for (Actor actor : square.getOccupants()) {

                // System.out.println( "Actor " + actor );

                if ((actor != source) && (!results.contains(actor))) {
                    for (String tag : tags) {
                        if (actor.hasTag(tag)) {

                            // System.out.println( "Has tag" );

                            // System.out.println( "Checking " + source + " vs " + actor );
                            if (source.touching(actor)) {
                                // System.out.println( "is overlapping" );
                                results.add(actor);
                                break;
                            }
                        }
                    }
                }
            }
        }
        
        return results;
    }
}
