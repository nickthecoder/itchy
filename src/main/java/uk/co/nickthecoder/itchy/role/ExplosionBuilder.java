/*******************************************************************************
 * Copyright (c) 2014 Nick Robinson All rights reserved. This program and the
 * accompanying materials are made available under the terms of the GNU Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.role;

import uk.co.nickthecoder.itchy.Actor;

/**
 * This is a "fluent" API for the Explosion class. Explosions are very flexible, and therefore have a vast quantity of attributes.
 * A fluent API allows for "method chaining". Here's an example :
 * 
 * <pre>
 * <code>
 * new ExplosionBuilder(actor)
 *     .projectiles(10).gravity(-0.2).fade(0.9, 3.5).speed(0.1, 1.5).vy(5)
 *     .pose("droplet").create(); 
 * </code>
 * </pre>
 * 
 * Note: the create method returns an Explosion (not the ExplosionBuilder), and therefore must be last.
 */
public class ExplosionBuilder extends Explosion.AbstractExplosionBuilder<Explosion, ExplosionBuilder>
{
    public ExplosionBuilder( Actor actor )
    {
        this.companion = new Explosion(actor);
    }

    @Override
    public ExplosionBuilder getThis()
    {
        return this;
    }
}
