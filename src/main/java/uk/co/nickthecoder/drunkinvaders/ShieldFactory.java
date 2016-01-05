/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.drunkinvaders;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.AbstractRole;
import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Pose;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.IntegerProperty;

public class ShieldFactory extends AbstractRole
{
    protected static final List<Property<Role, ?>> properties = new ArrayList<Property<Role, ?>>();

    static {
        properties.add(new IntegerProperty<Role>("width"));
        properties.add(new IntegerProperty<Role>("height"));
        properties.add(new IntegerProperty<Role>("spacing"));
    }

    public int width = 6;

    public int height = 4;

    public double spacing = 10;

    @Override
    public List<Property<Role, ?>> getProperties()
    {
        return properties;
    }
    
    @Override
    public void tick()
    {
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {

                String poseName = "full";
                if (y == this.height - 1) {
                    if (x == 0) {
                        poseName = "left";
                    } else if (x == this.width - 1) {
                        poseName = "right";
                    }
                }
                Pose pose = getActor().getCostume().getPose(poseName);

                Actor shieldActor = new Actor(pose);
                Shield shield = new Shield();
                shieldActor.setDirection(getActor().getAppearance().getDirection());
                shieldActor.setZOrder(getActor().getZOrder() + 1);
                getActor().getStage().add(shieldActor);
                shieldActor.moveTo(getActor().getX(), getActor().getY());
                shieldActor.moveForwards(x * this.spacing, y * this.spacing);
                shieldActor.setRole(shield);

            }
        }

        getActor().kill();

    }

}
