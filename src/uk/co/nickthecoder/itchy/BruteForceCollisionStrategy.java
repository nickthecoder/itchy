/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

/**
 * The default strategy for Actor's overlapping and pixelOverlap methods. It is Order n squared, and
 * therefore slow for large values of n.
 */

import java.util.HashSet;
import java.util.Set;

public class BruteForceCollisionStrategy implements CollisionStrategy
{
    public void update()
    {
        // Do nothing
    }

    public void remove()
    {
        // Do nothing
    }
    
    /**
     * Should the behaviour be excluded from consideration based on the exclude tags?
     * @param behaviour The behaviour who's tags are to be tested
     * @param excludeTags The list of tags which will exclude the behaviour, or null to include all behaviours.
     * @return true if the behaviour is tagged with any one of the excludeTags. 
     */
    public static boolean exclude( Behaviour behaviour, String[] excludeTags )
    {
        if (excludeTags == null) {
            return false;
        }

        if (excludeTags != null) {
            for (String excludeTag : excludeTags) {
                if (behaviour.hasTag(excludeTag)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static final BruteForceCollisionStrategy singleton = new BruteForceCollisionStrategy();

    @Override
    public Set<Behaviour> overlapping( Actor source, String[] includeTags, String[] excludeTags )
    {
        Set<Behaviour> results = new HashSet<Behaviour>();
        for (String tag : includeTags) {
            for (Behaviour otherBehaviour : Behaviour.allByTag(tag)) {
                Actor other = otherBehaviour.getActor();
                
                if ((other != source) && (!exclude(otherBehaviour, excludeTags))) {
                    if (!results.contains(other)) {
                        if (source.overlapping(other)) {
                            results.add(otherBehaviour);
                        }
                    }
                }
            }
        }
        return results;
    }

    @Override
    public Set<Behaviour> pixelOverlap( Actor source, String[] includeTags, String[] excludeTags )
    {
        Set<Behaviour> results = new HashSet<Behaviour>();
        for (String tag : includeTags) {
            for (Behaviour otherBehaviour : Behaviour.allByTag(tag)) {
                Actor other = otherBehaviour.getActor();

                if ((other != source) && (!exclude(otherBehaviour, excludeTags))) {
                    if (!results.contains(other)) {
                        if (source.pixelOverlap(other)) {
                            results.add(otherBehaviour);
                        }
                    }
                }
            }
        }
        return results;
    }


}
