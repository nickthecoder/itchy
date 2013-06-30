package uk.co.nickthecoder.drunkinvaders;

import java.util.Random;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Pose;
import uk.co.nickthecoder.itchy.TextPose;
import uk.co.nickthecoder.itchy.util.BorderPoseDecorator;
import uk.co.nickthecoder.itchy.util.ExplosionBehaviour;
import uk.co.nickthecoder.itchy.util.PoseDecorator;

import uk.co.nickthecoder.jame.RGBA;


public class AlienBehaviour extends Bouncy implements Shootable
{
    private static RGBA SPEECH_COLOR = new RGBA( 0, 0, 0 );

    public static final String[] SHOOTABLE_LIST = new String[]{ "shootable" };

    private static PoseDecorator bubbleCreator = new BorderPoseDecorator(
        DrunkInvaders.singleton.resources.getNinePatch( "speech" ), 10, 10, 20, 10 );

    public static final Random random = new Random();
    public double shootFactor = 0.001;

    public boolean tock = true;
    
    @Override
    public void init()
    {
        super.init();
        this.actor.addTag( "deadly" );
        this.actor.addTag( "shootable" );
        DrunkInvaders.singleton.addAliens( 1 );
    }


    @Override
    public void onKilled()
    {
        super.onKilled();
        if ( this.collisionStrategy != null ) {
            this.collisionStrategy.remove();
            this.collisionStrategy = null;
        }
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

        super.tick();
        
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
        DrunkInvaders.singleton.addAliens( -1 );

        TextPose textPose = new TextPose( this.actor.getCostume().getString( "death" ),
                DrunkInvaders.singleton.resources.getFont( "vera" ), 18, SPEECH_COLOR );
        Pose bubble = bubbleCreator.createPose( textPose );

        Actor yell = new Actor( bubble );
        yell.moveTo( this.actor );
        yell.moveBy( 0, 40 );
        yell.activate();
        yell.deathEvent( this.actor.getCostume(), "yell" );
        this.actor.getLayer().add( yell );

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

}


