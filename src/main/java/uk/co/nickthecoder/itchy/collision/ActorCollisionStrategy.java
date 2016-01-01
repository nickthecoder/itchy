/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.collision;

import java.util.Set;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Role;

/**
 * A collision strategy which is owned by a single Actor. i.e. the is a 1:1 relationship. Use as a base class for any strategy which need to
 * hold state information about each Actor. This was initially create because strategies were needed, where each actor was placed in one of
 * more squares, and updated when the actor moved.
 */
public abstract class ActorCollisionStrategy implements CollisionStrategy
{
    protected Actor actor;

    public ActorCollisionStrategy( Actor actor )
    {
        this.actor = actor;
    }

    public Actor getActor()
    {
        return this.actor;
    }

    public Set<Role> collisions( String... tags )
    {
        return this.collisions(this.actor, tags, null);
    }

    public Set<Role> collisions( String[] includeTags, String[] excludeTags )
    {
        return this.collisions(this.actor, includeTags, excludeTags);
    }

    public void update()
    {
    }

    public void remove()
    {
    }

}