/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.collision;

import java.util.List;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Role;

/**
 * A CollisionStrategy holds defines the algorithm which determines if one actor is touching or overlapping another actor. "touching" is
 * defined as one non-transparent pixel of actor A is drawn on top of actor B. "overlapping" just means that the actor's bounding rectangles
 * overlap. This is significantly faster, but generally, not very useful.
 * 
 * To test whether one actor touches/overlaps is always done by Actor.overalpping(Actor) and Actor.touching(Actor). However, if we want to
 * know if a bullet has hit any aliens, then a simplistic solution would be to test every single alien. This is the
 * BruteForceCollisionStrategy. For m bullets and n aliens, the complexity is Order( n*m ), which gets very expensive when n and m are
 * large.
 * 
 * A faster solution can bring the order closer to O( n ), by only testing for aliens that are near to the bullet. We can keep track of
 * which actors are near other ones, by creating an imaginary grid, and keeping track of which actors occupy which squares in the grid.
 * Moving the bullets and aliens becomes slightly more expensive (by updating the square's occupants), but collision detection becomes
 * significantly faster. This is the SinglePointCollisionStrategy and MultipleSquareCollisionStrategy (and the grid is called a
 * Neighbourhood).
 */
public interface CollisionStrategy
{
    public void update();

    public void remove();

    public List<Role> collisions( Actor actor, String... includeTags );
    
    public List<Role> collisions( Actor actor, String[] includeTags, String[] excludeTags );

}
