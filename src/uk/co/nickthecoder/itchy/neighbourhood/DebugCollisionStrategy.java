package uk.co.nickthecoder.itchy.neighbourhood;

import java.util.Set;

import uk.co.nickthecoder.drunkinvaders.DrunkInvaders;
import uk.co.nickthecoder.itchy.Actor;

public class DebugCollisionStrategy extends ActorCollisionStrategy
{
	
	private SinglePointCollisionStrategy strategy1;
	private ActorCollisionStrategy strategy2;
	
	public DebugCollisionStrategy( SinglePointCollisionStrategy a, ActorCollisionStrategy b )
	{
		super( a.getActor() );
		
		strategy1 = a;
		strategy2 = b;
	}
	
	@Override
	public Set<Actor> overlapping(Actor actor, String... tags)
	{
		Set<Actor> results1 = strategy1.overlapping( tags );
		Set<Actor> results2 = strategy2.overlapping( tags );
		
		if ( ! results1.equals( results2 ) ) {
			System.out.println( "Collision failed for " + getActor() );
			System.err.println( "Collision failed for " + getActor() );
			System.err.println( "Results1 : " + results1 );
			System.err.println( "Results2 : " + results2 );
			DrunkInvaders.singleton.debug();
		}
		return results1;
	}
	@Override
	public Set<Actor> touching(Actor actor, String... tags)
	{
		Set<Actor> results1 = strategy1.touching( tags );
		Set<Actor> results2 = strategy2.touching( tags );
		
		if ( ! results1.equals( results2 ) ) {
			System.out.println( "Touching failed for " + getActor() );
			System.err.println( "Touching failed for " + getActor() );
			System.err.println( "Results1 : " + results1 );
			System.err.println( "Results2 : " + results2 );
			DrunkInvaders.singleton.debug();
			
			System.err.println( "Source actor's square : " + strategy1.getSquare() );
			strategy1.getSquare().debug();
			
			System.exit( 1 );
		}
		return results1;
	}
	
	@Override
	public void update()
	{
		strategy1.update();
		strategy2.update();
	}
	
	@Override
	public void remove()
	{
		strategy1.remove();
		strategy2.remove();
	}
}
