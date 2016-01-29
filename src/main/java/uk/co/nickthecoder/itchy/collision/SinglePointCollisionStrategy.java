/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.collision;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Role;

/**
 * Uses a Neighbourhood to optimise Actor's overlapping and pixelOcerlap methods. This strategy uses a grid based neighbourhood, when the
 * actor is placed into a single NeighbourhoodSquare based on the Actors x,y coordintate. When checking for collisions, actors in the same
 * square, and neighbouring squares are considered. This means that all of the actors which use this strategy must not be larger than the
 * neighbourhood's square size. In fact, if an actor's origin is not its center, then the length to all edges must not be larger than half
 * the neighbourhood's square size.
 * 
 * It is possbile to mix and match SinglePointCollisionStrategy, and a stategy which places the actor into more than one neighbourhood
 * square (because that actor is bigger than the square size). However, this other strategy hasn't been written yet! This alternative will
 * be useful for large, static actors, such as collidable scenery.
 */

public class SinglePointCollisionStrategy extends ActorCollisionStrategy
{
    protected CollisionTest collisionTest;

    private Neighbourhood neighbourhood;

    private Square neighbourhoodSquare;

    public SinglePointCollisionStrategy( Actor actor, Neighbourhood neighbourhood )
    {
        this(PixelCollisionTest.instance, actor, neighbourhood);
    }

    public SinglePointCollisionStrategy( CollisionTest collisionTest, Actor actor, Neighbourhood neighbourhood )
    {
        super(actor);
        this.neighbourhood = neighbourhood;
        this.update();
    }

    public Square getSquare()
    {
        return this.neighbourhoodSquare;
    }

    @Override
    public void update()
    {
        Square ns = this.neighbourhood.getSquare(this.actor.getX(), this.actor.getY());
        if (ns != this.neighbourhoodSquare) {
            if (this.neighbourhoodSquare != null) {
                this.neighbourhoodSquare.remove(this.actor);
            }
            this.neighbourhoodSquare = ns;
            this.neighbourhoodSquare.add(this.actor);
        }
    }

    @Override
    public void remove()
    {
        if (this.neighbourhoodSquare != null) {
            this.neighbourhoodSquare.remove(this.actor);
            this.neighbourhoodSquare = null;
        }
    }

    private static final String[] EMPTY = {};
    
    @Override
    public List<Role> collisions( Actor actor, String... includeTags )
    {
        return collisions(actor, includeTags, EMPTY );
    }
    
    @Override
    public List<Role> collisions( Actor source, String[] includeTags, String[] excludeTags )
    {
        List<Role> results = new ArrayList<Role>();

        for (Square square : this.neighbourhoodSquare.getNeighbouringSquares()) {

            for (Actor actor : square.getOccupants()) {
                Role role = actor.getRole();

                if ((actor != source) && (!results.contains(actor))) {
                    if (!BruteForceCollisionStrategy.exclude(role, excludeTags)) {
                        for (String includeTag : includeTags) {
                            if (role.hasTag(includeTag)) {

                                if (this.collisionTest.collided(source, actor)) {
                                    results.add(role);
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
