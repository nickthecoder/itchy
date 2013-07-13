package uk.co.nickthecoder.drunkinvaders;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Appearance;
import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.Costume;
import uk.co.nickthecoder.itchy.util.Property;
import uk.co.nickthecoder.itchy.util.Util;

public class AlienFactory extends Behaviour
{

    @Property(label="Costume")
    public String costumeName;

    @Property(label="Delay per Alien")
    public double delayPerAlien = 0.500;

    @Property(label="Aliens")
    public int alienCount = 6;

    @Property(label="Spacing")
    public double spacing = 80;

    @Property(label="Fire Once Every (s)")
    public double fireOnceEvery = 1; // The aliens' average number of seconds between bombs

    private List<Actor> aliens;

    
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
            ab.vx = Util.randomBetween(2, 2.2);
            ab.vy = Util.randomBetween(0,0.6);
        }

        this.actor.kill();
    }

    private void createAlien()
    {
        Costume costume = DrunkInvaders.game.resources.getCostume(this.costumeName);
        Actor alien = new Actor(costume);
        Appearance alienAppearance = alien.getAppearance();
        Appearance thisAppearance = this.actor.getAppearance();
        
        alienAppearance.setDirection(this.actor.getAppearance().getDirection() - 90);
        alienAppearance.setScale( thisAppearance.getScale() );
        alienAppearance.setAlpha(0);
        
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
