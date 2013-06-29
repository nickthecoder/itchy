package uk.co.nickthecoder.itchy.neighbourhood;

/**
    The default strategy for Actor's overlapping and touching methods. It is Order n squared,
    and therefore slow for large values of n.
*/

import java.util.Set;
import java.util.HashSet;

import uk.co.nickthecoder.itchy.Actor;

public class BruteForceCollisionStrategy
    implements CollisionStrategy
{
	public static final BruteForceCollisionStrategy singleton = new BruteForceCollisionStrategy();
    
    public Set<Actor> overlapping( Actor source, String... tags )
    {
        Set<Actor> results = new HashSet<Actor>();
        for ( int i = 0; i < tags.length; i ++ ) {
            for ( Actor other : Actor.allByTag( tags[ i ] ) ) {

                if ( other != source ) {
                    if ( ! results.contains( other ) ) {
                        if ( source.overlapping( other ) ) {
                            results.add( other );
                        }
                    }
                }
            }
        }
        return results;
    }

    public Set<Actor> touching( Actor source, String... tags )
    {
        Set<Actor> results = new HashSet<Actor>();
        for ( int i = 0; i < tags.length; i ++ ) {
            for ( Actor other : Actor.allByTag( tags[ i ] ) ) {

                if ( other != source ) {
                    if ( ! results.contains( other ) ) {
                        if ( source.touching( other ) ) {
                            results.add( other );
                        }
                    }
                }
            }
        }
        return results;
    }
    
}

