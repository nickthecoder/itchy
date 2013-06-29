package uk.co.nickthecoder.drunkinvaders;

import java.util.Random;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.Pose;
import uk.co.nickthecoder.itchy.TextPose;
import uk.co.nickthecoder.itchy.neighbourhood.ActorCollisionStrategy;
import uk.co.nickthecoder.itchy.util.BorderPoseDecorator;
import uk.co.nickthecoder.itchy.util.ExplosionBehaviour;
import uk.co.nickthecoder.itchy.util.PoseDecorator;

import uk.co.nickthecoder.jame.RGBA;


public class AlienBehaviour extends Behaviour implements Shootable
{
    private static RGBA SPEECH_COLOR = new RGBA( 0, 0, 0 );

    public static final String[] BOUNCY_LIST = new String[]{ "bouncy" };

    public static final String[] SHOOTABLE_LIST = new String[]{ "shootable" };

    private static PoseDecorator bubbleCreator = new BorderPoseDecorator(
        DrunkInvaders.singleton.resources.getNinePatch( "speech" ), 10, 10, 20, 10 );

    public static final Random random = new Random();

    public double vx = 0;
    public double vy = 0;
    public double radius = 20;

    public double shootFactor = 0.001;

    public boolean tock = true;
    
    private ActorCollisionStrategy collisionStrategy;

    @Override
    public void init()
    {
        this.actor.addTag( "bouncy" );
        this.actor.addTag( "deadly" );
        this.actor.addTag( "shootable" );
        this.collisionStrategy = DrunkInvaders.singleton.createCollisionStrategy( this.actor );
    }

    public void onKilled()
    {
        this.collisionStrategy.remove();
        this.collisionStrategy = null;
    }
    
    @Override
    public void tick()
    {

        if ( this.actor.isDying() ) {
            return;
        }

        if ( DrunkInvaders.singleton.metronomeCountdown == 1 ) {
            if ( this.actor.getAnimation() == null ) {
                this.tock = ! this.tock;
                this.actor.event( this.tock ? "tock" : "tick" );
            }
        }

        if ( random.nextDouble() < this.shootFactor ) {
            this.fire();
        }
        this.actor.moveBy( this.vx, this.vy );

        if ( ( this.vy ) > 0 && ( this.actor.getY() + this.radius > 480 ) ) {
            this.vy = -this.vy;
        }
        if ( ( this.vx ) > 0 && ( this.actor.getX() + this.radius > 640 ) ) {
            this.vx = -this.vx;
        }
        if ( ( this.vy ) < 0 && ( this.actor.getY() - this.radius < 0 ) ) {
            this.vy = -this.vy;
        }
        if ( ( this.vx ) < 0 && ( this.actor.getX() - this.radius < 0 ) ) {
            this.vx = -this.vx;
        }

        /*
        for ( Actor other : Actor.allByTag( "bouncy" ) ) {

            if ( other != this.actor ) {
                //if ( overlapping( other ) ) {
                if ( this.actor.touching( other ) ) {
                    collide( this.actor, other );
                }
            }
        }

        for ( Actor other : Actor.allByTag( "shootable" ) ) {
            //if ( ( this.actor != other) && (!other.hasTag( "bouncy" )) && (this.actor.overlapping( other ) ) ) {
            if ( ( this.actor != other) && (!other.hasTag( "bouncy" )) && (this.actor.touching( other ) ) ) {
                ( (Shootable) other.getBehaviour() ).shot( this.actor );
                break;
            }
        }
        */
        this.collisionStrategy.update();
        
        for ( Actor touching : this.collisionStrategy.touching( BOUNCY_LIST ) ) {
            collide( this.actor, touching );
        }
        for ( Actor touching : this.collisionStrategy.touching( SHOOTABLE_LIST ) ) {
            if ( (this.actor != touching) && (!touching.hasTag( "bouncy" ) ) ) {
              ( (Shootable) touching.getBehaviour() ).shot( this.actor ); 
            }
        }
    }

    public void fire()
    {
        this.event( "fire" );

        Actor bullet = new Actor( DrunkInvaders.singleton.resources.getCostume( "bomb" ), "default" );
        bullet.moveTo( this.actor );
        bullet.getAppearance().setDirection( this.actor.getAppearance().getDirection() );
        DrunkInvaders.singleton.mainLayer.add( bullet );
        bullet.moveForward( 10 );
        bullet.setBehaviour( new BulletBehaviour( "killable" ) );
        bullet.activate();
    }

    @Override
    public void shot( Actor bullet )
    {
        TextPose textPose = new TextPose( this.actor.getCostume().getString( "death" ),
                DrunkInvaders.singleton.resources.getFont( "vera" ), 18, SPEECH_COLOR );
        Pose bubble = bubbleCreator.createPose( textPose );

        Actor yell = new Actor( bubble );
        yell.moveTo( this.actor );
        yell.moveBy( 0, 40 );
        yell.activate();
        yell.deathEvent( this.actor.getCostume(), "yell" );
        this.actor.getLayer().add( yell );
        DrunkInvaders.singleton.addAliens( -1 );


        Actor explosion = new Actor( DrunkInvaders.singleton.resources.getPose( "pixel" ) );
        ExplosionBehaviour eb = new ExplosionBehaviour();
        eb.distance = 0;
        eb.randomDistance = 20;
        eb.projectileCount = 20;
        eb.speed = 6.0;
        eb.randomSpeed = 0.1;
        eb.fade = 0.5;
        explosion.setBehaviour( eb );
        explosion.moveTo( this.actor );
        this.actor.getLayer().add( explosion );
        explosion.activate();

        this.actor.removeAllTags();
        this.deathEvent( "death" );

    }

    public boolean overlapping( Actor b )
    {
        Actor a = this.getActor();

        AlienBehaviour bba = this;
        AlienBehaviour bbb = (AlienBehaviour) b.getBehaviour();

        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();

        double dist = Math.sqrt( dx * dx + dy * dy );

        if ( dist > bba.radius + bbb.radius ) {
            return false;
        }
        return true;
    }

    public static void collide( Actor a, Actor b )
    {
        AlienBehaviour bba = (AlienBehaviour) a.getBehaviour();
        AlienBehaviour bbb = (AlienBehaviour) b.getBehaviour();

        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();

        double dist = Math.sqrt( dx * dx + dy * dy );

        double dvx = bbb.vx - bba.vx;
        double dvy = bbb.vy - bba.vy;

        double collision = ( dvx * dx + dvy * dy ) / dist;

        if ( collision < 0 ) {
            // They are moving away from each other
            return;
        }

        bba.vx += dx / dist * collision;
        bbb.vx -= dx / dist * collision;

        bba.vy += dy / dist * collision;
        bbb.vy -= dy / dist * collision;

        //a.getAppearance().adjustDirection( 10 );
        //b.getAppearance().adjustDirection( -10 );
    }
}


