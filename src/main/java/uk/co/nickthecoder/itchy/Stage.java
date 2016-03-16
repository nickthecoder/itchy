/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.Iterator;
import java.util.List;

import uk.co.nickthecoder.itchy.StageConstraint;
import uk.co.nickthecoder.itchy.editor.SceneDesigner;
import uk.co.nickthecoder.itchy.property.PropertySubject;

/**
 * A group of Actors, Draw on the screen using a {@link StageView}.
 */
public interface Stage extends PropertySubject<Stage>
{
    /**
     * 
     * @return
     * @priority 3
     */
    public Iterator<Actor> iterator();

    /**
     * 
     * @return
     * @priority 3
     */
    public List<Actor> getActors();

    /**
     * Adds the Actor to the layer. An Actor can only belong to one layer, so if it is already on a Layer, then it is
     * removed.
     * 
     * @param actor
     */
    public void add(Actor actor);

    /**
     * Removes all of the Actors from the Stage.
     * 
     * @priority 2
     */
    public void clear();

    /**
     * Removes the actor from the layer. This is automatically called when an actor is killed, however, if you call this
     * directly, then it
     * is possible for an Actor to continue to live (it's tick method to be called), and yet be invisible.
     * 
     * @param actor
     */
    public void remove(Actor actor);

    /**
     * Calls {@link Role#tick()} for all actors on this Stage.
     * 
     * @priority 2
     */
    public void tick();

    /**
     * Used internally by Itchy send notifications to all {@link StageListener}s that the actor's Role has changed.
     * 
     * @param actor
     * @priority 5
     */
    public void changedRole(Actor actor);

    /**
     * 
     * @param listener
     * @priority 3
     */
    public void addStageListener(StageListener listener);

    /**
     * 
     * @param listener
     * @priority 3
     */
    public void removeStageListener(StageListener listener);

    /**
     * A simple getter. StageConstraints are used by the {@link SceneDesigner} when placing Actors on the Stage.
     * 
     * @return
     * @priority 3
     */
    public StageConstraint getStageConstraint();

    /**
     * A simple setter. StageConstraints are used by the {@link SceneDesigner} when placing Actors on the Stage.
     * 
     * @param stageConstraint
     * @priority 3
     */
    public void setStageConstraint(StageConstraint stageConstraint);
}
