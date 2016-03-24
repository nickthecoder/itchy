/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.collision;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.util.Filter;

/**
 * Uses a {@link Neighbourhood} to optimise collision detection by only considering nearby Actors.
 * Create a SinglePointCollisionStrategy for each Actor, using a shared Neighbourhood.
 * <p>
 * Each actor is placed into a single {@link Block} based on the Actors x,y coordinate.
 * <p>
 * When checking for collisions, only actors in the same block, and in neighbouring blocks are considered. This means
 * that all of the actors which use this strategy must not be larger than the neighbourhood's block size. In fact, if an
 * actor's origin is not its center, then the length to all edges must not be larger than half the neighbourhood's block
 * size.
 * <p>
 * The block size must therefore be chosen very carefully. Too small, and some collisions will be missed. Too large will
 * lead to inefficiency (because there will be many actors in each Block). If in doubt, err on the large side!
 * <p>
 * See {@link NeighbourhoodCollisionStrategy} for a strategy which is a little smarter, and doesn't have a lower limit
 * on block size.
 * 
 * @priority 2
 */
public class SinglePointCollisionStrategy extends ActorCollisionStrategy
{
    private Neighbourhood neighbourhood;

    /**
     * The Block that this Actor is in.
     */
    private Block block;

    /**
     * Create a CollisionStrategy for the Actor (with a shared Neighbourhood).
     * The CollisionTest will be {@link PixelCollisionTest}.
     * 
     * @param actor
     * @param neighbourhood
     *            neighbourhood shared by all
     */
    public SinglePointCollisionStrategy(Actor actor, Neighbourhood neighbourhood)
    {
        this(PixelCollisionTest.instance, actor, neighbourhood);
    }

    /**
     * Create a CollisionStrategy for the Actor (with a shared Neighbourhood).
     * 
     * @param collisionTest
     * @param actor
     * @param neighbourhood
     *            neighbourhood shared by all
     */
    public SinglePointCollisionStrategy(CollisionTest collisionTest, Actor actor, Neighbourhood neighbourhood)
    {
        super(collisionTest, actor);
        this.neighbourhood = neighbourhood;
        this.update();
    }

    /**
     * @return The Block that the Actor is in.
     * @priority 3
     */
    public Block getBlock()
    {
        return this.block;
    }

    /**
     * Updates the Block that the Actor is in.
     */
    @Override
    public void update()
    {
        Block ns = this.neighbourhood.getBlock(this.actor.getX(), this.actor.getY());
        if (ns != this.block) {
            if (this.block != null) {
                this.block.remove(this.actor);
            }
            this.block = ns;
            this.block.add(this.actor);
        }
    }

    /**
     * Removes the Actor from the block.
     */
    @Override
    public void remove()
    {
        if (this.block != null) {
            this.block.remove(this.actor);
            this.block = null;
        }
    }

    @Override
    public List<Role> collisions(Actor source, String[] tags, int maxResults, Filter<Role> filter)
    {
        List<Role> results = new ArrayList<Role>();

        for (Block square : this.block.getNeighbouringBlocks()) {

            for (Actor actor : square.getOccupants()) {
                Role role = actor.getRole();

                if ((actor != source) && (!results.contains(role))) {
                    if (filter.accept(role)) {
                        for (String tag : tags) {
                            if (role.hasTag(tag)) {

                                if (this.collisionTest.collided(source, actor)) {
                                    results.add(role);
                                    if (results.size() >= maxResults) {
                                        return results;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        return results;
    }
}
