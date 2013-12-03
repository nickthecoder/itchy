/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores an undo/redo list
 */
public class UndoList
{
    private static final long MERGE_WINDOW = 1000; // 1 second

    private List<Undo> list;

    private long lastAddTime;

    private boolean undoing = false;

    /**
     * THe current position within the undo/redo list.
     */
    private int index = 0;

    public UndoList()
    {
        this.list = new ArrayList<Undo>();
    }

    public void undo()
    {
        if (canUndo()) {
            this.undoing = true;
            try {
                this.index--;
                System.out.println("Undo " + this.list.get(this.index));
                this.list.get(this.index).undo();
            } finally {
                this.undoing = false;
            }
        }
    }

    public void redo()
    {
        if (canRedo()) {
            this.undoing = true;
            try {
                System.out.println("Redo " + this.list.get(this.index));
                this.list.get(this.index).redo();
                this.index++;
            } finally {
                this.undoing = false;
            }
        }
    }

    public boolean isUndoing()
    {
        return this.undoing;
    }

    public boolean canUndo()
    {
        return this.index > 0;
    }

    public boolean canRedo()
    {
        return this.index < this.list.size();
    }

    public void add( Undo undo )
    {
        long time = System.currentTimeMillis();
        if (canUndo() && (time - this.lastAddTime < MERGE_WINDOW)) {
            if (this.list.get(this.index - 1).merge(undo)) {
                System.out.println("Merged");
                // Merged with the previous one, so no need to add to the list, or change index.
                return;
            }
        }
        while (this.list.size() > this.index) {
            this.list.remove(this.list.size() - 1);
            System.out.println("Removed item");
        }
        this.list.add(undo);
        this.index = this.list.size();
        this.lastAddTime = time;
        System.out.println("Added");
    }

    public void apply( Undo undo )
    {
        undo.redo();
        add(undo);
    }

    public void clear()
    {
        this.list.clear();
        this.index = 0;
    }
}
