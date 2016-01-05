/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.test;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.AbstractRole;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.DoubleProperty;
import uk.co.nickthecoder.itchy.property.IntegerProperty;
import uk.co.nickthecoder.itchy.role.ExplosionBuilder;
import uk.co.nickthecoder.itchy.role.PlainRole;

public class TestExplosion extends AbstractRole
{
    protected static final List<Property<Role, ?>> properties = new ArrayList<Property<Role, ?>>();

    static {
        properties.add(new IntegerProperty<Role>("id"));
        properties.add(new DoubleProperty<Role>("offsetForwards"));
        properties.add(new DoubleProperty<Role>("offsetSidewards"));
        properties.add(new DoubleProperty<Role>("offsetX"));
        properties.add(new DoubleProperty<Role>("offsetY"));
        properties.add(new DoubleProperty<Role>("distance"));
        properties.add(new DoubleProperty<Role>("life"));
        properties.add(new DoubleProperty<Role>("vx"));
        properties.add(new DoubleProperty<Role>("vy"));
        properties.add(new DoubleProperty<Role>("speedForwards"));
        properties.add(new DoubleProperty<Role>("speedSidewards"));
        properties.add(new DoubleProperty<Role>("gravity"));
        properties.add(new DoubleProperty<Role>("spinFrom"));
        properties.add(new DoubleProperty<Role>("spinTo"));
        properties.add(new DoubleProperty<Role>("fade"));
        properties.add(new DoubleProperty<Role>("growFactor"));
        properties.add(new DoubleProperty<Role>("alpha"));
        properties.add(new IntegerProperty<Role>("projectiles"));
        properties.add(new DoubleProperty<Role>("projectilesPerTick"));
        properties.add(new DoubleProperty<Role>("spreadFrom"));
        properties.add(new DoubleProperty<Role>("spreadTo"));
        properties.add(new DoubleProperty<Role>("randomSpread"));
        properties.add(new IntegerProperty<Role>("deltaZ"));
    }

    public int id;

    public double offsetForwards;

    public double offsetSidewards;

    public double offsetX;

    public double offsetY;

    public double distance;

    public double life;

    public double vx;

    public double vy;

    public double speedForwards;

    public double speedSidewards;

    public double gravity;

    public double spinFrom;

    public double spinTo;

    public double fade;

    public double growFactor = 1;

    public double alpha = 255;

    public int projectiles = 10;

    public int projectilesPerTick = -1;

    public double spreadFrom = 0;

    public double spreadTo = 360;

    public boolean randomSpread = false;

    public int deltaZ = 0;

    @Override
    public List<Property<Role, ?>> getProperties()
    {
        return properties;
    }

    @Override
    public void tick()
    {
        test0().create();
        getActor().setRole(new PlainRole());
    }

    private ExplosionBuilder test0()
    {
        ExplosionBuilder result = new ExplosionBuilder(getActor()).distance(this.distance)
                        .offset(this.offsetX, this.offsetY).offsetForwards(this.offsetForwards)
                        .offsetSidewards(this.offsetSidewards).speed(this.speedForwards, this.speedSidewards)
                        .vx(this.vx).vy(this.vy).gravity(this.gravity).alpha(this.alpha).life(this.life)
                        .fade(this.fade).spin(this.spinFrom, this.spinTo).rotate().adjustZOrder(this.deltaZ)
                        .pose("bomb").projectiles(this.projectiles).spread(this.spreadFrom, this.spreadTo)
                        .randomSpread(this.randomSpread);

        if (this.projectilesPerTick != -1) {
            result.projectilesPerTick(this.projectilesPerTick);
        }

        return result;
    }

}
