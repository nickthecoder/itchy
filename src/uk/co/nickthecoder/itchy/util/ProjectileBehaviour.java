package uk.co.nickthecoder.itchy.util;

import uk.co.nickthecoder.itchy.Behaviour;

public class ProjectileBehaviour extends Behaviour
{
    public double gravity;

    public double vx;

    public double vy;

    public double spin;

    public double fade;

    public int life = 10000; // An arbitary amount of time till it dies.
                             // Projectiles are usually short lived

    @Override
    public void tick()
    {
        this.actor.moveBy( this.vx, this.vy );
        this.vy += this.gravity;
        this.actor.getAppearance().adjustAlpha( -this.fade );
        this.actor.getAppearance().adjustDirection( this.spin );


        if ( ( this.life-- < 0 ) || ( this.actor.getAppearance().getAlpha() <= 0 ) ) {
            this.actor.kill();
        }

    }
}
