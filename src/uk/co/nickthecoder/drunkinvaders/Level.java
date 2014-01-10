/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.drunkinvaders;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.PlainSceneDirector;
import uk.co.nickthecoder.itchy.collision.CollisionStrategy;
import uk.co.nickthecoder.itchy.collision.NeighbourhoodCollisionStrategy;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.Keys;
import uk.co.nickthecoder.jame.event.ModifierKey;

public class Level extends PlainSceneDirector
{
    private int aliensRemaining;

    public boolean ending = false;

    public int getAliensRemaining()
    {
        return this.aliensRemaining;
    }

    public void addAliens( int n )
    {
        this.aliensRemaining += n;

        // We only care when the last alien was killed during play, not when fading the scene out.
        if (this.ending) {
            return;
        }

        if (this.aliensRemaining == 0) {
            System.out.println("No aliens remain. Starting next level");
            this.ending = true;
            DrunkInvaders.director.nextLevel();
        }
    }

    @Override
    public boolean onKeyDown( KeyboardEvent ke )
    {
        if (ke.symbol == Keys.ESCAPE) {
            this.ending = true;
            DrunkInvaders.director.startScene("menu");
            return true;
        }

        if (ke.symbol == Keys.p) {
            DrunkInvaders.director.getGame().pause.togglePause();
            return true;
        }

        if ((ke.symbol == Keys.n) && (ke.modifier(ModifierKey.CTRL))) {
            addAliens(-1);
        }

        return false;
    }

    @Override
    public CollisionStrategy getCollisionStrategy( Actor actor )
    {
        return new NeighbourhoodCollisionStrategy(actor, DrunkInvaders.director.neighbourhood);
    }

}
