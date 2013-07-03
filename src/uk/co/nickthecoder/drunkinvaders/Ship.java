package uk.co.nickthecoder.drunkinvaders;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Pose;
import uk.co.nickthecoder.itchy.TextPose;
import uk.co.nickthecoder.itchy.util.BorderPoseDecorator;
import uk.co.nickthecoder.itchy.util.DoubleProperty;
import uk.co.nickthecoder.itchy.util.ExplosionBehaviour;
import uk.co.nickthecoder.itchy.util.PoseDecorator;
import uk.co.nickthecoder.jame.Keys;
import uk.co.nickthecoder.jame.RGBA;

public class Ship extends Bouncy implements Shootable
{
    private static RGBA SPEECH_COLOR = new RGBA(0, 0, 0);

    public static final String[] DEADLY_LIST = new String[] { "deadly" };

    private static PoseDecorator bubbleCreator = new BorderPoseDecorator(
            DrunkInvaders.singleton.resources.getNinePatch("speech2"), 10, 10, 20, 10);

    private int recharge = 0;
    private static final int RECHARGE_DURATION = 40;
    
    private static final int SHIELD_POSE_COUNT = 7;
    
    private final double ox = 320;
    private final double oy = -1300;
    private double radius;
    private final double rotationSpeed = 0.003;
    private double angle;

    private Actor latestBullet;

    private boolean shielded = false;

    /**
     * 1 is fully charged 0 is fully drained.
     */
    private double shieldStrength = 1.0;

    private double shieldRechargeRate = 0.001;

    private double shieldDischargeRate = 0.01;

    @Override
    public void init()
    {
        super.init();

        this.mass = 100000000000.0;
        this.actor.addTag("killable");
        this.radius = Math.sqrt((this.actor.getX() - this.ox) * (this.actor.getX() - this.ox) +
                (this.actor.getY() - this.oy) * (this.actor.getY() - this.oy));
        this.angle = Math.atan2(this.actor.getY() - this.oy, this.actor.getX() - this.ox);
        this.actor.getAppearance().setDirectionRadians(this.angle);
        this.recalculateDirection();
    }

    @Override
    protected void addProperties()
    {
        super.addProperties();
        addProperty(new DoubleProperty("Shield Discharge Rate", "sheildDischargeRate"));
        addProperty(new DoubleProperty("Shield Recharge Rate", "sheildRechargeRate"));
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

        if (Itchy.singleton.isKeyDown(Keys.LSHIFT) || Itchy.singleton.isKeyDown(Keys.RSHIFT)) {
            if (!this.shielded) {
                activateShield();
            }
        } else {
            if (this.shielded) {
                deactivateShield();
            }
        }

        if (this.shielded) {
            super.tick();
            dischargeShield();

        } else {

            chargeShield();

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
                if ((this.recharge == 0) ||
                        ((this.latestBullet != null) && this.latestBullet.isDead())) {
                    this.event("charged");
                    this.recharge = 0;
                }
            } else {

                if (Itchy.singleton.isKeyDown(Keys.SPACE)) {
                    this.fire();
                }
            }

            this.collisionStrategy.update();

            for (Actor actor : this.collisionStrategy.touching(this.actor, DEADLY_LIST)) {
                this.shot(actor);
                ((Shootable) actor.getBehaviour()).shot(this.actor);
                break;
            }

        }

    }

    private void recalculateDirection()
    {
        this.actor.getAppearance().setDirectionRadians(this.angle);
        this.actor.setX(this.radius * Math.cos(this.angle) + this.ox);
        this.actor.setY(this.radius * Math.sin(this.angle) + this.oy);
    }

    public void activateShield()
    {
        long level = Math.round( this.shieldStrength * SHIELD_POSE_COUNT );
        if ( level > 0 ) {
            this.event("shield");
            long newLevel = Math.round( this.shieldStrength * SHIELD_POSE_COUNT );
            event( "shielded" + newLevel );
            this.shielded = true;
        } else {
            this.event("shieldFailed");
        }
    }

    public void deactivateShield()
    {
        this.event("deshield");
        this.shielded = false;
    }

    private void dischargeShield()
    {
        long oldLevel = Math.round( this.shieldStrength * SHIELD_POSE_COUNT );
        this.shieldStrength -= this.shieldDischargeRate;
        long newLevel = Math.round( this.shieldStrength * SHIELD_POSE_COUNT );

        if (this.shieldStrength < 0 ) {
            this.deactivateShield();
            return;
        }
        
        if  ( oldLevel != newLevel ) {
            event( "shielded" + newLevel );
        }
    }

    private void chargeShield()
    {
        this.shieldStrength += this.shieldRechargeRate;
        if (this.shieldStrength > 1) {
            this.shieldStrength = 1;
        }
    }
    
    public void fire()
    {
        if (this.shielded) {
            return;
        }

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
        if (this.shielded) {
            this.event("deflect");
            return;
        }

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
