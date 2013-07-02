package uk.co.nickthecoder.drunkinvaders;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Pose;
import uk.co.nickthecoder.itchy.TextPose;
import uk.co.nickthecoder.itchy.neighbourhood.ActorCollisionStrategy;
import uk.co.nickthecoder.itchy.util.BorderPoseDecorator;
import uk.co.nickthecoder.itchy.util.ExplosionBehaviour;
import uk.co.nickthecoder.itchy.util.PoseDecorator;
import uk.co.nickthecoder.jame.Keys;
import uk.co.nickthecoder.jame.RGBA;

public class Ship extends Behaviour implements Shootable
{
    private static RGBA SPEECH_COLOR = new RGBA(0, 0, 0);

    public static final String[] DEADLY_LIST = new String[] { "deadly" };

    private static PoseDecorator bubbleCreator = new BorderPoseDecorator(
            DrunkInvaders.singleton.resources.getNinePatch("speech2"), 10, 10, 20, 10);

    private int recharge = 0;
    private static final int RECHARGE_DURATION = 40;

    private final double ox = 320;
    private final double oy = -1300;
    private double radius;
    private final double rotationSpeed = 0.003;
    private double angle;

    private ActorCollisionStrategy collisionStrategy;

    private Actor latestBullet;

    @Override
    public void init()
    {
        this.actor.addTag("killable");
        this.radius = Math.sqrt((this.actor.getX() - this.ox) * (this.actor.getX() - this.ox) +
                (this.actor.getY() - this.oy) * (this.actor.getY() - this.oy));
        this.angle = Math.atan2(this.actor.getY() - this.oy, this.actor.getX() - this.ox);
        this.actor.getAppearance().setDirectionRadians(this.angle);
        this.recalculateDirection();

        this.collisionStrategy = DrunkInvaders.singleton.createCollisionStrategy(this.actor);
    }

    @Override
    public void onKill()
    {
        if (this.collisionStrategy != null) {
            this.collisionStrategy.remove();
            this.collisionStrategy = null;
        }
    }

    @Override
    public void tick()
    {

        if (Itchy.singleton.isKeyDown(Keys.LEFT)) {
            this.angle += this.rotationSpeed;
            this.recalculateDirection();
            if (this.actor.getX() < 30) {
                this.angle -= this.rotationSpeed;
                this.recalculateDirection();
            }
        }
        if (Itchy.singleton.isKeyDown(Keys.RIGHT)) {
            this.angle -= this.rotationSpeed;
            this.recalculateDirection();
            if (this.actor.getX() > 640 - 30) {
                this.angle += this.rotationSpeed;
                this.recalculateDirection();
            }
        }

        if (this.recharge > 0) {
            this.recharge--;
            if ((this.recharge == 0) || ((this.latestBullet != null) && this.latestBullet.isDead())) {
                this.event("charged");
                this.recharge = 0;
            }
        } else {

            if (Itchy.singleton.isKeyDown(Keys.SPACE)) {
                this.fire();
            }
        }

        if (Itchy.singleton.isKeyDown(Keys.p)) {
            System.out.println("Waiting...");
            getActor().sleep(3.0);
            System.out.println("Done...");
        }

        this.collisionStrategy.update();

        for (Actor actor : this.collisionStrategy.touching(this.actor, DEADLY_LIST)) {
            this.shot(actor);
            ((Shootable) actor.getBehaviour()).shot(this.actor);
            break;
        }

    }

    private void recalculateDirection()
    {
        this.actor.getAppearance().setDirectionRadians(this.angle);
        this.actor.setX(this.radius * Math.cos(this.angle) + this.ox);
        this.actor.setY(this.radius * Math.sin(this.angle) + this.oy);
    }

    public void fire()
    {
        this.event("fire");

        Actor bullet = new Actor(DrunkInvaders.singleton.resources.getCostume("bullet"), "default");
        this.latestBullet = bullet;
        bullet.moveTo(this.actor);
        bullet.getAppearance().setDirection(this.actor.getAppearance().getDirection());
        DrunkInvaders.singleton.mainLayer.add(bullet);
        bullet.moveForward(10);
        bullet.setBehaviour(new Bullet());
        bullet.addTag("killable");
        bullet.activate();

        this.recharge = RECHARGE_DURATION;
    }

    @Override
    public void shot( Actor other )
    {
        TextPose textPose = new TextPose(this.actor.getCostume().getString("death"),
                DrunkInvaders.singleton.resources.getFont("vera"), 18, SPEECH_COLOR);
        Pose bubble = bubbleCreator.createPose(textPose);

        Actor yell = new Actor(bubble);
        yell.moveTo(this.actor);
        yell.activate();
        this.actor.getLayer().add(yell);
        yell.deathEvent(this.actor.getCostume(), "yell");

        Actor explosion = new Actor(this.actor.getCostume().getPose("pixel")); // DrunkInvaders.singleton.resources.getPose(
                                                                               // "pixel" ) );
        ExplosionBehaviour eb = new ExplosionBehaviour();
        eb.distance = 0;
        eb.randomDistance = 20;
        eb.projectileCount = 20;
        eb.speed = 1.5;
        eb.randomSpeed = 0.1;
        eb.fade = 2.5;
        // explosion.getAppearance().setColorize( new RGBA( 0, 255, 0 ) );
        explosion.setBehaviour(eb);

        explosion.moveTo(this.actor);
        this.actor.getLayer().add(explosion);
        explosion.activate();

        this.deathEvent("death");
    }
}
