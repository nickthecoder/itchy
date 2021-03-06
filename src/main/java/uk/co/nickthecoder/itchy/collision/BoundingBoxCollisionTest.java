/*******************************************************************************
 * Copyright (c) 2014 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.collision;

import uk.co.nickthecoder.itchy.Actor;

public class BoundingBoxCollisionTest implements CollisionTest
{
    public static final BoundingBoxCollisionTest instance = new BoundingBoxCollisionTest();
    
    private BoundingBoxCollisionTest()
    {
    }
    
    @Override
    public boolean collided( Actor a, Actor b )
    {
        return a.overlapping(b);
    }

}

