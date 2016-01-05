/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.List;
import java.util.Set;

import uk.co.nickthecoder.itchy.collision.BruteForceCollisionStrategy;
import uk.co.nickthecoder.itchy.collision.CollisionStrategy;
import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.itchy.property.PropertySubject;
import uk.co.nickthecoder.itchy.util.ClassName;

/**
 * Determines how an Actor behaves, including how it moves and interacts with
 * other Actors. Role is arguably the most important part of Itchy, and most of
 * your code will probably be a type of Role.
 * <p>
 * You probably want to use {@link AbstractRole} as the base class for all of
 * your Roles.
 * <p>
 * Most of the time an Actor will have one Role for its whole life, but
 * sometimes it can be useful for an actor to change Roles. For example, an
 * Actor can have a "Doctor Jekyll" role, and when it drinks a potion, changes
 * role to "Mr Hyde". Another example of switching Roles is when Pacman eats a
 * power pellet, the enemies change Role from predators, to prey (the blue
 * ghosts), and when they are eaten they change again (a ghostly pair of eyes
 * returning to their home).
 * <p>
 * Roles have tags, each tag is just a String, but used to group sets of actors
 * together. Tags are most often used when checking for collisions. For example,
 * a bullet may check if it is colliding with an Roles which have a "shootable"
 * tag.
 */
public interface Role extends MessageListener, Cloneable, PropertySubject<Role> {
	/**
	 * @return The Actor which this Role controls.
	 */
	public Actor getActor();

	/**
	 * Adds a tag to the Role. Tags behave as a Set, so adding the same tag
	 * twice has no effect, and if you add a tag twice, and then remove it once,
	 * then it will not have that tag.
	 * 
	 * @param tag
	 *            The string for this tag - normally a single word, such as
	 *            "shootable".
	 */
	public void addTag(String tag);

	public boolean hasTag(String name);

	public Set<String> getTags();

	/**
	 * Each role can optionally have an ID which is set in the scene designer.
	 * Game code can then search for a particular role using Game.findRoleById.
	 * 
	 * @return The id for this role, or null if it has no id.
	 */
	public String getId();

	public void setId(String id);

	/**
	 * The collision strategy is usually determined by
	 * {@link SceneDirector#getCollisionStrategy(Actor)}, when the Role is
	 * {@link #born}, but individual roles are free to ignore this, and choose
	 * their own collision strategy. The default strategy is to compare this
	 * role's actor to all other actors using a pixel based collision test (
	 * {@link BruteForceCollisionStrategy#pixelCollision}). Brute force is
	 * simple to use, but will be very inefficient if there are large numbers of
	 * actors to test against.
	 * 
	 * @return The collision strategy used by this Role. It may be shared by
	 *         many roles, or unique to one role.
	 * @see CollisionStrategy CollisionStrategy
	 */
	public CollisionStrategy getCollisionStrategy();

	/**
	 * Called when the actor has both a role, and is on a stage. The order may
	 * vary: when added to a stage, and then given a role, it is called from
	 * Actor.setRole, but when the role is set first, it is called when the
	 * actor is added to a stage.
	 * <p>
	 * Should only be called internally by Itchy.
	 */
	void born();

	/**
	 * Called while starting a new scene, after all of the actors have been
	 * created and added to the grid, and just before the sceneDirector's
	 * onActivate method is called.
	 * <p>
	 * Should only be called internally by Itchy.
	 */
	void sceneCreated();

	/**
	 * Called when the role's actor is killed ie from {@link Actor#kill()}.
	 * <p>
	 * Should only be called internally by Itchy.
	 */
	void killed();

	/**
	 * Called when this role is assigned to an actor {@link Actor#setRole(Role)}
	 * .
	 * <p>
	 * Should only be called internally by Itchy.
	 * 
	 * @param actor
	 */
	void attached(Actor actor);

	/**
	 * Called when a difference role is assigned to this Role's Actor. It is not
	 * called when the actor dies.
	 * <p>
	 * Should only be called internally by Itchy.
	 */
	void detatched();

	/**
	 * Called once per frame for all active actors' roles.
	 * <p>
	 * Should only be called internally by Itchy.
	 */
	void animateAndTick();

	/**
	 * Called when a message is sent to this Role. Messages are a simple way to
	 * communicate with a Role, but it is often better to create a specific
	 * method, rather than passing strings to onMessage.
	 */
	@Override
	public void onMessage(String message);

	/**
	 * Used internally name the class for this Role. For a regular Java object,
	 * it will be the object's class name as returned by
	 * obj.getClass().getName(), however, for scripted role's it will be a
	 * string which identifies the filename of the script. This will include the
	 * script's suffix. e.g. "Alien.py".
	 * 
	 * @return The ClassName for this Role.
	 */
	public ClassName getClassName();

	/**
	 * Returns the list of properties which this type of Role has. For pure Java
	 * Roles, the properties can be automatically created using the
	 * {@link Property} annotation. For scripted Roles, the list of properties
	 * has to be built manually.
	 * <p>
	 * In an ideal world, this would be a class method, but as Java doesn't have
	 * class methods, we are stuck with an instance method (static methods don't
	 * give enough flexibility for this problem). If you want to find the
	 * properties for a given Role sub-class, create an instance and then call
	 * getProperties.
	 */
	@Override
	public List<AbstractProperty<Role, ?>> getProperties();
	
	public CostumeProperties createCostumeProperties();

}
