/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.collision;

import uk.co.nickthecoder.itchy.Actor;

/**
 * A collision strategy which is owned by a single Actor. i.e. there is a 1:1 relationship. Use as a base class for any
 * strategy which needs to hold state information about each Actor.
 * 
 * @priority 3
 */
public abstract class ActorCollisionStrategy extends AbstractCollisionStrategy
{
    protected Actor actor;

    public ActorCollisionStrategy(CollisionTest collisionTest, Actor actor)
    {
        super(collisionTest);
        this.actor = actor;
    }

    public Actor getActor()
    {
        return this.actor;
    }

}
