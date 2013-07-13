package uk.co.nickthecoder.itchy.animation;

import uk.co.nickthecoder.itchy.Actor;

public class TurnAnimation extends NumericAnimation
{
    /**
     * The total turn in degrees
     */
    public double turn;
    
    
    public TurnAnimation()
    {
        this(200, linear, 1);
    }

    public TurnAnimation( int ticks, Profile profile, double turn)
    {
        super(ticks, profile);
        this.turn = turn;
    }

    @Override
    public String getName()
    {
        return "Turn";
    }
    
    @Override
    public void tick( Actor actor, double amount, double delta )
    {
        actor.getAppearance().adjustDirection(this.turn * delta );
    }

}
