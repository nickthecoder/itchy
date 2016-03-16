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
import uk.co.nickthecoder.itchy.collision.NeighbourhoodCollisionStrategy;
import uk.co.nickthecoder.itchy.collision.SinglePointCollisionStrategy;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.util.ClassName;
import uk.co.nickthecoder.itchy.util.Filter;
import uk.co.nickthecoder.itchy.util.Tag;
import uk.co.nickthecoder.itchy.util.TagMembership;

public abstract class AbstractRole implements Role
{
    protected static final List<Property<Role, ?>> properties = new ArrayList<Property<Role, ?>>();

    private CollisionStrategy collisionStrategy = BruteForceCollisionStrategy.pixelCollision;

    /**
     * Finds all Roles with the given tag.
     * 
     * @param tag
     * @return
     */
    public static Set<Role> allByTag(String tag)
    {
        return Itchy.getGame().findRoleByTag(tag);
    }

    /**
     * REMOVE?
     * 
     * @param resources
     * @param className
     * @return
     * @priority 5
     */
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

    /**
     * 
     * @param resources
     * @param className
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ScriptException
     * @throws ClassNotFoundException
     * @priority 5
     */
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

    /**
     * Create the Role, at this point is is not attached to an Actor, so can do very little.
     */
    public AbstractRole()
    {
        this.tagMembership = new TagMembership<Role>(Itchy.getGame().roleTags, this);
    }

    /**
     * Used internally by Itchy.
     * 
     * @priority 5
     */
    @Override
    public List<Property<Role, ?>> getProperties()
    {
        return properties;
    }

    /**
     * The are different ways to test if two object are touching, the simplest is {@link BruteForceCollisionStrategy},
     * but is very slow if there are many objects. So for speed use {@link NeighbourhoodCollisionStrategy} or
     * {@link SinglePointCollisionStrategy}. The choice of collision strategy is made by the
     * {@link SceneDirector#getCollisionStrategy(Actor)}.
     * <p>
     * Most of the time you don't need to access the CollisionStrategy directly, instead use
     * {@link #collided(String...)} and {@link #collisions(String...)}.
     * 
     * @priority 3
     */
    @Override
    public CollisionStrategy getCollisionStrategy()
    {
        return this.collisionStrategy;
    }

    /**
     * Returns a list of Roles that have any of the tags speicified whose Actor is touching the Role's Actor.
     * <p>
     * <p>
     * If you only need to know if the Role is or isn't touching, then use {@link #collided(String...)} instead.
     * 
     * @param tags
     *            Ignores Role's that do not have any of these tags.
     * @return The list of touching Roles, or an empty list if none are touching.
     */
    public List<Role> collisions(String... tags)
    {
        return this.collisionStrategy.collisions(this.getActor(), tags);
    }

    /**
     * Does the same as {@link #collided(String...)}, but stops checking when maxResults are found.
     * 
     * @param maxResults
     *            The maximum number of Roles to return
     * @param tags
     *            Ignores Role's that do not have any of these tags.
     * @return
     * @priority 2
     */
    public List<Role> collisions(int maxResults, String... tags)
    {
        return this.collisionStrategy.collisions(this.getActor(), tags, maxResults);
    }

    /**
     * Does the same as {@link #collisions(String...)}, but gives more flexibility by allowing the results to be
     * filtered.
     * The filter
     * 
     * @param maxResults
     *            The maximum number of Roles to return
     * 
     * @param filter
     *            decides if the Role should be included in the results.
     * @param tags
     *            Ignores Role's that do not have any of these tags.
     * @return
     * @priority 2
     */
    public List<Role> collisions(int maxResults, Filter<Role> filter, String... tags)
    {
        return this.collisionStrategy.collisions(this.getActor(), tags, maxResults, filter);
    }

    /**
     * Tests if there is one of more Roles having the specified tags is touching this Role's Actor.
     * 
     * @param tags
     *            Ignores Role's that do not have any of these tags.
     * @return true if at least one matching Role's Actor is touching this Role's Actor
     */
    public boolean collided(String... tags)
    {
        return !this.collisionStrategy.collisions(this.getActor(), tags, 1).isEmpty();
    }

    /**
     * Tests if there is one of more Roles having the specified tags is touching this Role's Actor.
     * 
     * @param filter
     *            Ignores any Roles that are rejected by the Filter.
     * 
     * @param tags
     *            Ignores Role's that do not have any of these tags.
     * @return true if at least one matching Role's Actor is touching this Role's Actor
     * @priority 2
     */
    public boolean collided(Filter<Role> filter, String... tags)
    {
        return !this.collisionStrategy.collisions(this.getActor(), tags, 1, filter).isEmpty();
    }

    /**
     * Returns a Filter, which can be used in {@link #collided(Filter, String...)} and
     * {@link #collisions(int, Filter, String...)}. Only accepts those Roles that do NOT have the specified tag.
     * 
     * @param tag
     *            Roles with this tag are rejected
     * @return
     * @priority 2
     */
    public Filter<Actor> withoutTag(String tag)
    {
        return new WithoutTagFilter(tag);
    }

    /**
     * Used internally by Itchy.
     * 
     * @priority 5
     */
    @Override
    public ClassName getClassName()
    {
        return new ClassName(Role.class, this.getClass().getName());
    }

    /**
     * A shortcut way to get the Actor's Costume's CostumeFeatures.
     * <p>
     * See {@link Costume#getCostumeFeatures()}.
     * 
     * @return
     */
    public CostumeFeatures getCostumeFeatures()
    {
        return getActor().getCostume().getCostumeFeatures();
    }

    /**
     * @return true if this Role has the specified tag.
     */
    @Override
    public boolean hasTag(String tag)
    {
        return this.tagMembership.hasTag(tag);
    }

    /**
     * Adds a tag. Useful in conjection with {@link #collided(String...)} and {@link #collisions(String...)}.
     * 
     * @param tag
     *            Any string is allowed, but it is normal to use a single word, such as "deadly", "solid" etc.
     */
    @Override
    public void addTag(String tag)
    {
        this.tagMembership.add(tag);
    }

    /**
     * Removes a tag.
     * 
     * @param tag
     */
    public void removeTag(String tag)
    {
        this.tagMembership.remove(tag);
    }

    /**
     * Adds or removes a tag based on the boolean value.
     * 
     * @param tag
     *            The tag to add or remove
     * @param value
     *            true to add the tag, false to remove it.
     */
    public void tag(String tag, boolean value)
    {
        if (value) {
            this.addTag(tag);
        } else {
            this.removeTag(tag);
        }
    }

    /**
     * Returns all tags
     * 
     * @priority 3
     */
    @Override
    public Set<String> getTags()
    {
        return this.tagMembership.getTags();
    }

    /**
     * Removes all tags. Automatically called when the Actor is killed.
     */
    public void removeAllTags()
    {
        this.tagMembership.removeAll();
    }

    /**
     * Called when the role is first attached to its actor.
     * Override this method to perform one time initialisation.
     * <p>
     * Note that the {@link SceneDirector} may not be fully initialised when onBirth is called, so you may need to use
     * {@link #onSceneCreated()}, which is called later, when the SceneDirector has been initialised.
     */
    public void onBirth()
    {
        // Do nothing
    }

    /**
     * Called after the {@link Scene} has been loaded (later than {@link #onBirth()}).
     */
    public void onSceneCreated()
    {
        // Do nothing
    }

    /**
     * Called when the Role is attached to its actor.
     * Will be called shortly after the Role is created, but can be multiple times in the life of an Actor.
     * For example, using {@link SimplePause} will detach the
     * existing Roles, and replace them with a special "PauseRole"s. When the game is un-paused, the regular Roles are
     * re-attached, so onAttach() is called once more.
     * <p>
     * For one-time initialisation use {@link #onBirth()} and/or {@link #onSceneCreated()}.
     */
    public void onAttach()
    {
    }

    /**
     * The opposite of {@link #onAttach()}, called when {@link Actor#setRole(Role)} is called with a replacement Role.
     */
    public void onDetach()
    {
    }

    /**
     * Used by {@link Animation#setFinishedMessage(String)} and {@link Animation#setStartMessage(String)}, but can also
     * be used by your own game code to send arbitrary messages to your Roles. However, it is often better to create a
     * method rather than sending messages.
     */
    @Override
    public void onMessage(String message)
    {
        // do nothing
    }

    /**
     * Called when the Actor is killed using {@link Actor#kill()}, or once the animation has finished after
     * {@link Actor#deathEvent(String)}.
     */
    public void onDeath()
    {
        // Do nothing
    }

    /**
     * Fires onBirth
     * 
     * @priority 5
     */
    @Override
    public void born()
    {
        this.collisionStrategy = Itchy.getGame().getSceneDirector().getCollisionStrategy(this.getActor());
        onBirth();
    }

    /**
     * Fires onSceneCreated
     * 
     * @priority 5
     */
    public void sceneCreated()
    {
        this.onSceneCreated();
    }

    /**
     * Calls {@link #onDeath()} and then tidies up collisionStrategy, and clears all tags.
     * 
     * @priority 5
     */
    @Override
    public void killed()
    {
        onDeath();
        this.collisionStrategy.remove();
        this.tagMembership.removeAll();
    }

    /**
     * Creates a {@link CostumeFeatures} instance for the given Costume.
     * 
     * @param costume
     */
    @Override
    public CostumeFeatures createCostumeFeatures(Costume costume)
    {
        return new CostumeFeatures(costume);
    }

    /**
     * Used internally by Itchy when during {@link Actor#setRole(Role)}.
     * <p>
     * Calls {@link #onAttach()}
     * 
     * @priority 5
     */
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

    /**
     * Used internally by Itchy when a different Role is sent to {@link Actor#setRole(Role)}.
     * <p>
     * Removes all tags, and then calls {@link #onDetach()}.
     * 
     * @priority 5
     */
    @Override
    public void detatched()
    {
        this.tagMembership.removeAll();
        onDetach();
    }

    /**
     * A simple getter.
     */
    @Override
    public Actor getActor()
    {
        return this.actor;
    }

    /**
     * A convenience method for {@link Actor#event(String)}.
     */
    public void event(String eventName)
    {
        getActor().event(eventName);
    }

    /**
     * A convenience method for {@link Actor#event(String, String, AnimationEvent)}.
     * 
     * @priority 3
     */
    public void event(String eventName, String message, AnimationEvent ae)
    {
        getActor().event(eventName, message, ae);
    }

    /**
     * A convenience method for {@link Actor#endEvent(String)}.
     * 
     * @priority 3
     */
    public void endEvent(String eventName)
    {
        getActor().endEvent(eventName);
    }

    /**
     * A convenience method for {@link Actor#deathEvent(String)}.
     * 
     */
    public void deathEvent(String eventName)
    {
        getActor().deathEvent(eventName);
    }

    /**
     * Called by {@link Actor#tick()}, once every frame, for every actor in the game. If the actor has an animation, it
     * plays the next frame. Then calls {@link #tick()} which is where the real work is one.
     * 
     * @priority 5
     */
    @Override
    public void animate()
    {
        Actor actor = getActor();

        Animation animation = actor.getAnimation();
        if (animation != null) {
            AbstractAnimation.tick(animation, getActor());
            if (animation.isFinished()) {
                // The animation could have been changed by the animation finish event. In which case leave the new one.
                if (actor.getAnimation() == animation) {
                    actor.setAnimation(null);
                }
                if (actor.isDying()) {
                    actor.kill();
                    return;
                }
            }
        }
    }

    /**
     * Called once per frame (60 times per second). This is the most important method in the whole of Itchy,
     * because this will contain most of your game's logic.
     */
    @Override
    public void tick()
    {
    }

    /**
     * @priority 5
     */
    private class WithoutTagFilter implements Filter<Actor>
    {
        private String tag;

        public WithoutTagFilter(String tag)
        {
            this.tag = tag;
        }

        @Override
        public boolean accept(Actor subject)
        {
            return !subject.getRole().hasTag(tag);
        }

    }

}
