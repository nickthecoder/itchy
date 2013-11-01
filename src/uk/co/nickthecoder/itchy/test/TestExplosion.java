/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.test;

import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.extras.Explosion;
import uk.co.nickthecoder.itchy.util.Property;

public class TestExplosion extends Behaviour
{
    @Property(label = "Test ID")
    public int id;

    @Property(label = "Offset Forwards")
    public double offsetForwards;

    @Property(label = "Offset Sidewards")
    public double offsetSidewards;

    @Property(label = "Offset X")
    public double offsetX;

    @Property(label = "Offset Y")
    public double offsetY;

    @Property(label = "Life")
    public double life;

    @Property(label = "X Velocity")
    public double vx;

    @Property(label = "Y Velocity")
    public double vy;

    @Property(label = "Speed Forwards")
    public double speedForwards;

    @Property(label = "Speed Sideways")
    public double speedSidewards;

    @Property(label = "Gravity")
    public double gravity;

    @Property(label = "Spin")
    public double spin;

    @Property(label = "Fade")
    public double fade;

    @Property(label = "growFactor")
    public double growFactor = 1;

    @Property(label = "alpha")
    public double alpha = 255;

    @Property(label = "projectiles")
    public int projectiles = 10;

    @Property(label = "projectilesPerTick")
    public int projectilesPerTick = -1;

    @Property(label = "Heading From")
    public double spreadFrom = 0;

    @Property(label = "Heading To")
    public double spreadTo = 360;

    @Property(label = "Spread")
    public boolean randomSpread = false;

    @Override
    public void tick()
    {
        test0().createActor().activate();
        this.getActor().deactivate();
    }

    private Explosion test0()
    {
        Explosion result = new Explosion(this)
            .offset(this.offsetX, this.offsetY)
            .offsetForwards(this.offsetForwards).offsetSidewards(this.offsetSidewards)
            .speed(this.speedForwards, this.speedSidewards).vx(this.vx).vy(this.vy).gravity(this.gravity)
            .alpha(this.alpha).life(this.life)
            .fade(this.fade).spin(this.spin).pose("bomb")
            .projectiles(this.projectiles).rotate()
            .spread(this.spreadFrom, this.spreadTo).randomSpread(this.randomSpread);

        if (this.projectilesPerTick != -1) {
            result.projectilesPerTick(this.projectilesPerTick);
        }


        return result;
    }


}
