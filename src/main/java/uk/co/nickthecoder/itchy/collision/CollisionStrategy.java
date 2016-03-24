/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.collision;

import java.util.List;

import uk.co.nickthecoder.itchy.AbstractRole;
import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.util.Filter;

/**
 * Controls which Actors to consider when performing collision detection.
 * <a style="align:right;" href="{@docRoot} /collisionStrategy.html" alt="class diagram"><img src="{@docRoot}
 * /collisionStrategyThumb.png"/></a>
 * The simplest way of testing for collisions is to use {@link AbstractRole#collided(String...)} and
 * {@link AbstractRole#collisions(String...)}. However, if you have hundreds of potential
 * collisions, then the default {@link BruteForceCollisionStrategy} may be too slow, and you will need to switch to an
 * optimised CollisionStrategy, such as {@link uk.co.nickthecoder.itchy.collision.NeighbourhoodCollisionStrategy}.
 * <p>
 * To do this, your {@link SceneDirector} must choose a collision strategy for each actor like this :
 * 
 * <pre>
 * <code>
 * private Neighbourhood neighbourhood;
 * 
 * public void loading( Scene scene ) {
 *     this.neighbourhood = new StandardNeighbourhood( 50 );
 * }
 * 
 * public CollisionStrategy chooseCollisionStrategy(Actor actor) {
 *     return new NeighbourhoodCollisionStrategy( this.neighbourhood );
 * }
 * </code>
 * </pre>
 * 
 * Then in each of your Role's tick method something like this :
 * 
 * <pre>
 * <code>
 * public void tick() {
 *     // Perform all the movements
 *     self.getActor().moveBy( 2, 0 );
 *     
 *     this.getCollisionStrategy()}.update();
 *     
 *     // Now we can check for collisions...
 *     if ( this.collided("alien") ) {
 *         self.deathEvent("eaten");
 *     }
 *     
 *     // If we moved the actor again based on the collisions, then uncomment this :
 *     // this.getCollisionStrategy()}.update();
 * }
 * </code>
 * </pre>
 * 
 * <p>
 * Speed is not the only consideration when choosing a CollisionStrategy. For example, the demo game Destroy-Debris has
 * a world where the left wraps around to the right, and the top wraps around to the bottom. Therefore it needs a
 * special CollisionStrategy to handle the cases where an Actor is overlapping an edge. (See
 * {@link uk.co.nickthecoder.itchy.collision.WrappedCollisionStrategy}).
 * 
 * A CollisionStrategy doesn't actually test for Collisions, that is done by {@link CollisionTest}.
 */
public interface CollisionStrategy
{

    /**
     * Some CollisionStrategies are stateful, and need updating then the Actor is update. For example,
     * {@link NeighbourhoodCollisionStrategy} needs to be updated when the Actor moves, and when it is scaled (or even
     * just rotated).
     * <p>
     * Simple CollisionStrategies, such as {@link BruteForceCollisionStrategy} do not need to be updated.
     * 
     * @priority 2
     */
    public void update();

    /**
     * For CollisionStrategies, such as {@link NeighbourhoodCollisionStrategy}, removes the instance so that it no
     * longer takes part in collisions. Usually called when an Actor is killed.
     * <p>
     * Simple CollisionStrategies, such as {@link BruteForceCollisionStrategy} do not need to be removed.
     * 
     * @priority 2
     */
    public void remove();

    public List<Role> collisions(Actor actor, String[] tags, int maxResults, Filter<Role> filter);

}
