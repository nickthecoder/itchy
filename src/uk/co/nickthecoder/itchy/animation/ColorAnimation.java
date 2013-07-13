package uk.co.nickthecoder.itchy.animation;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.jame.RGBA;

public class ColorAnimation extends NumericAnimation
{
    public RGBA targetColor;
    
    private RGBA startColor;

    public ColorAnimation( int ticks, Profile profile, RGBA target )
    {
        super(ticks, profile);
        this.targetColor = target;
    }

    @Override
    public String getName()
    {
        return "Color";
    }

    @Override
    public void start( Actor actor )
    {
        this.startColor = actor.getAppearance().getColorize();
        if ( this.startColor == null ) {
            this.startColor = new RGBA(255,255,255,255);
            // this.startColor = new RGBA( targetColor.r, targetColor.g, targetColor.b, 0 );
        }
    }

    @Override
    public void tick( Actor actor, double amount, double delta )
    {
        double red = this.startColor.r + (this.targetColor.r - this.startColor.r) * amount;
        double green = this.startColor.g + (this.targetColor.g - this.startColor.g) * amount;
        double blue = this.startColor.b + (this.targetColor.b - this.startColor.b) * amount;
        double alpha = this.startColor.a + (this.targetColor.a - this.startColor.a) * amount;

        actor.getAppearance()
                .setColorize(new RGBA((int) red, (int) green, (int) blue, (int) alpha));
    }

}
