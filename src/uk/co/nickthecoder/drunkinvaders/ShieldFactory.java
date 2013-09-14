/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.drunkinvaders;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.Pose;
import uk.co.nickthecoder.itchy.util.Property;

public class ShieldFactory extends Behaviour
{
    @Property(label="Width")
    public int width = 6;

    @Property(label="Height")
    public int height = 4;

    @Property(label="Spacing")
    public double spacing = 10;

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
                Pose pose = this.getActor().getCostume().getPose( poseName );                

                Actor shield = new Actor(pose);
                Shield shieldBehaviour = new Shield();
                shield.getAppearance().setDirection(this.getActor().getAppearance().getDirection());
                this.getActor().getLayer().add(shield);
                shield.moveTo(this.getActor().getX(), this.getActor().getY());
                shield.moveForward(x * this.spacing, y * this.spacing);
                shield.setBehaviour(shieldBehaviour);
                shield.activate();

            }
        }

        this.getActor().kill();

    }

}
