package uk.co.nickthecoder.itchy.animation;

import uk.co.nickthecoder.itchy.Actor;

public class ScaleAnimation extends NumericAnimation
{
    public double target;
    
    private double initialScale;
    

    public ScaleAnimation()
    {
        this(200, linear, 1);
    }

    public ScaleAnimation( int ticks, Profile profile, double target )
    {
        super(ticks, profile);
        this.target = target;
    }

    @Override
    public String getName()
    {
        return "Scale";
    }

    @Override
    public void start( Actor actor )
    {
        this.initialScale = actor.getAppearance().getScale();
    }
    
    @Override
    public void tick( Actor actor, double amount, double delta )
    {
        double value = this.initialScale + (this.target - this.initialScale) * amount;
        actor.getAppearance().setScale(value);
    }

}
