/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.drunkinvaders;

import uk.co.nickthecoder.itchy.AbstractRole;
import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.property.Property;

public class Bullet extends AbstractRole implements Shootable
{
    @Property(label = "Speed")
    public double speed = 5.0;

    @Property(label = "Target Tag")
    public String targetTagName;

    public Bullet()
    {
        this("shootable");
    }

    public Bullet( String tagName )
    {
        super();
        this.targetTagName = tagName;
    }

    @Override
    public void shot( Actor by )
    {
        this.deathEvent("shot");
    }

    @Override
    public void tick()
    {
        getActor().moveForwards(this.speed);

        if (!DrunkInvaders.director.mainView.getVisibleRectangle().overlaps(getActor().getAppearance().getWorldRectangle())) {
            getActor().kill();
        }

        getCollisionStrategy().update();

        for (Role otherRole : getCollisionStrategy().collisions(getActor(),this.targetTagName)) {
            ((Shootable) otherRole).shot(getActor());
            getActor().kill();

            break;
        }
    }
}