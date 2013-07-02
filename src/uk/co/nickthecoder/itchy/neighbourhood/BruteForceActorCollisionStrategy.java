package uk.co.nickthecoder.itchy.neighbourhood;

import java.util.Set;

import uk.co.nickthecoder.itchy.Actor;

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
