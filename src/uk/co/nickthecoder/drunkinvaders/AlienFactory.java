package uk.co.nickthecoder.drunkinvaders;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Appearance;
import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.Costume;
import uk.co.nickthecoder.itchy.util.DoubleProperty;
import uk.co.nickthecoder.itchy.util.IntegerProperty;
import uk.co.nickthecoder.itchy.util.StringProperty;

public class AlienFactory extends Behaviour
{

    public String costumeName;

    public double delayPerAlien = 0.500;

    public int alienCount = 6;

    public double spacing = 80;

    public double fireOnceEvery = 1; // The aliens' average number of seconds between bombs

    private List<Actor> aliens;

    private static Random random = new Random();

    @Override
    protected void addProperties()
    {
        super.addProperties();

        addProperty(new StringProperty("Costume", "costumeName"));
        addProperty(new DoubleProperty("Delay per Alien", "delayPerAlien"));
        addProperty(new IntegerProperty("Aliens", "alienCount"));
        addProperty(new DoubleProperty("Spacing", "spacing"));
        addProperty(new DoubleProperty("Fire Once Every (s)", "fireOnceEvery"));
    }

    @Override
    public void init()
    {
        this.aliens = new ArrayList<Actor>(this.alienCount);
    }

    @Override
    public void tick()
    {
        this.actor.getAppearance().setAlpha(0);

        for (int i = 0; i < this.alienCount; i++) {
            createAlien();
            sleep(this.delayPerAlien);
            if ( this.actor.isDead()) {
                return;
            }
        }

        for (Actor actor : this.aliens) {
            Alien ab = (Alien) actor.getBehaviour();
            ab.vx = random.nextDouble() * 2.0 + 0.2;
            ab.vy = random.nextDouble() * 0.6;
        }

        this.actor.kill();
    }

    private void createAlien()
    {
        Costume costume = DrunkInvaders.singleton.resources.getCostume(this.costumeName);
        Actor alien = new Actor(costume);
        Appearance alienAppearance = alien.getAppearance();
        Appearance thisAppearance = this.actor.getAppearance();
        
        alienAppearance.setDirection(this.actor.getAppearance().getDirection() - 90);
        alienAppearance.setScale( thisAppearance.getScale() );
        
        Alien alienBehaviour = new Alien();
        alienBehaviour.fireOnceEvery = this.fireOnceEvery;
        
        alien.moveTo(this.actor.getX() + this.aliens.size() * this.spacing, this.actor.getY());
        this.actor.getLayer().add(alien);
        
        alien.setBehaviour(alienBehaviour);
        alien.activate();
        alien.event("birth");
        
        this.aliens.add(alien);
    }

}
