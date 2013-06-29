package uk.co.nickthecoder.itchy.neighbourhood;

import java.util.Set;

import uk.co.nickthecoder.itchy.Actor;

public interface CollisionStrategy
{

    public Set<Actor> overlapping( Actor actor, String... tags );

    public Set<Actor> touching( Actor actor, String... tags );
    

}
