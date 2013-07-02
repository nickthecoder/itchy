package uk.co.nickthecoder.itchy.util;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Behaviour;

public class FollowBehaviour extends Behaviour
{
    private final Actor following;

    private final double dx;

    private final double dy;

    public FollowBehaviour( Actor following )
    {
        this(following, 0, 0);
    }

    public FollowBehaviour( Actor following, double dx, double dy )
    {
        this.following = following;
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public void tick()
    {
        this.actor.moveTo(this.following.getX() + this.dx, this.following.getY() + this.dy);
    }

}
