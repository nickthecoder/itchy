/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.drunkinvaders;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Appearance;
import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.Costume;
import uk.co.nickthecoder.itchy.extras.Timer;
import uk.co.nickthecoder.itchy.util.Property;
import uk.co.nickthecoder.itchy.util.Util;

public class AlienFactory extends Behaviour
{

    @Property(label = "Costume")
    public String costumeName;

    @Property(label = "Delay per Alien")
    public double delayPerAlien = 0.500;

    @Property(label = "Aliens")
    public int alienCount = 6;

    @Property(label = "Spacing")
    public double spacing = 80;

    @Property(label = "Fire Once Every (s)")
    public double fireOnceEvery = 1; // The aliens' average number of seconds between bombs

    private List<Alien> aliens;

    private Timer timer;

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
        this.getActor().getAppearance().setAlpha(0);

        if (this.timer.isFinished()) {
            this.timer.reset();
            createAlien();

            if (this.aliens.size() == this.alienCount) {
                for (Alien ab : this.aliens) {
                    ab.vx = Util.randomBetween(2, 2.2);
                    ab.vy = Util.randomBetween(0, 0.6);
                }
                this.getActor().kill();

            }
        }

    }

    private void createAlien()
    {
        Costume costume = DrunkInvaders.game.resources.getCostume(this.costumeName);
        Actor alienActor = new Actor(costume);
        Appearance alienAppearance = alienActor.getAppearance();
        Appearance thisAppearance = this.getActor().getAppearance();

        alienActor.setDirection(this.getActor().getAppearance().getDirection() - 90);
        alienAppearance.setScale(thisAppearance.getScale());
        alienAppearance.setAlpha(0);

        Alien alienBehaviour = new Alien();
        alienBehaviour.fireOnceEvery = this.fireOnceEvery;

        alienActor.moveTo(this.getActor().getX() + this.aliens.size() * this.spacing, this
            .getActor().getY());
        this.getActor().getLayer().addTop(alienActor);

        alienActor.setBehaviour(alienBehaviour);
        alienActor.event("birth");

        this.aliens.add(alienBehaviour);
    }

}
