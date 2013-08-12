/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.extras;

import java.util.Random;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Appearance;
import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.Pose;

public class Explosion extends Behaviour
{
    public static Random random = new Random();

    public int projectileCount = 0;

    public int countPerTick = 1000;

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

    private boolean below = false;;
    
    private Actor source;
    
    
    public Explosion( Actor actor )
    {
        super();
        this.direction = actor.getAppearance().getDirection();
        this.source = actor;
    }

    public Actor createActor()
    {
        return this.createActor("");
    }

    public Actor createActor( String poseName )
    {
        Actor result;
        if ( this.source.getCostume() == null ) {
            result = new Actor( this.source.getAppearance().getPose());
        } else {
            result = new Actor(this.source.getCostume());
        }
        this.poseName = poseName;

        result.moveTo(this.source);
        result.getAppearance().setAlpha(0);
        result.setBehaviour(this);

        if ( this.below ) {
            this.source.getLayer().addBelow(result,this.source);
        } else {
            this.source.getLayer().add(result);
        }
        
        return result;
    }

    public Explosion projectiles( int value )
    {
        this.projectileCount = value;
        return this;
    }

    public Explosion projectilesPerClick( int value )
    {
        this.countPerTick = value;
        return this;
    }

    public Explosion gravity( double value )
    {
        this.gravity = value;
        return this;
    }

    /**
     * Are the particles images to be rotated? Typically set to false if the particles are small or
     * circular.
     * 
     * @return this.
     */
    public Explosion rotate( boolean value )
    {
        this.rotate = value;
        return this;
    }

    public Explosion spin( double value )
    {
        return this.spin(value, value);
    }

    public Explosion spin( double from, double to )
    {
        this.spin = from;
        this.randomSpin = to - from;
        return this;
    }

    public Explosion vx( double value )
    {
        return this.vx(value, value);
    }

    public Explosion vx( double from, double to )
    {
        this.vx = from;
        this.randomVx = to - from;
        return this;
    }

    public Explosion vy( double value )
    {
        return this.vy(value, value);
    }

    public Explosion vy( double from, double to )
    {
        this.vy = from;
        this.randomVy = to - from;
        return this;
    }

    public Explosion direction( double value )
    {
        return this.direction(value, value);
    }

    public Explosion direction( double from, double to )
    {
        this.direction = from;
        this.randomDirection = to - from;
        return this;
    }

    /**
     * All fragments point in the same direction as the source actor, The particles will more in the
     * direction given by the "heading" method, (which defaults to 0..360 degrees (i.e. randomly in
     * a full circle)
     * 
     * @return this
     */
    public Explosion forwards()
    {
        this.rotate(true);
        direction(this.source.getAppearance().getDirection());
        this.sameDirection = false;
        return this;
    }

    public Explosion heading( double value )
    {
        return this.heading(value, value);
    }

    public Explosion heading( double from, double to )
    {
        this.sameDirection = false;
        this.heading = from;
        this.randomHeading = to - from;
        return this;
    }

    public Explosion speed( double value )
    {
        return this.speed(value, value);
    }

    public Explosion speed( double from, double to )
    {
        this.speed = from;
        this.randomSpeed = to - from;
        return this;
    }

    public Explosion distance( double value )
    {
        return this.distance(value, value);
    }

    public Explosion distance( double from, double to )
    {
        this.distance = from;
        this.randomDistance = to - from;
        return this;
    }

    public Explosion fade( double value )
    {
        return this.fade(value, value);
    }

    public Explosion fade( double from, double to )
    {
        this.fade = from;
        this.randomFade = to - from;
        return this;
    }

    public Explosion scale( double value )
    {
        return this.scale(value, value);
    }

    public Explosion scale( double from, double to )
    {
        this.scale = from;
        this.randomScale = to - from;
        return this;
    }

    public Explosion grow( double value )
    {
        return this.grow(value, value);
    }

    public Explosion grow( double from, double to )
    {
        this.grow = from;
        this.randomGrow = to - from;
        return this;
    }

    public Explosion life( int value )
    {
        return this.life(value, value);
    }

    public Explosion life( int from, int to )
    {
        this.life = from;
        this.randomLife = to - from;
        return this;
    }

    public Explosion alpha( int alpha )
    {
        return alpha(alpha, alpha);
    }

    public Explosion alpha( int from, int to )
    {
        this.alpha = from;
        this.randomAlpha = to - from;
        return this;
    }

    public Explosion below()
    {
        this.below = true;
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

            Pose pose = null;
            if ((this.poseName != null) && (this.actor.getCostume() != null)) {
                pose = this.actor.getCostume().getPose(this.poseName);
            }
            if (pose == null) {
                pose = this.actor.getAppearance().getPose();
            }
            
            Actor actor = new Actor(pose);
            Appearance appearance = actor.getAppearance();
            Projectile behaviour = new Projectile();

            double direction = this.direction + random.nextDouble() * this.randomDirection;
            if (this.rotate) {
                appearance.setDirection(direction);
            }
            appearance.setScale(this.scale + random.nextDouble() * this.randomScale);
            appearance.setAlpha(this.alpha + random.nextDouble() * this.randomAlpha);

            actor.moveTo(this.actor);
            actor.moveForward(this.distance + random.nextDouble() * this.randomDistance);
            
            behaviour.growFactor = this.grow + random.nextDouble() * this.randomGrow;
            behaviour.life = this.life + (int) (random.nextDouble() * this.randomLife);
            behaviour.spin = this.spin + random.nextDouble() * this.randomSpin;

            behaviour.vx = this.vx + random.nextDouble() * this.randomVx;
            behaviour.vy = this.vy + random.nextDouble() * this.randomVy;
            behaviour.gravity = this.gravity;

            behaviour.fade = this.fade + random.nextDouble() * this.randomFade;

            // Do speed and randomSpeed
            if ((this.speed != 0) || (this.randomSpeed != 0)) {
                if (!this.sameDirection) {
                    direction = this.heading + random.nextDouble() * this.randomHeading;
                }
                double cos = Math.cos(direction / 180.0 * Math.PI);
                double sin = Math.sin(direction / 180.0 * Math.PI);
                behaviour.vx += cos * (this.speed + random.nextDouble() * this.randomSpeed);
                behaviour.vy -= sin * (this.speed + random.nextDouble() * this.randomSpeed);
            }

            appearance.setColorize(this.actor.getAppearance().getColorize());

            actor.setBehaviour(behaviour);
            if (this.below) {
                this.actor.getLayer().addBelow(actor, this.actor);
            } else {
                this.actor.getLayer().add(actor);
            }
            actor.activate();
        }
    }

}
