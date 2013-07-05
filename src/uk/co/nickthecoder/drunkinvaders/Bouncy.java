package uk.co.nickthecoder.drunkinvaders;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.neighbourhood.ActorCollisionStrategy;
import uk.co.nickthecoder.itchy.util.DoubleProperty;

public class Bouncy extends Behaviour
{
    public static final String[] BOUNCY_LIST = new String[] { "bouncy" };

    public double vx = 0;

    public double vy = 0;

    public double radius = 20;
    
    public double mass = 1;

    protected ActorCollisionStrategy collisionStrategy;

    @Override
    protected void addProperties()
    {
        super.addProperties();
        addProperty(new DoubleProperty("Speed X", "vx"));
        addProperty(new DoubleProperty("Speed Y", "vy"));
        addProperty(new DoubleProperty("Mass", "mass"));
        
    }

    @Override
    public void init()
    {
        super.init();
        this.actor.addTag("bouncy");
        this.collisionStrategy = DrunkInvaders.singleton.createCollisionStrategy(this.actor);

    }

    @Override
    public void onKill()
    {
        super.onKill();
        this.collisionStrategy.remove();
        this.collisionStrategy = null;
    }

    @Override
    public void tick()
    {
        this.actor.moveBy(this.vx, this.vy);

        double radius = this.radius * this.getActor().getAppearance().getScale();
        
        if ((this.vy) > 0 && (this.actor.getY() + radius > 480)) {
            this.vy = -this.vy;
        }
        if ((this.vx) > 0 && (this.actor.getX() + radius > 640)) {
            this.vx = -this.vx;
        }
        if ((this.vy) < 0 && (this.actor.getY() - radius < 0)) {
            this.vy = -this.vy;
        }
        if ((this.vx) < 0 && (this.actor.getX() - radius < 0)) {
            this.vx = -this.vx;
        }

        this.collisionStrategy.update();

        for (Actor touching : this.collisionStrategy.touching(BOUNCY_LIST)) {
            collide(this.actor, touching);
        }

    }

    public static void collide( Actor a, Actor b )
    {
        Bouncy bba = (Bouncy) a.getBehaviour();
        Bouncy bbb = (Bouncy) b.getBehaviour();

        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();

        double dist = Math.sqrt(dx * dx + dy * dy);

        double dvx = bbb.vx - bba.vx;
        double dvy = bbb.vy - bba.vy;

        // The speed of the collision in the direction of the line between their centres.
        double collision = (dvx * dx + dvy * dy) / dist;

        if (collision < 0) {
            // They are moving away from each other
            return;
        }

        // Assume mass goes up by the cube of its size (which is appropriate for a 3D object).
        // Maybe it should be my the square, if we think they are only 2D shapes!
        double scaleA = bba.getActor().getAppearance().getScale();
        double scaleB = bbb.getActor().getAppearance().getScale();
        double massA = bba.mass * scaleA * scaleA * scaleA;
        double massB = bbb.mass * scaleB * scaleB * scaleB;
        
        double massSum = massA + massB;
        
        bba.vx += dx / dist * collision * 2 * massB / massSum;
        bbb.vx -= dx / dist * collision * 2 * massA / massSum;

        bba.vy += dy / dist * collision * 2 * massB / massSum;
        bbb.vy -= dy / dist * collision * 2 * massA / massSum;

    }
    
    public static void collideOld( Actor a, Actor b )
    {
                
        Bouncy bba = (Bouncy) a.getBehaviour();
        Bouncy bbb = (Bouncy) b.getBehaviour();

        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();

        double dist = Math.sqrt(dx * dx + dy * dy);

        double dvx = bbb.vx - bba.vx;
        double dvy = bbb.vy - bba.vy;

        double collision = (dvx * dx + dvy * dy) / dist;

        if (collision < 0) {
            // They are moving away from each other
            return;
        }
        
        bba.vx += dx / dist * collision;
        bbb.vx -= dx / dist * collision;

        bba.vy += dy / dist * collision;
        bbb.vy -= dy / dist * collision;

    }

}
