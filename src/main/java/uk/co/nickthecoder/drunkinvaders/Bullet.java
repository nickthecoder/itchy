/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.drunkinvaders;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.AbstractRole;
import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.StageView;
import uk.co.nickthecoder.itchy.property.DoubleProperty;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.StringProperty;

public class Bullet extends AbstractRole implements Shootable
{
    protected static final List<Property<Role, ?>> properties = new ArrayList<Property<Role, ?>>();

    static {
        properties.add(new DoubleProperty<Role>("speed"));
        properties.add(new StringProperty<Role>("targetTagName"));
    }

    public double speed = 5.0;

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
    public List<Property<Role, ?>> getProperties()
    {
        return properties;
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

        StageView mainView = Itchy.getGame().getLayout().findStageView("main");
        if (!mainView.getVisibleRectangle().overlaps(getActor().getAppearance().getWorldRectangle())) {
            getActor().kill();
        }

        getCollisionStrategy().update();

        for (Role otherRole : getCollisionStrategy().collisions(getActor(), new String[] {this.targetTagName})) {
            ((Shootable) otherRole).shot(getActor());
            getActor().kill();

            break;
        }
    }
}
