package uk.co.nickthecoder.drunkinvaders;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.animation.NumericAnimation;
import uk.co.nickthecoder.itchy.animation.ScaleAnimation;
import uk.co.nickthecoder.itchy.extras.Explosion;
import uk.co.nickthecoder.itchy.extras.Fragment;
import uk.co.nickthecoder.itchy.extras.Talk;
import uk.co.nickthecoder.itchy.util.Property;
import uk.co.nickthecoder.itchy.util.Util;
import uk.co.nickthecoder.jame.RGBA;

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
    public void init()
    {
        super.init();

        this.actor.addTag("deadly");
        this.actor.addTag("shootable");

        // Create the fragments for the explosions when I get shot.
        new Fragment().actor(this.actor).create("fragment");
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
        if (this.collisionStrategy != null) {
            this.collisionStrategy.remove();
            this.collisionStrategy = null;
        }
    }

    @Override
    public void tick()
    {
        if (this.actor.isDying()) {
            return;
        }

        if (DrunkInvaders.game.metronomeCountdown == 1) {
            if (this.actor.getAnimation() == null) {
                this.tock = !this.tock;
                this.actor.event(this.tock ? "tock" : "tick");
            }
        }

        if (Util.randomOnceEvery(this.fireOnceEvery)) {
            this.fire();
        }

        super.tick();

        for (Actor other : touching(SHOOTABLE_LIST)) {
            if ((this.actor != other) && (!other.hasTag("bouncy"))) {
                ((Shootable) other.getBehaviour()).shot(this.actor);
            }
        }
    }

    public void fire()
    {
        this.event("fire");

        Actor bullet = new Actor(DrunkInvaders.game.resources.getCostume("bomb"), "default");
        bullet.moveTo(this.actor);
        bullet.getAppearance().setDirection(this.actor.getAppearance().getDirection());
        DrunkInvaders.game.mainLayer.add(bullet);
        bullet.moveForward(10);
        bullet.setBehaviour(new Bullet("killable"));
        bullet.activate();
    }

    @Override
    public void shot( Actor bullet )
    {

        new Explosion(this.actor)
            .projectiles(20)
            .forwards()
            .fade(1.2)
            .speed(0.1, 1)
            .createActor("fragment").activate();

        new Explosion(this.actor)
            .projectiles(40).projectilesPerClick(10)
            .distance(0, 20)
            .speed(3, 6)
            .fade(0.5)
            .createActor("pixel")
            .activate();

        double scale = this.getActor().getAppearance().getScale();
        if (scale > 1) {
            ScaleAnimation scaleAnimation = new ScaleAnimation(10, NumericAnimation.linear, scale,
                scale / (this.shotsRequired + 2) * (this.shotsRequired + 1));
            this.getActor().setAnimation(scaleAnimation);
        }

        this.shotsRequired--;
        if (this.shotsRequired > 0) {
            event("shot");
            return;
        }


        Actor yell = new Talk(this)
            .message("death")
            .font("vera", 18)
            .color(SPEECH_COLOR)
            .bubble("speechBubble")
            .offset(0, 40)
            .margin(10, 10, 20, 10)
            .createActor();
        yell.activate();
        yell.deathEvent(this.actor.getCostume(), "yell");

        this.actor.removeAllTags();
        this.deathEvent("death");

    }

}
