/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.collision;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.WorldRectangle;
import uk.co.nickthecoder.itchy.util.Filter;

/**
 * Uses a {@link Neighbourhood}, which is grid of {@link Block}s, to optimise collision detection.
 * Create a NeighbourhoodCollisionStrategy for each Actor, using a shared Neighbourhood.
 * <p>
 * Unlike {@link SinglePointCollisionStrategy}, this can be used when Actors are larger than a single Block.
 * <p>
 * 
 * <p>
 * During simple tests, this performed equally to SinglePointCollisionStrategy> Because this doesn't have the
 * restrictions that SinglePointCollisionStrategy has, there is little point using SinglePointCollisionStrategy.
 * <p>
 * <h2>How it works</h2>
 * The Actor is added to all of the Blocks that overlap the Actor's bounding rectangle. To test for collisions, iterate
 * through all of these Blocks, and then perform collision detection with other Actors within these Blocks.
 * <p>
 * If your collisions sometimes fail, check that you are calling {@link #update()} whenever the Actors' bounding
 * rectangles may have changed. i.e. when the Actor moves, is scaled, is rotated, and when its Makeup is changed.
 */
public class NeighbourhoodCollisionStrategy extends ActorCollisionStrategy
{
    private Neighbourhood neighbourhood;

    /**
     * The Block at the top left corner of the Actor's bounding rectangle.
     * Together with {@link #bottomRight}, this defines the set of blocks needed to be considered when checking
     * collisions.
     */
    private Block topLeft;

    /**
     * The Block at the bottom right corner of the Actor's bounding rectangle.
     */
    private Block bottomRight;

    /**
     * 
     * @param actor
     * @param neighbourhood
     *            A neighbourhood shared by all NeighbourhoodCollisionStrategies.
     */
    public NeighbourhoodCollisionStrategy(Actor actor, Neighbourhood neighbourhood)
    {
        this(PixelCollisionTest.instance, actor, neighbourhood);
    }

    /**
     * 
     * @param collisionTest
     * @param actor
     * @param neighbourhood
     *            A neighbourhood shared by all NeighbourhoodCollisionStrategies.
     */
    public NeighbourhoodCollisionStrategy(CollisionTest collisionTest, Actor actor, Neighbourhood neighbourhood)
    {
        super(collisionTest, actor);
        this.neighbourhood = neighbourhood;
        this.update();
    }

    /**
     * Find the Blocks at the top left and bottom right of the Actor's bounding rectangle.
     * If either are different from the previous time update was called, then remove the Actor from the old Blocks that
     * it occupied, and add the Actor as an occupant of all of the Blocks within that area.
     */
    @Override
    public void update()
    {
        WorldRectangle rect = this.actor.getAppearance().getWorldRectangle();

        Block newTopLeft = this.neighbourhood.getBlock(rect.x, rect.y);
        Block newBottomRight = this.neighbourhood.getBlock(rect.x + rect.width, rect.y +
            rect.height);

        if ((this.topLeft == null) || (this.topLeft != newTopLeft) ||
            (this.bottomRight != newBottomRight)) {

            if (this.topLeft != null) {
                for (Iterator<Block> i = this.neighbourhood.blockIterator(this.topLeft,
                    this.bottomRight); i.hasNext();) {
                    Block square = i.next();
                    square.remove(this.actor);
                }
            }

            this.topLeft = newTopLeft;
            this.bottomRight = newBottomRight;

            for (Iterator<Block> i = this.neighbourhood.blockIterator(this.topLeft,
                this.bottomRight); i.hasNext();) {
                Block square = i.next();
                square.add(this.actor);
            }

        }
    }

    /**
     * Removes the Actor from all of the Block it occupied.
     */
    @Override
    public void remove()
    {
        if (this.topLeft != null) {

            for (Iterator<Block> i = this.neighbourhood.blockIterator(this.topLeft,
                this.bottomRight); i.hasNext();) {
                Block square = i.next();
                square.remove(this.actor);
            }

            this.topLeft = null;
            this.bottomRight = null;
        }
    }

    @Override
    public List<Role> collisions(Actor source, String[] tags, int maxResults, Filter<Role> filter)
    {
        List<Role> results = new ArrayList<Role>();

        for (Iterator<Block> i = this.neighbourhood.blockIterator(this.topLeft, this.bottomRight); i
            .hasNext();) {
            Block square = i.next();

            for (Actor actor : square.getOccupants()) {
                Role role = actor.getRole();

                if ((actor != source) && (!results.contains(role))) {
                    if (filter.accept(role)) {
                        for (String tag : tags) {
                            if (role.hasTag(tag)) {

                                if (this.collisionTest.collided(source, actor)) {
                                    results.add(role);
                                    if (results.size() > maxResults) {
                                        return results;
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }

        return results;
    }
}
