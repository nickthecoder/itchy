package uk.co.nickthecoder.itchy.animation;

import uk.co.nickthecoder.itchy.Actor;

public class AlphaAnimation extends NumericAnimation
{
    /**
     * The final delta value
     */
    public double target;
    
    private double initialValue;
    

    public AlphaAnimation()
    {
        this(200, linear, 255);
    }

    public AlphaAnimation( int ticks, Profile profile, double target )
    {
        super(ticks, profile);
        this.target = target;
    }

    @Override
    public String getName()
    {
        return "Alpha";
    }

    @Override
    public void start( Actor actor )
    {
        this.initialValue = actor.getAppearance().getAlpha();
    }
    
    @Override
    public void tick( Actor actor, double amount, double delta )
    {
        double value = this.initialValue + (this.target - this.initialValue ) * amount;
        actor.getAppearance().setAlpha(value);
    }

}
