/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

/**
 * The default strategy for Actor's overlapping and pixelOverlap methods. It is Order n squared, and therefore slow for large values of n.
 */

import java.util.HashSet;
import java.util.Set;

public class BruteForceCollisionStrategy implements CollisionStrategy
{
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

    public static final BruteForceCollisionStrategy singleton = new BruteForceCollisionStrategy();

    @Override
    public Set<Role> overlapping( Actor source, String[] includeTags, String[] excludeTags )
    {
        Set<Role> results = new HashSet<Role>();
        for (String tag : includeTags) {
            for (Role otherRole : AbstractRole.allByTag(tag)) {
                Actor other = otherRole.getActor();

                if ((other != source) && (!exclude(otherRole, excludeTags))) {
                    if (!results.contains(other)) {
                        if (source.overlapping(other)) {
                            results.add(otherRole);
                        }
                    }
                }
            }
        }
        return results;
    }

    @Override
    public Set<Role> pixelOverlap( Actor source, String[] includeTags, String[] excludeTags )
    {
        Set<Role> results = new HashSet<Role>();
        for (String tag : includeTags) {
            for (Role otherRole : AbstractRole.allByTag(tag)) {
                Actor other = otherRole.getActor();

                if ((other != source) && (!exclude(otherRole, excludeTags))) {
                    if (!results.contains(other)) {
                        if (source.pixelOverlap(other)) {
                            results.add(otherRole);
                        }
                    }
                }
            }
        }
        return results;
    }

}
