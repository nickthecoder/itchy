package uk.co.nickthecoder.itchy;

import java.util.Set;


public class BruteForceActorCollisionStrategy extends ActorCollisionStrategy
{

    public BruteForceActorCollisionStrategy( Actor actor )
    {
        super(actor);
    }

    @Override
    public Set<Actor> overlapping( Actor actor, String... tags )
    {
        return BruteForceCollisionStrategy.singleton.overlapping(actor, tags);
    }

    @Override
    public Set<Actor> touching( Actor actor, String... tags )
    {
        return BruteForceCollisionStrategy.singleton.touching(actor, tags);
    }

}
