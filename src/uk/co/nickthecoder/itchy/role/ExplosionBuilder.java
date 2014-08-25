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
 *     .pose("droplet").createActor(); 
 * </code>
 * </pre>
 * 
 * And is the same as this long winded form :
 * 
 * <pre>
 * <code>
 * Explosion explosion = new itchy.extras.Explosion(actor);
 * explosion.projectiles = 10;
 * explosion.gravity = -0.2;
 * explosion.setFade(0.9, 3.5);
 * explosion.setSpeed(0.1, 1.5);
 * explosion.vy(5);
 * explosion.setPose("droplet");
 * explosion.createActor();
 * </code>
 * </pre>
 * 
 * Note that the createActor method returns an Actor (not the Explosion), and therefore must be last.
 * 
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
