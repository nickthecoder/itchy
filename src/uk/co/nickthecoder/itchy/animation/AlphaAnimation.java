package uk.co.nickthecoder.itchy.animation;

import uk.co.nickthecoder.itchy.Actor;

public class AlphaAnimation extends NumericAnimation
{
    public double from;

    public double to;

    public AlphaAnimation()
    {
        this(200, linear, 255, 0);
    }

    public AlphaAnimation( int ticks, Profile profile, double from, double to )
    {
        super(ticks, profile);
        this.from = from;
        this.to = to;
    }

    @Override
    public String getName()
    {
        return "Alpha";
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
        double value = this.from + (this.to - this.from) * amount;
        actor.getAppearance().setAlpha(value);
    }

}
