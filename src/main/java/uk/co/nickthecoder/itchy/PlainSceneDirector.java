/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.collision.BruteForceCollisionStrategy;
import uk.co.nickthecoder.itchy.collision.CollisionStrategy;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

/**
 * Does nothing, but it is handy to use as a subclass, rather than creating your own empty methods for those methods
 * that you don't care about.
 */
public class PlainSceneDirector implements SceneDirector
{
    protected static final List<Property<SceneDirector, ?>> properties = new ArrayList<Property<SceneDirector, ?>>();

    @Override
    public void loading(Scene scene)
    {
    }

    @Override
    public void onLoaded()
    {
    }

    @Override
    public void onActivate()
    {
    }

    @Override
    public void onDeactivate()
    {
    }

    @Override
    public void onMouseDown(MouseButtonEvent mbe)
    {
    }

    @Override
    public void onMouseUp(MouseButtonEvent mbe)
    {
    }

    @Override
    public void onMouseMove(MouseMotionEvent mme)
    {
    }

    @Override
    public void onKeyDown(KeyboardEvent ke)
    {
    }

    @Override
    public void onKeyUp(KeyboardEvent ke)
    {
    }

    @Override
    public void onMessage(String message)
    {
    }

    @Override
    public void tick()
    {
    }

    @Override
    public CollisionStrategy getCollisionStrategy(Actor actor)
    {
        return BruteForceCollisionStrategy.pixelCollision;
    }

    @Override
    public List<Property<SceneDirector, ?>> getProperties()
    {
        return properties;
    }

}
