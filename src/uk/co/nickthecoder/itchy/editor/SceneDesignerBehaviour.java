/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import javax.script.ScriptException;

import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.Resources;

public class SceneDesignerBehaviour extends Behaviour
{
    public Behaviour actualBehaviour;

    public void setBehaviourClassName( Resources resources, String name ) throws ClassNotFoundException,
        InstantiationException, IllegalAccessException, ScriptException
    {
        this.actualBehaviour = Behaviour.createBehaviour(resources, name);
    }

    public String getBehaviourClassName()
    {
        return this.actualBehaviour.getClassName();
    }

    @Override
    public void tick()
    {
    }

}
