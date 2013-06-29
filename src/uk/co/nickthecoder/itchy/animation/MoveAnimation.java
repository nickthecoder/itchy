package uk.co.nickthecoder.itchy.animation;

import uk.co.nickthecoder.itchy.Actor;

public class MoveAnimation extends NumericAnimation
{
    public double dx;
    public double dy;

    public MoveAnimation()
    {
        this( 200, NumericAnimation.unit, 0, 0 );
    }

    public MoveAnimation( int ticks, Profile profile, double dx, double dy )
    {
        super( ticks, profile );
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public String getName()
    {
        return "Move";
    }

    @Override
    public void tick( Actor actor, double amount )
    {
        actor.moveBy( this.dx * amount, this.dy * amount );
    }

}
