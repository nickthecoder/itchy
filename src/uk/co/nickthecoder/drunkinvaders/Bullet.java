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
import uk.co.nickthecoder.itchy.util.Property;

public class Bullet extends Behaviour implements Shootable
{
    @Property(label="Speed")
    public double speed = 5.0;

    @Property(label="Target Tag")
    public String targetTagName;

    
    
    public Bullet()
    {
        this("shootable");
    }

    public Bullet( String tagName )
    {
        super();
        this.targetTagName = tagName;
    }

    @Override
    public void onAttach()
    {
        super.onAttach();
        this.collisionStrategy = DrunkInvaders.game.createCollisionStrategy(this.getActor());
    }
    
    @Override
    public void onDetach()
    {
        super.onDetach();
        resetCollisionStrategy();
    }

    @Override
    public void shot( Actor by )
    {
        this.deathEvent("shot");
    }

    @Override
    public void tick()
    {
        this.getActor().moveForwards(this.speed);

        if (!this.getActor().isOnScreen()) {
            this.getActor().kill();
        }

        this.collisionStrategy.update();

        for (Actor other : pixelOverlap(this.targetTagName)) {
            ((Shootable) other.getBehaviour()).shot(this.getActor());
            this.getActor().kill();

            break;
        }
    }
}
