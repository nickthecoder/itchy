/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.drunkinvaders;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.util.Tag;

@Tag(names = { "killable", "shootable" })
public class Shield extends Bouncy implements Shootable
{
    public Shield()
    {
        this.mass = 10000;
    }

    @Override
    public void shot( Actor by )
    {
        this.deathEvent("shot");
    }

    @Override
    public void tick()
    {
        // If we have bounced, then kill ourselves.
        if ((this.vx != 0) || (this.vy!=0)) {
            this.getActor().kill();
        }
    }
}
