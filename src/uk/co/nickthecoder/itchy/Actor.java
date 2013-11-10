/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.List;
import java.util.Set;

import uk.co.nickthecoder.itchy.animation.Animation;
import uk.co.nickthecoder.itchy.animation.CompoundAnimation;
import uk.co.nickthecoder.itchy.util.AbstractProperty;
import uk.co.nickthecoder.itchy.util.Property;
import uk.co.nickthecoder.itchy.util.PropertySubject;
import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Surface;

public class Actor implements PropertySubject<Actor>
{
    private static NullBehaviour unsedBehaviour = new NullBehaviour();

    private static Pose startPose( Costume costume, String name )
    {
        Pose pose = costume.getPose(name);
        if (pose != null) {
            return pose;
        }
        pose = costume.getPose("default");
        if (pose != null) {
            return pose;
        }

        // It doesn't have a pose, so it will be a TextPose...
        String text = costume.getString(name);
        if (text == null) {
            text = "?";
        }
        Font font = costume.getFont(name);
        if (font == null) {
            return ImagePose.getDummyPose();
        }
        return new TextPose(text, font, 20);
    }

    private static int nextId = 1;

    public final int id;

    private static List<AbstractProperty<Actor, ?>> properties = AbstractProperty.findAnnotations(Actor.class);

    Behaviour behaviour;

    private Animation animation;

    private final Appearance appearance;

    private Costume costume;

    private ActorsLayer layer;

    private double x;
    private double y;
    private boolean active = false;
    private boolean dead = false;
    private boolean dying = false;
    private int zOrder = 0;

    private double heading;

    private double activationDelay;

    private String startEvent = "default";

    private CollisionStrategy collisionStrategy = BruteForceCollisionStrategy.singleton;

    private boolean fullyCreated = false;

    public Actor( Costume costume )
    {
        this(costume, "default");
    }

    public Actor( Costume costume, String poseName )
    {
        this(startPose(costume, poseName));

        this.costume = costume;
        this.setDirection(this.getAppearance().getPose().getDirection());
    }

    public Actor( Pose pose )
    {
        this.id = nextId;
        nextId++;
        this.costume = null;
        this.appearance = new Appearance(pose);
        this.appearance.setActor(this);

        this.behaviour = unsedBehaviour;

        this.x = 0;
        this.y = 0;
        this.setDirection(pose.getDirection());
    }

    @Property(label = "Start Event")
    public String getStartEvent()
    {
        return this.startEvent;
    }

    public void setStartEvent( String value )
    {
        this.startEvent = value;
    }

    /**
     * Sets the heading that the actor is travelling in. It does NOT affect the rotation of the
     * actor's image.
     * 
     * @param degrees
     */
    public void setHeading( double degrees )
    {
        this.heading = degrees;
    }

    public void setHeadingRadians( double radians )
    {
        this.setHeading(radians * 180 / Math.PI);
    }

    @Property(label = "Heading")
    public double getHeading()
    {
        return this.heading;
    }

    public double getHeadingRadians()
    {
        return this.heading / 180 * Math.PI;
    }

    /**
     * Sets the heading and the appearance's direction.
     * 
     * @param degrees
     */
    public void setDirection( double degrees )
    {
        getAppearance().setDirection(degrees);
        setHeading(getAppearance().getDirection());
    }

    /**
     * Sets the heading and the appearance's direction.
     * 
     * @param degrees
     */
    public void setDirectionRadians( double radians )
    {
        setDirection(radians * 180 / Math.PI);
    }

    /**
     * Sets the heading and the appearance's direction.
     * 
     * @param degrees
     */
    public void setDirection( Actor other )
    {
        getAppearance().setDirection(other);
        setHeading(getAppearance().getDirection());
    }

    public Costume getCostume()
    {
        return this.costume;
    }

    public void setCostume( Costume costume )
    {
        this.costume = costume;
    }

    public boolean getYAxisPointsDown()
    {
        if (this.layer == null) {
            return false;
        }
        return this.layer.getYAxisPointsDown();
    }

    public double getCornerY()
    {
        if (this.getYAxisPointsDown()) {
            return this.getY() - this.getAppearance().getOffsetY();
        } else {
            return this.getY() - this.getAppearance().getSurface().getHeight() +
                this.getAppearance().getOffsetY();
        }
    }

    public double getCornerX()
    {
        return this.getX() - this.getAppearance().getOffsetX();
    }

    public ActorsLayer getLayer()
    {
        return this.layer;
    }

    public void removeFromLayer()
    {
        setLayer(null);
    }

    public void setLayer( ActorsLayer layer )
    {
        if (this.layer == layer) {
            return;
        }

        if (this.layer != null) {
            this.layer.remove(this);
        }

        if (layer != null) {
            layer.add(this);
        }
    }

    void setLayerAttribute( ActorsLayer layer )
    {
        this.layer = layer;
        checkFullyCreated();
    }

    public void setAnimation( Animation animation )
    {
        setAnimation(animation, AnimationEvent.REPLACE);
    }

    public void setAnimation( Animation animation, AnimationEvent ae )
    {
        if (animation == null) {
            this.animation = null;
            return;
        }

        Animation newAnimation;

        if ((this.animation == null) || (ae == AnimationEvent.REPLACE)) {
            newAnimation = animation.copy();
            newAnimation.start(this);

        } else if (ae == AnimationEvent.IGNORE) {
            return;

        } else {
            CompoundAnimation ca = new CompoundAnimation(ae == AnimationEvent.SEQUENCE);
            ca.add(this.getAnimation());
            ca.add(animation.copy());
            ca.startExceptFirst(this);
            newAnimation = ca;
        }

        newAnimation.addMessageListener(getBehaviour());
        this.animation = newAnimation;
    }

    public Animation getAnimation()
    {
        return this.animation;
    }

    public void event( String eventName )
    {
        this.event(this.costume, eventName, AnimationEvent.REPLACE);
    }

    public void event( String eventName, AnimationEvent ae )
    {
        this.event(this.costume, eventName, ae);
    }

    public enum AnimationEvent
    {
        REPLACE,
        SEQUENCE,
        PARALLEL,
        IGNORE
    }

    public void event( Costume costume, String eventName, AnimationEvent ae )
    {
        if (costume == null) {
            return;
        }
        Pose pose = costume.getPose(eventName);
        if (pose != null) {
            this.appearance.setPose(pose);
            this.setAnimation(null);
        }

        Animation animation = costume.getAnimation(eventName);
        this.setAnimation(animation, ae);

        ManagedSound cs = costume.getCostumeSound(eventName);
        if (cs != null) {
            Itchy.soundManager.play(this, eventName, cs);
        }
    }

    /**
     * Will fade out or stop sounds corresponding to the given even name. Future versions of Itchy
     * may also stop corresponding animations.
     * 
     * @param eventName
     */
    public void endEvent( String eventName )
    {
        Itchy.soundManager.end(this, eventName);
    }

    public void deathEvent( String eventName )
    {
        this.deathEvent(this.costume, eventName);
    }

    public void deathEvent( Costume costume, String eventName )
    {
        this.dying = true;
        this.event(costume, eventName, AnimationEvent.REPLACE);
        if ((costume == null) || (costume.getAnimation(eventName) == null)) {
            this.kill();
        }
    }

    public void deathAnimation( String animationName )
    {
        Animation animation = Itchy.getResources().getAnimation(animationName);
        if (animation == null) {
            this.kill();
        } else {
            this.dying = true;
            this.setAnimation(animation);
        }
    }

    @Property(label = "X")
    public double getX()
    {
        return this.x;
    }

    @Property(label = "Y")
    public double getY()
    {
        return this.y;
    }

    /**
     * This is NOT the opposite of isOnScreen.
     * 
     * @return True is any part of the actor's bounding rectangle is off screen.
     */
    public boolean isOffScreen()
    {
        if (this.layer == null) {
            return true;
        }

        return !this.appearance.getWorldRectangle().within(this.layer.worldRect);
    }

    /**
     * This is NOT the opposite of isOffScreen.
     * 
     * @return True if any part of the actor's bounding rectangle is on screen.
     */
    public boolean isOnScreen()
    {
        if (this.layer == null) {
            return false;
        }

        return this.appearance.visibleWithin(this.layer.getWorldRectangle());
    }

    private void checkFullyCreated()
    {
        if (this.fullyCreated) {
            return;
        }

        if ((this.layer != null) && (this.behaviour != unsedBehaviour)) {
            this.fullyCreated = true;
            this.behaviour.onBirth();
            Itchy.getGame().add(this);
        }
    }

    public final void setBehaviour( Behaviour behaviour )
    {
        if (behaviour == this.behaviour) {
            return;
        }

        if (this.behaviour != null) {
            this.behaviour.detatch();
        }

        this.behaviour = behaviour == null ? new NullBehaviour() : behaviour;
        this.behaviour.attach(this);

        checkFullyCreated();
    }

    public Behaviour getBehaviour()
    {
        return this.behaviour;
    }

    public Appearance getAppearance()
    {
        return this.appearance;
    }

    public boolean isActive()
    {
        return this.active;
    }

    public void setActivationDelay( double value )
    {
        this.activationDelay = value;
    }

    @Property(label = "Activation Delay")
    public double getActivationDelay()
    {
        return this.activationDelay;
    }

    public void activateAfter( final double seconds )
    {
        this.setBehaviour(new DelayedActivation(seconds, this.getBehaviour()));
    }

    /**
     * @return true if kill has been called.
     */
    public boolean isDead()
    {
        return this.dead;
    }

    /**
     * Called when the actor is no longer wanted. It will be removed from its Layer (during the next
     * frame rendering), and therefore will not be visible. It will be deactivated (i.e. its tick
     * method won't be called any more) It will have all of its tags removed.
     * 
     * Note, you must not try to resurrect an Actor once it has been killed, instead create a new
     * Actor.
     */
    public void kill()
    {
        if (!this.dead) {
            this.dead = true;
            getBehaviour().die();

            resetCollisionStrategy();
        }
    }

    public boolean isDying()
    {
        return this.dying;
    }

    public Surface getSurface()
    {
        return this.appearance.getSurface();
    }

    public void setX( double x )
    {
        this.x = x;
        this.appearance.onMoved();
    }

    public void setY( double y )
    {
        this.y = y;
        this.appearance.onMoved();
    }

    public void moveTo( Actor other )
    {
        this.moveTo(other.getX(), other.getY());
    }

    public void moveTo( double x, double y )
    {
        this.x = x;
        this.y = y;
        this.appearance.onMoved();
    }

    public void moveBy( double x, double y )
    {
        this.x += x;
        this.y += y;
        this.appearance.onMoved();
    }

    public void moveForwards( double amount )
    {
        double theta = this.getHeadingRadians();
        double cosa = Math.cos(theta);
        double sina = Math.sin(theta);

        if (this.getYAxisPointsDown()) {
            this.moveBy((cosa * amount), (-sina * amount));
        } else {
            this.moveBy((cosa * amount), (sina * amount));
        }

    }

    public void moveForwards( double forward, double sideways )
    {
        double theta = this.getHeadingRadians();
        double cosa = Math.cos(theta);
        double sina = Math.sin(theta);

        if (this.getYAxisPointsDown()) {
            this.moveBy((cosa * forward) - (sina * sideways), (-sina * forward) - (cosa * sideways));
        } else {
            this.moveBy((cosa * forward) - (sina * sideways), (sina * forward) + (cosa * sideways));
        }

    }

    public void moveTowards( Actor actor, double amount )
    {
        double dx = actor.x - this.x;
        double dy = actor.y - this.y;

        double scale = Math.sqrt(dx * dx + dy * dy);
        if (scale == 0) {
            return;
        }
        this.moveBy(dx * amount / scale, dy * amount / scale);
    }

    public double distance( Actor other )
    {
        double dx = this.x - other.x;
        double dy = this.y - other.y;

        return Math.sqrt(dx * dx + dy * dy);
    }

    public void play( String soundName )
    {
        this.costume.getSound(soundName).play();
    }

    public boolean contains( int x, int y )
    {
        return this.getAppearance().getWorldRectangle().contains(x, y);
    }

    public static Behaviour nearest( double x, double y, String tag )
    {
        Behaviour closestBehaviour = null;
        double closestDistance = Double.MAX_VALUE;

        for (Behaviour otherBehaviour : Behaviour.allByTag(tag)) {
            Actor other = otherBehaviour.getActor();
            double distance = other.distanceTo(x, y);
            if (distance < closestDistance) {
                closestDistance = distance;
                closestBehaviour = otherBehaviour;
            }
        }
        return closestBehaviour;
    }

    /**
     * If there are a large number of Actors with this tag, then this will be slow, because unlike
     * overalpping and touching, there is no optimisation based on CollisionStrategy.
     */
    public Behaviour nearest( String tag )
    {
        Behaviour closestBehaviour = null;
        double closestDistance = Double.MAX_VALUE;

        for (Behaviour otherBehaviour : Behaviour.allByTag(tag)) {
            Actor other = otherBehaviour.getActor();
            if (other != this) {
                double distance = other.distanceTo(this);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestBehaviour = otherBehaviour;
                }
            }
        }
        return closestBehaviour;
    }

    public double distanceTo( double x, double y )
    {
        return Math.sqrt((this.x - x) * (this.x - x) + (this.y - y) * (this.y - y));
    }

    public double distanceTo( Actor other )
    {
        return Math.sqrt((this.x - other.x) * (this.x - other.x) + (this.y - other.y) *
            (this.y - other.y));
    }

    public double directionOf( Actor other )
    {
        return Math.atan2(other.y - this.y, other.x - this.x) * 180.0 / Math.PI;
    }

    public boolean overlapping( Actor other )
    {
        if (this.appearance.getWorldRectangle().overlaps(other.appearance.getWorldRectangle())) {
            return true;
        }
        return false;
    }

    public boolean isText()
    {
        return !(getAppearance().getPose() instanceof ImagePose);
    }

    /**
     * For an Actor displaying text, this is the same as the method 'contains', but for other actors
     * (displaying an image), it is the same as the method 'pixelOverlap'.
     * 
     * This should be used whenever you want to know if the mouse is clicking the actor.
     */
    public boolean hitting( int x, int y )
    {
        if (isText()) {
            return this.contains(x, y);
        } else {
            return this.pixelOverlap(x, y);
        }
    }

    public boolean pixelOverlap( int x, int y )
    {
        return this.pixelOverlap(x, y, 0);
    }

    public boolean pixelOverlap( int x, int y, int alphaThreashold )
    {
        if (this.getAppearance().getWorldRectangle().contains(x, y)) {

            Surface surface = this.getAppearance().getSurface();
            if (surface.hasAlphaChannel()) {

                double px = x - this.getX() + this.getAppearance().getOffsetX();
                double py;
                if (this.getYAxisPointsDown()) {
                    py = y - this.getY() + this.getAppearance().getOffsetY();
                } else {
                    py = this.getAppearance().getOffsetY() - y + this.getY();
                }
                RGBA color = surface.getPixelRGBA((int) px, (int) py);
                return color.a > alphaThreashold;

            } else {
                return true;
            }

        }
        return false;
    }

    public boolean pixelOverlap( Actor other )
    {
        int dx = ((int) this.getX() - this.appearance.getOffsetX()) -
            ((int) (other.getX()) - other.appearance.getOffsetX());
        int dy = this.getYAxisPointsDown() ? ((int) this.getY() - this.appearance.getOffsetY()) -
            ((int) (other.getY()) - other.appearance.getOffsetY())
            : ((int) -this.getY() - this.appearance.getOffsetY()) -
                ((int) (-other.getY()) - other.appearance.getOffsetY());

        return this.getAppearance().getSurface()
            .pixelOverlap(other.getAppearance().getSurface(), dx, dy, 64);

    }

    public void resetCollisionStrategy()
    {
        this.setCollisionStrategy(null);
    }

    public CollisionStrategy getCollisionStrategy()
    {
        return this.collisionStrategy;
    }

    public void setCollisionStrategy( CollisionStrategy collisionStrategy )
    {
        this.collisionStrategy.remove();
        this.collisionStrategy = collisionStrategy == null ? BruteForceCollisionStrategy.singleton : collisionStrategy;
    }

    public Set<Behaviour> pixelOverlap( String tag )
    {
        return this.collisionStrategy.pixelOverlap(this, new String[] { tag }, null);
    }

    public Set<Behaviour> pixelOverlap( String... tags )
    {
        return this.collisionStrategy.pixelOverlap(this, tags, null);
    }

    public Set<Behaviour> pixelOverlap( String[] including, String[] excluding )
    {
        return this.collisionStrategy.pixelOverlap(this, including, excluding);
    }

    public Set<Behaviour> overlapping( String tag )
    {
        return this.collisionStrategy.overlapping(this, new String[] { tag }, null);
    }

    public Set<Behaviour> overlapping( String... tags )
    {
        return this.collisionStrategy.overlapping(this, tags, null);
    }

    public Set<Behaviour> overlapping( String[] including, String[] excluding )
    {
        return this.collisionStrategy.overlapping(this, including, excluding);
    }

    public void zOrderUp()
    {
        if (this.layer != null) {
            this.layer.zOrderUp(this);
        }
    }

    public void zOrderDown()
    {
        if (this.layer != null) {
            this.layer.zOrderDown(this);
        }
    }

    public void zOrderTop()
    {
        if (this.layer != null) {
            this.layer.addTop(this);
        }
    }

    public void zOrderBottom()
    {
        if (this.layer != null) {
            this.layer.addBottom(this);
        }
    }

    @Property(label = "Z Order")
    public int getZOrder()
    {
        return this.zOrder;
    }

    public void setZOrder( int value )
    {
        if (this.zOrder != value) {
            this.zOrder = value;
            if (this.layer != null) {
                this.layer.add(this);
            }
        }
    }

    void setZOrderAttribute( int value )
    {
        this.zOrder = value;
    }

    public void adjustZOrder( int delta )
    {
        setZOrder(this.zOrder + delta);
    }

    public void tick()
    {
        if (this.behaviour != null) {
            this.behaviour.animateAndTick();
        }
    }

    @Override
    public String toString()
    {
        return "Actor #" + this.id + " @ " + getX() + "," + getY() +
            " size(" + this.getAppearance().getWidth() + "," + this.getAppearance().getHeight() +
            ") " +
            (getBehaviour() == null ? "" : "(" + getBehaviour().getClass().getName() + ")");
    }

    @Override
    public List<AbstractProperty<Actor, ?>> getProperties()
    {
        return properties;
    }

}
