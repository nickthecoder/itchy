/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.test;

import uk.co.nickthecoder.itchy.AbstractRole;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.role.PlainRole;
import uk.co.nickthecoder.itchy.role.ProjectileBuilder;

public class TestProjectiles extends AbstractRole
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
            test0().create();
        } else if (this.id == 1) {
            test1().create();
        }
        getActor().setRole(new PlainRole());
    }

    private ProjectileBuilder test0()
    {
        return new ProjectileBuilder(getActor())
            .offset(this.offsetX, this.offsetY)
            .offsetForwards(this.offsetForwards).offsetSidewards(this.offsetSidewards)
            .alpha(this.alpha)
            .life(this.life).vx(this.vx).vy(this.vy)
            .speed(this.speedForwards, this.speedSidewards).gravity(this.gravity).spin(this.spin)
            .fade(this.fade).growFactor(this.growFactor).eventName("bomb");
    }

    private ProjectileBuilder test1()
    {
        return test0().rotate(true);
    }

}