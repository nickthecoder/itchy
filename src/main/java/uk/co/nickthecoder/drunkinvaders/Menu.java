/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.drunkinvaders;

import uk.co.nickthecoder.itchy.Input;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.PlainSceneDirector;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.Keys;

public class Menu extends PlainSceneDirector
{
    protected Input inputExit;

    protected Input inputPlay;

    protected Input inputAbout;

    protected Input inputEditor;

    @Override
    public void onActivate()
    {
        this.inputExit = Input.find("exit");
        this.inputPlay = Input.find("play");
        this.inputAbout = Input.find("about");
        this.inputEditor = Input.find("editor");
    }

    @Override
    public void onKeyDown( KeyboardEvent ke )
    {
        if (ke.symbol == Keys.ESCAPE) {
            DrunkInvaders.director.getGame().end();
            ke.stopPropagation();
        }

        if ((ke.symbol == Keys.p) || (ke.symbol == Keys.RETURN)) {
            Itchy.getGame().startScene("levels");
            ke.stopPropagation();
        }

        if ((ke.symbol == Keys.a) || (ke.symbol == Keys.c)) {
            Itchy.getGame().startScene("about");
        }

        if ((ke.symbol == Keys.F12) || (ke.symbol == Keys.e)) {
            Itchy.getGame().startEditor();
        }
    }

}
