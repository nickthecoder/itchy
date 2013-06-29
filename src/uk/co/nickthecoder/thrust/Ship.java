package uk.co.nickthecoder.thrust;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.ScrollableLayer;
import uk.co.nickthecoder.jame.Keys;

public class Ship extends Behaviour {

	public static final double gravity = -0.005;

	public double rotationSpeed = 0.9;

	public double thrust = 0.03;
	
	public double speedX = 0.0;
	public double speedY = 0.0;
	
	public double pickupDistance = 200;
	
	private Rod rod;
	
	
	@Override
	public void init()
	{
		this.actor.addTag("solid");
	}
	
	@Override
	public void tick() {
		
        if ( Itchy.singleton.isKeyDown( Keys.LEFT ) ) {
            this.actor.getAppearance().adjustDirection( this.rotationSpeed );
        }
        if ( Itchy.singleton.isKeyDown( Keys.RIGHT ) ) {
            this.actor.getAppearance().adjustDirection( - this.rotationSpeed );
        }
        if ( Itchy.singleton.isKeyDown( Keys.SPACE ) || Itchy.singleton.isKeyDown( Keys.UP ) ) {
        	this.thrust();
        }
        if ( (this.rod == null) && (Itchy.singleton.isKeyDown( Keys.a )) ) {
        	Actor ballActor = this.actor.nearest( "ball" );
        	if ( ( ballActor != null ) && ( ballActor.distanceTo( this.actor ) < this.pickupDistance ) ) {
	        	this.rod = new Rod( this, (Ball) ballActor.getBehaviour() );
        	}
        }
        
        this.speedY += Ship.gravity;
        this.actor.moveBy( this.speedX, this.speedY );
        
        ((ScrollableLayer) this.actor.getLayer()).ceterOn(this.actor); 
        
        for ( Actor other : Actor.allByTag( "solid" ) ) {
        	if ( other == this.actor ) continue;
            if ( this.actor.touching( other ) ) {
                this.die();
                return;
            }
        }
	}

	public void rodDisconnected()
	{
		this.rod = null;
	}
	
	public void thrust()
	{
		double direction = this.actor.getAppearance().getDirectionRadians();
        double cos = Math.cos( direction );
        double sin = Math.sin( direction );

        this.speedX += this.thrust * cos;
        this.speedY += this.thrust * sin;
	}
	

	public void die()
	{
		if ( this.rod != null ) {
			this.rod.disconnect();
		}
        this.deathEvent( "death" );	
	}
	
}
