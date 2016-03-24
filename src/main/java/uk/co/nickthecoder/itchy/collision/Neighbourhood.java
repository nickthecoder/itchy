/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.collision;

import java.util.Iterator;

/**
 * A rectangular grid of {@link Block}s.
 * <p>
 * Used by {@link NeighbourhoodCollisionStrategy} and {@link SinglePointCollisionStrategy} to speed up collision
 * detection, by only considering nearby Actors. SinglePointCollisionStrategy links to each Actor from only <b>one</b>
 * Block, whereas NeighbourhoodCollisionStrategy links to an Actor in all of the Blocks where the Actors bounding
 * rectangle overlaps the Block.
 */
public interface Neighbourhood
{

    /**
     * Resets the Neighbourhood, so that there are no Actors held within it.
     * You can use this to reset the Neighbourhood at the beginning of a Scene, however, it is probably easier to create
     * a new Neighbourhood within your SceneDirector, that way, you will have a new Neighbourhood for each scene.
     */
    public abstract void clear();

    /**
     * @return The width of the Blocks within this Neighbourhood.
     */
    public abstract double getBlockWidth();

    /**
     * @return The height of the Blocks within this Neighbourhood.
     */
    public abstract double getBlockHeight();

    /**
     * Looks for a Block within the neighbourhood. If a block at the given coordinates hasn't been
     * created yet, then that block is created.
     * <p>
     * Used when adding an Actor to the Neighbourhood.
     * 
     * @return The block at the given coordinate
     * @see #getExistingBlock(double, double)
     */
    public abstract Block getBlock(double x, double y);

    /**
     * Looks for a Block that has already been created.
     * <p>
     * Used when looking for an Actor. If there is no Block, then there is no need to create a new one.
     * 
     * @return The Block at the given coordinates, or null if there is no Block.
     * @see #getBlock(double, double)
     */
    public abstract Block getExistingBlock(double x, double y);

    /**
     * Iterates over the set of blocks contained by the rectangle defined by the top left and bottom right blocks.
     * 
     * @param topLeft
     * @param bottomRight
     *            Note, the iteration INCLUDES this block, and others in its row and column. This is different to most
     *            range tests, where the
     *            "to" is usually exclusive.
     */
    public abstract Iterator<Block> blockIterator(Block topLeft, Block bottomRight);

}
