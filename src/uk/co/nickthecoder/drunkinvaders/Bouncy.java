package uk.co.nickthecoder.drunkinvaders;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.neighbourhood.ActorCollisionStrategy;

public class Bouncy extends Behaviour 
{
    public static final String[] BOUNCY_LIST = new String[]{ "bouncy" };


    public double vx = 0;
    
    public double vy = 0;
    
    public double radius = 20;

    protected ActorCollisionStrategy collisionStrategy;

    @Override
    public void init()
    {
        super.init();
        this.actor.addTag( "bouncy" );
        this.collisionStrategy = DrunkInvaders.singleton.createCollisionStrategy( this.actor );

    }
    
    @Override
    public void onKilled()
    {
        super.onKilled();
        if ( this.collisionStrategy != null ) {
            this.collisionStrategy.remove();
            this.collisionStrategy = null;
        }
    }

    @Override
    public void tick()
    {
        this.actor.moveBy( this.vx, this.vy );

        if ( ( this.vy ) > 0 && ( this.actor.getY() + this.radius > 480 ) ) {
            this.vy = -this.vy;
        }
        if ( ( this.vx ) > 0 && ( this.actor.getX() + this.radius > 640 ) ) {
            this.vx = -this.vx;
        }
        if ( ( this.vy ) < 0 && ( this.actor.getY() - this.radius < 0 ) ) {
            this.vy = -this.vy;
        }
        if ( ( this.vx ) < 0 && ( this.actor.getX() - this.radius < 0 ) ) {
            this.vx = -this.vx;
        }

        this.collisionStrategy.update();
        
        for ( Actor touching : this.collisionStrategy.touching( BOUNCY_LIST ) ) {
            collide( this.actor, touching );
        }

    }
    
    public static void collide( Actor a, Actor b )
    {
        Bouncy bba = (Bouncy) a.getBehaviour();
        Bouncy bbb = (Bouncy) b.getBehaviour();

        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();

        double dist = Math.sqrt( dx * dx + dy * dy );

        double dvx = bbb.vx - bba.vx;
        double dvy = bbb.vy - bba.vy;

        double collision = ( dvx * dx + dvy * dy ) / dist;

        if ( collision < 0 ) {
            // They are moving away from each other
            return;
        }

        bba.vx += dx / dist * collision;
        bbb.vx -= dx / dist * collision;

        bba.vy += dy / dist * collision;
        bbb.vy -= dy / dist * collision;

    }
    
}

