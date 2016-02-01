/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.AbstractRole;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.util.ClassName;

public class SceneDesignerRole extends AbstractRole
{
    public Role actualRole;

    public SceneDesignerRole( Role role )
    {
        this.actualRole = role;
    }
    
    public ClassName getRoleClassName()
    {
        return this.actualRole.getClassName();
    }

    public String getId()
    {
        return this.actualRole.getId();
    }
    
    public void setId( String id )
    {
        this.actualRole.setId(id);
    }
    
    @Override
    public void tick()
    {
    }

}
