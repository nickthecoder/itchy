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

public class Pacman extends Behaviour
{

    @Override
    public void onAttach()
    {
        super.onAttach();
        
        this.getActor().addTag("deadly");
        this.collisionStrategy = DrunkInvaders.game.createCollisionStrategy(this.getActor());
    }
    @Override
    public void onDetach()
    {
        super.onDetach();
        
        this.getActor().removeTag("deadly");
        resetCollisionStrategy();
    }
    
    @Override
    public void tick()
    {
        this.collisionStrategy.update();
        
        for (Actor other : touching(Alien.SHOOTABLE_LIST)) {
            if ((this.getActor() != other) && (!other.hasTag("bouncy"))) {
                ((Shootable) other.getBehaviour()).shot(this.getActor());
            }
        }
    }
}
