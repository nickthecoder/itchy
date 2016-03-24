/*******************************************************************************
 * Copyright (c) 2014 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.collision;

import uk.co.nickthecoder.itchy.Actor;

/**
 * Tests if two Actors have collided.
 * <p>
 * We could use a very simple test based on the Actor's bounding boxes. This is quick, but usually not very good. See
 * {@link BoundingBoxCollisionTest}.
 * <p>
 * We could use a pixel based collision test. This is slower, but just as easy to use, because all the information is in
 * the artwork. See {@link PixelCollisionTest}.
 * <p>
 * There are other possibilities, such as drawing squares and circles over each Pose, and checking for overlaps using
 * mathematics. This would arguably give the best results for some games, but is harder to setup than pixel based
 * collision detection. Itchy's aim is to make writing games easy, so pixel collision detection is preferred.
 */
public interface CollisionTest
{
    /**
     * Tests if the two Actors have collided.
     * 
     * @param a
     * @param b
     * @return true iff the two actors have collided
     */
    public boolean collided(Actor a, Actor b);
}
