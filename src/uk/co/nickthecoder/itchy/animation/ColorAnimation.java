package uk.co.nickthecoder.itchy.animation;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.jame.RGBA;

public class ColorAnimation extends NumericAnimation
{
    public RGBA startColor;

    public RGBA endColor;

    public ColorAnimation( int ticks, Profile profile, RGBA start, RGBA end )
    {
        super( ticks, profile );
        this.startColor = start;
        this.endColor = end;
    }

    @Override
    public String getName()
    {
        return "Color";
    }

    @Override
    public void start( Actor actor )
    {
        actor.getAppearance().setColorize( this.startColor );
    }

    @Override
    public void tick( Actor actor, double amount )
    {
        double red   = this.startColor.r + ( this.endColor.r - this.startColor.r ) * amount;
        double green = this.startColor.g + ( this.endColor.g - this.startColor.g ) * amount;
        double blue  = this.startColor.b + ( this.endColor.b - this.startColor.b ) * amount;
        double alpha = this.startColor.a + ( this.endColor.a - this.startColor.a ) * amount;

        actor.getAppearance().setColorize( new RGBA( (int) red, (int) green, (int) blue, (int) alpha ) );
    }


}
