package uk.co.nickthecoder.itchy.extras;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Behaviour;

public class Projectile extends Behaviour
{
    public double gravity;

    public double vx;

    public double vy;

    public double speed;

    public double spin;

    public double fade;

    public double growFactor = 1;

    public int life = 1000;
    
    public Actor createActor( Actor source, String poseName )
    {
        Actor actor = new Actor( source.getCostume().getPose( poseName ) );
        actor.getAppearance().setDirection( source.getAppearance().getDirection());
        actor.moveTo( source );
        actor.setBehaviour(this);
        source.getLayer().add(actor);
        
        return actor;
    }
    
    public Projectile speed( double value )
    {
        this.speed = value;
        return this;
    }
    public Projectile vx( double value )
    {
        this.vx = value;
        return this;
    }
    public Projectile vy( double value )
    {
        this.vy = value;
        return this;
    }
    public Projectile gravity( double value )
    {
        this.gravity = value;
        return this;
    }
    public Projectile spin( double value )
    {
        this.spin = value;
        return this;
    }
    public Projectile fade( double value )
    {
        this.fade = value;
        return this;
    }
    public Projectile growFactor( double value )
    {
        this.growFactor = value;
        return this;
    }
    
    @Override
    public void tick()
    {
        this.actor.moveBy(this.vx, this.vy);
        this.actor.moveForward(this.speed);
        this.vy += this.gravity;
        this.actor.getAppearance().adjustAlpha(-this.fade);
        this.actor.getAppearance().adjustDirection(this.spin);

        if (this.growFactor != 1) {
            this.actor.getAppearance().setScale(
                this.actor.getAppearance().getScale() * this.growFactor);
        }

        if ((this.life-- < 0) || (this.actor.getAppearance().getAlpha() <= 0)) {
            this.actor.kill();
        }

    }
}