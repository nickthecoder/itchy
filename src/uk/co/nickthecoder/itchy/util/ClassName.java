/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.util;

import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.script.ScriptManager;
import uk.co.nickthecoder.itchy.script.ScriptedBehaviour;

/**
 * Holds the name of a class, which the user can set within the editor, used fro Behaviour,
 * SceneBehaviour and Game.
 * 
 * The class name can either be a regular Java class (in which case the name will be a fully
 * qualified java class name), or a scripted class (in which case the name will be the filename of
 * the script such as "Ship.js").
 * 
 */
public class ClassName
{
    public static ClassName getClassName( Behaviour behaviour )
    {
        if (behaviour instanceof ScriptedBehaviour) {
            return ((ScriptedBehaviour) behaviour).getClassName();
        } else {
            return new ClassName( behaviour.getClass().getName());
        }
    }
    
    public String name;

    public ClassName( String name )
    {
        this.name = name;
    }

    @Override
    public boolean equals( Object other )
    {
        if (other instanceof ClassName) {
            return ((ClassName) other).name.equals(this.name);
        } else {
            return false;
        }
    }

    public boolean isScript()
    {
        return ScriptManager.isScript(this);
    }

}
