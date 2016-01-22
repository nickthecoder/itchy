/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import uk.co.nickthecoder.itchy.property.Property;

public abstract class AbstractStage implements Stage
{
    private static final List<Property<Stage,?>> properties = new ArrayList<Property<Stage,?>>();

    public StageConstraint stageConstraint;

    private List<StageListener> stageListeners = new LinkedList<StageListener>();

    public AbstractStage()
    {
        this.stageConstraint = new NullStageConstraint();
    }

    @Override
    public StageConstraint getStageConstraint()
    {
        return this.stageConstraint;
    }

    public void setStageConstraint( StageConstraint sc )
    {
        this.stageConstraint = sc;
    }

    @Override
    public void add( Actor actor )
    {
        for (StageListener listener : this.stageListeners) {
            listener.onAdded(this, actor);
        }
    }

    @Override
    public void changedRole( Actor actor )
    {
        for (StageListener listener : this.stageListeners) {
            listener.onChangedRole(this, actor);
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

    @Override
    public List<Property<Stage,?>> getProperties()
    {
        return properties;
    }
}
