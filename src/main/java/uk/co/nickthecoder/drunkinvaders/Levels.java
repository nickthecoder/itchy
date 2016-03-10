/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.drunkinvaders;

import uk.co.nickthecoder.itchy.Input;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.PlainSceneDirector;
import uk.co.nickthecoder.jame.event.KeyboardEvent;

public class Levels extends PlainSceneDirector
{
    protected Input inputExit;

    protected Input inputPlay;

    @Override
    public void onActivate()
    {
        this.inputExit = Input.find("exit");
        this.inputPlay = Input.find("play");
    }

    @Override
    public void onKeyDown( KeyboardEvent ke )
    {
        if (this.inputExit.matches(ke)) {
            Itchy.getGame().startScene("menu");
            ke.stopPropagation();
        }

        if (this.inputPlay.matches(ke)) {
            DrunkInvaders.director.play();
            ke.stopPropagation();
        }
    }

}
