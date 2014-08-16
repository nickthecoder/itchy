/*******************************************************************************
 * Copyright (c) 2014 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.role;

import uk.co.nickthecoder.itchy.Actor;

public class ExplosionBuilder extends Explosion.AbstractExplosionBuilder<Explosion, ExplosionBuilder>
{
    public ExplosionBuilder( Actor actor )
    {
        companion = new Explosion( actor );
    }
    public ExplosionBuilder getThis()
    {
        return this;
    }
}

