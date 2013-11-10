/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.test;

import uk.co.nickthecoder.itchy.AbstractBehaviour;
import uk.co.nickthecoder.itchy.NullBehaviour;
import uk.co.nickthecoder.itchy.extras.Projectile;
import uk.co.nickthecoder.itchy.util.Property;

public class TestProjectiles extends AbstractBehaviour
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

    @Override
    public void tick()
    {
        if (this.id == 0) {
            test0().createActor();
        } else if (this.id == 1) {
            test1().createActor();
        }
        getActor().setBehaviour(new NullBehaviour());
    }

    private Projectile test0()
    {
        return new Projectile(this)
            .offset(this.offsetX, this.offsetY)
            .offsetForwards(this.offsetForwards).offsetSidewards(this.offsetSidewards)
            .alpha(this.alpha)
            .life(this.life).vx(this.vx).vy(this.vy)
            .speed(this.speedForwards, this.speedSidewards).gravity(this.gravity).spin(this.spin)
            .fade(this.fade).growFactor(this.growFactor).eventName("bomb");
    }

    private Projectile test1()
    {
        return test0().rotate(true);
    }

}
