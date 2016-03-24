/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.collision;

import java.util.List;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.util.Filter;

/**
 * Performs the collision tests using two different CollisionStrategies.
 * Reports any differences to stderr.
 * <p>
 * This is useful when debugging a complex CollisionStrategy. You can compare it to BruteForceCollisionStrategy, which
 * is simple, (but slow).
 * 
 * @priority 5
 */
public class DebugCollisionStrategy implements CollisionStrategy
{

    private CollisionStrategy strategy1;
    private CollisionStrategy strategy2;

    public DebugCollisionStrategy(CollisionStrategy a, CollisionStrategy b)
    {
        this.strategy1 = a;
        this.strategy2 = b;
    }

    @Override
    public List<Role> collisions(Actor actor, String[] includeTags, int maxResults, Filter<Role> filter)
    {
        List<Role> results1 = this.strategy1.collisions(actor, includeTags, maxResults, filter);
        List<Role> results2 = this.strategy2.collisions(actor, includeTags, maxResults, filter);

        if (!results1.equals(results2)) {
            System.err.println("Pixel Collision failed for " + actor);
            System.err.println("Results1 : " + results1);
            System.err.println("Results2 : " + results2);

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
