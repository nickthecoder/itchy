package uk.co.nickthecoder.itchy.animation;

public class BounceProfile implements Profile
{
    @Override
    public double amount( double amount )
    {
        return amount < 0.5 ? ( 0.5 - amount ) * 2 : ( amount - 0.5 ) * -2;
    }
}
