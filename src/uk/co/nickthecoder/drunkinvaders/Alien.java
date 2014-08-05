/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.drunkinvaders;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.animation.Eases;
import uk.co.nickthecoder.itchy.animation.ScaleAnimation;
import uk.co.nickthecoder.itchy.extras.Fragment;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.role.Explosion;
import uk.co.nickthecoder.itchy.role.Talk;
import uk.co.nickthecoder.itchy.util.Tag;
import uk.co.nickthecoder.itchy.util.Util;

@Tag(names = { "deadly", "shootable" })
public class Alien extends Bouncy implements Shootable
{
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
        for (Role otherRole : getCollisionStrategy().collisions(this.getActor(), SHOOTABLE_LIST)) {
            Actor other = otherRole.getActor();
            if ((getActor() != other) && (!otherRole.hasTag("bouncy"))) {
                ((Shootable) other.getRole()).shot(getActor());
            }
        }
    }

    public void fire()
    {
        if (getActor().getAnimation() != null) {
            // Can't fire while the alien is growing from the mothership.
            return;
        }
        this.event("fire");

        Actor bullet = new Actor(DrunkInvaders.director.getGame().resources.getCostume("bomb"));
        bullet.event("default");
        bullet.moveTo(getActor());
        bullet.setDirection(getActor().getAppearance().getDirection());
        DrunkInvaders.director.mainStage.addTop(bullet);
        // Scaled up aliens have scaled up bullets
        bullet.moveForwards(15 * getActor().getAppearance().getScale());
        bullet.getAppearance().setScale(getActor().getAppearance().getScale());
        bullet.setRole(new Bullet("killable"));
    }

    @Override
    public void shot( Actor bullet )
    {

        new Explosion(getActor())
            .projectiles(20)
            .fade(3)
            .speed(1, 3, 0, 0)
            .eventName("fragment")
            .distance(10 * getActor().getAppearance().getScale())
            .scale(1)
            .createActor();

        new Explosion(getActor())
            .projectiles(40).projectilesPerTick(10)
            .offsetForwards(-10, 10).offsetSidewards(-10, 10)
            .distance(10 * getActor().getAppearance().getScale())
            .speed(5, 9, 0, 0)
            .fade(3)
            .scale(1)
            .eventName("pixel")
            .createActor();

        double scale = getActor().getAppearance().getScale();

        this.shotsRequired--;
        if (this.shotsRequired > 0) {
            event("shot");

            if (scale > 1) {
                double newScale = 1 + (scale - 1) * (this.shotsRequired) / (this.shotsRequired + 1);
                ScaleAnimation scaleAnimation = new ScaleAnimation(10, Eases.linear, newScale);
                getActor().setAnimation(scaleAnimation);
            }

            return;
        }

        Actor yell = new Talk(this)
            .eventName("death").style("yell")
            .offset(0, 40).direction(0)
            .createActor();
        yell.setCostume(getActor().getCostume());
        yell.deathEvent("yell");

        removeAllTags();
        this.deathEvent("death");

    }

}
