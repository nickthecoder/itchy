/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.drunkinvaders;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.animation.NumericAnimation;
import uk.co.nickthecoder.itchy.animation.ScaleAnimation;
import uk.co.nickthecoder.itchy.extras.Explosion;
import uk.co.nickthecoder.itchy.extras.Fragment;
import uk.co.nickthecoder.itchy.extras.Talk;
import uk.co.nickthecoder.itchy.util.Property;
import uk.co.nickthecoder.itchy.util.Tag;
import uk.co.nickthecoder.itchy.util.Util;
import uk.co.nickthecoder.jame.RGBA;

@Tag(names = { "deadly", "shootable" })
public class Alien extends Bouncy implements Shootable
{
    private static RGBA SPEECH_COLOR = new RGBA(0, 0, 0);

    public static final String[] SHOOTABLE_LIST = new String[] { "shootable" };

    @Property(label = "Fire Every (s)")
    public double fireOnceEvery = 1.0; // Average duration between bombs in seconds

    @Property(label = "Shots Required")
    public int shotsRequired = 1;

    public boolean tock = true;

    @Override
    public void onBirth()
    {
        super.onBirth();
        DrunkInvaders.director.addAliens(1);
        new Fragment().actor(getActor()).createPoses("fragment");
    }

    @Override
    public void onDeath()
    {
        super.onDeath();
        DrunkInvaders.director.addAliens(-1);
    }

    @Override
    public void tick()
    {
        if (DrunkInvaders.director.metronomeCountdown == 1) {
            if (getActor().getAnimation() == null) {
                this.tock = !this.tock;
                getActor().event(this.tock ? "tock" : "tick");
            }
        }

        if (Util.randomOnceEvery(this.fireOnceEvery)) {
            this.fire();
        }

        super.tick();

        // This isn't neat - can we have a "killable" tag, which Ship and Shield will both have?
        for (Behaviour otherBehaviour : getActor().pixelOverlap(SHOOTABLE_LIST)) {
            Actor other = otherBehaviour.getActor();
            if ((getActor() != other) && (!otherBehaviour.hasTag("bouncy"))) {
                ((Shootable) other.getBehaviour()).shot(getActor());
            }
        }
    }

    public void fire()
    {
        this.event("fire");

        Actor bullet = new Actor(DrunkInvaders.director.getGame().resources.getCostume("bomb"));
        bullet.moveTo(getActor());
        bullet.setDirection(getActor().getAppearance().getDirection());
        DrunkInvaders.director.mainStage.addTop(bullet);
        bullet.moveForwards(10);
        bullet.setBehaviour(new Bullet("killable"));
    }

    @Override
    public void shot( Actor bullet )
    {

        new Explosion(getActor())
            .projectiles(20)
            .fade(1.2)
            .speed(1, 3)
            .pose("fragment")
            .scale(1)
            .createActor();

        new Explosion(getActor())
            .projectiles(40).projectilesPerTick(10)
            .offsetForwards(-10, 10).offsetSidewards(-10, 10)
            .speed(3, 6)
            .fade(0.5)
            .scale(1)
            .pose("pixel")
            .createActor();

        double scale = getActor().getAppearance().getScale();

        this.shotsRequired--;
        if (this.shotsRequired > 0) {
            event("shot");

            if (scale > 1) {
                double newScale = 1 + (scale - 1) * (this.shotsRequired) / (this.shotsRequired + 1);
                ScaleAnimation scaleAnimation = new ScaleAnimation(10, NumericAnimation.linear, newScale);
                getActor().setAnimation(scaleAnimation);
            }

            return;
        }

        Actor yell = new Talk(this)
            .eventName("yell").color(SPEECH_COLOR).margin(10, 10, 20, 10)
            .bubble("speechBubble")
            .offset(0, 40).direction(0)
            .createActor();
        yell.deathEvent(getActor().getCostume(), "yell");

        removeAllTags();
        this.deathEvent("death");

    }

}
