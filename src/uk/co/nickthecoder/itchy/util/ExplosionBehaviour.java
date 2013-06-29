package uk.co.nickthecoder.itchy.util;

import java.util.Random;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Appearance;
import uk.co.nickthecoder.itchy.Behaviour;

public class ExplosionBehaviour extends Behaviour
{
    public static Random random = new Random();

    public int projectileCount = 0;

    public int createInterval = 0;

    public double spin = 0;

    public double randomSpin = 0;

    public double vx = 0;

    public double randomVx = 0;

    public double vy = 0;

    public double randomVy = 0;

    public double direction = 0;

    public double randomDirection = 360;

    public boolean rotate = false;

    public boolean sameHeading = true;

    public double heading = 0;

    public double randomHeading = 360;

    public double speed = 0;

    public double randomSpeed = 0;

    public double distance = 0;

    public double randomDistance = 0;

    public double fade = 0;

    public double randomFade = 0;

    private int waiting = 0;

    @Override
    public void tick()
    {
        this.waiting--;

        while ( this.waiting <= 0 ) {

            this.waiting = this.createInterval;

            if ( this.projectileCount == 0 ) {
                this.actor.kill();
                return;
            } else if ( this.projectileCount > 0 ) {
                this.projectileCount--;
            }

            Actor actor = new Actor( this.actor.getAppearance().getPose() );
            Appearance appearance = actor.getAppearance();
            ProjectileBehaviour behaviour = new ProjectileBehaviour();

            actor.moveTo( this.actor );
            appearance.setDirection( this.direction + random.nextDouble() * this.randomDirection );
            actor.moveForward( this.distance + random.nextDouble() * this.randomDistance );

            behaviour.spin = this.spin + random.nextDouble() * this.randomSpin;

            behaviour.vx = this.vx + random.nextDouble() * this.randomVx;
            behaviour.vy = this.vy + random.nextDouble() * this.randomVy;

            behaviour.fade = this.fade + random.nextDouble() * this.randomFade;

            // Do speed and randomSpeed
            if ( this.speed != 0 ) {
                double direction = this.sameHeading ? appearance.getDirection() : this.heading + random.nextDouble()
                    * this.randomHeading;
                double cos = Math.cos( direction / 180.0 * Math.PI );
                double sin = Math.sin( direction / 180.0 * Math.PI );
                behaviour.vx += cos * ( this.speed + random.nextDouble() * this.randomSpeed );
                behaviour.vy -= sin * ( this.speed + random.nextDouble() * this.randomSpeed );
            }
            if ( !this.rotate ) {
                appearance.setDirection( 0 );
            }

            appearance.setColorize( this.actor.getAppearance().getColorize() );

            actor.setBehaviour( behaviour );
            this.actor.getLayer().add( actor );
            actor.activate();
        }
    }

}
