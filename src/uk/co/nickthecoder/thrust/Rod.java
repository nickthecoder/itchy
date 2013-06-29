package uk.co.nickthecoder.thrust;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Pose;
import uk.co.nickthecoder.jame.Keys;

public class Rod extends Behaviour {

	
	public Ship ship;
	
	public Ball ball;
	
	public boolean extended;
	
	public boolean connected; 
	
	private int poseWidth;
	
	public Rod( Ship ship, Ball ball )
	{
		super();
		this.ship = ship;
		this.ball = ball;
		this.connected = false;
		this.extended = false;
		
		Pose pose = ship.getActor().getCostume().getPose("rod");
    	Actor rodActor = new Actor( pose );
    	poseWidth = pose.getSurface().getWidth();
		rodActor.getAppearance().setScale( 0.1 );
    	rodActor.moveTo( ship.getActor() );
    	rodActor.setBehaviour( this );
    	ship.getActor().getLayer().add( rodActor );
    	rodActor.activate();

	}

	@Override
	public void tick()
	{
        this.actor.moveTo( this.ship.getActor() );
		this.actor.getAppearance().setDirection( this.ball.getActor() );

		double shipBallDistance = ship.getActor().distanceTo( ball.getActor() );

		if ( ! extended ) {
			
			if ( Itchy.singleton.isKeyDown( Keys.a ) ) {
				this.actor.getAppearance().adjustScale( 0.01 );
				// Has the rod extended far enough to reach the ball?
				if ( this.actor.getAppearance().getScale() * this.poseWidth >= shipBallDistance ) {
					this.extended = true;
				}
			} else {
				this.actor.getAppearance().adjustScale( -0.02 );
				if ( this.actor.getAppearance().getScale() <= 0 ) {
					this.disconnect();
					return;
				}

			}
			
			if ( shipBallDistance > ship.pickupDistance ) {
				this.disconnect();
				return;
			}
			
		} else {
			
			this.actor.getAppearance().setScale( shipBallDistance / this.poseWidth );
			
			if ( ! connected ) {

				if ( this.actor.distanceTo( this.ball.getActor() ) >= this.ship.pickupDistance ) {
					this.ball.connect( this );
					this.connected = true;
				}
			
			} else {
				
		        double dist = this.actor.distanceTo( this.ball.getActor() );
		        double dd = dist - this.ship.pickupDistance;
		
		        double angle = this.actor.getAppearance().getDirectionRadians();
		        double cos = Math.cos( angle );
		        double sin = Math.sin( angle );
		
		        double dx1 = cos * dd;
		        double dy1 = sin * dd;

		        double ballFactor = 1.0 / (this.ball.weight + 1); 
		        double shipFactor = this.ball.weight / (this.ball.weight + 1); 
		
		        this.ship.getActor().moveBy( dx1 * shipFactor, dy1 * shipFactor );
		        this.ship.speedX += dx1 * shipFactor;
		        this.ship.speedY += dy1 * shipFactor;
		
		        this.ball.getActor().moveBy( -dx1 * ballFactor, -dy1 * ballFactor );
		        this.ball.speedX -= dx1 * ballFactor;
		        this.ball.speedY -= dy1 * ballFactor;
		        
			}
		}
		
	}

	public void disconnect()
	{
		this.ship.rodDisconnected();
		this.actor.kill();
	}
	
	
}
