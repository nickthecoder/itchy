package uk.co.nickthecoder.drunkinvaders;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.Costume;
import uk.co.nickthecoder.itchy.util.Property;

public class ShieldFactory extends Behaviour
{
    @Property(label="Width")
    public int width = 6;

    @Property(label="Height")
    public int height = 4;

    @Property(label="Spacing")
    public double spacing = 10;

    @Override
    public void tick()
    {
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {

                Costume costume = DrunkInvaders.game.resources.getCostume("shield");
                if (y == this.height - 1) {
                    if (x == 0) {
                        costume = DrunkInvaders.game.resources.getCostume("shield-l");
                    } else if (x == this.width - 1) {
                        costume = DrunkInvaders.game.resources.getCostume("shield-r");
                    }
                }

                Actor shield = new Actor(costume);
                Shield shieldBehaviour = new Shield();
                shield.getAppearance().setDirection(this.actor.getAppearance().getDirection());
                this.actor.getLayer().add(shield);
                shield.moveTo(this.actor.getX(), this.actor.getY());
                shield.moveForward(x * this.spacing, y * this.spacing);
                shield.setBehaviour(shieldBehaviour);
                shield.activate();

            }
        }

        this.actor.kill();

    }

}
