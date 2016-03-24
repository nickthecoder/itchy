/*******************************************************************************
 * Copyright (c) 2014 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.collision;

import java.util.List;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.WrappedWorld;
import uk.co.nickthecoder.itchy.WrappedStageView;
import uk.co.nickthecoder.itchy.util.Filter;

/**
 * Checks for collisions within a world where the left edge is joined to the right edge,
 * and/or the top edge is joined to the bottom edge.
 * <p>
 * It does this by testing if the Actor is overlapping one of the boundaries, and if it is, it makes two sets of
 * collision detections, moving the Actor to the "other side" of the world.
 * <p>
 * For example, if the world extends from 0 to 1,000, and an actor is at (10, 100), but is 50 pixels wide, then it will
 * first test for collisions at (10, 100) and then move the actor to (1010, 100) and test again. It then moves the actor
 * back it its original position.
 * <p>
 * This is done for the left/right edges and the top/bottom edges, so there can be up to nine times as many collision
 * tests as a regular (non-wrapped) world.
 * <p>
 * The actual collision detection isn't handled by this class, instead it uses another CollisionStrategy, which is
 * passed into the constructor {@link #WrappedCollisionStrategy(WrappedWorld, CollisionStrategy)}.
 * <p>
 * The location of the joins is defined by {@link WrappedWorld}. If you only want to join one pair of edges, make the
 * other edges at {@link Integer#MIN_VALUE} and {@link Integer#MAX_VALUE}, that way Actors will never get close the
 * edges. Note, It is likely you will use {@link WrappedStageView}, which implements Wrapped.
 * <p>
 * The demo game Destroy-Debris uses WrappedCollisionStrategy.
 */
public class WrappedCollisionStrategy implements CollisionStrategy
{
    /**
     * The regular (non-wrapped) CollisionStrategy.
     */
    private CollisionStrategy regularCollisionStrategy;

    /**
     * Defines the edges of the world - where the left,right,top and bottom edges are.
     */
    private WrappedWorld wrappedWorld;

    /**
     * Uses a {@link BruteForceCollisionStrategy}.
     * 
     * @param wrappedWorld
     *            The extent of the world.
     */
    public WrappedCollisionStrategy(WrappedWorld wrappedWorld)
    {
        this(wrappedWorld, BruteForceCollisionStrategy.instance);
    }

    public WrappedCollisionStrategy(WrappedWorld wrappedWorld, CollisionStrategy regularCollisionStrategy)
    {
        super();
        this.wrappedWorld = wrappedWorld;
        this.regularCollisionStrategy = regularCollisionStrategy;
    }

    @Override
    public List<Role> collisions(Actor actor, String[] includeTags, int maxResults, Filter<Role> filter)
    {
        wrappedWorld.normalise(actor);

        List<Role> result = collisions2(actor, includeTags, maxResults, filter);

        if (result.size() >= maxResults) {
            return result;
        }

        if (wrappedWorld.overlappingLeft(actor)) {
            actor.setX(actor.getX() + wrappedWorld.getWidth());
            update();
            result.addAll(collisions2(actor, includeTags, maxResults - result.size(), filter));
            actor.setX(actor.getX() - wrappedWorld.getWidth());
            update();
        }

        if (result.size() >= maxResults) {
            return result;
        }

        if (wrappedWorld.overlappingRight(actor)) {
            actor.setX(actor.getX() - wrappedWorld.getWidth());
            update();
            result.addAll(collisions2(actor, includeTags, maxResults - result.size(), filter));
            actor.setX(actor.getX() + wrappedWorld.getWidth());
            update();
        }

        return result;
    }

    private List<Role> collisions2(Actor actor, String[] tags, int maxResults, Filter<Role> filter)
    {
        List<Role> result = regularCollisionStrategy.collisions(actor, tags, maxResults, filter);

        if (wrappedWorld.overlappingBottom(actor)) {
            actor.setY(actor.getY() + wrappedWorld.getHeight());
            update();
            result.addAll(regularCollisionStrategy.collisions(actor, tags, maxResults, filter));
            actor.setY(actor.getY() - wrappedWorld.getHeight());
            update();
        }

        if (wrappedWorld.overlappingTop(actor)) {
            actor.setY(actor.getY() - wrappedWorld.getHeight());
            update();
            result.addAll(regularCollisionStrategy.collisions(actor, tags, maxResults, filter));
            actor.setY(actor.getY() + wrappedWorld.getHeight());
            update();
        }

        return result;
    }

    /**
     * Updates the regular CollisionStrategy.
     */
    @Override
    public void update()
    {
        this.regularCollisionStrategy.update();
    }

    /**
     * Removes the regular CollisionStrategy.
     */
    @Override
    public void remove()
    {
        this.regularCollisionStrategy.remove();
    }

}