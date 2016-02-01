/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.itchy.collision.CollisionStrategy;
import uk.co.nickthecoder.itchy.property.PropertySubject;

public interface SceneDirector extends MouseListener, KeyListener, MessageListener, PropertySubject<SceneDirector>
{
    /**
     * Called early during the load process, before the actors have been fully  initialised.
     * This is primarily here, so that a SceneDirector can choose to merge additional scenes. 
     */
    public void loading( Scene scene );

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
    
    public CollisionStrategy getCollisionStrategy( Actor actor );
}
