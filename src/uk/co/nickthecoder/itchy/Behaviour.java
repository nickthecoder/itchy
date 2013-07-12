package uk.co.nickthecoder.itchy;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import uk.co.nickthecoder.itchy.util.DoubleProperty;
import uk.co.nickthecoder.itchy.util.FontProperty;
import uk.co.nickthecoder.itchy.util.IntegerProperty;
import uk.co.nickthecoder.itchy.util.AbstractProperty;
import uk.co.nickthecoder.itchy.util.RGBAProperty;
import uk.co.nickthecoder.itchy.util.StringPropert;
import uk.co.nickthecoder.itchy.util.Property;
import uk.co.nickthecoder.jame.RGBA;

public abstract class Behaviour
{
    public ActorCollisionStrategy collisionStrategy;

    private final static HashMap<Class<?>, List<AbstractProperty<Behaviour, ?>>> allProperties = new HashMap<Class<?>, List<AbstractProperty<Behaviour, ?>>>();

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

    public Actor createActor( ActorsLayer layer, String costumeName )
    {
        Costume costume = Itchy.singleton.getResources().getCostume(costumeName);
        Actor actor = new Actor(costume);
        layer.add(actor);
        actor.setBehaviour(this);

        return actor;
    }

    public List<AbstractProperty<Behaviour, ?>> getProperties()
    {
        List<AbstractProperty<Behaviour, ?>> result = allProperties.get(this.getClass());
        if (result == null) {
            result = new ArrayList<AbstractProperty<Behaviour, ?>>();
            allProperties.put(this.getClass(), result);
            this.addProperties();
        }
        return result;
    }

    /**
     * For Itchy Gurus Only.
     * 
     * Allows a behaviour to manually add a property, which will appear in the GUI scene editor.
     * Most behaviour's won't need this, instead they will use a '@Property(label="Whatever")'
     * annotation above the field.
     *
     * The only good reason to use addProperty, is if you want to add a property to a Behaviour,
     * which cannot be implemented as a simple field.
     * 
     * Must only be called from within addProperties to ensure that the property won't be added twice.
     */
    protected void addProperty( AbstractProperty<Behaviour, ?> property )
    {
        allProperties.get(this.getClass()).add(property);
    }
    
    /**
     * For Itchy Gurus Only.
     * 
     * Override this method, and then call addProperty for each property you wish to add.
     */
    protected void addProperties()
    {
        Class<? extends Behaviour> klass = this.getClass();

        for (Field field : klass.getFields()) {
            Property property = field.getAnnotation(Property.class);
            if (property != null) {
                AbstractProperty<Behaviour, ?> gProperty = createProperty(field, property);
                if (gProperty != null) {
                    addProperty(gProperty);
                }
            }
        }
    }

    private AbstractProperty<Behaviour, ?> createProperty( Field field, Property property )
    {
        String name = field.getName();
        String label = property.label();

        Class<?> klass = field.getType();

        if (klass == int.class) {
            return new IntegerProperty<Behaviour>(label, name);
        }
        if (klass == double.class) {
            return new DoubleProperty<Behaviour>(label, name);
        }
        if (klass == String.class) {
            return new StringPropert<Behaviour>(label, name);
        }
        if (klass == RGBA.class) {
            return new RGBAProperty<Behaviour>(label, name, false, true);
        }
        if (klass == Font.class) {
            return new FontProperty<Behaviour>(label, name);
        }
        
        System.err.println("Unexpected property : " +
            field.getDeclaringClass() + "." +
            field.getName());

        return null;
    }

    public void attach( Actor actor )
    {
        assert (this.actor == null);
        this.actor = actor;
        if (this.collisionStrategy == null) {
            this.collisionStrategy = new BruteForceActorCollisionStrategy(this.actor);
        }
        this.init();
    }

    public Actor getActor()
    {
        return this.actor;
    }

    public Set<Actor> overlapping( String... tags )
    {
        return this.collisionStrategy.overlapping(tags);
    }

    public Set<Actor> touching( String... tags )
    {
        return this.collisionStrategy.touching(tags);
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

    public void onMessage( String message )
    {
        // do nothing
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
