package uk.co.nickthecoder.drunkinvaders;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.Costume;
import uk.co.nickthecoder.itchy.util.DoubleProperty;
import uk.co.nickthecoder.itchy.util.IntegerProperty;
import uk.co.nickthecoder.itchy.util.Property;
import uk.co.nickthecoder.itchy.util.StringProperty;

public class AlienFactory extends Behaviour
{

    public String costumeName;

    public int delay = 0;

    public int greenFlag = 1300;

    public int ticksPerAlien = 20;

    public int alienCount = 6;

    public double spacing = 80;

    public double shootRate = 1;
    
    private int ticks = 0;

    private int ticksRemaining = 0;

    private List<Actor> aliens;

    private static Random random = new Random();

    @Override
    protected void addProperties( List<Property<Behaviour, ?>> list )
    {
        super.addProperties( list );
        list.add( new StringProperty<Behaviour>( "Costume", "costumeName" ) );
        list.add( new IntegerProperty<Behaviour>( "Delay", "delay" ) );
        list.add( new IntegerProperty<Behaviour>( "Green Flag", "greenFlag" ) );
        list.add( new IntegerProperty<Behaviour>( "Delay per Alien", "ticksPerAlien" ) );
        list.add( new IntegerProperty<Behaviour>( "Aliens", "alienCount" ) );
        list.add( new DoubleProperty<Behaviour>( "Spacing", "spacing" ) );
        list.add( new DoubleProperty<Behaviour>( "Shoot Rate", "shootRate" ) );
    }

    @Override
    public void init()
    {
        this.aliens = new ArrayList<Actor>( this.alienCount );
    }

    @Override
    public void tick()
    {
        this.actor.getAppearance().setAlpha( 0 );

        this.ticks++;
        if ( this.ticks < this.delay ) {
            return;
        }

        if ( this.ticksRemaining-- <= 0 ) {
            if ( this.aliens.size() < this.alienCount ) {

                this.ticksRemaining = this.ticksPerAlien;

                Costume costume = DrunkInvaders.singleton.resources.getCostume( this.costumeName );
                Actor alien = new Actor( costume );
                alien.getAppearance().setDirection( this.actor.getAppearance().getDirection() - 90 );
                AlienBehaviour alienBehaviour = new AlienBehaviour();
                alienBehaviour.shootFactor = shootRate / 1000.0;
                alien.moveTo( this.actor.getX() + this.aliens.size() * this.spacing, this.actor.getY() );
                this.actor.getLayer().add( alien );
                alien.setBehaviour( alienBehaviour );
                alien.activate();
                alien.event( "birth" );
                this.aliens.add( alien );
            }
        }

        if ( this.ticks >= this.greenFlag ) {
            this.actor.kill();

            for ( Actor actor : this.aliens ) {
                AlienBehaviour ab = (AlienBehaviour) actor.getBehaviour();
                ab.vx = random.nextDouble() * 2.0 + 0.2;
                ab.vy = random.nextDouble() * 0.6;
                // ab.getActor().event( "start" );
            }
        }

    }

}
