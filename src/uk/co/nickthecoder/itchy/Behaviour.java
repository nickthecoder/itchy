/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.List;

import uk.co.nickthecoder.itchy.util.AbstractProperty;
import uk.co.nickthecoder.itchy.util.PropertySubject;

public interface Behaviour extends MessageListener, Cloneable, PropertySubject<Behaviour>
{
    public Actor getActor();

    public boolean hasTag( String name );

    public void birth();

    public void die();

    public void attach( Actor actor );

    public void detatch();

    public void animateAndTick();

    public Behaviour clone();

    @Override
    public void onMessage( String message );

    @Override
    public List<AbstractProperty<Behaviour, ?>> getProperties();
}