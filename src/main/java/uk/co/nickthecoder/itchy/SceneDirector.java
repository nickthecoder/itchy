/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.itchy.collision.BruteForceCollisionStrategy;
import uk.co.nickthecoder.itchy.collision.CollisionStrategy;
import uk.co.nickthecoder.itchy.collision.NeighbourhoodCollisionStrategy;
import uk.co.nickthecoder.itchy.collision.SinglePointCollisionStrategy;
import uk.co.nickthecoder.itchy.editor.SceneDesigner;
import uk.co.nickthecoder.itchy.property.PropertySubject;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

/**
 * Controls a single {@link Scene}. Defined on the "Info" tab of the {@link SceneDesigner}.
 * Created when a Scene is loaded
 *
 */
public interface SceneDirector extends MouseListener, KeyListener, MessageListener, PropertySubject<SceneDirector>
{
    /**
     * Called early during the load process, before the actors have been fully initialised.
     * This is primarily here, so that a SceneDirector can choose to merge additional scenes.
     */
    public void loading(Scene scene);

    /**
     * Called after the scene has been loaded, and all of the actor's have been added to the stages.
     * At this stage, none of the actor's have had their onSceneCreated called.
     */
    public void onLoaded();

    /**
     * Called after the scene has been loaded, and all of the actor's have been added to the stages, and all roles
     * have had their onSceneCreated called.
     */
    public void onActivate();

    public void onDeactivate();

    public void tick();

    @Override
    public void onKeyDown(KeyboardEvent ke);

    @Override
    public void onKeyUp(KeyboardEvent ke);

    @Override
    public void onMouseDown(MouseButtonEvent event);

    @Override
    public void onMouseUp(MouseButtonEvent event);

    @Override
    public void onMouseMove(MouseMotionEvent event);

    /**
     * Chooses the type of {@link CollisionStrategy}. The {@link PlainSceneDirector} always returns
     * {@link BruteForceCollisionStrategy}, which is easy to use, but isn't as quick as other, more complex
     * strategies.
     * <p>
     * I suggest you stick to the default, and investigate the altenatives, such as
     * {@link NeighbourhoodCollisionStrategy} and {@link SinglePointCollisionStrategy} if your game runs too slowly.
     * 
     * @param actor
     *            The Actor for whom the collision strategy is for.
     * @return A collision strategy. Some Strategies require a new instance of each Actor, others, such as
     *         BruteForceCollisionStrategy can reuse a single instance.
     */
    public CollisionStrategy getCollisionStrategy(Actor actor);
}
