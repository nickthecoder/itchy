package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.co.nickthecoder.itchy.util.Property;

public abstract class Behaviour
{
    private final HashMap<Class<?>,List<Property<Behaviour,?>>> allProperties = new HashMap<Class<?>,List<Property<Behaviour,?>>>();

    public static boolean isValidClassName( String behaviourClassName )
    {
        try {
            Class<?> klass = Class.forName( behaviourClassName );
            Object testBehaviour = klass.newInstance();
            if ( ! (testBehaviour instanceof Behaviour) ) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    protected Actor actor;

    public Behaviour()
    {
        this.actor = null;
    }

    public List<Property<Behaviour,?>> getProperties()
    {
        List<Property<Behaviour,?>> result = this.allProperties.get( this.getClass() );
        if ( result == null ) {
            result = new ArrayList<Property<Behaviour,?>>();
            this.addProperties( result );
            this.allProperties.put(  this.getClass(), result );
        }
        return result;
    }

    protected void addProperties( List<Property<Behaviour,?>> list )
    {
    }

    public void attach( Actor actor )
    {
        assert ( this.actor == null );
        this.actor = actor;
        this.init();
    }

    public void init()
    {
    }

    public Actor getActor()
    {
        return this.actor;
    }

    public void play( String soundName )
    {
        this.actor.play( soundName );
    }

    public void event( String poseName )
    {
        this.actor.event( poseName );
    }

    public void deathEvent( String poseName )
    {
        this.actor.deathEvent( poseName );
    }

    public abstract void tick();

    public void onActivated()
    {
        // do nothing
    }

    public void onDeactivated()
    {
        // do nothing
    }

    public void onKilled()
    {
        // do nothing
    }

}
