package uk.co.nickthecoder.itchy.util;

import java.util.List;
import java.util.Random;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Appearance;
import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.Pose;
import uk.co.nickthecoder.itchy.PoseResource;

public class ExplosionBehaviour extends Behaviour
{
    public static Random random = new Random();

    public int projectileCount = 0;

    public int countPerTick = 0;

    public double gravity = 0;

    public boolean rotate = false;

    public boolean sameDirection = true;
    
    
    public double spin = 0;

    public double randomSpin = 0;

    public double vx = 0;

    public double randomVx = 0;

    public double vy = 0;

    public double randomVy = 0;

    public double direction = 0;

    public double randomDirection = 360;

    public double heading = 0;

    public double randomHeading = 360;

    public double speed = 0;

    public double randomSpeed = 0;

    public double distance = 0;

    public double randomDistance = 0;

    public double fade = 0;

    public double randomFade = 0;

    public int life = 300;

    public int randomLife = 300;

    public int alpha = 255;
    
    public int randomAlpha = 0;
    
    public double scale = 1;
    
    public double randomScale = 0;
    
    public double grow = 1;
    
    public double randomGrow = 0;
    
    private String poseName;

    public Actor createActor( Actor source )
    {
        return this.createActor( source, "" );
    }
    
    public Actor createActor( Actor source, String poseName )
    {
        Actor result = new Actor( source.getCostume() );
        this.poseName = poseName;
        
        result.moveTo(source);
        result.getAppearance().setAlpha(0);
        result.setBehaviour(this);
        
        source.getLayer().add(result);

        return result;
    }
    
    public ExplosionBehaviour usePoses( Actor actor, String poseName )
    {
        this.actor.setCostume( actor.getCostume() );
        this.poseName = poseName;
        
        return this;
    }
    
    public ExplosionBehaviour projectiles( int value )
    {
        this.projectileCount = value;
        return this;
    }
    public ExplosionBehaviour projectilesPerClick( int value )
    {
        this.countPerTick = value;
        return this;
    }
    public ExplosionBehaviour gravity( double value )
    {
        this.gravity = value;
        return this;
    }
    public ExplosionBehaviour rotate( boolean value )
    {
        this.rotate = value;
        return this;
    }
    
    public ExplosionBehaviour spin( double value )
    {
        return this.spin( value, value );
    }
    public ExplosionBehaviour spin( double from, double to )
    {
        this.spin = from;
        this.randomSpin = to - from;
        return this;
    }

    public ExplosionBehaviour vx( double value )
    {
        return this.vx( value, value );
    }
    public ExplosionBehaviour vx( double from, double to )
    {
        this.vx = from;
        this.randomVx = to - from;
        return this;
    }

    public ExplosionBehaviour vy( double value )
    {
        return this.vy( value, value );
    }
    public ExplosionBehaviour vy( double from, double to )
    {
        this.vy = from;
        this.randomVy = to - from;
        return this;
    }

    public ExplosionBehaviour direction( double value )
    {
        return this.direction( value, value );
    }
    public ExplosionBehaviour direction( double from, double to )
    {
        this.direction = from;
        this.randomDirection = to - from;
        return this;
    }

    public ExplosionBehaviour heading( double value )
    {
        return this.heading( value, value );
    }
    public ExplosionBehaviour heading( double from, double to )
    {
        this.sameDirection = false;
        this.heading = from;
        this.randomHeading = to - from;
        return this;
    }

    public ExplosionBehaviour speed( double value )
    {
        return this.speed( value, value );
    }
    public ExplosionBehaviour speed( double from, double to )
    {
        this.speed = from;
        this.randomSpeed = to - from;
        return this;
    }

    public ExplosionBehaviour distance( double value )
    {
        return this.distance( value, value );
    }
    public ExplosionBehaviour distance( double from, double to )
    {
        this.distance = from;
        this.randomDistance = to - from;
        return this;
    }


    public ExplosionBehaviour fade( double value )
    {
        return this.fade( value, value );
    }
    public ExplosionBehaviour fade( double from, double to )
    {
        this.fade = from;
        this.randomFade = to - from;
        return this;
    }

    public ExplosionBehaviour scale( double value )
    {
        return this.scale( value, value );
    }
    public ExplosionBehaviour scale( double from, double to )
    {
        this.scale = from;
        this.randomScale = to - from;
        return this;
    }

    public ExplosionBehaviour grow( double value )
    {
        return this.grow( value, value );
    }
    public ExplosionBehaviour grow( double from, double to )
    {
        this.grow = from;
        this.randomGrow = to - from;
        return this;
    }

    public ExplosionBehaviour life( int value )
    {
        return this.life( value, value );
    }
    public ExplosionBehaviour life( int from, int to )
    {
        this.life = from;
        this.randomLife = to - from;
        return this;
    }
    
    public ExplosionBehaviour alpha( int alpha )
    {
        return alpha( alpha, alpha );
    }

    public ExplosionBehaviour alpha( int from, int to )
    {
        this.alpha = from;
        this.randomAlpha = to - from;
        return this;
    }
    
    
    
    @Override
    public void tick()
    {
        for (int i = 0; i < this.countPerTick; i++) {

            if (this.projectileCount <= 0) {
                this.actor.kill();
                return;
            }
            this.projectileCount--;

            Pose pose;
            if ( this.poseName != null ) {
                pose = this.actor.getCostume().getPose( this.poseName );
            } else {
                pose = this.actor.getAppearance().getPose();
            }
            
            Actor actor = new Actor(pose);
            Appearance appearance = actor.getAppearance();
            ProjectileBehaviour behaviour = new ProjectileBehaviour();

            actor.moveTo(this.actor);
            actor.moveForward(this.distance + random.nextDouble() * this.randomDistance);

            appearance.setDirection(this.direction + random.nextDouble() * this.randomDirection);
            appearance.setScale(this.scale + random.nextDouble() * this.randomScale);            
            appearance.setAlpha( this.alpha + random.nextDouble() * this.randomAlpha );
            
            behaviour.growFactor = this.grow + random.nextDouble() * this.randomGrow;
            behaviour.life = this.life + (int) (random.nextDouble() * this.randomLife);
            behaviour.spin = this.spin + random.nextDouble() * this.randomSpin;

            behaviour.vx = this.vx + random.nextDouble() * this.randomVx;
            behaviour.vy = this.vy + random.nextDouble() * this.randomVy;
            behaviour.gravity = this.gravity;

            behaviour.fade = this.fade + random.nextDouble() * this.randomFade;

            // Do speed and randomSpeed
            if ((this.speed != 0) || (this.randomSpeed != 0)) {
                double direction = sameDirection ? appearance.getDirection() :
                    this.heading + random.nextDouble() * this.randomHeading;
                double cos = Math.cos(direction / 180.0 * Math.PI);
                double sin = Math.sin(direction / 180.0 * Math.PI);
                behaviour.vx += cos * (this.speed + random.nextDouble() * this.randomSpeed);
                behaviour.vy -= sin * (this.speed + random.nextDouble() * this.randomSpeed);
            }
            if (!this.rotate) {
                appearance.setDirection(0);
            }

            appearance.setColorize(this.actor.getAppearance().getColorize());

            actor.setBehaviour(behaviour);
            this.actor.getLayer().add(actor);
            actor.activate();
        }
    }

}
