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
 * Uses a Neighbourhood to optimise Actor's overlapping and pixelOverlap methods. This strategy uses a grid based neighbourhood. It is
 * designed to be used for objects larger than a single square. It can be mixed and matched with SinglePointCollisionStrategy.
 * 
 * During simple tests, this performed equally to SinglePointCollisionStrategy, and as it doesn't have the restrictions that
 * SinglePointCollisionStrategy has, then this class is preferable.
 * 
 * Adds the actor to all of the neighbourhood squares within its bounding rectangle. To test for collisions, iterate through all of these
 * squares, comparing with each of the the squares occupants. Note that if another actor shares two or more squares, then the collision test
 * will be made two or more times. This is an acceptable overhead, only because this strategy is expected to be used rarely for moving
 * objects. If this assumption isn't true, then its worth creating a new strategy which keeps track of which actors have already been tested
 * to prevent duplicate collision tests.
 * 
 * Note. If combined with SinglePointCollisionStrategy, then actors using this strategy must be passive (they never test for collision, only
 * the other actor tests for collision). i.e. MultiplsSquareCollisionStrategy.pixelOverlap is never called. This is needed because
 * SinglPoint only track a single square, and tests all surrounding squares, whereas MultiplSquare tracks all (partially) occupied squares,
 * and only considers them. So if a MultiSquare actor tests for collision with a SinglePoint, it may miss it if the single point is outside
 * the square occupied by the tester, but the actors nevertheless overlap.
 */

/*
 * Idealy, this class shouldn't make assumptions about the geometry of the neighbourhood. For
 * example, can it cope with the circular side scrollers, or the doughnut world of asteroids? To do
 * this we need to let neighbourhood handle the iteration from top left to bottom right. Note, at
 * the time of writing neither circular, nor doughnut shaped Neighbourhoods have been implemented.
 */

public class NeighbourhoodCollisionStrategy extends ActorCollisionStrategy
{
    protected CollisionTest collisionTest;

    private Neighbourhood neighbourhood;

    private Square topLeft;

    private Square bottomRight;

    public NeighbourhoodCollisionStrategy( Actor actor, Neighbourhood neighbourhood )
    {
        this( PixelCollisionTest.instance, actor, neighbourhood);
    }

    public NeighbourhoodCollisionStrategy( CollisionTest collisionTest, Actor actor, Neighbourhood neighbourhood )
    {
        super(actor);
        this.collisionTest = collisionTest;
        this.neighbourhood = neighbourhood;
        this.update();
    }
    
    @Override
    public void update()
    {
        WorldRectangle rect = this.actor.getAppearance().getWorldRectangle();

        Square newTopLeft = this.neighbourhood.getSquare(rect.x, rect.y);
        Square newBottomRight = this.neighbourhood.getSquare(rect.x + rect.width, rect.y +
            rect.height);

        if ((this.topLeft == null) || (this.topLeft != newTopLeft) ||
            (this.bottomRight != newBottomRight)) {

            if (this.topLeft != null) {
                for (Iterator<Square> i = this.neighbourhood.squareIterator(this.topLeft,
                    this.bottomRight); i.hasNext();) {
                    Square square = i.next();
                    square.remove(this.actor);
                }
            }

            this.topLeft = newTopLeft;
            this.bottomRight = newBottomRight;

            for (Iterator<Square> i = this.neighbourhood.squareIterator(this.topLeft,
                this.bottomRight); i.hasNext();) {
                Square square = i.next();
                square.add(this.actor);
            }

        }
    }

    @Override
    public void remove()
    {
        if (this.topLeft == null) {
            for (Iterator<Square> i = this.neighbourhood.squareIterator(this.topLeft,
                this.bottomRight); i.hasNext();) {
                Square square = i.next();
                square.remove(this.actor);
            }
        }
    }
        
    public static final int MAX_RESULTS = 100;
    
    @Override
    public List<Role> collisions( Actor actor, String[] tags )
    {
        return collisions(actor, tags, MAX_RESULTS, acceptFilter );
    }
    
    @Override
    public List<Role> collisions( Actor actor, String[] tags, int maxResults )
    {
        return collisions(actor, tags, maxResults, acceptFilter );
    }
    
    @Override
    public List<Role> collisions( Actor source, String[] tags, int maxResults, Filter<Role> filter )
    {
        List<Role> results = new ArrayList<Role>();

        for (Iterator<Square> i = this.neighbourhood.squareIterator(this.topLeft, this.bottomRight); i
            .hasNext();) {
            Square square = i.next();

            for (Actor actor : square.getOccupants()) {
                Role role = actor.getRole();

                if ((actor != source) && (!results.contains(role))) {
                    if (filter.accept( role ) ) {
                        for (String tag : tags) {
                            if (role.hasTag(tag)) {
    
                                if (this.collisionTest.collided(source, actor)) {
                                    results.add(role);
                                    if ( results.size() > maxResults) {
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
