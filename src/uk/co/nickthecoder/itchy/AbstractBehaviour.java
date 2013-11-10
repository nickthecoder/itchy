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

import javax.script.ScriptException;

import uk.co.nickthecoder.itchy.animation.Animation;
import uk.co.nickthecoder.itchy.util.AbstractProperty;
import uk.co.nickthecoder.itchy.util.ClassName;
import uk.co.nickthecoder.itchy.util.Tag;
import uk.co.nickthecoder.itchy.util.TagMembership;

public abstract class AbstractBehaviour implements Behaviour
{
    private final static HashMap<Class<?>, List<AbstractProperty<Behaviour, ?>>> allProperties = new HashMap<Class<?>, List<AbstractProperty<Behaviour, ?>>>();

    public static Set<Behaviour> allByTag( String tag )
    {
        return Itchy.getGame().findBehaviourByTag(tag);
    }

    public static boolean isValidClassName( Resources resources, ClassName className )
    {
        if (resources.isValidScript(className)) {
            return true;
        }
        try {
            Class.forName(className.name).asSubclass(Behaviour.class);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static Behaviour createBehaviour( Resources resources, ClassName className )
        throws InstantiationException, IllegalAccessException, ScriptException,
        ClassNotFoundException
    {
        if (resources.isValidScript(className)) {
            return Resources.getScriptManager().createBehaviour(className);
        } else {
            Class<?> klass = Class.forName(className.name);
            return (Behaviour) klass.newInstance();
        }
    }

    private Actor actor;

    private final TagMembership<Behaviour> tagMembership;

    public AbstractBehaviour()
    {
        this.tagMembership = new TagMembership<Behaviour>(Itchy.getGame().behaviourTags, this);
    }

    @Override
    public boolean hasTag( String name )
    {
        return this.tagMembership.hasTag(name);
    }

    public void addTag( String tag )
    {
        this.tagMembership.add(tag);
    }

    public void removeTag( String tag )
    {
        this.tagMembership.remove(tag);
    }

    public void removeAllTags()
    {
        this.tagMembership.removeAll();
    }

    /**
     * Called when the behaviour is first attached to its actor. Override this method to perform one
     * time initialisation.
     */
    public void onBirth()
    {
        // Do nothing
    }

    public void onDeath()
    {
        // Do nothing
    }

    @Override
    public void birth()
    {
        onBirth();
    }

    @Override
    public void die()
    {
        onDeath();
        this.tagMembership.removeAll();
    }

    @Override
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

    @Override
    public void attach( Actor actor )
    {
        assert ((getActor() == null) || (getActor() == actor));
        this.actor = actor;

        Tag tags = this.getClass().getAnnotation(Tag.class);
        if (tags != null) {
            for (String name : tags.names()) {
                addTag(name);
            }
        }
        this.onAttach();
    }

    @Override
    public void detatch()
    {
        onDetach();

        Tag tags = this.getClass().getAnnotation(Tag.class);
        if (tags != null) {
            for (String name : tags.names()) {
                removeTag(name);
            }
        }
    }

    @Override
    public Actor getActor()
    {
        return this.actor;
    }

    public void play( String soundName )
    {
        getActor().play(soundName);
    }

    public void event( String poseName )
    {
        getActor().event(poseName);
    }

    public void endEvent( String poseName )
    {
        getActor().endEvent(poseName);
    }

    public void deathEvent( String poseName )
    {
        getActor().deathEvent(poseName);
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

    // TODO Remove?
    public void sendMessage( String message )
    {
        onMessage(message);
    }

    /**
     * Called by Actor.tick, once every frame, for every actor in the game. If the actor has an
     * animation, it plays the next frame. Then calls this.tick which is where the real work is one.
     * <p>
     * This method was created so that Pause could make actors animation stop, as well as the
     * behaviour's tick methods not firing. This is done by creating a PauseBehaviour, which does
     * nothing in tickHandler.
     */
    @Override
    public void animateAndTick()
    {
        Animation animation = getActor().getAnimation();
        if (animation != null) {

            animation.tick(getActor());
            if (animation.isFinished()) {
                getActor().setAnimation(null);
                if (getActor().isDying()) {
                    getActor().kill();
                    return;
                }
            }
        }
        if (!getActor().isDead()) {
            tick();
        }
    }

    public abstract void tick();

    @Override
    public Behaviour clone()
    {
        try {
            AbstractBehaviour result = (AbstractBehaviour) super.clone();
            result.actor = null;

            return result;

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

}
