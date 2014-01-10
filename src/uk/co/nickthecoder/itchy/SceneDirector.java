/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.itchy.collision.CollisionStrategy;
import uk.co.nickthecoder.itchy.property.PropertySubject;

public interface SceneDirector extends MouseListener, KeyListener, MessageListener, PropertySubject<SceneDirector>
{
    public void onActivate();

    public void onDeactivate();

    public void tick();
    
    public CollisionStrategy getCollisionStrategy( Actor actor );
}
