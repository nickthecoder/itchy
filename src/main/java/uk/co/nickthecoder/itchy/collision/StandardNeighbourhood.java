/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.collision;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uk.co.nickthecoder.itchy.Actor;

/**
 * A rectangular grid of {@link Block}s.
 * <p>
 * Used by {@link NeighbourhoodCollisionStrategy} and {@link SinglePointCollisionStrategy} to speed up collision
 * detection, by only considering nearby Actors. SinglePointCollisionStrategy links to each Actor from only <b>one</b>
 * Block, whereas NeighbourhoodCollisionStrategy links to an Actor in all of the Blocks where the Actors bounding
 * rectangle overlaps the Block.
 * <p>
 * Blocks are held in a 2D arrangement using a list of rows, where each row has a list of Blocks. This structure is
 * dynamic (it will grow automatically as needed). You can request the block for any point, and if a Block does not yet
 * exist for that point, then a new Block will be created and added into the array.
 * 
 */
public class StandardNeighbourhood implements Neighbourhood
{
    /**
     * The width of each Block in pixels.
     */
    private final double blockWidth;

    /**
     * The height of each Block in pixels.
     */
    private final double blockHeight;

    /**
     * The offset of the top-most row. Each NeighbourhoodRow has its own x-offset.
     */
    private double oy;

    /**
     * The Blocks are arranged in a list of rows (each row has a list of Blocks).
     */
    private final List<NeighbourhoodRow> rows;

    /**
     * Create a StandardNeighbourhood with square Blocks.
     * 
     * @param blockSize
     */
    public StandardNeighbourhood(double blockSize)
    {
        this(blockSize, blockSize);
    }

    /**
     * Create a StandardNeighbourhood with rectangular Blocks.
     * 
     * @param blockWidth
     * @param blockHeight
     */
    public StandardNeighbourhood(double blockWidth, double blockHeight)
    {
        this.blockWidth = blockWidth;
        this.blockHeight = blockHeight;
        this.rows = new ArrayList<NeighbourhoodRow>();
        this.oy = 0;
    }

    @Override
    public void clear()
    {
        this.rows.clear();
        this.oy = 0;
    }

    @Override
    public double getBlockWidth()
    {
        return this.blockWidth;
    }

    @Override
    public double getBlockHeight()
    {
        return this.blockHeight;
    }

    @Override
    public Block getBlock(double x, double y)
    {
        NeighbourhoodRow row = this.getExistingRow(y);
        if (row == null) {
            row = this.createRow(y);
        }
        return row.getBlock(x);
    }

    @Override
    public Iterator<Block> blockIterator(final Block topLeft, final Block bottomRight)
    {
        return new Iterator<Block>()
        {

            private double x = topLeft.getX();
            private double y = topLeft.getY();

            @Override
            public boolean hasNext()
            {
                return (this.y <= bottomRight.getY());
            }

            @Override
            public Block next()
            {
                Block block = StandardNeighbourhood.this.getBlock(this.x, this.y);
                this.x += StandardNeighbourhood.this.blockWidth;
                if (this.x > bottomRight.getX()) {
                    this.y += StandardNeighbourhood.this.blockHeight;
                    this.x = topLeft.getX();
                }
                return block;
            }

            @Override
            public void remove()
            {
                throw new RuntimeException("Cannot remove using this Neighbourhood.blockIterator");
            }

        };
    }

    @Override
    public Block getExistingBlock(double x, double y)
    {
        NeighbourhoodRow row = this.getExistingRow(y);
        if (row == null) {
            return null;
        }
        Block result = row.getExistingBlock(x);
        if (result == null) {
            return null;
        }
        return result;
    }

    /**
     * Finds the row for the given Y coordinate, or null if there is no row there.
     * 
     * @param y
     * @return The row if found, otherwise null
     */
    private NeighbourhoodRow getExistingRow(double y)
    {
        int iy = (int) Math.floor((y - this.oy) / this.blockHeight);
        if ((iy < 0) || (iy >= this.rows.size())) {
            return null;
        }
        return this.rows.get(iy);
    }

    /**
     * Called by {@link #getBlock(double, double)}, when {@link #getExistingRow(double)} is null.
     * 
     * @param y
     * @return A new NeighbourhoodRow.
     */
    private NeighbourhoodRow createRow(double y)
    {
        int iy = (int) Math.floor((y - this.oy) / StandardNeighbourhood.this.blockHeight);

        if (iy < 0) {

            List<NeighbourhoodRow> newRows = new ArrayList<NeighbourhoodRow>(-iy);
            this.oy += iy * StandardNeighbourhood.this.blockHeight;
            for (int i = 0; i <= -iy - 1; i++) {
                newRows.add(new NeighbourhoodRow(this.oy + i * this.blockHeight));
            }
            this.rows.addAll(0, newRows);

        } else if (iy >= this.rows.size()) {

            int extra = iy - this.rows.size() + 1;
            for (int i = 0; i < extra; i++) {
                NeighbourhoodRow row = new NeighbourhoodRow(this.oy + this.rows.size() * this.blockHeight);
                this.rows.add(row);
            }

        } else {
            throw new RuntimeException("Attempt to recreate an existing row " + y);
        }

        return this.getExistingRow(y);
    }

    /**
     * @priority 5
     */
    public void debug()
    {
        System.err.println("StandardNeighbourhood : " + this.blockWidth + "x" + this.blockHeight + " oy=" + this.oy);
        double y = this.oy;
        for (NeighbourhoodRow row : this.rows) {
            System.err.println("\nRow : " + y + " ... " + row.y);

            double x = row.ox;
            for (Block sq : row.row) {
                System.err.println("\n" + sq + " : expected : " + x + "," + y + "\n ");
                for (Actor actor : sq.getOccupants()) {
                    System.err.println(actor);
                }
                x += getBlockWidth();
            }

            y += getBlockHeight();
        }
    }

    private class NeighbourhoodRow
    {
        private double ox;

        private final double y;

        private final List<Block> row;

        public NeighbourhoodRow(double y)
        {
            this.y = y;
            this.ox = 0;
            this.row = new ArrayList<Block>();
        }

        public Block getBlock(double x)
        {
            Block result = this.getExistingBlock(x);
            if (result == null) {
                result = this.createBlock(x);
            }
            return result;

        }

        private Block getExistingBlock(double x)
        {
            int ix = (int) Math.floor((x - this.ox) / StandardNeighbourhood.this.blockWidth);
            if ((ix < 0) || (ix >= this.row.size())) {
                return null;
            }
            return this.row.get(ix);
        }

        private Block createBlock(double x)
        {
            int ix = (int) Math.floor((x - this.ox) / StandardNeighbourhood.this.blockWidth);

            if (ix < 0) {
                List<Block> newBlocks = new ArrayList<Block>(-ix);
                this.ox += ix * StandardNeighbourhood.this.blockWidth;
                for (int i = 0; i < -ix; i++) {
                    newBlocks.add(new Block(
                        StandardNeighbourhood.this,
                        this.ox + i * StandardNeighbourhood.this.blockWidth,
                        this.y));
                }
                this.row.addAll(0, newBlocks);
                for (int i = 0; i < -ix; i++) {
                    this.row.get(i).initialiseNeighbours();
                }
            } else {
                int extra = ix - this.row.size() + 1;
                for (int i = 0; i < extra; i++) {
                    Block block = new Block(
                        StandardNeighbourhood.this,
                        this.ox + (this.row.size()) * StandardNeighbourhood.this.blockWidth,
                        this.y);

                    this.row.add(block);
                    block.initialiseNeighbours();
                }

            }

            return this.getExistingBlock(x);
        }

    }

}
