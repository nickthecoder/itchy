package uk.co.nickthecoder.drunkinvaders;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Behaviour;

public class Shield extends Behaviour implements Shootable
{
    public Shield()
    {
    }

    @Override
    public void init()
    {
        this.actor.addTag("killable");
        this.actor.addTag("shootable");
        this.collisionStrategy = DrunkInvaders.singleton.createCollisionStrategy(this.actor);
    }

    @Override
    public void onKill()
    {
        if (this.collisionStrategy != null) {
            this.collisionStrategy.remove();
            this.collisionStrategy = null;
        }
    }

    @Override
    public void shot( Actor by )
    {
        this.deathEvent("shot");
    }

    @Override
    public void tick()
    {
    }
}
