/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.drunkinvaders;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.animation.NumericAnimation;
import uk.co.nickthecoder.itchy.animation.ScaleAnimation;
import uk.co.nickthecoder.itchy.extras.Explosion;
import uk.co.nickthecoder.itchy.extras.Fragment;
import uk.co.nickthecoder.itchy.extras.Talk;
import uk.co.nickthecoder.itchy.util.Property;
import uk.co.nickthecoder.itchy.util.Tag;
import uk.co.nickthecoder.itchy.util.Util;
import uk.co.nickthecoder.jame.RGBA;

@Tag(names = {"deadly","shootable"})
public class Alien extends Bouncy implements Shootable
{
    private static RGBA SPEECH_COLOR = new RGBA(0, 0, 0);

    public static final String[] SHOOTABLE_LIST = new String[] { "shootable" };

    @Property(label = "Fire Every (s)")
    public double fireOnceEvery = 1.0; // Average duration between bombs in seconds

    @Property(label = "Shots Required")
    public int shotsRequired = 1;

    public boolean tock = true;

    public void init()
    {
        // Create the fragments for the explosions when I get shot.
        new Fragment().actor(this.getActor()).createPoses("fragment");        
    }

    @Override
    public void onActivate()
    {
        super.onActivate();
        DrunkInvaders.game.addAliens(1);
    }

    @Override
    public void onDeactivate()
    {
        super.onActivate();
        DrunkInvaders.game.addAliens(-1);
    }

    @Override
    public void onKill()
    {
        super.onKill();
        resetCollisionStrategy();
    }

    @Override
    public void tick()
    {
        if (this.getActor().isDying()) {
            return;
        }

        if (DrunkInvaders.game.metronomeCountdown == 1) {
            if (this.getActor().getAnimation() == null) {
                this.tock = !this.tock;
                this.getActor().event(this.tock ? "tock" : "tick");
            }
        }

        if (Util.randomOnceEvery(this.fireOnceEvery)) {
            this.fire();
        }

        super.tick();

        for (Actor other : pixelOverlap(SHOOTABLE_LIST)) {
            if ((this.getActor() != other) && (!other.hasTag("bouncy"))) {
                ((Shootable) other.getBehaviour()).shot(this.getActor());
            }
        }
    }

    public void fire()
    {
        this.event("fire");

        Actor bullet = new Actor(DrunkInvaders.game.resources.getCostume("bomb"));
        bullet.moveTo(this.getActor());
        bullet.setDirection(this.getActor().getAppearance().getDirection());
        DrunkInvaders.game.mainLayer.addTop(bullet);
        bullet.moveForwards(10);
        bullet.setBehaviour(new Bullet("killable"));
        bullet.activate();
    }

    @Override
    public void shot( Actor bullet )
    {

        new Explosion(this.getActor())
            .projectiles(20)
            .fade(1.2)
            .speed(1, 3)
            .pose("fragment")
            .createActor().activate();

        new Explosion(this.getActor())
            .projectiles(40).projectilesPerTick(10)
            .offsetForwards(-10, 10).offsetSidewards(-10, 10)
            .speed(3, 6)
            .fade(0.5)
            .pose("pixel")
            .createActor()
            .activate();

        double scale = this.getActor().getAppearance().getScale();

        this.shotsRequired--;
        if (this.shotsRequired > 0) {
            event("shot");

            if (scale > 1) {
                double newScale = 1 + (scale -1) * (this.shotsRequired ) / ( this.shotsRequired + 1 );
                ScaleAnimation scaleAnimation = new ScaleAnimation(10, NumericAnimation.linear, newScale);
                this.getActor().setAnimation(scaleAnimation);
            }

            return;
        }

        Actor yell = new Talk(this)
            .eventName("yell").color(SPEECH_COLOR).margin(10, 10, 20, 10)
            .bubble("speechBubble")
            .offset(0, 40).direction(0)
            .createActor();
        yell.activate();
        yell.deathEvent(this.getActor().getCostume(), "yell");

        this.getActor().removeAllTags();
        this.deathEvent("death");

    }

}
