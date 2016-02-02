/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.drunkinvaders;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.AbstractRole;
import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Appearance;
import uk.co.nickthecoder.itchy.Costume;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.extras.Timer;
import uk.co.nickthecoder.itchy.property.DoubleProperty;
import uk.co.nickthecoder.itchy.property.IntegerProperty;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.StringProperty;
import uk.co.nickthecoder.itchy.util.Util;

public class AlienFactory extends AbstractRole
{
    protected static final List<Property<Role, ?>> properties = new ArrayList<Property<Role, ?>>();

    static {
        properties.add(new StringProperty<Role>("costumeName"));
        properties.add(new DoubleProperty<Role>("delayPerAlien"));
        properties.add(new IntegerProperty<Role>("alienCount"));
        properties.add(new DoubleProperty<Role>("spacing"));
        properties.add(new DoubleProperty<Role>("fireOnceEvery").hint("seconds"));
    }

    public String costumeName;

    public double delayPerAlien = 0.500;

    public int alienCount = 6;

    public double spacing = 80;

    public double fireOnceEvery = 1; // The aliens' average number of seconds between bombs

    private List<Alien> aliens;

    private Timer timer;

    @Override
    public List<Property<Role, ?>> getProperties()
    {
        return properties;
    }

    @Override
    public void onBirth()
    {
        super.onBirth();
        this.aliens = new ArrayList<Alien>(this.alienCount);
        this.timer = Timer.createTimerSeconds(this.delayPerAlien);
    }

    @Override
    public void tick()
    {
        getActor().getAppearance().setAlpha(0);

        if (this.timer.isFinished()) {
            this.timer.reset();
            createAlien();

            if (this.aliens.size() == this.alienCount) {
                for (Alien ab : this.aliens) {
                    ab.vx = Util.randomBetween(2, 2.2);
                    ab.vy = Util.randomBetween(0, 0.6);
                }
                getActor().kill();

            }
        }

    }

    private void createAlien()
    {
        Costume costume = DrunkInvaders.director.getGame().resources.getCostume(this.costumeName);
        Actor alienActor = new Actor(costume);
        Appearance alienAppearance = alienActor.getAppearance();
        Appearance thisAppearance = getActor().getAppearance();

        alienActor.setDirection(getActor().getAppearance().getDirection() - 90);
        alienAppearance.setScale(thisAppearance.getScale());
        alienAppearance.setAlpha(0);

        Alien alienRole = new Alien();
        alienRole.fireOnceEvery = this.fireOnceEvery;

        alienActor.moveTo(getActor().getX() + this.aliens.size() * this.spacing, getActor().getY());
        alienActor.setZOrder(getActor().getZOrder());
        getActor().getStage().add(alienActor);

        alienActor.setRole(alienRole);
        alienActor.event("birth");

        this.aliens.add(alienRole);
    }

}
