package uk.co.nickthecoder.drunkinvaders;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Behaviour;

public class Pacman extends Behaviour
{

    @Override
    public void init()
    {
        super.init();

        this.actor.addTag("deadly");
        this.collisionStrategy = DrunkInvaders.game.createCollisionStrategy(this.actor);
    }
    
    @Override
    public void tick()
    {
        this.collisionStrategy.update();
        
        for (Actor other : touching(Alien.SHOOTABLE_LIST)) {
            if ((this.actor != other) && (!other.hasTag("bouncy"))) {
                ((Shootable) other.getBehaviour()).shot(this.actor);
            }
        }
    }
}
