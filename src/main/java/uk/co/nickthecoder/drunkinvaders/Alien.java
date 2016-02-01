/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.drunkinvaders;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Costume;
import uk.co.nickthecoder.itchy.CostumeFeatures;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.ZOrderStage;
import uk.co.nickthecoder.itchy.animation.Eases;
import uk.co.nickthecoder.itchy.animation.ScaleAnimation;
import uk.co.nickthecoder.itchy.extras.Fragments;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.DoubleProperty;
import uk.co.nickthecoder.itchy.property.IntegerProperty;
import uk.co.nickthecoder.itchy.role.ExplosionBuilder;
import uk.co.nickthecoder.itchy.role.TalkBuilder;
import uk.co.nickthecoder.itchy.util.Tag;
import uk.co.nickthecoder.itchy.util.Util;

@Tag(names = { "deadly", "shootable" })
public class Alien extends Bouncy implements Shootable
{
    protected static final List<Property<Role, ?>> properties = new ArrayList<Property<Role, ?>>();

    static {
        properties.addAll(Bouncy.properties);
        properties.add(new DoubleProperty<Role>("fireOnceEvery").hint("seconds"));
        properties.add(new IntegerProperty<Role>("shotsRequired"));
    }

    public static final String[] SHOOTABLE_LIST = new String[] { "shootable" };

    public double fireOnceEvery = 1.0; // Average duration between bombs in seconds

    public int shotsRequired = 1;

    public boolean tock = true;

    @Override
    public List<Property<Role, ?>> getProperties()
    {
        return properties;
    }

    @Override
    public void onBirth()
    {
        super.onBirth();
        DrunkInvaders.director.addAliens(1);
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
        ((ZOrderStage) Itchy.getGame().getLayout().findStage("main")).addTop(bullet);
        // Scaled up aliens have scaled up bullets
        bullet.moveForwards(15 * getActor().getAppearance().getScale());
        bullet.getAppearance().setScale(getActor().getAppearance().getScale());
        bullet.setRole(new Bullet("killable"));
    }

    @Override
    public void shot(Actor bullet)
    {
        // Explode pieces outwards
        new ExplosionBuilder(getActor()).fragments(getAlienCostumeProperties().fragments)
            .fade(3).speed(1, 3, 0, 0)
            .spin(-1, 1).create();

        // Particles exploding outwards
        new ExplosionBuilder(getActor())
            .projectiles(40).projectilesPerTick(10)
            .offsetForwards(-10, 10).offsetSidewards(-10, 10)
            .distance(10 * getActor().getAppearance().getScale())
            .speed(5, 9, 0, 0).fade(3).scale(1).eventName("pixel")
            .create();

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


        Actor yell = new TalkBuilder(getActor())
            .eventName("death").style("yell")
            .offset(0, 40).direction(0)
            .create().getActor();
        
        yell.setCostume(getActor().getCostume());
        // TODO Do we want a deathEvent, or just and event?
        //yell.deathEvent("yell");

        this.deathEvent("death");
        removeAllTags();

    }

    @Override
    public AlienCostumeProperties createCostumeFeatures(Costume costume)
    {
        return new AlienCostumeProperties(costume);
    }

    AlienCostumeProperties getAlienCostumeProperties()
    {
        return (AlienCostumeProperties) this.getActor().getCostume().getCostumeFeatures();
    }

    public static class AlienCostumeProperties extends CostumeFeatures
    {
        public Fragments fragments;

        public AlienCostumeProperties(Costume costume)
        {
            super(costume);
            this.fragments = new Fragments().create(costume.getPose("default"));
        }
    }
}
