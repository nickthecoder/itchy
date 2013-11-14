/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.Iterator;
import java.util.List;

/**
 * A group of Actors, which are ultimately drawn at the the same time using a View.
 */
public interface Stage
{
    public String getName();

    /**
     * @return True if this layer shouldn't be edited within the SceneDesigner.
     */
    // TODO - remove this if/when we have a createDeisgnLayer.
    public boolean isLocked();

    public Iterator<Actor> iterator();

    public List<Actor> getActors();

    /**
     * Adds the Actor to the layer. An Actor can only belong to one layer, so if it is already on a
     * Layer, then it is removed.
     * 
     * @param actor
     */
    public void add( Actor actor );

    /**
     * Removes the actor from the layer. This is automatically called when an actor is killed,
     * however, if you call this directly, then it is possible for an Actor to continue to live
     * (it's tick method to be called), and yet be invisible.
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

    /**
     * Used by the SceneDesigner to create the stages where Actors are edited. Most stages will just
     * create another instance of themselves, but some may have different behaviour during the
     * design compared to during actual game play.
     * 
     * @return
     */
    public Stage createDesignStage();
}
