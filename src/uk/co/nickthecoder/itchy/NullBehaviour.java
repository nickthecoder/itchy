/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.Collections;
import java.util.List;

import uk.co.nickthecoder.itchy.util.AbstractProperty;

/**
 * Does nothing - useful for scenery, and other game objects which don't do anything!
 */
public final class NullBehaviour implements Behaviour
{
    private Actor actor;

    @Override
    public Actor getActor()
    {
        return this.actor;
    }

    @Override
    public boolean hasTag( String name )
    {
        return false;
    }

    @Override
    public void birth()
    {
    }

    @Override
    public void die()
    {
    }

    @Override
    public void attach( Actor actor )
    {
        this.actor = actor;
    }

    @Override
    public void detatch()
    {
    }

    @Override
    public void animateAndTick()
    {
    }

    @Override
    public void onMessage( String message )
    {
    }

    @Override
    public List<AbstractProperty<Behaviour, ?>> getProperties()
    {
        return Collections.emptyList();
    }

    @Override
    public Behaviour clone()
    {
        try {
            return (Behaviour) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
