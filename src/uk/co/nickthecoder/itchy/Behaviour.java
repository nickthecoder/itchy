package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.co.nickthecoder.itchy.util.GProperty;

public abstract class Behaviour
{
    private final HashMap<Class<?>, List<GProperty<Behaviour, ?>>> allProperties = new HashMap<Class<?>, List<GProperty<Behaviour, ?>>>();

    public static boolean isValidClassName( String behaviourClassName )
    {
        try {
            Class<?> klass = Class.forName(behaviourClassName);
            Object testBehaviour = klass.newInstance();
            if (!(testBehaviour instanceof Behaviour)) {
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

    public List<GProperty<Behaviour, ?>> getProperties()
    {
        List<GProperty<Behaviour, ?>> result = this.allProperties.get(this.getClass());
        if (result == null) {
            result = new ArrayList<GProperty<Behaviour, ?>>();
            this.allProperties.put(this.getClass(), result);
            this.addProperties();
        }
        return result;
    }

    protected void addProperties() // List<Property<?>> list )
    {
    }

    protected void addProperty( GProperty<Behaviour, ?> property )
    {
        List<GProperty<Behaviour, ?>> result = this.allProperties.get(this.getClass());
        result.add(property);
    }

    public void attach( Actor actor )
    {
        assert (this.actor == null);
        this.actor = actor;
        this.init();
    }

    public Actor getActor()
    {
        return this.actor;
    }

    public void play( String soundName )
    {
        this.actor.play(soundName);
    }

    public void event( String poseName )
    {
        this.actor.event(poseName);
    }

    public void deathEvent( String poseName )
    {
        this.actor.deathEvent(poseName);
    }

    public void sleep( double seconds )
    {
        this.actor.sleep(seconds);
    }

    public void delay( double seconds )
    {
        this.actor.delay(seconds);
    }

    /**
     * You may override this method to do one-time initialisation. Use this instead of a
     * Constructor, because the behaviour will not be fully formed in the constructor - it won't be
     * attached to its Actor yet.
     * 
     * Consider using onActivated for game logic, and in particular, never use sleep or delay from
     * within init - weird things will happen!
     */
    public void init()
    {
    }

    public void onKill()
    {
        // do nothing
    }

    public void onActivate()
    {
        // do nothing
    }

    public void onDeactivate()
    {
        // do nothing
    }

    public abstract void tick();

}
