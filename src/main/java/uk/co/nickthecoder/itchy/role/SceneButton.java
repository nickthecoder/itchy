/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.role;

import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.property.Property;

/**
 * When clicked, this button will start a given scene. The scene is given by the sceneName property, which can be editted within the Scene
 * Designer.
 */
public class SceneButton extends Button
{
    @Property(label = "Scene Name")
    public String sceneName;

    @Override
    protected void onClick()
    {
        super.onClick();
        Itchy.getGame().startScene(this.sceneName);
    }

}
