/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.Set;


public class BruteForceActorCollisionStrategy extends ActorCollisionStrategy
{

    public BruteForceActorCollisionStrategy( Actor actor )
    {
        super(actor);
    }

    @Override
    public Set<Actor> overlapping( Actor actor, String... tags )
    {
        return BruteForceCollisionStrategy.singleton.overlapping(actor, tags);
    }

    @Override
    public Set<Actor> touching( Actor actor, String... tags )
    {
        return BruteForceCollisionStrategy.singleton.touching(actor, tags);
    }

}
