package uk.co.nickthecoder.drunkinvaders;

import java.util.List;
import java.util.Random;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.Costume;
import uk.co.nickthecoder.itchy.neighbourhood.ActorCollisionStrategy;
import uk.co.nickthecoder.itchy.util.DoubleProperty;
import uk.co.nickthecoder.itchy.util.ExplosionBehaviour;
import uk.co.nickthecoder.itchy.util.IntegerProperty;
import uk.co.nickthecoder.itchy.util.Property;
import uk.co.nickthecoder.itchy.util.StringProperty;


public class MothershipBehaviour
    extends Bouncy
    implements Shootable
{
    // private static RGBA SPEECH_COLOR = new RGBA( 0, 0, 0 );

    public static final String[] SHOOTABLE_LIST = new String[]{ "shootable" };

    private static Random random = new Random();

    
    public String costumeName;

    /**
     *  Determines how quickly the aliens are born.
     */
    public int birthDelay = 100;
    
    /**
     *  The shoot rate for the aliens being created
     */
    public double shootRate = 1;

    /**
     * Determines when the ship begins to move, and create aliens.
     */
    public int greenFlag = 1000;
    
    /**
     * The number of aliens to create
     */
    public int aliens;
    
    
    
    /**
     *  When it reaches 'borthDelay' an alien is created.
     */
    private int birthCounter = 0;

    private int greenFlagCounter = 0;

    /**
     * When it reaches 'aliens' no more aliens will be created
     */
    private int alienCounter = 0;
    
    private ActorCollisionStrategy collisionStrategy;

    @Override
    public void init()
    {
        super.init();
        this.actor.addTag( "deadly" );
        this.actor.addTag( "shootable" );
        this.collisionStrategy = DrunkInvaders.singleton.createCollisionStrategy( this.actor );
    }

    @Override
    protected void addProperties( List<Property<Behaviour, ?>> list )
    {
        super.addProperties( list );
        list.add( new DoubleProperty<Behaviour>( "Speed X", "vx" ) );
        list.add( new DoubleProperty<Behaviour>( "Speed Y", "vy" ) );
        list.add( new StringProperty<Behaviour>( "Costume", "costumeName" ) );
        list.add( new IntegerProperty<Behaviour>( "Birth Delay", "birthDelay" ) );
        list.add( new IntegerProperty<Behaviour>( "Green Flag", "greenFlag" ) );
        list.add( new IntegerProperty<Behaviour>( "Aliens", "aliens" ) );
        list.add( new DoubleProperty<Behaviour>( "Shoot Rate", "shootRate" ) );
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
        
        greenFlagCounter++;
        if ( greenFlagCounter < greenFlag ) {
            return;
        }
        if ( greenFlagCounter == greenFlag ) {
            this.event( "green" );
            DrunkInvaders.singleton.addAliens( 1 );
        }         

        super.tick();
        
        birthCounter ++;
        if ( birthCounter > birthDelay ) {
            birthCounter = 0;
            if ( this.alienCounter < this.aliens ) {               
                this.alienCounter ++;
                giveBirth();
            }
        }

        for ( Actor touching : this.collisionStrategy.touching( SHOOTABLE_LIST ) ) {
            if ( (this.actor != touching) && (!touching.hasTag( "bouncy" ) ) ) {
              ( (Shootable) touching.getBehaviour() ).shot( this.actor ); 
            }
        }
    }

    public void giveBirth()
    {
        this.event( "birth" );

        Costume costume = DrunkInvaders.singleton.resources.getCostume( this.costumeName );
        Actor alien = new Actor( costume );
        alien.getAppearance().setDirection( this.actor.getAppearance().getDirection() - 90 );
        AlienBehaviour alienBehaviour = new AlienBehaviour();
        alienBehaviour.shootFactor = shootRate / 1000.0;
        alien.setBehaviour( alienBehaviour );

        alienBehaviour.vx = this.vx + random.nextDouble() * 0.4 - 0.2; 
        alien.moveTo( this.actor.getX(), this.actor.getY() );
        if ( this.actor.getY() < 200 ) {
            alien.moveForward( 5, 0 );
            alienBehaviour.vy = this.vy + 1; // + random.nextDouble() * -1; 
        } else {
            alien.moveForward( 5, 0 );            
            alienBehaviour.vy = this.vy - 1; // + random.nextDouble() * -1; 
        }

        this.actor.getLayer().add( alien );
        alien.activate();
        alien.event( "dropped" );
    }

    @Override
    public void shot( Actor bullet )
    {
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

}


