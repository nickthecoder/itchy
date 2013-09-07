/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import uk.co.nickthecoder.itchy.util.AbstractProperty;

public abstract class Behaviour implements MessageListener
{
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

    public CollisionStrategy collisionStrategy = BruteForceCollisionStrategy.singleton;

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
     * Must only be called from within addProperties to ensure that the property won't be added
     * twice.
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
        AbstractProperty.addProperties(this.getClass(), allProperties.get(this.getClass()));
    }

    public void attach( Actor actor )
    {
        assert (this.actor == null);
        this.actor = actor;
        this.actor.addTag(this.getClass().getName());
        this.onAttach();
    }

    public void detatch()
    {
        this.actor.removeTag(this.getClass().getName());
        Itchy.singleton.gameLoopJob.add(new Task() {
            @Override
            public void run()
            {
                Behaviour.this.onDetach();
            }
        });
    }

    public Actor getActor()
    {
        return this.actor;
    }

    public Set<Actor> overlapping( String... tags )
    {
        return this.collisionStrategy.overlapping(this.actor,tags,null);
    }

    /**
     * Returns all Actors with a given type of Behaviour which are touching this Behaviour's Actor.
     * 
     * @param klass
     *        The type of Behaviour to match. If you want to test for base classes or interfaces,
     *        then you must manually add appropriate tags in the Behaviour's onAttach method. e.g. :
     * 
     *        <pre>
     * public void onAttach()
     * {
     *     this.actor.addTag(MyBaseClass.class.getName());
     * }
     * 
     * public void onDetatch()
     * {
     *     this.actor.removeTag(MyBaseClass.class.getName());
     * }
     * </pre>
     * @return The set of all touching Actors with matching behaviours.
     */
    public Set<Actor> touching( Class<Behaviour> klass )
    {
        return touching(klass.getName());
    }

    public Set<Actor> touching( String... tags )
    {
        return this.collisionStrategy.touching(this.actor,tags,null);
    }

    public Set<Actor> overlapping( String[] including, String[] excluding )
    {
        return this.collisionStrategy.overlapping(this.actor,including, excluding);
    }

    public Set<Actor> touching( String[] including, String[] excluding )
    {
        return this.collisionStrategy.touching(this.actor,including, excluding);
    }

    public void play( String soundName )
    {
        this.actor.play(soundName);
    }

    public void event( String poseName )
    {
        this.actor.event(poseName);
    }

    public void endEvent( String poseName )
    {
        this.actor.endEvent(poseName);
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
     * Called when the Behaviour is first attached to its actor. For most behaviours, this will be
     * when the actor is first created. You may override this method to do one-time initialisation.
     * Use this instead of a Constructor, because the behaviour will not be fully formed in the
     * constructor - it won't be attached to its Actor yet.
     * 
     * Consider using onActivated for game logic, and in particular, never use sleep or delay from
     * within onAttach - weird things will happen!
     */
    public void onAttach()
    {
    }

    public void onDetach()
    {
    }

    @Override
    public void onMessage( String message )
    {
        // do nothing
    }

    public void sendMessage( final String message )
    {
        Itchy.singleton.addTask(new Task() {
            @Override
            public void run()
            {
                Behaviour.this.onMessage(message);
            }

        });
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
