/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.List;

import uk.co.nickthecoder.itchy.util.AbstractProperty;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

/**
 * Does nothing, but it is handy to use as a subclass, rather than creating your own empty methods
 * for those methods that you don't care about.
 */
public class NullSceneBehaviour implements SceneBehaviour
{
    @Override
    public void onActivate()
    {
    }

    @Override
    public void onDeactivate()
    {
    }

    @Override
    public boolean onMouseDown( MouseButtonEvent mbe )
    {
        return false;
    }

    @Override
    public boolean onMouseUp( MouseButtonEvent mbe )
    {
        return false;
    }

    @Override
    public boolean onMouseMove( MouseMotionEvent mme )
    {
        return false;
    }

    @Override
    public boolean onKeyDown( KeyboardEvent ke )
    {
        return false;
    }

    @Override
    public boolean onKeyUp( KeyboardEvent ke )
    {
        return false;
    }

    @Override
    public void onMessage( String message )
    {
    }

    @Override
    public void tick()
    {
    }

    @Override
    public List<AbstractProperty<SceneBehaviour, ?>> getProperties()
    {
        Class<? extends SceneBehaviour> klass = this.getClass().asSubclass(SceneBehaviour.class);
        
        return AbstractProperty.findAnnotations(klass);
    }

}
