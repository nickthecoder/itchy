package uk.co.nickthecoder.drunkinvaders;

import java.util.List;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.neighbourhood.ActorCollisionStrategy;
import uk.co.nickthecoder.itchy.util.DoubleProperty;
import uk.co.nickthecoder.itchy.util.Property;
import uk.co.nickthecoder.itchy.util.StringProperty;

public class BulletBehaviour extends Behaviour implements Shootable
{
    public double speed = 5.0;

    public String targetTagName;
    
    private ActorCollisionStrategy collisionStrategy;

        
    public BulletBehaviour()
    {
        this( "shootable" );
    }

    public BulletBehaviour( String tagName )
    {
        super();
        this.targetTagName = tagName;
    }
    
    @Override
    public void init()
    {
        this.collisionStrategy = DrunkInvaders.singleton.createCollisionStrategy( this.actor );
    }

    public void onKilled()
    {
        this.collisionStrategy.remove();
        this.collisionStrategy = null;
    }
    
    @Override
    protected void addProperties( List<Property<Behaviour, ?>> list )
    {
        super.addProperties( list );
        list.add( new StringProperty<Behaviour>( "Target Tag", "targetTagName" ) );
        list.add( new DoubleProperty<Behaviour>( "Speed", "speed" ) );
    }

    @Override
    public void shot( Actor by )
    {
        this.deathEvent( "shot" );
    }

    @Override
    public void tick()
    {
        this.actor.moveForward( this.speed );

        if ( ! this.actor.isOnScreen() ) {
            this.actor.kill();
        }

        /*
        for ( Actor other : Actor.allByTag( this.targetTagName ) ) {
            if ( this.actor.overlapping( other ) ) {
            //if ( this.actor.touching( other ) ) {
                ( (Shootable) other.getBehaviour() ).shot( this.actor );
                this.actor.kill();

                break;
            }
        }
        */
        if ( this.collisionStrategy != null ) {
            this.collisionStrategy.update();
                    
            for ( Actor touching : this.collisionStrategy.touching( this.targetTagName ) ) {
                ( (Shootable) touching.getBehaviour() ).shot( this.actor );
                this.actor.kill();

                break;
            }
        }
    }
}

