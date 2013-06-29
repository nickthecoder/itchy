package uk.co.nickthecoder.thrust;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Behaviour;

public class Ball extends Behaviour {

	public double speedX = 0;
	public double speedY = 0;
	public double weight = 1.0;
	
	private boolean moving = false;
	private Rod rod;
	
	@Override
	public void init()
	{
		this.actor.addTag("solid");
		this.actor.addTag("ball");
	}
	
	public void connect( Rod rod )
	{
		this.moving = true;
		this.rod = rod;
	}
	
	@Override
	public void tick()
	{
		if ( this.moving ) {
			
	        for ( Actor other : Actor.allByTag( "solid" ) ) {
	        	if ( other == this.actor ) continue;
	            if ( this.actor.touching( other ) ) {
	                this.die();
	                return;
	            }
	        }
	        
	        this.speedY += Ship.gravity;
	        this.getActor().moveBy( this.speedX, this.speedY );
		}

	}

	public void die()
	{
		if ( this.rod != null ) {
			this.rod.disconnect();
		}
        this.deathEvent( "death" );
	}
	
}
