/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.drunkinvaders;

import uk.co.nickthecoder.itchy.PlainSceneDirector;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.Keys;

public class Menu extends PlainSceneDirector
{

    @Override
    public boolean onKeyDown( KeyboardEvent ke )
    {
        if (ke.symbol == Keys.ESCAPE) {
            DrunkInvaders.director.getGame().end();
            return true;
        }

        if ((ke.symbol == Keys.p) || (ke.symbol == Keys.RETURN)) {
            DrunkInvaders.director.startScene("levels");
            return true;
        }

        if ((ke.symbol == Keys.a) || (ke.symbol == Keys.c)) {
            DrunkInvaders.director.startScene("about");
        }

        if ((ke.symbol == Keys.F12) || (ke.symbol == Keys.e)) {
            DrunkInvaders.director.getGame().startEditor();
        }

        return false;
    }

}
