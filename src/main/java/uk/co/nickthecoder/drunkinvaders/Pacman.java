/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.drunkinvaders;

import uk.co.nickthecoder.itchy.AbstractRole;
import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Role;

public class Pacman extends AbstractRole
{
    @Override
    public void onBirth()
    {
        addTag("deadly");
    }

    @Override
    public void tick()
    {
        getCollisionStrategy().update();

        for (Role role : collisions(Alien.SHOOTABLE_LIST)) {
            Actor other = role.getActor();

            if ((getActor() != other) && (!role.hasTag("bouncy"))) {
                ((Shootable) other.getRole()).shot(getActor());
            }
        }
    }
}
