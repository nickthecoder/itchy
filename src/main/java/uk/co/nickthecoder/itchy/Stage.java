/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.Iterator;
import java.util.List;

import uk.co.nickthecoder.itchy.StageConstraint;
import uk.co.nickthecoder.itchy.property.PropertySubject;

/**
 * A group of Actors, which are ultimately drawn at the the same time using a View.
 */
public interface Stage extends PropertySubject<Stage>
{
    public Iterator<Actor> iterator();

    public List<Actor> getActors();

    /**
     * Adds the Actor to the layer. An Actor can only belong to one layer, so if it is already on a Layer, then it is removed.
     * 
     * @param actor
     */
    public void add( Actor actor );

    public void tick();
    
    public void changedRole( Actor actor );

    /**
     * Removes the actor from the layer. This is automatically called when an actor is killed, however, if you call this directly, then it
     * is possible for an Actor to continue to live (it's tick method to be called), and yet be invisible.
     * 
     * @param actor
     */
    public void remove( Actor actor );

    /**
     * Removes all of the Actors from the Stage.
     */
    public void clear();

    public void addStageListener( StageListener listener );

    public void removeStageListener( StageListener listener );

    public StageConstraint getStageConstraint();

    public void setStageConstraint(StageConstraint stageConstraint);
}
