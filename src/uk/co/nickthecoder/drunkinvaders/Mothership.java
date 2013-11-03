/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.drunkinvaders;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Costume;
import uk.co.nickthecoder.itchy.extras.Timer;
import uk.co.nickthecoder.itchy.util.Property;
import uk.co.nickthecoder.itchy.util.Tag;
import uk.co.nickthecoder.itchy.util.Util;

@Tag(names = {"deadly","shootable"})
public class Mothership extends Alien
{
    public static final String[] SHOOTABLE_LIST = new String[] { "shootable" };

    /**
     * The number of aliens to create
     */
    @Property(label = "Children")
    public int childrenCount;

    @Property(label = "Children's Costume")
    public String costumeName;

    /**
     * The time in seconds between children being born.
     */
    @Property(label = "Birth Interval (s)")
    public double birthInterval = 1.0;

    /**
     * The children's average duration is seconds between bombs
     */
    @Property(label = "Child Fire Once Every (s)")
    public double childFireOnceEvery = 1;

    /**
     * How long in seconds for the first child to be born after the mothership is activated.
     */
    @Property(label = "First Born Delay (s)")
    public double firstBornDelay = 0;

    private Timer firstBornTimer;

    private Timer birthTimer;

    @Override
    public void init()
    {
        super.init();
        this.firstBornTimer = Timer.createTimerSeconds(this.firstBornDelay);
    }

    @Override
    public void onAttach()
    {
        super.onAttach();
        this.collisionStrategy = DrunkInvaders.game.createCollisionStrategy(this.getActor());
    }
    
    @Override
    public void onDetach()
    {
        super.onDetach();
        resetCollisionStrategy();
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

        Costume costume = DrunkInvaders.game.resources.getCostume(this.costumeName);
        Actor alien = new Actor(costume);
        alien.setDirection(this.getActor().getAppearance().getDirection());
        Alien alienBehaviour = new Alien();
        alienBehaviour.fireOnceEvery = this.childFireOnceEvery;
        alien.setBehaviour(alienBehaviour);

        alienBehaviour.vx = Util.randomBetween(-0.2, 0.2) + this.vx;
        alien.moveTo(this.getActor().getX(), this.getActor().getY());
        if (this.getActor().getY() < 200) {
            alien.moveForwards(5, 0);
            alienBehaviour.vy = this.vy + 1;
        } else {
            alien.moveForwards(-5, 0);
            alienBehaviour.vy = this.vy - 1;
        }

        this.getActor().getLayer().addTop(alien);
        alien.activate();
        alien.event("dropped");
    }

}
