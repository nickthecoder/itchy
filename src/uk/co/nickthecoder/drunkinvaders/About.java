/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.drunkinvaders;

import uk.co.nickthecoder.itchy.Input;
import uk.co.nickthecoder.itchy.PlainSceneDirector;
import uk.co.nickthecoder.jame.event.KeyboardEvent;

public class About extends PlainSceneDirector
{
    protected Input inputExit;

    @Override
    public void onActivate()
    {
        inputExit = Input.find("exit");
    }

    @Override
    public boolean onKeyDown( KeyboardEvent ke )
    {
        if (this.inputExit.matches(ke)) {
            DrunkInvaders.director.startScene("menu");
            return true;
        }

        return false;
    }

}
