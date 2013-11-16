/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.drunkinvaders;

import uk.co.nickthecoder.itchy.AbstractRole;
import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.util.Tag;

@Tag(names = { "killable", "shootable" })
public class Shield extends AbstractRole implements Shootable
{
    public Shield()
    {
    }

    @Override
    public void onAttach()
    {
        super.onAttach();

        getActor().setCollisionStrategy(DrunkInvaders.director.createCollisionStrategy(getActor()));
    }

    @Override
    public void onDetach()
    {
        super.onDetach();

        getActor().resetCollisionStrategy();
    }

    @Override
    public void shot( Actor by )
    {
        this.deathEvent("shot");
    }

    @Override
    public void tick()
    {
    }
}
