package uk.co.nickthecoder.itchy.animation;

import uk.co.nickthecoder.itchy.Actor;

public class TurnAnimation extends NumericAnimation
{
    public double from;

    public double to;

    public TurnAnimation()
    {
        this(200, linear, 1, 1);
    }

    public TurnAnimation( int ticks, Profile profile, double from, double to )
    {
        super(ticks, profile);
        this.from = from;
        this.to = to;
    }

    @Override
    public String getName()
    {
        return "Turn";
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
        actor.getAppearance().adjustDirection(this.from + (this.to - this.from) * amount);
    }

}
