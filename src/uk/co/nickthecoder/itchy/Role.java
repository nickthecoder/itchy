/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.List;

import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.itchy.property.PropertySubject;
import uk.co.nickthecoder.itchy.script.ScriptedRole;

/**
 * Determines the how an Actor behaves, including how it moves and interacts with other Actors. Role is arguably the most important part of
 * Itchy, and most of your code will probably be a type of Role.
 * <p>
 * If you are writing your game in Javascript, then your Roles will be instances of {@link ScriptedRole}. If you are writing your game in
 * Java, then you might want to use {@link AbstractRole} as the base class for all of your Roles.
 * <p>
 * Most of the time an Actor will have one Role for its whole life, but sometimes it can be useful for an actor to change Roles. For
 * example, an Actor can have a "Doctor Jekyll" role, and when it drinks a potion, changes role to "Mr Hyde". Another example of switching
 * Roles is when Pacman eats a power pellet, the enemies change Role from chasing, to running away (blue ghosts), and when they are eaten
 * they change again (a pair of eyes) returning to their home, where they change back to their original role as chasers.
 */
public interface Role extends MessageListener, Cloneable, PropertySubject<Role>
{
    public Actor getActor();

    public boolean hasTag( String name );

    public void birth();

    public void die();

    public void attach( Actor actor );

    public void detatch();

    public void animateAndTick();

    public Role clone();

    @Override
    public void onMessage( String message );

    @Override
    public List<AbstractProperty<Role, ?>> getProperties();
}
