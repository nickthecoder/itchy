/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.animation.AbstractAnimation;
import uk.co.nickthecoder.itchy.animation.Animation;
import uk.co.nickthecoder.itchy.animation.CompoundAnimation;
import uk.co.nickthecoder.itchy.collision.PixelCollisionTest;
import uk.co.nickthecoder.itchy.property.DoubleProperty;
import uk.co.nickthecoder.itchy.property.IntegerProperty;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.PropertySubject;
import uk.co.nickthecoder.itchy.property.StringProperty;
import uk.co.nickthecoder.itchy.role.PlainRole;
import uk.co.nickthecoder.itchy.util.ClassName;
import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Surface;

public class Actor implements PropertySubject<Actor>
{
    protected static final List<Property<Actor, ?>> properties = new ArrayList<Property<Actor, ?>>();

    static {
        properties.add(new DoubleProperty<Actor>("x"));
        properties.add(new DoubleProperty<Actor>("y"));
        properties.add(new DoubleProperty<Actor>("heading"));
        properties.add(new StringProperty<Actor>("startEvent").defaultValue("default"));
        properties.add(new DoubleProperty<Actor>("activationDelay"));
        properties.add(new IntegerProperty<Actor>("zOrder"));        
        properties.add(new StringProperty<Actor>("role.id"));
    }

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
        TextStyle textStyle = costume.getTextStyle(name);
        if (textStyle == null) {
            return ImagePose.getDummyPose();
        }
        return new TextPose(text, textStyle);
    }

    private static int nextId = 1;

    public final int id;

    Role role;

    private Animation animation;

    private final Appearance appearance;

    private Costume costume;

    private Stage stage;

    private double x;
    private double y;
    private boolean active = false;
    private boolean dead = false;
    private boolean dying = false;
    private int zOrder = 0;

    private double heading;

    private double activationDelay;

    private String startEvent = "default";

    private boolean fullyCreated = false;

    public Actor( Costume costume )
    {
        this(costume, "default");
    }

    public Actor( Costume costume, String poseName )
    {
        // Note, that we only set the pose based on poseName, we do NOT set the animation, or
        // play a sound. If an event should be played on birth, then it must be done explicitly
        // by calling theActor.event(eventName) after theActor has been created.
        this(startPose(costume, poseName));
        this.costume = costume;
    }

    public Actor( Pose pose )
    {
        this.id = nextId;
        nextId++;
        this.costume = null;
        this.appearance = new Appearance(pose);
        this.appearance.setActor(this);

        this.role = null;

        this.x = 0;
        this.y = 0;
        this.setDirection(pose.getDirection());
    }
    
    public String getStartEvent()
    {
        return this.startEvent;
    }

    public void setStartEvent( String value )
    {
        this.startEvent = value;
    }

    /**
     * Sets the heading that the actor is travelling in. It does NOT affect the rotation of the actor's image.
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

    public double getHeading()
    {
        return this.heading;
    }

    public double getHeadingRadians()
    {
        return this.heading / 180 * Math.PI;
    }

    public void adjustHeading( double degrees )
    {
        this.setHeading(this.heading + degrees);
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
     * @param radians
     *        The new heading in radians
     */
    public void setDirectionRadians( double radians )
    {
        setDirection(radians * 180 / Math.PI);
    }

    public double getDirection()
    {
        return this.getAppearance().getDirection();
    }

    /**
     * Adjusts the heading by the given amount, and points the image in that direction too.
     * 
     * @param degrees
     *        The number of degrees to turn by (positive is anticlockwise).
     */
    public void adjustDirection( double degrees )
    {
        adjustHeading(degrees);
        getAppearance().setDirection(this.heading);
    }

    public Costume getCostume()
    {
        return this.costume;
    }

    public void setCostume( Costume costume )
    {
        this.costume = costume;
    }

    public Actor createCompanion( String name )
    {
        return createCompanion(name, "default");
    }

    public Actor createCompanion( String name, String startEvent )
    {
        Costume costume;
        costume = this.costume.getCompanion(name);
        if ( costume == null ) {
            costume = Itchy.getGame().resources.getCompanionCostume(this.costume, name);
        }
        Actor actor = costume.createActor(startEvent);
        actor.moveTo(this);
        getStage().add(actor);

        return actor;
    }

    public double getCornerY()
    {
        return this.getY() - this.getAppearance().getSurface().getHeight() + this.getAppearance().getOffsetY();
    }

    public double getCornerX()
    {
        return this.getX() - this.getAppearance().getOffsetX();
    }

    public Stage getStage()
    {
        return this.stage;
    }

    public void removeFromStage()
    {
        setStage(null);
    }

    public void setStage( Stage stage )
    {
        if (this.stage == stage) {
            return;
        }

        if (this.stage != null) {
            this.stage.remove(this);
        }

        if (stage != null) {
            stage.add(this);
        }
    }

    /**
     * Called by a Stage, AFTER the actor is added to their collection. The order is vital to ensure that the stage and the actor
     * are fully formed when onBirth is called.
     * Also called with a null value just BEFORE the actor is removed from a stage's collection. This order isn't so important.
     * @param stage
     */
    void setStageAttribute( Stage stage )
    {
        /*
        // Stages should always add the actor to their collection before calling setStageAttribute
        if (stage != null) {
            if (!stage.getActors().contains(this)) {
                throw new RuntimeException( "Setting stage attribute for a stage which doesn't contain me.");
            }
        }
        */
        this.stage = stage;
        checkFullyCreated();
    }

    public void setAnimation( Animation animation )
    {
        setAnimation(animation, AnimationEvent.FAST_FORWARD);
    }

    public void setAnimation( Animation animation, AnimationEvent ae )
    {

        if (animation == null) {
            if (ae == AnimationEvent.FAST_FORWARD) {
                // If we are trying to FAST_FORWARD the old animation with null, then stop the old animation if there is one.
                if ( (this.animation != null) && ( ! this.animation.isFinished()) ) {
                    this.animation.fastForward(this);
                }
                this.animation = null;
                return;
                
            } else if (ae == AnimationEvent.REPLACE) {
                this.animation = null;
                return;
                
            } else {
                // If we are trying to merge the old animation with null, then just let the old animation continue.
                // We also do nothing when ae==IGNORE
                return;
            }
        }

        Animation newAnimation;

        // What do we do when an animation is in progress. Either replace it, ignore the new animation or merge them.
        if ((this.animation == null) || (ae == AnimationEvent.REPLACE)) {
            newAnimation = animation.copy();

            if ( (this.animation != null) && ( ! this.animation.isFinished()) ) {
                this.animation.fastForward(this);
            }

            newAnimation.start(this);
            
        } else if (ae == AnimationEvent.IGNORE) {
            return;

        } else {
            // Merge the two animations (either in sequence or in parallel, depending on "ae")
            CompoundAnimation ca = new CompoundAnimation(ae == AnimationEvent.SEQUENCE);
            ca.add(this.getAnimation());
            ca.add(animation.copy());
            ca.startExceptFirst(this);
            newAnimation = ca;
        }

        this.animation = newAnimation;
        AbstractAnimation.tick(this.animation,this);
    }

    public Animation getAnimation()
    {
        return this.animation;
    }

    public enum AnimationEvent
    {
        REPLACE,
        FAST_FORWARD,
        SEQUENCE,
        PARALLEL,
        IGNORE
    }

    public void event( String eventName )
    {
        this.event(eventName, null, AnimationEvent.REPLACE);
    }

    public void event( String eventName, String message )
    {
        this.event(eventName, message, AnimationEvent.REPLACE);
    }

    public void event( String eventName, String message, AnimationEvent ae  )
    {
        if (this.costume == null) {
            return;
        }
        Pose pose = this.costume.getPose(eventName);
        if (pose != null) {
            this.appearance.setPose(pose);
        }

        Animation animation = this.costume.getAnimation(eventName);
        if ( message != null ) {
            if (animation == null) {
                // If there is no animation, but a completion message is specified, then send the message straight away.
                this.getRole().onMessage(message);
            } else {
                animation = animation.copy();
                animation.setFinishedMessage(message);
            }
        }
        this.setAnimation(animation, ae);
        
        ManagedSound cs = this.costume.getCostumeSound(eventName);
        if (cs != null) {
            Itchy.soundManager.play(this, eventName, cs);
        }
    }

    public void deathEvent( String eventName )
    {
        deathEvent(eventName, null, AnimationEvent.REPLACE);
    }

    public void deathEvent( String eventName, String message )
    {
        deathEvent(eventName, message, AnimationEvent.REPLACE);
    }

    public void deathEvent( String eventName, String message, AnimationEvent ae )
    {
        this.dying = true;
        this.event(eventName, message, ae);
        if ((this.costume == null) || (this.costume.getAnimation(eventName) == null)) {
            this.kill();
        }
    }

    /**
     * Will fade out or stop sounds corresponding to the given even name. Future versions of Itchy may also stop corresponding animations.
     * 
     * @param eventName
     */
    public void endEvent( String eventName )
    {
        Itchy.soundManager.end(this, eventName);
    }

    /**
     * @return true iff not dying or dead.
     */
    public boolean isAlive()
    {
        return !(this.dying || this.dead);
    }

    public double getX()
    {
        return this.x;
    }

    public double getY()
    {
        return this.y;
    }

    private void checkFullyCreated()
    {
        if (this.fullyCreated) {
            return;
        }

        if ((this.stage != null) && (this.role != null)) {
            this.fullyCreated = true;
            this.role.born();
        }
    }

    /**
     * Called by Delayed activation, when the actor is to be activated.
     */
    void activate( Role role )
    {
        this.role = role;
        this.event(getStartEvent());
        this.role = null;
        this.setRole(role);

        // The normal way that role.birth is called happened to this DelayedActivation,
        // but we want it to be called for the actual role as well...
        role.born();
    }

    public final void setRole( Role role )
    {
        if (role == this.role) {
            return;
        }

        if (this.role != null) {
            this.role.detatched();
        }

        this.role = role == null ? new PlainRole() : role;
        this.role.attached(this);

        if (this.stage != null) {
            this.stage.changedRole(this);
        }

        checkFullyCreated();
    }

    public Role getRole()
    {
        return this.role;
    }

    public ClassName getRoleClassName()
    {
        return this.role.getClassName();
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

    public double getActivationDelay()
    {
        return this.activationDelay;
    }

    /**
     * @return true if kill has been called.
     */
    public boolean isDead()
    {
        return this.dead;
    }

    /**
     * Called when the actor is no longer wanted. It will be removed from its Layer (during the next frame rendering), and therefore will
     * not be visible. It will be deactivated (i.e. its tick method won't be called any more) It will have all of its tags removed.
     * 
     * Note, you must not try to resurrect an Actor once it has been killed, instead create a new Actor.
     */
    public void kill()
    {
        if (!this.dead) {
            this.dead = true;
            if (this.role != null) {
                this.role.killed();
            }
            
            if (this.stage != null) {
                this.stage.remove(this);
            }
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
        this.appearance.invalidatePosition();
    }

    public void setY( double y )
    {
        this.y = y;
        this.appearance.invalidatePosition();
    }

    public void moveTo( Actor other )
    {
        this.moveTo(other.getX(), other.getY());
    }

    public void moveTo( double x, double y )
    {
        this.x = x;
        this.y = y;
        this.appearance.invalidatePosition();
    }

    public void moveBy( double x, double y )
    {
        this.x += x;
        this.y += y;
        this.appearance.invalidatePosition();
    }

    public void moveForwards( double amount )
    {
        double theta = this.getHeadingRadians();
        double cosa = Math.cos(theta);
        double sina = Math.sin(theta);

        this.moveBy((cosa * amount), (sina * amount));
    }

    public void moveForwards( double forward, double sideways )
    {
    	this.moveAngle( this.getHeading(), forward, sideways);
    }

    public void moveAngle( double degrees, double amount )
    {
        double theta = degrees / 180 * Math.PI;
        double cosa = Math.cos(theta);
        double sina = Math.sin(theta);

        this.moveBy((cosa * amount), (sina * amount));
    }

    public void moveAngle( double degrees, double forward, double sideways )
    {
    	double theta = degrees / 180 * Math.PI;
        double cosa = Math.cos(theta);
        double sina = Math.sin(theta);

        this.moveBy((cosa * forward) - (sina * sideways), (sina * forward) + (cosa * sideways));
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

    public static Role nearest( double x, double y, String tag )
    {
        Role closestRole = null;
        double closestDistance = Double.MAX_VALUE;

        for (Role otherRole : AbstractRole.allByTag(tag)) {
            Actor other = otherRole.getActor();
            double distance = other.distanceTo(x, y);
            if (distance < closestDistance) {
                closestDistance = distance;
                closestRole = otherRole;
            }
        }
        return closestRole;
    }

    /**
     * If there are a large number of Actors with this tag, then this will be slow, because unlike overalpping and touching, there is no
     * optimisation based on CollisionStrategy.
     */
    public Role nearest( String tag )
    {
        Role closestRole = null;
        double closestDistance = Double.MAX_VALUE;

        for (Role otherRole : AbstractRole.allByTag(tag)) {
            Actor other = otherRole.getActor();
            if (other != this) {
                double distance = other.distanceTo(this);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestRole = otherRole;
                }
            }
        }
        return closestRole;
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

    public double directionOf( double x, double y )
    {
        return Math.atan2(y - this.y, x - this.x) * 180.0 / Math.PI;
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
     * For an Actor displaying text, this is the same as the method 'contains', but for other actors (displaying an image), it is the same
     * as the method 'pixelOverlap'.
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
        return this.pixelOverlap(x, y, PixelCollisionTest.DEFAULT_THRESHOLD);
    }

    public boolean pixelOverlap( int x, int y, int alphaThreashold )
    {
        if (this.getAppearance().getWorldRectangle().contains(x, y)) {

            Surface surface = this.getAppearance().getSurface();
            if (surface.hasAlphaChannel()) {

                double px = x - this.getX() + this.getAppearance().getOffsetX();
                double py = this.getAppearance().getOffsetY() - y + this.getY();
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
        return pixelOverlap( other, 1 );
    }
    
    public boolean pixelOverlap( Actor other, int threshold )
    {
        int dx = ((int) this.getX() - this.appearance.getOffsetX()) - ((int) (other.getX()) - other.appearance.getOffsetX());
        int dy = ((int) -this.getY() - this.appearance.getOffsetY()) - ((int) (-other.getY()) - other.appearance.getOffsetY());

        return this.getAppearance().getSurface()
            .pixelOverlap(other.getAppearance().getSurface(), dx, dy, threshold);
    }

    public int getZOrder()
    {
        return this.zOrder;
    }

    public void setZOrder( int value )
    {
        if (this.zOrder != value) {
            if (this.stage == null) {
                this.zOrder = value;
            } else {
                Stage stage = this.stage;
                stage.remove(this);
                this.zOrder = value;
                stage.add(this);
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
        if (this.role != null) {
            try {

                this.role.animate();

                if ((!this.isDead()) && (!this.isDying())) {
                    if (this.role.getActor() == null) {
                        // The role may have changed due to an animation's finishedMessage.
                        return;
                    }
                    this.role.tick();
                }

            } catch (Exception e) {
                Itchy.handleException(e);
            }
        }
    }

    @Override
    public String toString()
    {
        return "Actor #" + this.id + " @ " + getX() + "," + getY() + " " +
            (getRole() == null ? "" : "(" + getRole().getClass().getName() + ")");
    }

    public String info()
    {
        return "Actor #" + this.id + " @ " + getX() + "," + getY() +
            " size(" + this.getAppearance().getWidth() + "," + this.getAppearance().getHeight() +
            ") " +
            (getRole() == null ? "" : "(" + getRole().getClass().getName() + ")");
    }

    @Override
    public List<Property<Actor, ?>> getProperties()
    {
        return properties;
    }

}
