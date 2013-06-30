package uk.co.nickthecoder.itchy.animation;

import uk.co.nickthecoder.itchy.Actor;

public class ScaleAnimation extends NumericAnimation
{
    public double from;

    public double to;

    public ScaleAnimation()
    {
        this( 0, linear, 1, 2 );
    }

    public ScaleAnimation( int ticks, Profile profile, double from, double to )
    {
        super( ticks, profile );
        this.from = from;
        this.to = to;
    }

    @Override
    public String getName()
    {
        return "Scale";
    }

    public double getFrom()
    {
        return this.from;
    }

    public double getTo()
    {
        return this.to;
    }

    @Override
    public void tick( Actor actor, double amount )
    {
        actor.getAppearance().setScale( amount );
    }

}
