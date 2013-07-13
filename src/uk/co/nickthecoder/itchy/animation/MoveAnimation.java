package uk.co.nickthecoder.itchy.animation;

import uk.co.nickthecoder.itchy.Actor;

public class MoveAnimation extends NumericAnimation
{
    /**
     * The total X distance to move
     */
    public double dx;
    
    /**
     * The total Y distance to move.
     */
    public double dy;

    public MoveAnimation()
    {
        this(200, NumericAnimation.linear, 0, 0);
    }

    public MoveAnimation( int ticks, Profile profile, double dx, double dy )
    {
        super(ticks, profile);
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public String getName()
    {
        return "Move";
    }

    @Override
    public void tick( Actor actor, double amount, double delta )
    {
        actor.moveBy(this.dx * delta, this.dy * delta);
    }

}
