/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.drunkinvaders;

import uk.co.nickthecoder.itchy.AbstractBehaviour;
import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.util.Tag;

@Tag(names = {"deadly"})
public class Pacman extends AbstractBehaviour
{

    @Override
    public void onAttach()
    {
        super.onAttach();
        
        getActor().setCollisionStrategy(DrunkInvaders.game.createCollisionStrategy(getActor()));
    }
    @Override
    public void onDetach()
    {
        super.onDetach();
        
        getActor().resetCollisionStrategy();
    }
    
    @Override
    public void tick()
    {
        getActor().getCollisionStrategy().update();
        
        for (Behaviour behaviour : getActor().pixelOverlap(Alien.SHOOTABLE_LIST)) {
            Actor other = behaviour.getActor();
            
            if ((getActor() != other) && (!behaviour.hasTag("bouncy"))) {
                ((Shootable) other.getBehaviour()).shot(getActor());
            }
        }
    }
}
