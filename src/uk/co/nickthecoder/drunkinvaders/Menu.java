/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.drunkinvaders;

import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.NullSceneBehaviour;
import uk.co.nickthecoder.jame.Keys;
import uk.co.nickthecoder.jame.event.KeyboardEvent;

public class Menu extends NullSceneBehaviour
{
    
    @Override
    public boolean onKeyDown( KeyboardEvent ke )
    {
        if (ke.symbol == Keys.ESCAPE) {
            Itchy.singleton.terminate();
            return true;
        }

        if ((ke.symbol == Keys.p) || (ke.symbol == Keys.RETURN)) {
            DrunkInvaders.game.startScene("levels");
            return true;
        }

        if (ke.symbol == Keys.a) {
            DrunkInvaders.game.startScene("about");
        }

        if (ke.symbol == Keys.w) {
            DrunkInvaders.game.startScene("completed");
        }
        
        return false;
    }

}
