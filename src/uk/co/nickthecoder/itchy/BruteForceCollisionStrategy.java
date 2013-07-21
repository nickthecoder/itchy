/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

/**
 * The default strategy for Actor's overlapping and touching methods. It is Order n squared, and
 * therefore slow for large values of n.
 */

import java.util.HashSet;
import java.util.Set;

public class BruteForceCollisionStrategy implements CollisionStrategy
{

    /**
     * Should the actor be excluded from consideration based on the exclude tags?
     * @param actor The actor who's tags are to be tested
     * @param excludeTags The list of tags which will exclude the actor, or null to include all actors.
     * @return true if the actor is tagged with any one of the excludeTags. 
     */
    public static boolean exclude( Actor actor, String[] excludeTags )
    {
        if (excludeTags == null) {
            return false;
        }

        if (excludeTags != null) {
            for (String excludeTag : excludeTags) {
                if (actor.hasTag(excludeTag)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static final BruteForceCollisionStrategy singleton = new BruteForceCollisionStrategy();

    @Override
    public Set<Actor> overlapping( Actor source, String[] includeTags, String[] excludeTags )
    {
        Set<Actor> results = new HashSet<Actor>();
        for (String tag : includeTags) {
            for (Actor other : Actor.allByTag(tag)) {

                if ((other != source) && (!exclude(other, excludeTags))) {
                    if (!results.contains(other)) {
                        if (source.overlapping(other)) {
                            results.add(other);
                        }
                    }
                }
            }
        }
        return results;
    }

    @Override
    public Set<Actor> touching( Actor source, String[] includeTags, String[] excludeTags )
    {
        Set<Actor> results = new HashSet<Actor>();
        for (String tag : includeTags) {
            for (Actor other : Actor.allByTag(tag)) {

                if ((other != source) && (!exclude(other, excludeTags))) {
                    if (!results.contains(other)) {
                        if (source.touching(other)) {
                            results.add(other);
                        }
                    }
                }
            }
        }
        return results;
    }


}
