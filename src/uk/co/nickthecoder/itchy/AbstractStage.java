/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractStage implements Stage
{
    // TODO Is this appropriate?
    public boolean yAxisPointsDown;

    public final String name;

    /**
     * Used by the editor - can actors be placed into this stage? This is handy if you have a stage
     * for non-game objects, for example a stage for a mini-map, control panel, dash boards etc.
     */
    public boolean locked = false;

    private List<StageListener> stageListeners = new LinkedList<StageListener>();

    public AbstractStage( String name )
    {
        this.name = name;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public boolean getYAxisPointsDown()
    {
        return this.yAxisPointsDown;
    }

    @Override
    public boolean isLocked()
    {
        return this.locked;
    }

    @Override
    public void add( Actor actor )
    {
        for (StageListener listener : this.stageListeners) {
            listener.onAdded(this, actor);
        }
    }

    @Override
    public void remove( Actor actor )
    {
        for (StageListener listener : this.stageListeners) {
            listener.onRemoved(this, actor);
        }
    }

    @Override
    public abstract void clear();

    @Override
    public void addStageListener( StageListener listener )
    {
        this.stageListeners.add(listener);
    }

    @Override
    public void removeStageListener( StageListener listener )
    {
        this.stageListeners.add(listener);
    }

}
