/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.test;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.AbstractRole;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.itchy.property.DoubleProperty;
import uk.co.nickthecoder.itchy.property.IntegerProperty;
import uk.co.nickthecoder.itchy.role.PlainRole;
import uk.co.nickthecoder.itchy.role.ProjectileBuilder;

public class TestProjectiles extends AbstractRole
{
    protected static final List<AbstractProperty<Role, ?>> properties = new ArrayList<AbstractProperty<Role, ?>>();

    static {
        properties.add(new IntegerProperty<Role>("id"));
        properties.add(new DoubleProperty<Role>("offsetForwards"));
        properties.add(new DoubleProperty<Role>("offsetSidewards"));
        properties.add(new DoubleProperty<Role>("offsetX"));
        properties.add(new DoubleProperty<Role>("offsetY"));
        properties.add(new DoubleProperty<Role>("life"));
        properties.add(new DoubleProperty<Role>("vx"));
        properties.add(new DoubleProperty<Role>("vy"));
        properties.add(new DoubleProperty<Role>("speedForwards"));
        properties.add(new DoubleProperty<Role>("speedSidewards"));
        properties.add(new DoubleProperty<Role>("gravity"));
        properties.add(new DoubleProperty<Role>("spin"));
        properties.add(new DoubleProperty<Role>("fade"));
        properties.add(new DoubleProperty<Role>("growFactor"));
        properties.add(new DoubleProperty<Role>("alpha"));
    }

    public int id;

    public double offsetForwards;

    public double offsetSidewards;

    public double offsetX;

    public double offsetY;

    public double life;

    public double vx;

    public double vy;

    public double speedForwards;

    public double speedSidewards;

    public double gravity;

    public double spin;

    public double fade;

    public double growFactor = 1;

    public double alpha = 255;

    @Override
    public List<AbstractProperty<Role, ?>> getProperties()
    {
        return properties;
    }

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
        return new ProjectileBuilder(getActor()).offset(this.offsetX, this.offsetY).offsetForwards(this.offsetForwards)
                        .offsetSidewards(this.offsetSidewards).alpha(this.alpha).life(this.life).vx(this.vx)
                        .vy(this.vy).speed(this.speedForwards, this.speedSidewards).gravity(this.gravity)
                        .spin(this.spin).fade(this.fade).growFactor(this.growFactor).eventName("bomb");
    }

    private ProjectileBuilder test1()
    {
        return test0().rotate(true);
    }

}
