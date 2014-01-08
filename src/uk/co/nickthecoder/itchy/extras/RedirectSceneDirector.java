/*******************************************************************************
 * Copyright (c) 2014 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.extras;

import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.PlainSceneDirector;
import uk.co.nickthecoder.itchy.property.Property;

/**
 * Redirects to another Scene when loaded. This can be useful if you want to have two names for one scene.
 * 
 * @author nick
 * 
 */
public class RedirectSceneDirector extends PlainSceneDirector
{
    @Property(label="Redirect Scene Name")
    public String redirectSceneName;
    
    @Override
    public void onActivate()
    {
        Itchy.getGame().startScene( redirectSceneName );
    }
}
