/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.drunkinvaders;

import uk.co.nickthecoder.itchy.Input;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.PlainSceneDirector;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.Symbol;

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
        if (ke.symbol == Symbol.ESCAPE) {
            DrunkInvaders.director.getGame().end();
            ke.stopPropagation();
        }

        if ((ke.symbol == Symbol.p) || (ke.symbol == Symbol.RETURN)) {
            Itchy.getGame().startScene("levels");
            ke.stopPropagation();
        }

        if ((ke.symbol == Symbol.a) || (ke.symbol == Symbol.c)) {
            Itchy.getGame().startScene("about");
        }

        if ((ke.symbol == Symbol.F12) || (ke.symbol == Symbol.e)) {
            Itchy.getGame().startEditor();
        }
    }

}
