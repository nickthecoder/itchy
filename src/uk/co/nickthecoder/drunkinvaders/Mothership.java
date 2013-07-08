package uk.co.nickthecoder.drunkinvaders;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Costume;
import uk.co.nickthecoder.itchy.util.DoubleProperty;
import uk.co.nickthecoder.itchy.util.IntegerProperty;
import uk.co.nickthecoder.itchy.util.StringProperty;
import uk.co.nickthecoder.itchy.util.Util;

public class Mothership extends Alien
{
    // private static RGBA SPEECH_COLOR = new RGBA( 0, 0, 0 );

    public static final String[] SHOOTABLE_LIST = new String[] { "shootable" };

    public String costumeName;

    /**
     * The time in seconds between children being born.
     */
    public double birthInterval = 1.0;

    /**
     * The children's average duration is seconds between bombs
     */
    public double childFireOnceEvery = 1;

    /**
     * How long in seconds for the first child to be born after the mothership is activated.
     */
    public double firstBornDelay = 0;

    /**
     * The number of aliens to create
     */
    public int childrenCount;

    @Override
    public void init()
    {
        super.init();
        this.actor.addTag("deadly");
        this.actor.addTag("shootable");
        this.collisionStrategy = DrunkInvaders.singleton.createCollisionStrategy(this.actor);
    }

    @Override
    protected void addProperties()
    {
        super.addProperties();
        addProperty(new DoubleProperty("First Born Delay", "firstBornDelay"));
        addProperty(new DoubleProperty("Birth Interval", "birthInterval"));
        addProperty(new IntegerProperty("Children", "childrenCount"));
        addProperty(new StringProperty("Children's Costume", "costumeName"));
        addProperty(new DoubleProperty("Child Fire Once Every", "childFireOnceEvery"));
    }

    @Override
    public void onActivate()
    {
        super.onActivate();

        this.getActor().sleep(this.firstBornDelay);
        for (int i = 0; i < this.childrenCount; i++) {
            if ( this.actor.isDead()) {
                return;
            }
            giveBirth();
            this.actor.sleep(this.birthInterval);
        }
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

    public void giveBirth()
    {
        this.event("giveBirth");

        Costume costume = DrunkInvaders.singleton.resources.getCostume(this.costumeName);
        Actor alien = new Actor(costume);
        alien.getAppearance().setDirection(this.actor.getAppearance().getDirection());
        Alien alienBehaviour = new Alien();
        alienBehaviour.fireOnceEvery = this.childFireOnceEvery;
        alien.setBehaviour(alienBehaviour);

        alienBehaviour.vx = Util.randomBetween(-0.2, 0.2) + this.vx;
        alien.moveTo(this.actor.getX(), this.actor.getY());
        if (this.actor.getY() < 200) {
            alien.moveForward(5, 0);
            alienBehaviour.vy = this.vy + 1;
        } else {
            alien.moveForward(-5, 0);
            alienBehaviour.vy = this.vy - 1;
        }

        this.actor.getLayer().add(alien);
        alien.activate();
        alien.event("dropped");
    }

}
