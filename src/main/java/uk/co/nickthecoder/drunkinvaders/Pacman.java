/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.drunkinvaders;

import uk.co.nickthecoder.itchy.AbstractRole;
import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.util.Tag;

@Tag(names = { "deadly" })
public class Pacman extends AbstractRole
{
    @Override
    public void tick()
    {
        getCollisionStrategy().update();

        for (Role role : getCollisionStrategy().collisions(getActor(),Alien.SHOOTABLE_LIST)) {
            Actor other = role.getActor();

            if ((getActor() != other) && (!role.hasTag("bouncy"))) {
                ((Shootable) other.getRole()).shot(getActor());
            }
        }
    }
}
