/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import uk.co.nickthecoder.itchy.animation.Animation;
import uk.co.nickthecoder.itchy.util.AbstractProperty;
import uk.co.nickthecoder.itchy.util.DoubleProperty;
import uk.co.nickthecoder.itchy.util.FontProperty;
import uk.co.nickthecoder.itchy.util.RGBAProperty;
import uk.co.nickthecoder.itchy.util.StringProperty;
import uk.co.nickthecoder.itchy.util.TagCollection;
import uk.co.nickthecoder.itchy.util.TagMembership;
import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Sound;
import uk.co.nickthecoder.jame.Surface;

public class Actor extends Task
{
    // private static Map<Class,Integer> _nextIds = new HashMap<Class,Integer>();
    private static int _nextId = 1;

    private int _id;

    private static List<AbstractProperty<Actor, ?>> normalProperties;
    private static List<AbstractProperty<Actor, ?>> textProperties;

    Behaviour behaviour;

    private Animation animation;

    private final Appearance appearance;

    private Costume costume;

    private ActorsLayer layer;

    private static TagCollection<Actor> actorTags = new TagCollection<Actor>();

    private final TagMembership<Actor> tagMembership;

    private double x;
    private double y;
    private boolean active = false;
    private boolean dead = false;
    private boolean dying = false;

    private boolean ticking = false;

    private double activationDelay;
    
    private String startEvent = "default";

    public Actor( Costume costume )
    {
        this(costume, "default");
    }

    private static Pose startPose( Costume costume, String name )
    {
        Pose pose = costume.getPose(name);
        if ( pose == null ) {
            pose = costume.getPose("default");
        }
        if (pose == null ) {
            pose = ImagePose.getDummyPose();
        }
        return pose;
    }
    
    public Actor( Costume costume, String eventName )
    {
        this(startPose(costume, eventName));
        
        this.costume = costume;
        this.event(eventName);
        this.getAppearance().setDirection(this.getAppearance().getPose().getDirection());
    }

    public Actor( Pose pose )
    {
        this._id = _nextId;
        _nextId++;

        this.costume = null;
        this.appearance = new Appearance(pose);
        this.tagMembership = new TagMembership<Actor>(actorTags, this);
        this.appearance.setActor(this);

        this.setBehaviour(new NullBehaviour());

        this.x = 0;
        this.y = 0;
        this.getAppearance().setDirection(pose.getDirection());
    }

    public String getStartEvent()
    {
        return startEvent;
    }
    
    public void setStartEvent( String value )
    {
        startEvent = value;
    }

    public List<AbstractProperty<Actor, ?>> getProperties()
    {
        if (normalProperties == null) {
            normalProperties = new ArrayList<AbstractProperty<Actor, ?>>();

            normalProperties.add(new DoubleProperty<Actor>("X", "x"));
            normalProperties.add(new DoubleProperty<Actor>("Y", "y"));
            normalProperties.add(new StringProperty<Actor>("Start Event", "startEvent"));
            normalProperties.add(new DoubleProperty<Actor>("Alpha", "appearance.alpha"));
            normalProperties.add(new DoubleProperty<Actor>("Direction", "appearance.direction"));
            normalProperties.add(new DoubleProperty<Actor>("Scale", "appearance.scale"));
            normalProperties.add(new RGBAProperty<Actor>("Colorize", "appearance.colorize", true,
                true));
            normalProperties.add(new DoubleProperty<Actor>("Activation Delay", "activationDelay"));

            textProperties = new ArrayList<AbstractProperty<Actor, ?>>();
            textProperties.addAll(normalProperties);
            textProperties.add(new FontProperty<Actor>("Font", "appearance.pose.font"));
            textProperties.add(new DoubleProperty<Actor>("Font Size", "appearance.pose.fontSize"));
            textProperties.add(new StringProperty<Actor>("Text", "appearance.pose.text"));
            textProperties.add(new RGBAProperty<Actor>("Text Color", "appearance.pose.color",
                false, false));

        }

        if (this.getAppearance().getPose() instanceof TextPose) {
            return textProperties;
        } else {
            return normalProperties;
        }
    }

    public Costume getCostume()
    {
        return this.costume;
    }

    public void setCostume( Costume costume )
    {
        this.costume = costume;
    }

    public static Set<Actor> allByTag( String tag )
    {
        return actorTags.getTagMemberships(tag);
    }

    public boolean hasTag( String name )
    {
        return this.tagMembership.hasTag(name);
    }

    public void addTag( String tag )
    {
        this.tagMembership.add(tag);
    }

    public void removeTag( String tag )
    {
        this.tagMembership.remove(tag);
    }

    public void removeAllTags()
    {
        this.tagMembership.removeAllExcept("active");
    }

    public boolean getYAxisPointsDown()
    {
        if (this.layer == null) {
            return true;
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

    void setLayer( ActorsLayer layer )
    {
        if (this.layer != null) {
            this.layer = null;
        }
        this.layer = layer;
    }

    public void setAnimation( Animation animation )
    {
        if (animation != null) {
            this.animation = animation.copy();
            this.animation.start(this);
            this.animation.tick(this);
            this.animation.addMessageListener(getBehaviour());

        } else {
            this.animation = null;
        }
    }

    public Animation getAnimation()
    {
        return this.animation;
    }

    public void event( String eventName )
    {
        this.event(this.costume, eventName);
    }

    public void event( Costume costume, String eventName )
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
        if (animation != null) {
            this.setAnimation(animation);
        }

        Sound sound = costume.getSound(eventName);
        if (sound != null) {
            sound.play();
        }
    }

    public void deathEvent( String eventName )
    {
        this.deathEvent(this.costume, eventName);
    }

    public void deathEvent( Costume costume, String eventName )
    {
        this.dying = true;
        this.event(costume, eventName);
        if ((costume == null) || (costume.getAnimation(eventName) == null)) {
            this.kill();
        }
    }

    public void deathAnimation( String animationName )
    {
        Animation animation = Itchy.singleton.getResources().getAnimation(animationName);
        if (animation == null) {
            this.kill();
        } else {
            this.dying = true;
            this.setAnimation(animation);
        }
    }

    public double getX()
    {
        return this.x;
    }

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

    public final void setBehaviour( Behaviour behaviour )
    {
        this.behaviour = behaviour;
        behaviour.attach(this);
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

    public double getActivationDelay()
    {
        return this.activationDelay;
    }

    public void activateAfter( final double seconds )
    {
        Itchy.singleton.gameLoopJob.add(new Task() {
            @Override
            public void run()
            {
                sleep(seconds);
                activate();
            }
        });
    }

    public void activate()
    {
        if ((!this.dead) && (!this.active)) {
            this.addTag("active");
            this.active = true;
            Itchy.singleton.gameLoopJob.add(new Task() {
                @Override
                public void run()
                {
                    Actor.this.getBehaviour().onActivate();
                }
            });
        }
    }

    /**
     * Prevents the actors tick method from being called. The actor may still be visible, and be
     * accessible through tags etc.
     */
    public void deactivate()
    {
        if (this.active) {
            this.active = false;
            this.removeTag("active");
            Itchy.singleton.gameLoopJob.add(new Task() {
                @Override
                public void run()
                {
                    Actor.this.getBehaviour().onDeactivate();
                }
            });
        }
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
            this.deactivate();
            Itchy.singleton.addTask(new Task() {
                @Override
                public void run()
                {
                    Actor.this.getBehaviour().onKill();
                }
            });
            this.tagMembership.removeAll();
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

    public void moveForward( double amount )
    {
        double theta = this.appearance.getDirection() / 180.0 * Math.PI;
        double cosa = Math.cos(theta);
        double sina = Math.sin(theta);

        if (this.getYAxisPointsDown()) {
            this.moveBy((cosa * amount), (-sina * amount));
        } else {
            this.moveBy((cosa * amount), (sina * amount));
        }

    }

    public void moveForward( double forward, double sideways )
    {
        double theta = this.appearance.getDirectionRadians();
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
        if ( scale == 0) {
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

    /**
     * If there are a large number of Actors with this tag, then this will be slow, because unlike
     * overalpping and touching, there is no optimisation based on CollisionStrategy.
     */
    public Actor nearest( String tag )
    {
        Actor closestActor = null;
        double closestDistance = Double.MAX_VALUE;

        for (Actor other : Actor.allByTag(tag)) {
            if (other != this) {
                double distance = other.distanceTo(this);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestActor = other;
                }
            }
        }
        return closestActor;
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

    /**
     * For an Actor displaying text, this is the same as the method 'contains', but for
     * other actors (displaying an image), it is the same as the method 'touching'.
     * 
     * This should be used whenever you want to know if the mouse is clicking the actor.
     */
    public boolean hitting( int x, int y )
    {
        if ( this.getAppearance().getAlpha() < 1 ) {
            return false;
        }
        
        if ( getAppearance().getPose() instanceof ImagePose ) {
            return this.touching(x,y);
        } else {
            return this.contains(x,y);
        }
    }
    
    public boolean touching( int x, int y )
    {
        return this.touching(x, y, 0);
    }

    public boolean touching( int x, int y, int alphaThreashold )
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

    public boolean touching( Actor other )
    {
        int dx = ((int) this.getX() - this.appearance.getOffsetX()) -
            ((int) (other.getX()) - other.appearance.getOffsetX());
        int dy = this.getYAxisPointsDown() ? ((int) this.getY() - this.appearance.getOffsetY()) -
            ((int) (other.getY()) - other.appearance.getOffsetY())
            : ((int) -this.getY() - this.appearance.getOffsetY()) -
                ((int) (-other.getY()) - other.appearance.getOffsetY());

        return this.getAppearance().getSurface()
            .overlaps(other.getAppearance().getSurface(), dx, dy, 64);

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
            this.layer.zOrderTop(this);
        }
    }

    public void zOrderBottom()
    {
        if (this.layer != null) {
            this.layer.zOrderBottom(this);
        }
    }

    public void tick()
    {
        // If a task sleeps during a tick, don't allow it to tick again until it awakes from its
        // sleep.
        if (this.ticking) {
            return;
        }

        try {
            this.ticking = true;

            if (this.animation != null) {

                this.animation.tick(this);
                if (this.animation.isFinished()) {
                    this.setAnimation(null);
                    if (this.dying) {
                        this.kill();
                        return;
                    }
                }
            }
            if (!this.dead) {
                this.behaviour.tick();
            }
        } finally {
            this.ticking = false;
        }
    }

    @Override
    public String toString()
    {
        return "Actor #" + this._id + " @ " + getX() + "," + getY() +
            " size(" + this.getAppearance().getWidth() + "," + this.getAppearance().getHeight() +
            ") " +
            (getBehaviour() == null ? "" : "(" + getBehaviour().getClass().getName() + ")");
    }

    @Override
    public void run()
    {
        this.tick();
    }
}
