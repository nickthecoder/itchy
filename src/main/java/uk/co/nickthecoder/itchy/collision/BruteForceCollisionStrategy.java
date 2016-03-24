/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.collision;

/**
 * The default collision strategy. Simple, but slow for large numbers of actors. 
 */

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.AbstractRole;
import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.util.Filter;

public class BruteForceCollisionStrategy extends AbstractCollisionStrategy
{
    /**
     * One instance that can be shared. Uses a {@link PixelCollisionTest}.
     */
    public static final BruteForceCollisionStrategy instance = new BruteForceCollisionStrategy();

    /**
     * Create a new instance with a {@link PixelCollisionTest}.
     */
    public BruteForceCollisionStrategy()
    {
        this(PixelCollisionTest.instance);
    }

    /**
     * Create a new instance with a CollisionTest of your own choosing.
     * 
     * @param collisionTest
     */
    public BruteForceCollisionStrategy(CollisionTest collisionTest)
    {
        super(collisionTest);
    }

    /**
     * Does nothing.
     * 
     * @priority 3
     */
    @Override
    public void update()
    {
        // Do nothing
    }

    /**
     * Does nothing.
     * 
     * @priority 3
     */
    @Override
    public void remove()
    {
        // Do nothing
    }

    @Override
    public List<Role> collisions(Actor source, String[] includeTags, int maxResults, Filter<Role> filter)
    {
        List<Role> results = new ArrayList<Role>();
        for (String tag : includeTags) {
            for (Role otherRole : AbstractRole.findRolesByTag(tag)) {
                Actor other = otherRole.getActor();
                if (other == null) {
                    continue;
                }

                if ((other != source) && (filter.accept(otherRole))) {
                    if (!results.contains(other)) {
                        if (this.collisionTest.collided(source, other)) {
                            results.add(otherRole);
                            if (results.size() >= maxResults) {
                                return results;
                            }
                        }
                    }
                }
            }
        }
        return results;
    }

}
