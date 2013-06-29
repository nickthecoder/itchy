package uk.co.nickthecoder.itchy.animation;

public class ConstantProfile implements Profile
{
    private final double constant;

    public ConstantProfile( double constant )
    {
        this.constant = constant;
    }

    @Override
    public double amount( double amount )
    {
        return this.constant;
    }

}
