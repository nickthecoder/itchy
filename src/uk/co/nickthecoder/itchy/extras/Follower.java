package uk.co.nickthecoder.itchy.extras;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.Costume;

public class Follower extends Behaviour
{
    public Actor following;

    private double dx = 0;

    private double dy = 0;

    private double distance = 0;

    public Follower( Actor following )
    {
        this.following = following;
    }

    public Follower distance( double distance )
    {
        this.distance = distance;
        return this;
    }

    public Follower offset( double x, double y )
    {
        this.dx = x;
        this.dy = y;
        return this;
    }

    @Override
    public void tick()
    {
        this.actor.moveTo(this.following);
        this.actor.moveForward(this.distance);
        this.actor.moveBy(this.dx, this.dy);
    }

    public Actor createActor()
    {
        return createActor(this.following.getCostume());
    }

    public Actor createActor( Costume costume )
    {
        Actor actor = new Actor(costume);
        actor.moveTo(this.following);
        this.following.getLayer().add(actor);
        actor.setBehaviour(this);

        return actor;
    }
}
