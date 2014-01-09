/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.neighbourhood;

import java.util.Set;

import uk.co.nickthecoder.drunkinvaders.DrunkInvaders;
import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.ActorCollisionStrategy;
import uk.co.nickthecoder.itchy.Role;

public class DebugCollisionStrategy extends ActorCollisionStrategy
{

    private SinglePointCollisionStrategy strategy1;
    private ActorCollisionStrategy strategy2;

    public DebugCollisionStrategy( SinglePointCollisionStrategy a, ActorCollisionStrategy b )
    {
        super(a.getActor());

        this.strategy1 = a;
        this.strategy2 = b;
    }


    private static final String[] EMPTY = {};
    
    @Override
    public Set<Role> collisions( Actor actor, String... includeTags )
    {
        return collisions(actor, includeTags, EMPTY );
    }
    
    @Override
    public Set<Role> collisions( Actor actor, String[] includeTags, String[] excludeTags )
    {
        Set<Role> results1 = this.strategy1.collisions(includeTags, excludeTags);
        Set<Role> results2 = this.strategy2.collisions(includeTags, excludeTags);

        if (!results1.equals(results2)) {
            System.err.println("Pixel Collision failed for " + getActor());
            System.err.println("Results1 : " + results1);
            System.err.println("Results2 : " + results2);
            DrunkInvaders.director.debug();

            System.err.println("Source actor's square : " + this.strategy1.getSquare());
            this.strategy1.getSquare().debug();

            System.exit(1);
        }
        return results1;
    }

    @Override
    public void update()
    {
        this.strategy1.update();
        this.strategy2.update();
    }

    @Override
    public void remove()
    {
        this.strategy1.remove();
        this.strategy2.remove();
    }
}
