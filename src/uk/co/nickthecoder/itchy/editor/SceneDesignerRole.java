/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import javax.script.ScriptException;

import uk.co.nickthecoder.itchy.AbstractRole;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.util.ClassName;

public class SceneDesignerRole extends AbstractRole
{
    public Role actualRole;

    public void setRoleClassName( Resources resources, ClassName className ) throws ClassNotFoundException,
        InstantiationException, IllegalAccessException, ScriptException
    {
        this.actualRole = AbstractRole.createRole(resources, className);
    }

    public ClassName getRoleClassName()
    {
        return ClassName.getClassName(this.actualRole);
    }

    @Override
    public void tick()
    {
    }

}
