/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.drunkinvaders;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Costume;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.extras.Timer;
import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.itchy.property.DoubleProperty;
import uk.co.nickthecoder.itchy.property.IntegerProperty;
import uk.co.nickthecoder.itchy.property.StringProperty;
import uk.co.nickthecoder.itchy.util.Tag;
import uk.co.nickthecoder.itchy.util.Util;

@Tag(names = { "deadly", "shootable" })
public class Mothership extends Alien
{
    protected static final List<AbstractProperty<Role, ?>> properties = new ArrayList<AbstractProperty<Role, ?>>();

    static {
        properties.add(new IntegerProperty<Role>("childrenCount"));
        properties.add(new StringProperty<Role>("costumeName").label("Childrens' Costume"));
        properties.add(new DoubleProperty<Role>("birthInterval").hint("seconds"));
        properties.add(new DoubleProperty<Role>("childFireOnceEvery"));
        properties.add(new DoubleProperty<Role>("firstBornDelay").hint("seconds"));
    }

    public static final String[] SHOOTABLE_LIST = new String[] { "shootable" };

    /**
     * The number of aliens to create
     */
    public int childrenCount;

    public String costumeName;

    /**
     * The time in seconds between children being born.
     */
    public double birthInterval = 1.0;

    /**
     * The children's average duration is seconds between bombs
     */
    public double childFireOnceEvery = 1;

    /**
     * How long in seconds for the first child to be born after the mothership is activated.
     */
    public double firstBornDelay = 0;

    private Timer firstBornTimer;

    private Timer birthTimer;

    @Override
    public List<AbstractProperty<Role, ?>> getProperties()
    {
        return properties;
    }

    @Override
    public void onBirth()
    {
        super.onBirth();
        this.firstBornTimer = Timer.createTimerSeconds(this.firstBornDelay);
    }

    @Override
    public void tick()
    {
        super.tick();

        if (this.firstBornTimer.isFinished()) {
            if (this.birthTimer == null) {
                this.birthTimer = Timer.createTimerSeconds(this.birthInterval);

            } else if (this.birthTimer.isFinished()) {
                if (this.childrenCount > 0) {
                    this.childrenCount--;
                    giveBirth();
                    this.birthTimer.reset();
                }
            }
        }

    }

    public void giveBirth()
    {
        this.event("giveBirth");

        Costume costume = DrunkInvaders.director.getGame().resources.getCostume(this.costumeName);
        Actor alienActor = new Actor(costume);
        alienActor.setDirection(getActor().getAppearance().getDirection());
        Alien alien = new Alien();
        alien.fireOnceEvery = this.childFireOnceEvery;
        alienActor.setRole(alien);

        alien.vx = Util.randomBetween(-0.2, 0.2) + this.vx;
        alienActor.moveTo(getActor().getX(), getActor().getY());
        if (getActor().getY() < 200) {
            alienActor.moveBy(0, 10);
            alien.vy = this.vy + 1;
        } else {
            alienActor.moveBy(0, -10);
            alien.vy = this.vy - 1;
        }

        alienActor.setZOrder(getActor().getZOrder());
        getActor().getStage().add(alienActor);
        alienActor.event("dropped");
    }

}
