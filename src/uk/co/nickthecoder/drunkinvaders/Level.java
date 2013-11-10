/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.drunkinvaders;

import uk.co.nickthecoder.itchy.PlainSceneBehaviour;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.Keys;

public class Level extends PlainSceneBehaviour
{
    
    @Override
    public boolean onKeyDown( KeyboardEvent ke )
    {
        if (ke.symbol == Keys.ESCAPE) {
            DrunkInvaders.game.startScene("menu");
            return true;
        }
    
        if (ke.symbol == Keys.p) {
            DrunkInvaders.game.pause.togglePause();
            return true;
        }
        
        return false;
    }

}
