package uk.co.nickthecoder.drunkinvaders;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.neighbourhood.ActorCollisionStrategy;

public class ShieldBehaviour extends Behaviour implements Shootable
{
    private ActorCollisionStrategy collisionStrategy;
    
    
    public ShieldBehaviour()
    {
    }
    
    @Override
    public void init()
    {
        this.actor.addTag( "killable" );
        this.actor.addTag( "shootable" );
        this.collisionStrategy = DrunkInvaders.singleton.createCollisionStrategy( this.actor );
    }

    public void onKilled()
    {
        this.collisionStrategy.remove();
        this.collisionStrategy = null;
    }
    
    @Override
    public void shot( Actor by )
    {
        this.deathEvent( "shot" );
    }

    @Override
    public void tick()
    {
    }
}
