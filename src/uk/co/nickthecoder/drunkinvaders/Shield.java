/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.drunkinvaders;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Behaviour;

public class Shield extends Behaviour implements Shootable
{
    public Shield()
    {
    }

    @Override
    public void onAttach()
    {
        this.getActor().addTag("killable");
        this.getActor().addTag("shootable");
        this.collisionStrategy = DrunkInvaders.game.createCollisionStrategy(this.getActor());
    }

    @Override
    public void onKill()
    {
        if (this.collisionStrategy != null) {
            this.collisionStrategy.remove();
            this.collisionStrategy = null;
        }
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
