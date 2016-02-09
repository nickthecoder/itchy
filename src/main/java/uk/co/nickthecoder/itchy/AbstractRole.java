/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.script.ScriptException;

import uk.co.nickthecoder.itchy.Actor.AnimationEvent;
import uk.co.nickthecoder.itchy.animation.AbstractAnimation;
import uk.co.nickthecoder.itchy.animation.Animation;
import uk.co.nickthecoder.itchy.collision.BruteForceCollisionStrategy;
import uk.co.nickthecoder.itchy.collision.CollisionStrategy;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.util.ClassName;
import uk.co.nickthecoder.itchy.util.Filter;
import uk.co.nickthecoder.itchy.util.Tag;
import uk.co.nickthecoder.itchy.util.TagMembership;

public abstract class AbstractRole implements Role
{
    protected static final List<Property<Role, ?>> properties = new ArrayList<Property<Role, ?>>();

    private CollisionStrategy collisionStrategy = BruteForceCollisionStrategy.pixelCollision;

    private String id;

    public static Set<Role> allByTag(String tag)
    {
        return Itchy.getGame().findRoleByTag(tag);
    }

    public static boolean isValidClassName(Resources resources, ClassName className)
    {
        if (resources.isValidScript(className)) {
            return true;
        }
        try {
            Class.forName(className.name).asSubclass(Role.class);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static Role createRole(Resources resources, ClassName className) throws InstantiationException,
                    IllegalAccessException, ScriptException, ClassNotFoundException
    {
        if (resources.isValidScript(className)) {
            return resources.getGame().getScriptManager().createRole(className);
        } else {
            Class<?> klass = Class.forName(className.name);
            return (Role) klass.newInstance();
        }
    }

    private Actor actor;

    private final TagMembership<Role> tagMembership;

    public AbstractRole()
    {
        this.tagMembership = new TagMembership<Role>(Itchy.getGame().roleTags, this);
    }

    @Override
    public List<Property<Role, ?>> getProperties()
    {
        return properties;
    }

    @Override
    public String getId()
    {
        return this.id;
    }

    @Override
    public void setId(String id)
    {
        if (id != null) {
            id = id.trim();
            if ("".equals(id)) {
                id = null;
            }
        }
        this.id = id;
    }

    @Override
    public CollisionStrategy getCollisionStrategy()
    {
        return this.collisionStrategy;
    }

    public List<Role> collisions(String... tags)
    {
        return this.collisionStrategy.collisions(this.getActor(), tags);
    }

    public List<Role> collisions(int maxResults, String... tags)
    {
        return this.collisionStrategy.collisions(this.getActor(), tags, maxResults);
    }

    public List<Role> collisions(int maxResults, Filter<Actor> filter, String... tags)
    {
        return this.collisionStrategy.collisions(this.getActor(), tags, maxResults, filter);
    }

    public boolean collided(String... tags)
    {
        return !this.collisionStrategy.collisions(this.getActor(), tags, 1).isEmpty();
    }

    
    @Override
    public ClassName getClassName()
    {
        return new ClassName(Role.class, this.getClass().getName());
    }

    public CostumeFeatures getCostumeFeatures()
    {
        return getActor().getCostume().getCostumeFeatures();
    }

    @Override
    public boolean hasTag(String name)
    {
        return this.tagMembership.hasTag(name);
    }

    @Override
    public void addTag(String tag)
    {
        this.tagMembership.add(tag);
    }

    public void removeTag(String tag)
    {
        this.tagMembership.remove(tag);
    }

    public void tag(String name, boolean value)
    {
        if (value) {
            this.addTag(name);
        } else {
            this.removeTag(name);
        }
    }

    @Override
    public Set<String> getTags()
    {
        return this.tagMembership.getTags();
    }

    public void removeAllTags()
    {
        this.tagMembership.removeAll();
    }

    /**
     * Called when the role is first attached to its actor. Override this method to perform one time initialisation.
     */
    public void onBirth()
    {
        // Do nothing
    }

    public void onSceneCreated()
    {
        // Do nothing
    }

    public void onDeath()
    {
        // Do nothing
    }

    @Override
    public void born()
    {
        this.collisionStrategy = Itchy.getGame().getSceneDirector().getCollisionStrategy(this.getActor());
        onBirth();
    }

    public void sceneCreated()
    {
        this.onSceneCreated();
    }

    @Override
    public void killed()
    {
        onDeath();
        this.collisionStrategy.remove();
        this.tagMembership.removeAll();
    }

    @Override
    public CostumeFeatures createCostumeFeatures(Costume costume)
    {
        return new CostumeFeatures(costume);
    }

    @Override
    public void attached(Actor actor)
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
    public void detatched()
    {
        this.tagMembership.removeAll();
        onDetach();
    }

    @Override
    public Actor getActor()
    {
        return this.actor;
    }

    public void play(String soundName)
    {
        getActor().play(soundName);
    }

    public void event(String eventName, String message, AnimationEvent ae)
    {
        getActor().event(eventName, message, ae);
    }

    public void event(String eventName, String message)
    {
        getActor().event(eventName, message);
    }

    public void event(String poseName)
    {
        getActor().event(poseName);
    }

    public void endEvent(String poseName)
    {
        getActor().endEvent(poseName);
    }

    public void deathEvent(String poseName)
    {
        getActor().deathEvent(poseName);
    }

    /**
     * Called when the Role is first attached to its actor. For most roles, this will be when the actor is first
     * created. You may override this method to do one-time initialisation. Use this instead of a Constructor, because
     * the role will not be fully formed in the constructor - it won't be attached to its Actor yet.
     * 
     * Consider using onActivated for game logic, and in particular, never use sleep or delay from within onAttach -
     * weird things will happen!
     */
    public void onAttach()
    {
    }

    public void onDetach()
    {
    }

    @Override
    public void onMessage(String message)
    {
        // do nothing
    }

    /**
     * Called by Actor.tick, once every frame, for every actor in the game. If the actor has an animation, it plays the
     * next frame. Then calls this.tick which is where the real work is one.
     * <p>
     * This method was created so that Pause could make actors animation stop, as well as the role's tick methods not
     * firing. This is done by creating a PauseRole, which does nothing in tickHandler.
     */
    @Override
    public void animate()
    {
        Actor actor = getActor();

        Animation animation = actor.getAnimation();
        if (animation != null) {
            AbstractAnimation.tick(animation,getActor());
            if (animation.isFinished()) {
                // The animation could have been changed by the animation finish event. In which case leave the new one.
                if ( actor.getAnimation() == animation ) {
                    actor.setAnimation(null);
                }
                if (actor.isDying()) {
                    actor.kill();
                    return;
                }
            }
        }
    }

    @Override
    public void tick()
    {
    }

}
