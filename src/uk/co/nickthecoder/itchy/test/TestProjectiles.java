/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.test;

import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.extras.Projectile;
import uk.co.nickthecoder.itchy.util.Property;

public class TestProjectiles extends Behaviour
{
    @Property(label="Test ID")
    public int id;

    @Override
    public void tick()
    {
        if ( id == 0 ) {
            test0();
        }
        this.getActor().deactivate();
    }
    
    private void test0()
    {
        new Projectile(this).speed(2).startEvent("bomb").createActor().activate();
    }
}
