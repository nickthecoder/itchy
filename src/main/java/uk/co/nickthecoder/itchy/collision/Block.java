/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.collision;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.co.nickthecoder.itchy.Actor;

/**
 * A part of a {@link Neighbourhood}.
 * 
 * @priority 3
 */
public class Block
{
    /**
     * The Blocks that touch this one, including diagonally, and also this Block. Therefore the length is from 1 to 9.
     */
    private List<Block> neighbours;

    /**
     * The Actors that have been placed in this Block. There is no rule saying which Actors should be in a Block.
     * {@link NeighbourhoodCollisionStrategy} uses Blocks differently to {@link SinglePointCollisionStrategy}.
     */
    private Set<Actor> occupants;

    private Neighbourhood neighbourhood;

    /**
     * The x position of this Block in the world.
     */
    private double ox;

    /**
     * The y position of this Block in the world.
     */
    private double oy;

    /**
     * Create a new Block.
     * Note, if you wish to use {@link #getNeighbouringBlocks()}, then you must also call
     * {@link #initialiseNeighbours()} soon after the constructor.
     * 
     * @param nbh
     * @param x
     * @param y
     */
    public Block(Neighbourhood nbh, double x, double y)
    {
        this.neighbourhood = nbh;
        this.occupants = new HashSet<Actor>();
        this.ox = x;
        this.oy = y;
    }

    /**
     * Caches the list of Blocks that touch this one, including diagonally, and also this Block. Therefore the length is
     * from 1 to 9.
     */
    public void initialiseNeighbours()
    {

        this.neighbours = new ArrayList<Block>(9);

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {

                if ((dx == 0) && (dy == 0)) {
                    this.neighbours.add(this);

                    if (this.neighbourhood.getBlock(this.ox, this.oy) != this) {
                        throw new RuntimeException("Block is in the wrong place");
                    }

                } else {
                    Block neighbour = this.neighbourhood.getExistingBlock(
                        this.ox + dx * this.neighbourhood.getBlockWidth(),
                        this.oy + dy * this.neighbourhood.getBlockHeight());

                    if (neighbour == this) {
                        throw new RuntimeException("Block in two places at once. " + dx + "," + dy);
                    }

                    if (neighbour != null) {

                        if (this.ox != neighbour.ox - dx * this.neighbourhood.getBlockWidth()) {
                            throw new RuntimeException("Incorrect x neighbour");
                        }

                        if (this.oy != neighbour.oy - dy * this.neighbourhood.getBlockHeight()) {
                            throw new RuntimeException("Incorrect y neighbour");
                        }

                        if (neighbour.neighbours != null) {
                            this.neighbours.add(neighbour);
                            neighbour.neighbours.add(this);
                        }
                    }

                }
            }
        }

    }

    /**
     * This is only valid if you have previously called {@link #initialiseNeighbours()}.
     * 
     * @return
     */
    public List<Block> getNeighbouringBlocks()
    {
        return Collections.unmodifiableList(this.neighbours);
    }

    /**
     * @return The X position of this Block
     */
    public double getX()
    {
        return this.ox;
    }

    /**
     * @return The Y position of this Block
     */
    public double getY()
    {
        return this.oy;
    }

    /**
     * Adds the Actor as an occupant of this Block.
     * 
     * @param actor
     */
    public void add(Actor actor)
    {
        this.occupants.add(actor);
    }

    /**
     * Removes the Actor as an occupant of this Block.
     * 
     * @param actor
     */
    public void remove(Actor actor)
    {
        this.occupants.remove(actor);
    }

    /**
     * 
     * @return The set of Actors that have been added to this Block.
     * @see #add(Actor)
     * @see #remove(Actor)
     */
    public Set<Actor> getOccupants()
    {
        return Collections.unmodifiableSet(this.occupants);
    }

    /**
     * Prints debugging info to stderr.
     * 
     * @priority 5
     */
    public void debug()
    {
        System.err.println("Debugging Block : " + this + "( " + this.ox + "," + this.oy + ")");
        System.err.println("Occupants : " + this.occupants);

        System.err.println();

        for (Block nb : this.neighbours) {
            System.err.println("Neighbour : " + nb);
            System.err.println("  Occupants : " + nb.occupants);
            System.err.println("  mutual neighbours : " + nb.neighbours.contains(this));
        }
    }

    /**
     * @priority 3
     */
    @Override
    public String toString()
    {
        return "Block (" + this.ox + "," + this.oy + ")";
    }
}
