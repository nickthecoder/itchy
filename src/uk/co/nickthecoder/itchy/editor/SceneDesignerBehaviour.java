/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.Behaviour;

public class SceneDesignerBehaviour extends Behaviour
{
    public Behaviour actualBehaviour;

    public void setBehaviourClassName( String name ) throws ClassNotFoundException,
        InstantiationException, IllegalAccessException
    {
        Class<?> klass = Class.forName(name);
        this.actualBehaviour = (Behaviour) klass.newInstance();
    }

    public String getBehaviourClassName()
    {
        return this.actualBehaviour.getClass().getName();
    }

    @Override
    public void tick()
    {
    }

}
