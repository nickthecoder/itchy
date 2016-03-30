/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.animation.Animation;
import uk.co.nickthecoder.itchy.animation.CompoundAnimation;
import uk.co.nickthecoder.itchy.collision.CollisionStrategy;
import uk.co.nickthecoder.itchy.editor.Editor;
import uk.co.nickthecoder.itchy.editor.SceneDesigner;
import uk.co.nickthecoder.itchy.property.DoubleProperty;
import uk.co.nickthecoder.itchy.property.IntegerProperty;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.PropertySubject;
import uk.co.nickthecoder.itchy.property.StringProperty;
import uk.co.nickthecoder.itchy.role.PlainRole;
import uk.co.nickthecoder.itchy.util.ClassName;
import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Surface;

/**
 * Actors are at the very core of Itchy, every game object is an Actor. For example, in the space invaders game,
 * each space invader is an actor, so is your ship, the bullets and the shields. More surprisingly, the score is also an
 * actor, as well as the background image.
 * <p>
 * Each Actor has a {@link Role} which defines the behaviour of the Actor. Most of your game code will within Roles.
 * <p>
 * Each Actor has its own {@link Appearance}, which holds information such as the scale (size), its angle (if it has
 * been rotated) and its alpha (if it is semi-transparent).
 * <p>
 * Actors also have a {@link Costume}. A Costume may be shared by many Actors. For example in space invaders, your ship
 * has one Costume, and all of your ship's bullets share another Costume. Costumes hold an image (or a set of images),
 * which are called {@link Pose}, but also have other goodies such as sounds.
 * <p>
 * Actors are added to a {@link Stage}, and the Stage is displayed on the screen via a {@link StageView}. Most of the
 * time, you don't need to worry about Stages, or StageViews, because Actors are added to Stages automatically when the
 * scene is loaded.
 * 
 */
final public class Actor implements PropertySubject<Actor>
{
    /**
     * @priority 5
     */
    protected static final List<Property<Actor, ?>> properties = new ArrayList<Property<Actor, ?>>();

    static {
        properties.add(new DoubleProperty<Actor>("x"));
        properties.add(new DoubleProperty<Actor>("y"));
        properties.add(new DoubleProperty<Actor>("heading"));
        properties.add(new StringProperty<Actor>("startEvent").defaultValue("default"));
        properties.add(new DoubleProperty<Actor>("activationDelay"));
        properties.add(new IntegerProperty<Actor>("zOrder"));
        properties.add(new StringProperty<Actor>("id").allowNull(true).defaultValue(null));
    }

    private static Pose startPose(Costume costume, String name)
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

    private static int nextSequenceNumber = 1;

    private final int sequenceNumber;

    private String id;

    private Role role;

    private Animation animation;

    private final Appearance appearance;

    private Costume costume;

    private Stage stage;

    private Point position;

    private boolean active = false;
    private boolean dead = false;
    private boolean dying = false;
    private int zOrder = 0;

    private double heading;

    private double activationDelay;

    private String startEvent = "default";

    private boolean fullyCreated = false;

    /**
     * Create an actor with a costume, using the "default" pose.
     */
    public Actor(Costume costume)
    {
        this(costume, "default");
    }

    /**
     * Create an actor with a {@link Costume}, and choose which of the costume's {@link Pose}s to use.
     * 
     * @param costume
     * @param poseName
     *            The name of the "event" to use as the Actor's {@link Pose}. Note this is NOT the name of a Pose within
     *            the {@link Resources}.
     * @priority 4
     */
    public Actor(Costume costume, String poseName)
    {
        // Note, that we only set the pose based on poseName, we do NOT set the animation, or
        // play a sound. If an event should be played on birth, then it must be done explicitly
        // by calling theActor.event(eventName) after theActor has been created.
        this(startPose(costume, poseName));
        this.costume = costume;
    }

    /**
     * Create an actor without a {@link Costume}
     * 
     * @param pose
     * @priority 4
     */
    public Actor(Pose pose)
    {
        this.sequenceNumber = nextSequenceNumber;
        nextSequenceNumber++;

        this.id = null;
        this.costume = null;
        this.appearance = new Appearance(pose);
        this.appearance.setActor(this);

        this.role = null;

        this.position = new Point(0, 0);
        this.setDirection(pose.getDirection());
    }

    /**
     * @return A unique ID of this actor. This is useful when debugging a game, its a quick and simple way to
     *         keep track of a single actor.
     * @priority 3
     */
    public int getSequenceNumber()
    {
        return this.sequenceNumber;
    }

    /**
     * Each actor can be assigned a String id (on the "Actor" tab within the {@link SceneDesigner}.
     * This is useful when you want to find a specific Actor. For example to make a key unlock a specific door, give the
     * door a unique ID in the Scene Designer. When you collect the key you can find that door using the method
     * {@link Game#findActorById(String)}.
     * <p>
     * Note. It is up to you to ensure that the IDs are unique. If they aren't the behaviour of
     * {@link Game#findActorById(String)} is undefined. If you want to find a set of game objects (for example, if you
     * want a key to open many doors), then use {@link Role#addTag(String)} and {@link Game#findRolesByTag(String)}.
     * 
     * @return The ID
     * @priority 3
     */
    public String getId()
    {
        return this.id;
    }

    /**
     * Sets the Actor's ID. Typically you will use the {@link SceneDesigner} to set the IDs for actors, so this method
     * is rarely used.
     * <p>
     * Find an Actor with a given ID using {@link Game#findRolesByTag(String)}.
     * 
     * @param id
     * @priority 3
     */
    public void setId(String id)
    {
        if (this.id != null) {
            // If there is another actor with this id, don't remove it.
            if (Itchy.getGame().actorsById.get(this.id) == this) {
                Itchy.getGame().actorsById.remove(this.id);
            }
        }

        if (id != null) {
            id = id.trim();
            if ("".equals(id)) {
                id = null;
            }
        }
        this.id = id;

        if (this.id != null) {
            Itchy.getGame().actorsById.put(this.id, this);
        }
    }

    /**
     * Only used by the {@link SceneDesigner}.
     * <p>
     * When a {@link Scene} is loaded, each Actor has a startEvent, which is usually "default", but can be changed
     * within the {@link SceneDesigner}. The startEvent determines which {@link Pose} to use, as well as which
     * {@link Animation} to start, and which {@link ManagedSound} to play when the actor is born.
     * 
     * @return The start event for this Actor
     * @priority 5
     */
    public String getStartEvent()
    {
        return this.startEvent;
    }

    /**
     * Only used by the {@link SceneDesigner}.
     * 
     * @param value
     * @priority 5
     */
    public void setStartEvent(String value)
    {
        this.startEvent = value;
    }

    /**
     * Sets the heading that the actor is travelling in, when used in conjunction with {@link #moveForwards(double)}.
     * It does NOT affect the rotation of the Actor's {@link Pose}. To rotate, use
     * {@link Appearance#setDirectionTowards(Actor)}.
     * To change both the angle of the Pose, and the Actor's heading, use {@link #setDirection(double)}.
     * 
     * @param degrees
     *            The angle in degrees. 0 is to the right (eastwards), 90 is straight up (i.e. anti-clockwise).
     * @priority 1
     */
    public void setHeading(double degrees)
    {
        this.heading = degrees;
    }

    /**
     * Does the same as {@link #setHeading(double)}, but uses radians, rather than degrees. Always use radians when
     * using trigonometric functions cos, sin etc.
     * 
     * @param radians
     * @priority 2
     */
    public void setHeadingRadians(double radians)
    {
        this.setHeading(radians * 180 / Math.PI);
    }

    /**
     * Gets the direction the Actor is heading in, ie the direction it will move when using
     * {@link #moveForwards(double)}.
     * This is NOT the angle of rotation, see {@link Appearance#getDirection()}.
     * 
     * @return The heading in degrees.
     * @priority 1
     */
    public double getHeading()
    {
        return this.heading;
    }

    /**
     * Does the same as {@link #getHeading()}, but uses radians rather than degrees.
     * 
     * @return The heading in radians.
     * @priority 2
     */
    public double getHeadingRadians()
    {
        return this.heading / 180 * Math.PI;
    }

    /**
     * 
     * @param degrees
     * @priority 2
     */
    public void adjustHeading(double degrees)
    {
        this.setHeading(this.heading + degrees);
    }

    /**
     * Sets the heading and the appearance's direction.
     * 
     * @param degrees
     * @priority 2
     */
    public void setDirection(double degrees)
    {
        getAppearance().setDirection(degrees);
        setHeading(getAppearance().getDirection());
    }

    /**
     * Sets the heading and the appearance's direction.
     * 
     * @param radians
     *            The new heading in radians
     * @priority 3
     */
    public void setDirectionRadians(double radians)
    {
        setDirection(radians * 180 / Math.PI);
    }

    /**
     * A handy shortcut for {@link Appearance#getDirection()}
     * 
     * @return The angle of rotation in degrees.
     * @priority 3
     */
    public double getDirection()
    {
        return this.getAppearance().getDirection();
    }

    /**
     * Adjusts both the heading, and the appearance's direction.
     * 
     * @param delta
     * @priority 2
     */
    public void adjustDirection(double delta)
    {
        this.setHeading(this.heading + delta);
        this.appearance.adjustDirection(delta);
    }

    /**
     * @return The Costume, or null if the Actor has no Costume.
     */
    public Costume getCostume()
    {
        return this.costume;
    }

    /**
     * A simple setter.
     * 
     * @param costume
     *            May be null
     * @priority 2
     */
    public void setCostume(Costume costume)
    {
        this.costume = costume;
    }

    /**
     * Looks up this Actor's Costume for a "Companion" Costume using the given eventName, and uses this Companion
     * Costume to create a new Actor.
     * <p>
     * If more than one Costume is found with the given event name, then one is picked at random.
     * 
     * @param eventName
     *            The name used to find the companion costume.
     *            Note, this is NOT the name of a Costume within the {@link Resources}.
     * @return
     *         A newly created Actor at the same place as this Actor, and on the same stage.
     *         Its Costume (and therefore its Pose) is looked up in this Actor's 'companion' event.
     *         Its Role is taken from its Costume.
     */
    public Actor createCompanion(String eventName)
    {
        return createCompanion(eventName, "default");
    }

    /**
     * Does the same as {@link #createCompanion(String)}, but the startEvent does not have to be "default".
     * 
     * @param eventName
     *            The name used to find the companion costume.
     *            Note, this is NOT the name of a Costume within the {@link Resources}.
     * @param startEvent
     *            The start event for the new companion actor (which determines which {@link Pose} is used, and which
     *            {@link Animation} to use as well as a sound to play when the Actor is born.
     * @return
     *         A newly created Actor at the same place as this Actor, and on the same stage.
     *         Its Costume (and therefore its Pose) is looked up in this Actor's 'companion' event.
     *         Its Role is taken from its Costume.
     * @priority 3
     */
    public Actor createCompanion(String eventName, String startEvent)
    {
        Costume costume;
        costume = this.costume.getCompanion(eventName);
        Actor actor = costume.createActor(startEvent);
        actor.moveTo(this);
        getStage().add(actor);

        return actor;
    }

    /**
     * 
     * @return
     * @priority 5
     */
    public double getCornerY()
    {
        return this.getY() - this.getAppearance().getSurface().getHeight() + this.getAppearance().getOffsetY();
    }

    /**
     * 
     * @return
     * @priority 5
     */
    public double getCornerX()
    {
        return this.getX() - this.getAppearance().getOffsetX();
    }

    /**
     * A simple getter
     * 
     * @return The {@link Stage} the Actor is on, or null if the Actor is not on a Stage.
     */
    public Stage getStage()
    {
        return this.stage;
    }

    /**
     * Removes the Actor from its stage.
     * 
     * @priority 3
     */
    public void removeFromStage()
    {
        setStage(null);
    }

    /**
     * Places the actor on a Stage. If the actor is already on a Stage, then it will be removed from that Stage before
     * being
     * added to the new one. An Actor can only be on a single Stage at a time.
     * 
     * @param stage
     *            The Stage to be added to, or null to remove the actor from its current stage.
     */
    public void setStage(Stage stage)
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
     * Called by a Stage, AFTER the actor is added to their collection. The order is vital to ensure that the stage and
     * the actor
     * are fully formed when onBirth is called.
     * Also called with a null value just BEFORE the actor is removed from a stage's collection. This order isn't so
     * important.
     * 
     * @param stage
     * @priority 5
     */
    void setStageAttribute(Stage stage)
    {
        /*
         * // Stages should always add the actor to their collection before calling setStageAttribute
         * if (stage != null) {
         * if (!stage.getActors().contains(this)) {
         * throw new RuntimeException( "Setting stage attribute for a stage which doesn't contain me.");
         * }
         * }
         */
        this.stage = stage;
        checkFullyCreated();
    }

    /**
     * Begins an animation.
     * If an animation is already part way through then the current animation is "fast-forwarded" to the end.
     * See {@link #setAnimation(Animation, AnimationEvent)} if you do not want the fast-forward behaviour.
     * It is more common to begin an animation using {@link #event(String)}, because it is easier, and gives more
     * flexibility.
     * 
     * @param animation
     * @priority 4
     */
    public void setAnimation(Animation animation)
    {
        setAnimation(animation, AnimationEvent.FAST_FORWARD);
    }

    /**
     * Begins an animation.
     * If an animation is already part way through then the {@link AnimationEvent} determines whether the old animation
     * is simply replaced, fast-forwarded, or merged.
     * 
     * @param animation
     *            The new animation
     * @param animationEvent
     *            Determines the behaviour when an animation is already part way through.
     * @priority 4
     */
    public void setAnimation(Animation animation, AnimationEvent animationEvent)
    {
        if (animationEvent == AnimationEvent.IGNORE) {
            if (this.animation != null) {
                return;
            }

        } else if (animationEvent == AnimationEvent.FAST_FORWARD) {
            if ((this.animation != null) && (!this.animation.isFinished())) {
                this.animation.fastForward(this);
            }

        } else if (animationEvent == AnimationEvent.REPLACE) {
            this.animation = null;
        }

        if (animation == null) {
            this.animation = null;
            return;
        }

        if ((this.animation != null)
            && ((animationEvent == AnimationEvent.SEQUENCE) || ((animationEvent == AnimationEvent.PARALLEL)))) {
            // Merge the two animations (either in sequence or in parallel, depending on "ae")
            CompoundAnimation ca = new CompoundAnimation(animationEvent == AnimationEvent.SEQUENCE);
            ca.add(this.animation);
            ca.add(animation.copy());
            ca.startExceptFirst(this);
            this.animation = ca;
        } else {
            this.animation = animation.copy();
            this.animation.start(this);
        }

        // AbstractAnimation.tick(this.animation, this);
    }

    /**
     * A simple getter
     * 
     * @return The animation currently in effect, or null if the Actor is not currently animated.
     * @priority 4
     */
    public Animation getAnimation()
    {
        return this.animation;
    }

    /**
     * Determines the behaviour when {@link Actor#setAnimation(Animation, AnimationEvent)} is called, but the Actor is
     * already
     * in the middle of another Animation.
     * 
     * <ul>
     * <li>{@link #REPLACE} The existing animation is simply stopped.</li>
     * <li>{@link #FAST_FORWARD} The existing animation is fast-forwarded to the end.</li>
     * <li>{@link #SEQUENCE} The two animations are merged, so that the new animation will start once the old one has
     * finished.</li>
     * <li>{@link #PARALLEL} The two animations are merged, so that both animations are carried out together in
     * parallel.</li>
     * <li>{@link #IGNORE} The new animation is ignored, and the old animation continues as normal.
     * </ul>
     *
     */
    public enum AnimationEvent
    {
        REPLACE,
        FAST_FORWARD,
        SEQUENCE,
        PARALLEL,
        IGNORE
    }

    /**
     * Initiate an event, which can change the Actor's {@link Pose}, begin an {@link Animation}, and/or cause a Sound to
     * play.
     * 
     * @param eventName
     *            The name of the event (as defined in the Costume tab of the {@link Editor}
     */
    public void event(String eventName)
    {
        this.event(eventName, null, AnimationEvent.REPLACE);
    }

    /**
     * Initiate an event, which can change the Actor's {@link Pose}, begin an {@link Animation}, and/or cause a Sound to
     * play.
     * 
     * @param eventName
     *            The name of the event (as defined in the Costume tab of the {@link Editor}
     * @param message
     *            The message sent to the {@link Role} when the event's {@link Animation} finishes. If the event has no
     *            animation, then the message is sent straight away.
     * @priority 3
     */
    public void event(String eventName, String message)
    {
        this.event(eventName, message, AnimationEvent.REPLACE);
    }

    /**
     * Take full control over initiating an event. Event can change the Actor's {@link Pose}, begin an {@link Animation}
     * , and/or cause a Sound to play.
     * 
     * @param eventName
     *            The name of the event (as defined in the Costume tab of the {@link Editor}
     * @param message
     *            The message sent to the {@link Role} when the event's {@link Animation} finishes. If the event has no
     *            animation, then the message is sent straight away.
     * @param animationEvent
     *            Determines the behaviour when the Actor is currently in the middle of an {@link Animation} and the
     *            event has an Animation too.
     * @priority 4
     */
    public void event(String eventName, String message, AnimationEvent animationEvent)
    {
        if (this.costume == null) {
            return;
        }
        Pose pose = this.costume.getPose(eventName);
        if (pose != null) {
            this.appearance.setPose(pose);
        }

        Animation animation = this.costume.getAnimation(eventName);
        if (message != null) {
            if (animation == null) {
                // If there is no animation, but a completion message is specified, then send the message straight away.
                this.getRole().onMessage(message);
            } else {
                animation = animation.copy();
                animation.setFinishedMessage(message);
            }
        }

        if (animation != null) {
            this.setAnimation(animation, animationEvent);
        }

        ManagedSound cs = this.costume.getCostumeSound(eventName);
        if (cs != null) {
            Itchy.soundManager.play(this, eventName, cs);
        }
    }

    /**
     * Does the same as {@link #event(String)}, but also kills the actor when the event finishes. If the event has no
     * Animation, then the Actor is killed straight away.
     * 
     * @param eventName
     *            The name of the event (as defined in the Costume tab of the {@link Editor}
     * 
     */
    public void deathEvent(String eventName)
    {
        deathEvent(eventName, null, AnimationEvent.REPLACE);
    }

    /**
     * Does the same as {@link #event(String, String)}, but also kills the actor when the event finishes.
     * If the event has no Animation, then the Actor is killed straight away.
     * 
     * @param eventName
     *            The name of the event (as defined in the Costume tab of the {@link Editor}
     * @param message
     *            The message sent to the Actor's {@link Role} when the event's {@link Animation} finishes. If the event
     *            has no animation, then the message is sent straight away.
     * @priority 3
     */
    public void deathEvent(String eventName, String message)
    {
        deathEvent(eventName, message, AnimationEvent.REPLACE);
    }

    /**
     * Does the same as {@link #event(String, String, AnimationEvent)}, but also kills the actor when the event
     * finishes. If the event has no Animation, then the Actor is killed straight away.
     * 
     * @param eventName
     *            The name of the event (as defined in the Costume tab of the {@link Editor}
     * @param message
     *            The message sent to the Actor's {@link Role} when the event's {@link Animation} finishes. If the event
     *            has no animation, then the message is sent straight away.
     * @param animationEvent
     *            Determines the behaviour when the Actor is currently in the middle of an {@link Animation} and the
     *            event has an Animation too.
     * @priority 4
     */
    public void deathEvent(String eventName, String message, AnimationEvent animationEvent)
    {
        this.dying = true;
        this.event(eventName, message, animationEvent);
        if ((this.costume == null) || (this.costume.getAnimation(eventName) == null)) {
            this.kill();
        }
    }

    /**
     * Will fade out or stop sounds corresponding to the given even name. Future versions of Itchy may also stop
     * corresponding animations.
     * 
     * @param eventName
     * @priority 3
     */
    public void endEvent(String eventName)
    {
        Itchy.soundManager.end(this, eventName);
    }

    /**
     * Return true if neither {@link #deathEvent(String)} nor {@link #kill()} have been called on this Actor.
     * Note, this is not quite the opposite of {@link #isDead()}
     *
     * @return true iff not dying nor dead.
     * 
     */
    public boolean isAlive()
    {
        return !(this.dying || this.dead);
    }

    /**
     * Return true if {@link #kill()} has been called.
     * Note, this is not quite the opposite of {@link #isAlive}, because this method returns false when a
     * {@link #deathEvent(String)} has been called and the event's Animation hasn't finished yet.
     * 
     * @return true if {@link #kill()} has been called.
     */
    public boolean isDead()
    {
        return this.dead;
    }

    /**
     * 
     * @return true if a {@link #deathEvent(String)} has been called.
     * @priority 2
     */
    public boolean isDying()
    {
        return this.dying;
    }

    private void checkFullyCreated()
    {
        if (this.fullyCreated) {
            return;
        }

        if ((this.stage != null) && (this.role != null)) {
            if (this.role.getClass() != DelayedActivation.class) {
                this.fullyCreated = true;
                this.role.born();
            }
        }
    }

    /**
     * Sets the Actor's {@link Role}. The Actor's old Role (if there is one) is detached (which will cause
     * {@link AbstractRole#onDetach()} to be called). The new role is then attached (which will cause
     * {@link AbstractRole#onAttach()} to be called).
     * <p>
     * If this is the first time this Actor has had its Role set, and the Actor is on a {@link Stage}, then the
     * {@link AbstractRole#onBirth()} will also be called.
     * <p>
     * Most of the time, you do not need to worry about setting an Actor's Role, as they are set automatically when the
     * scene is loaded. However, in rare cases you may choose to change an Actor's Role during the game. For example, in
     * Pac-Man, the ghosts change behaviour when Pac-Man eats a power pill. You could implement this by changing the
     * ghosts' roles from a "chasing" to "running away". However, there are other ways of implementing this, which are
     * arguably better. See The-Mings demo game for an example of changing an Actor's behaviour without changing its
     * Role.
     * <p>
     * Note. An Actor can have a delay set in the SceneDesigner. At the beginning of the scene, it will have a
     * {@link DelayedActivation} Role, and the real Role will be set after the delay has elapsed.
     * 
     * @param role
     *            The new Role for the Actor. If null, is passed, then the a {@link PlainRole} will be assigned, instead
     *            of null.
     * @priority 3
     */
    public void setRole(Role role)
    {
        if ((this.role != null) && (this.role.getClass() == DelayedActivation.class)) {
            this.event(this.startEvent);
        }

        if (role == this.role) {
            return;
        }

        if (this.role != null) {
            this.role.detach();
        }

        this.role = role == null ? new PlainRole() : role;
        this.role.attach(this);

        if (this.stage != null) {
            this.stage.changedRole(this);
        }

        checkFullyCreated();
    }

    /**
     * @return The Actor's {@link Role}. When an Actor is first created, the Role may be null, but should soon be set to
     *         a non-null value.
     */
    public Role getRole()
    {
        return this.role;
    }

    /**
     * Used internally by Itchy while editing scenes, and also while loading a scene.
     * 
     * @return
     * @priority 5
     */
    public ClassName getRoleClassName()
    {
        return this.role.getClassName();
    }

    /**
     * @return The Actor's {@link Appearance}, which is never null.
     */
    public Appearance getAppearance()
    {
        return this.appearance;
    }

    /**
     * 
     * @return
     * @priority 5
     */
    public boolean isActive()
    {
        return this.active;
    }

    /**
     * Used internally by Itchy when editing a scene, and when loading a scene.
     * 
     * @param value
     * @priority 5
     */
    public void setActivationDelay(double value)
    {
        this.activationDelay = value;
    }

    /**
     * Used internally by Itchy when editing a scene, and when loading a scene.
     * 
     * @return
     * @priority 5
     */
    public double getActivationDelay()
    {
        return this.activationDelay;
    }

    /**
     * Called when the actor is no longer wanted. It will be removed from its Stage (during the next frame rendering),
     * and therefore will not be visible. It will be deactivated (i.e. its tick method won't be called any more).
     * It will have all of its tags removed. The Actor's ID is also reset to null.
     * <p>
     * Note, you must not try to resurrect an Actor by adding it to a Stage after it has been killed.
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
            this.setId(null);
        }
    }

    public Point getPosition()
    {
        return this.position;
    }

    /**
     * @return The X coordinate of the Actor.
     */
    public double getX()
    {
        return this.position.getX();
    }

    /**
     * @return The Y coordinate of the Actor. Note that the Y axis points <b>upwards</b>, and zero is at the bottom.
     */
    public double getY()
    {
        return this.position.getY();
    }

    /**
     * Sets the X coordinate of the Actor.
     * 
     * @param x
     */
    public void setX(double x)
    {
        this.position = new Point(x, this.position.getY());
        this.appearance.invalidatePosition();
    }

    /**
     * Sets the Y coordinate of the Actor. Note that the Y axis points <b>upwards</b>, and zero is at the bottom.
     * 
     * @param y
     */
    public void setY(double y)
    {
        this.position = new Point(this.position.getX(), y);
        this.appearance.invalidatePosition();
    }

    /**
     * Moves the 'other' Actor to the same position as this Actor.
     * 
     * @deprecated Use moveTo( other.getPosition() )
     * @param other
     * @priority 3
     */
    public void moveTo(Actor other)
    {
        this.moveTo(other.getPosition());
    }

    /**
     * Set both X and Y coordinates.
     * 
     * @param x
     * @param y
     *            Note that the Y axis points <b>upwards</b>, and zero is at the bottom.
     */
    public void moveTo(double x, double y)
    {
        this.position = new Point(x, y);
        this.appearance.invalidatePosition();
    }

    public void moveTo(Point position)
    {
        this.position = position;
        this.appearance.invalidatePosition();
    }

    /**
     * Adds to the current X,Y coordinates.
     * 
     * @param x
     * @param y
     *            Note that the Y axis points <b>upwards</b>, and zero is at the bottom.
     */
    public void moveBy(double x, double y)
    {
        this.position = this.position.translate(x, y);
        this.appearance.invalidatePosition();
    }

    /**
     * Uses the Actor's {@link #getHeading()}, and moves the Actor forwards.
     * 
     * @param amount
     *            The amount of pixels to move forwards.
     * @priority 2
     */
    public void moveForwards(double amount)
    {
        this.moveTo(this.position.translateRadians(this.getHeadingRadians(), amount));
        // double theta = this.getHeadingRadians();
        // double cosa = Math.cos(theta);
        // double sina = Math.sin(theta);

        // this.moveBy((cosa * amount), (sina * amount));
    }

    /**
     * Uses the Actor's heading and moves the Actor forwards.
     * 
     * @param amount
     *            The amount of pixels to move forwards.
     * @param sideways
     *            The amount of pixels to move sideways (+ve numbers are to the Actor's left).
     * @deprecated
     */
    public void moveForwards(double forward, double sideways)
    {
        this.moveAngle(this.getHeading(), forward, sideways);
    }

    /**
     * 
     * @param degrees
     * @param distance
     * @priority 3
     * @deprecated
     */
    public void moveAngle(double degrees, double distance)
    {
        this.moveTo(this.position.translateDegrees(degrees, distance));

        // double theta = degrees / 180 * Math.PI;
        // double cosa = Math.cos(theta);
        // double sina = Math.sin(theta);

        // this.moveBy((cosa * distance), (sina * distance));
    }

    /**
     * 
     * @param degrees
     * @param forward
     * @param sideways
     * @priority 3
     * @deprecated
     */
    public void moveAngle(double degrees, double forward, double sideways)
    {
        this.moveTo(this.position.translateDegrees(degrees, forward, sideways));
        //double theta = degrees / 180 * Math.PI;
        //double cosa = Math.cos(theta);
        //double sina = Math.sin(theta);

        //this.moveBy((cosa * forward) - (sina * sideways), (sina * forward) + (cosa * sideways));
    }

    /**
     * Moves this Actor towards the <code>other</code> Actor, by <code>distance</code> pixels.
     * If the distance between the Actors is less than the distance to be moved, then do NOT overshoot, and
     * instead move to the other Actor's position.
     * 
     * @param other
     *            The Actor in whose direction we will move towards.
     * @param displacement
     *            The distance to move
     * @priority 3
     * @deprecated
     */
    public void moveTowards(Actor other, double displacement)
    {
        this.moveTo(this.position.towards(other.position, displacement));
        // double dx = other.getX() - this.getX();
        // double dy = other.getY() - this.getY();

        // double distance = Math.sqrt(dx * dx + dy * dy);
        // double scale = distance < displacement ? 1 : displacement / distance;

        // this.moveBy(dx * scale, dy * scale);
    }

    /**
     * 
     * @param other
     * @return
     * @priority 3
     * @deprecated Same as distanceTo, which is also deprecated
     */
    public double distance(Actor other)
    {
        return this.position.distance(other.position);
        // double dx = this.getX() - other.getX();
        // double dy = this.getY() - other.getY();

        // return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * 
     * @param x
     * @param y
     * @return
     * @priority 3
     * @deprecated
     */
    public double distanceTo(double x, double y)
    {
        return this.position.distance(new Point(x, y));
        // return Math.sqrt((this.getX() - x) * (this.getX() - x) + (this.getY() - y) * (this.getY() - y));
    }

    /**
     * 
     * @param other
     * @return
     * @priority 3
     * @deprecated
     */
    public double distanceTo(Actor other)
    {
        return this.position.distance(other.getPosition());
        // return Math.sqrt((this.getX() - other.getX()) * (this.getX() - other.getX()) + (this.getY() - other.getY()) *
        // (this.getY() - other.getY()));
    }

    /**
     * 
     * @param x
     * @param y
     * @return
     * @priority 3
     * @deprecated
     */
    public double directionOf(double x, double y)
    {
        return this.position.directionDegrees(new Point(x, y));
        // return Math.atan2(y - this.getY(), x - this.getX()) * 180.0 / Math.PI;
    }

    /**
     * 
     * @param other
     * @return
     * @priority 3
     * @deprecated
     */
    public double directionOf(Actor other)
    {
        return this.position.directionDegrees(other.getPosition());
        // return Math.atan2(other.getY() - this.getY(), other.getX() - this.getX()) * 180.0 / Math.PI;
    }

    /**
     * 
     * @param x
     * @param y
     * @return
     * @priority 3
     */
    public boolean contains(int x, int y)
    {
        return this.getAppearance().getWorldRectangle().contains(x, y);
    }

    /**
     * 
     * @param x
     * @param y
     * @param tag
     * @return
     * @priority 3
     */
    public static Role nearest(double x, double y, String tag)
    {
        Role closestRole = null;
        double closestDistance = Double.MAX_VALUE;

        for (Role otherRole : AbstractRole.findRolesByTag(tag)) {
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
     * If there are a large number of Actors with this tag, then this will be slow, because unlike overlapping and
     * touching, there is no optimisation based on {@link CollisionStrategy}.
     * 
     * @param tag
     * @return
     * @priority 3
     */
    public Role nearest(String tag)
    {
        Role closestRole = null;
        double closestDistance = Double.MAX_VALUE;

        for (Role otherRole : AbstractRole.findRolesByTag(tag)) {
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

    /**
     * 
     * @param other
     * @return
     * @priority 3
     */
    public boolean overlapping(Actor other)
    {
        if (this.appearance.getWorldRectangle().overlaps(other.appearance.getWorldRectangle())) {
            return true;
        }
        return false;
    }

    /**
     * Checks if this Actor is visible on screen. If there are multiple {@link StageViews}, for the Actor's
     * {@link Stage}, then true is returned if the Actor is visible on any of them.
     * <p>
     * Note. Only the Actor's bounding rectangle is considered. If the Actor has an alpha value of zero, or if only
     * transparent pixels are one screen, then this method can still return true.
     * 
     * @return true iff the Actor is visible on screen.
     */
    public boolean isVisible()
    {
        if (this.getStage() == null) {
            return false;
        }

        Layout layout = Itchy.getGame().getLayout();
        for (Layer layer : layout.getLayers()) {
            Stage stage = layer.getStage();
            if (stage == this.getStage()) {
                if (this.getAppearance().visibleWithin(layer.getStageView().worldRect)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 
     * @return true iff the Actor is text (ie has a {@link TextPose}, rather than {@link ImagePose}).
     * @priority 2
     */
    public boolean isText()
    {
        return !(getAppearance().getPose() instanceof ImagePose);
    }

    /**
     * For an Actor displaying text, this is the same as {@link #contains(int, int)}, but for other actors (displaying
     * an image), it is the same as {@link #pixelOverlap(int, int)}
     * <p>
     * If you want to use this in conjunction with mouse position, then you need to convert the screen coordinates to
     * world coordinates. See {@link StageView#getWorldX(int)} and {@link StageView#getWorldY(int)}.
     * 
     * @param worldX
     *            The world X coordinate (so if the view is scrolled, this will be different to the screen coordinate).
     * @param worldY
     *            The world Y coordinate. Note the Y axis points upwards (i.e. never the same of screen coordinates)
     * @priority 3
     */
    public boolean hitting(int worldX, int worldY)
    {
        if (isText()) {
            return this.contains(worldX, worldY);
        } else {
            return this.pixelOverlap(worldX, worldY);
        }
    }

    /**
     * The threshold value when using {@link #pixelOverlap(int,int)}.
     */
    public static final int DEFAULT_ALPHA_THRESHOLD = 10;

    /**
     * Checks if a given world coordinate is a visible part of this Actor.
     * <p>
     * If you want to use this in conjunction with mouse position, then you need to convert the screen coordinates to
     * world coordinates. See {@link StageView#getWorldX(int)} and {@link StageView#getWorldY(int)}.
     * 
     * @param worldX
     *            The world X coordinate. (so if the view is scrolled, this will be different to the screen coordinate).
     * @param worldY
     *            The world Y coordinate. Note the Y axis points upwards (i.e. never the same of screen coordinates)
     * @return true iff the point is within the Actor's image, and the pixel is sufficiently opaque.
     * @priority 3
     */
    public boolean pixelOverlap(int worldX, int worldY)
    {
        return this.pixelOverlap(worldX, worldY, DEFAULT_ALPHA_THRESHOLD);
    }

    /**
     * Checks if a given world coordinate is a visible part of this Actor.
     * <p>
     * If you want to use this in conjunction with mouse position, then you need to convert the screen coordinates to
     * world coordinates. See {@link StageView#getWorldX(int)} and {@link StageView#getWorldY(int)}.
     * 
     * @param worldX
     *            The world X coordinate. (so if the view is scrolled, this will be different to the screen coordinate).
     * @param worldY
     *            The world Y coordinate. Note the Y axis points upwards (i.e. never the same of screen coordinates)
     * @param alphaThreashold
     *            How opaque the pixel needs to be. This is an alpha channel value from 0 to 255.
     * @return true iff the point is within the Actor's image, and the pixel is sufficiently opaque.
     * @priority 3
     */
    public boolean pixelOverlap(int worldX, int worldY, int alphaThreashold)
    {
        if (this.getAppearance().getWorldRectangle().contains(worldX, worldY)) {

            Surface surface = this.getAppearance().getSurface();
            if (surface.hasAlphaChannel()) {

                double px = worldX - this.getX() + this.getAppearance().getOffsetX();
                double py = this.getAppearance().getOffsetY() - worldY + this.getY();
                RGBA color = surface.getPixelRGBA((int) px, (int) py);
                return color.a > alphaThreashold;

            } else {
                return true;
            }

        }
        return false;
    }

    /**
     * Checks if one Actor's pixels overlap another Actor's pixels.
     * 
     * @param other
     * @return true if the two Actor's pixels overlap, and those pixels are sufficiently opaque (i.e. not transparent).
     * @priority 3
     */
    public boolean pixelOverlap(Actor other)
    {
        return pixelOverlap(other, 1);
    }

    /**
     * Checks if one Actor's pixels overlap another Actor's pixels.
     * 
     * @param other
     * @return true if the two Actor's pixels overlap, and those pixels are sufficiently opaque (i.e. not transparent).
     * @priority 3
     */
    public boolean pixelOverlap(Actor other, int threshold)
    {
        int dx = ((int) this.getX() - this.appearance.getOffsetX())
            - ((int) (other.getX()) - other.appearance.getOffsetX());
        int dy = ((int) -this.getY() - this.appearance.getOffsetY())
            - ((int) (-other.getY()) - other.appearance.getOffsetY());

        return this.getAppearance().getSurface()
            .pixelOverlap(other.getAppearance().getSurface(), dx, dy, threshold);
    }

    /**
     * The Actors' Z-Order determines the order the Actors are drawn, and therefore which Actor is obscured when two
     * Actors overlap. (This assumes you are using a {@link ZOrderStage}, which is the default type of Stage).
     * 
     * @return The Z-Order.
     * @priority 2
     */
    public int getZOrder()
    {
        return this.zOrder;
    }

    /**
     * The Actors' Z-Order determines the order the Actors are drawn, and therefore which Actor is obscured when two
     * Actors overlap. (This assumes you are using a {@link ZOrderStage}, which is the default type of Stage).
     * 
     * @param value
     *            The Z-Order.
     * @priority 2
     */
    public void setZOrder(int value)
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

    /**
     * Used internally by Itchy
     * 
     * @param value
     * @priority 5
     */
    void setZOrderAttribute(int value)
    {
        this.zOrder = value;
    }

    /**
     * 
     * @param delta
     * @priority 3
     */
    public void adjustZOrder(int delta)
    {
        setZOrder(this.zOrder + delta);
    }

    /**
     * Called once per frame (60 times per second), there is no need to call an Actor's tick method from your game's
     * code.
     * Calls {@link Animation#tick(Actor)} as well as {@link Role#tick()}.
     * 
     * @priority 5
     */
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

    /**
     * Can be useful for debugging.
     * 
     * @priority 3
     */
    @Override
    public String toString()
    {
        return "Actor #" + this.sequenceNumber + " @ " + getX() + "," + getY() + " " +
            (getRole() == null ? "" : "(" + getRole().getClass().getName() + ")");
    }

    /**
     * Used internally by Itchy.
     * 
     * @priority 5
     */
    @Override
    public List<Property<Actor, ?>> getProperties()
    {
        return properties;
    }

}
