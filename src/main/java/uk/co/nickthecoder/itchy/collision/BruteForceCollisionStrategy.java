/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.collision;

/**
 * The default strategy for Actor's overlapping and pixelOverlap methods. It is Order n squared, and therefore slow for large values of n.
 */

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.AbstractRole;
import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.util.Filter;

public class BruteForceCollisionStrategy implements CollisionStrategy
{
    public static final BruteForceCollisionStrategy pixelCollision = new BruteForceCollisionStrategy();
    
    private CollisionTest collisionTest;
    
    public BruteForceCollisionStrategy()
    {
        this( PixelCollisionTest.instance );
    }
    
    public BruteForceCollisionStrategy( CollisionTest ct )
    {
        this.collisionTest = ct;
    }
    
    @Override
    public void update()
    {
        // Do nothing
    }

    @Override
    public void remove()
    {
        // Do nothing
    }

    /**
     * Should the role be excluded from consideration based on the exclude tags?
     * 
     * @param role
     *        The role who's tags are to be tested
     * @param excludeTags
     *        The list of tags which will exclude the role, or null to include all roles.
     * @return true if the role is tagged with any one of the excludeTags.
     */
    public static boolean exclude( Role role, String[] excludeTags )
    {
        if (excludeTags == null) {
            return false;
        }

        if (excludeTags != null) {
            for (String excludeTag : excludeTags) {
                if (role.hasTag(excludeTag)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public List<Role> collisions( Actor actor, String[] tags )
    {
        return collisions(actor, tags, MAX_RESULTS, acceptFilter );
    }
    
    @Override
    public List<Role> collisions( Actor actor, String[] tags, int maxResults )
    {
        return collisions(actor, tags, maxResults, acceptFilter );
    }
    
    @Override
    public List<Role> collisions( Actor source, String[] includeTags, int maxResults, Filter<Role> filter )
    {
        List<Role> results = new ArrayList<Role>();
        for (String tag : includeTags) {
            for (Role otherRole : AbstractRole.allByTag(tag)) {
                Actor other = otherRole.getActor();
                if (other == null) {
                    continue;
                }

                if ((other != source) && (filter.accept( otherRole ))) {
                    if (!results.contains(other)) {
                        if (this.collisionTest.collided(source,other)) {
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
