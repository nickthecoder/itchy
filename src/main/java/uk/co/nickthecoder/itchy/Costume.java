/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import uk.co.nickthecoder.itchy.ManagedSound.MultipleRole;
import uk.co.nickthecoder.itchy.animation.Animation;
import uk.co.nickthecoder.itchy.editor.Editor;
import uk.co.nickthecoder.itchy.editor.SceneDesigner;
import uk.co.nickthecoder.itchy.gui.PropertiesForm;
import uk.co.nickthecoder.itchy.property.AnimationProperty;
import uk.co.nickthecoder.itchy.property.BooleanProperty;
import uk.co.nickthecoder.itchy.property.ClassNameProperty;
import uk.co.nickthecoder.itchy.property.CostumeProperty;
import uk.co.nickthecoder.itchy.property.DoubleProperty;
import uk.co.nickthecoder.itchy.property.EnumProperty;
import uk.co.nickthecoder.itchy.property.FontProperty;
import uk.co.nickthecoder.itchy.property.IntegerProperty;
import uk.co.nickthecoder.itchy.property.PoseResourceProperty;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.PropertySubject;
import uk.co.nickthecoder.itchy.property.RGBAProperty;
import uk.co.nickthecoder.itchy.property.SoundProperty;
import uk.co.nickthecoder.itchy.property.StringProperty;
import uk.co.nickthecoder.itchy.role.PlainRole;
import uk.co.nickthecoder.itchy.util.ClassName;
import uk.co.nickthecoder.jame.Sound;
import uk.co.nickthecoder.jame.Surface;

/**
 * Costumes contain {@link Pose}s, {@link Animation}s and {@link ManagedSound}. In simple cases, each type of Actor has
 * just one Costume. For example the player's ship in the demo game 'Drunk Invaders' has just one Costume. However,
 * the aliens have three different Costumes.
 * <p>
 * By using Costumes, it is easy to get get extra features, with no extra coding. For example, the aliens in Drunk
 * Invaders each have their own sound effect when they die, but the game code just calls
 * {@link AbstractRole#deathEvent(String)}, and Itchy takes care of the rest.
 * <p>
 * What's more, each event can have multiple events sharing the same name, in which case one is picked at random. This
 * is used in Drunk Invaders for the speech bubbles when the aliens (and the players) die.
 * <p>
 * Most of the time, you don't need to call any methods of Costume directly, instead call
 * {@link AbstractRole#event(String)}, and this will change the Actor's Pose, Animation play or sound effect.
 */
public class Costume implements NamedSubject<Costume>, Cloneable
{
    protected static final List<Property<Costume, ?>> properties = new ArrayList<Property<Costume, ?>>();

    static {
        properties.add(new StringProperty<Costume>("name").allowBlank(false));
        properties.add(new ClassNameProperty<Costume>(Role.class, "roleClassName"));
        properties.add(new IntegerProperty<Costume>("defaultZOrder"));
        properties.add(new BooleanProperty<Costume>("showInDesigner"));
        properties.add(new IntegerProperty<Costume>("order").hint("(within scene designer's toolbox)"));
    }

    /**
     * Used internally by Itchy.
     * 
     * @priority 5
     */
    @Override
    public List<Property<Costume, ?>> getProperties()
    {
        return properties;
    }

    /**
     * Used internally by Itchy.
     * 
     * @priority 5
     */
    public ClassName roleClassName = new ClassName(Role.class, uk.co.nickthecoder.itchy.role.PlainRole.class.getName());

    /**
     * The value of the Actor's Z-Order when it is first created.
     */
    public int defaultZOrder;

    /**
     * If false, then the costume will not appear in the Costumes tab of the {@link SceneDesigner}
     */
    public boolean showInDesigner = true;

    private String name;

    /**
     * When requesting a Pose/Animation/Sound, if this Costume doesn't have a matching named event, then the
     * extendedFrom Costume is used. Most Costumes won't need this feature. It is useful when multiple costumes
     * all have a common events. For example, the three alien costumes in Drunk Invaders can all share a single
     * bullet Companion event.
     */
    private Costume extendedFrom;

    /**
     * The order of the Costume in the {@link SceneDesigner}'s Costumes tab.
     */
    private int order;

    private CostumeFeatures costumeFeatures;

    private HashMap<String, List<AnimationResource>> animationChoices;

    private HashMap<String, List<String>> stringChoices;

    private HashMap<String, List<ManagedSound>> soundChoices;

    private HashMap<String, List<PoseResource>> poseChoices;

    private HashMap<String, List<TextStyle>> textStyleChoices;

    /**
     * A Costume can link to other costumes by a String key. These are called "companions", because they tend to be used
     * to create companion objects. For example, "Space Miner" (asteroids) uses this when one "rock" breaks up into
     * smaller rocks. The smaller rocks are the companions of the larger one.
     */
    private HashMap<String, List<Costume>> companionChoices;

    /**
     * When loading a costume, we may have circular references, which can only be resolved when all costumes have been
     * loaded. This stores the list of names of costumes during the load phase, which are then converted into
     * companionChoices at the end.
     * 
     * @priority 5
     */
    public HashMap<String, List<String>> companionStringChoices;

    private static final Random random = new Random();

    /**
     * Create a new Costume.
     */
    public Costume()
    {
        extendedFrom = null;

        stringChoices = new HashMap<String, List<String>>();
        soundChoices = new HashMap<String, List<ManagedSound>>();
        poseChoices = new HashMap<String, List<PoseResource>>();
        textStyleChoices = new HashMap<String, List<TextStyle>>();
        animationChoices = new HashMap<String, List<AnimationResource>>();
        companionChoices = new HashMap<String, List<Costume>>();
        companionStringChoices = new HashMap<String, List<String>>();
    }

    /**
     * A simple getter.
     * 
     * @priority 2
     */
    @Override
    public String getName()
    {
        return name;
    }

    /**
     * A simple setter.
     * 
     * @priority 2
     */
    @Override
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * A simple getter - the order the Costumes are displayed in the {@link SceneDesigner}'s Costumes tab.
     * 
     * @return
     * @priority 5
     */
    public int getOrder()
    {
        return order;
    }

    /**
     * A simple setter - the order the Costumes are displayed in the {@link SceneDesigner}'s Costumes tab.
     * 
     * @param order
     */
    public void setOrder(int order)
    {
        this.order = order;
    }

    /**
     * Used internally by Itchy.
     * When requesting a Pose/Animation/Sound, if this Costume doesn't have a matching named event, then the
     * extendedFrom Costume is used. Most Costumes won't need this feature. It is useful when multiple costumes
     * all have a common events. For example, the three alien costumes in Drunk Invaders can all share a single
     * bullet Companion event.
     * 
     * @return
     * @priority 5
     */
    public String getExtendedFromName()
    {
        Costume base = this.getExtendedFrom();
        if (base == null) {
            return null;
        } else {
            return Itchy.getGame().resources.getCostumeName(base);
        }
    }

    /**
     * Used internally by Itchy to create a thumbnail image for the Costume.
     * 
     * @return
     * @priority 5
     */
    public Surface getThumbnail()
    {
        return Itchy.getGame().resources.getThumbnail(this);
    }

    /**
     * Create an {@link Actor} based on this Costume, including setting the Z-Order to this Costume's defaultZOrder, and
     * creating a new Role specified by this Costume.
     * The initial {@link Pose} and any {@link Animation} is based on the startEvent given (use "default" as a good
     * default).
     * <p>
     * The new Actor will not be placed on a Stage at this point, and therefore will not have its onBirth method called.
     * 
     * @param startEvent
     * @return A new Actor, with this Costume, and a Role defined by this Costume.
     */
    public Actor createActor(String startEvent)
    {
        Actor actor = new Actor(this, startEvent);
        actor.setZOrder(this.defaultZOrder);
        Role role;
        try {
            role = (Role) roleClassName.createInstance(Itchy.getGame().resources);

        } catch (Exception e) {
            role = new PlainRole();
            e.printStackTrace();
        }
        actor.setRole(role);
        actor.event(startEvent);

        return actor;
    }

    /**
     * The name part of a {@link ClassName} when the costumeFeatures attribute was set. If this has changed, then the
     * costumeFeatures needs to be recreated.
     */
    private String roleClassNameForCostumeFeatures;

    /**
     * Gets the CostumeFeatures for this Costume. CostumeFeatures can hold game-specific details. If you have two
     * "Alien" costumes (which share the same Role subclass), then CostumeFeatures can hold details about the two
     * different
     * aliens, such as how strong they are, how frequent they fire etc.
     * <p>
     * If however, you want every alien to be unique, and their strength and fire rate to be set for each individual,
     * then these attributes belong on the alien's Role.
     * <p>
     * The CostumeFeatures data can be edited from the Costume dialog within the {@link Editor}.
     * <p>
     * Override the method {@link Role#createCostumeFeatures(Costume)} to create a subclass of CostumeFeatures specific
     * to your game.
     * 
     * @return
     */
    public CostumeFeatures getCostumeFeatures()
    {
        // If the Role has changed since we last created the costume properties, then we can't use
        // the old costumeProperties.
        if ((roleClassNameForCostumeFeatures != null)
            && (!roleClassNameForCostumeFeatures.equals(roleClassName.name))) {

            costumeFeatures = null;
        }

        if (costumeFeatures == null) {
            try {
                roleClassNameForCostumeFeatures = roleClassName.name;
                Role dummyRole = (Role) roleClassName.createInstance(Itchy.getGame().resources);
                costumeFeatures = dummyRole.createCostumeFeatures(this);
            } catch (Exception e) {
                e.printStackTrace();
                costumeFeatures = new PlainCostumeFeatures();
            }
        }
        return costumeFeatures;
    }

    /**
     * Used when the Role has been reloaded, and therefore the costume features may have different properties.
     * Attempts to copy the CostumeFeature's properties across, but silently ignores any errors.
     * Errors are expected, because a property may have been removed or their type changed.
     * 
     * @priority 5
     */
    public void resetCostumeFeatures()
    {
        if (this.costumeFeatures != null) {
            CostumeFeatures oldFeatures = this.costumeFeatures;
            this.costumeFeatures = null;
            CostumeFeatures newFeatures = getCostumeFeatures();

            for (Property<CostumeFeatures, ?> property : newFeatures.getProperties()) {
                try {
                    property.setValue(newFeatures, property.getSafeValue(oldFeatures));
                } catch (Exception e) {

                }
            }
        }
    }

    /**
     * A simple getter. This attribute is set using the "Edit Costume" dialog in the {@link Editor}.
     * 
     * @return
     * @priority 3
     */
    public Costume getExtendedFrom()
    {
        return extendedFrom;
    }

    /**
     * 
     * @param costume
     * @priority 5
     */
    public void setExtendedFrom(Costume costume)
    {
        extendedFrom = costume;
    }

    /**
     * The set of event names of type TextStyle.
     * 
     * @return
     * @priority 3
     */
    public Set<String> getTextStyleNames()
    {
        return textStyleChoices.keySet();
    }

    /**
     * The set of event names of type String.
     * 
     * @return
     * @priority 3
     */
    public List<String> getStringNames()
    {
        return Resources.sortNames(stringChoices.keySet());
    }

    /**
     * The set of event names of type Pose.
     * 
     * @return
     * @priority 3
     */
    public List<String> getPoseNames()
    {
        return Resources.sortNames(poseChoices.keySet());
    }

    /**
     * The set of event names of type Sound.
     * 
     * @return
     * @priority 3
     */
    public List<String> getSoundNames()
    {
        return Resources.sortNames(soundChoices.keySet());
    }

    /**
     * The set of event names of type Animation.
     * 
     * @return
     * @priority 3
     */
    public List<String> getAnimationNames()
    {
        return Resources.sortNames(animationChoices.keySet());
    }

    /**
     * The set of event names of type companion (which is a Costume).
     * 
     * @return
     * @priority 3
     */
    public List<String> getCompanionNames()
    {
        return Resources.sortNames(companionChoices.keySet());
    }

    // String

    /**
     * Adds a String event to the Costume
     * 
     * @priority 3
     */
    public void addString(String name, String value)
    {
        List<String> choices = stringChoices.get(name);
        if (choices == null) {
            choices = new ArrayList<String>();
            stringChoices.put(name, choices);
        }
        choices.add(value);
    }

    /**
     * Removed a String event from the Costume
     * 
     * @priority 3
     */
    public void removeString(String name, String value)
    {
        List<String> choices = stringChoices.get(name);
        assert (choices.contains(value));
        choices.remove(value);
    }

    /**
     * Gets a String value using an event "name", and if no String was found, returns a default value.
     * 
     * @param name
     * @param defaultValue
     * @return
     */
    public String getString(String name, String defaultValue)
    {
        String result = this.getString(name);
        if (result == null) {
            return defaultValue;
        }

        return result;
    }

    /**
     * Gets a String value using an event "name", and if no String was found, returns null.
     * 
     * @param name
     * @param defaultValue
     * @return
     */
    public String getString(String name)
    {
        List<String> strings = stringChoices.get(name);
        if ((strings == null) || (strings.size() == 0)) {
            if (extendedFrom != null) {
                return extendedFrom.getString(name);
            }
            return null;
        }
        String string = strings.get(random.nextInt(strings.size()));
        return string;
    }

    /**
     * Returns a set of strings for the given event name
     * 
     * @param name
     * @return A set of strings, or null if no string events were found with that name.
     */
    public List<String> getStringChoices(String name)
    {
        return stringChoices.get(name);
    }

    // Companion (Costume)

    /**
     * Add a "Companion" event. Used by the {@link Editor}.
     * 
     * @param name
     * @param costume
     * @priority 3
     */
    public void addCompanion(String name, Costume costume)
    {
        List<Costume> choices = companionChoices.get(name);
        if (choices == null) {
            choices = new ArrayList<Costume>();
            companionChoices.put(name, choices);
        }
        choices.add(costume);
    }

    /**
     * Remove a "Companion" event. Used by the {@link Editor}.
     * 
     * @param name
     * @param costume
     * @priority 3
     */
    public void removeCompanion(String name, Costume costume)
    {
        List<Costume> choices = companionChoices.get(name);
        assert (choices.contains(costume));
        choices.remove(costume);
    }

    /**
     * Get a Companion for a give event. If there is more than one, one is picked at random.
     * Typically, you will create the AQctor using {@link #createActor(String)}, rather than calling this directly.
     * 
     * @param name
     * @return
     * @priority 3
     */
    public Costume getCompanion(String name)
    {
        List<Costume> choices = companionChoices.get(name);
        if ((choices == null) || (choices.size() == 0)) {
            if (extendedFrom != null) {
                return extendedFrom.getCompanion(name);
            }
            return null;
        }
        if (choices.size() == 0) {
            return null;
        }
        Costume costume = choices.get(random.nextInt(choices.size()));
        return costume;
    }

    /**
     * Get the list of all companion Costumes for a given event, or null if none are found.
     * 
     * @param name
     * @return
     * @priority 3
     */
    public List<Costume> getCompanionChoices(String name)
    {
        return companionChoices.get(name);
    }

    // Pose

    /**
     * Add a Pose event. Used by the {@link Editor}
     * 
     * @param name
     * @param poseResource
     * @priority 3
     */
    public void addPose(String name, PoseResource poseResource)
    {
        List<PoseResource> choices = poseChoices.get(name);
        if (choices == null) {
            choices = new ArrayList<PoseResource>();
            poseChoices.put(name, choices);
        }
        choices.add(poseResource);
    }

    /**
     * Remove a Pose event. Used by the {@link Editor}
     * 
     * @param name
     * @param poseResource
     * @priority 3
     */
    public void removePose(String name, PoseResource resource)
    {
        List<PoseResource> choices = poseChoices.get(name);
        assert (choices.contains(resource));
        choices.remove(resource);
    }

    /**
     * Get a PoseResource for a give event. If there is more than one, one is picked at random.
     * 
     * @param name
     * @return
     * @priority 3
     */
    public PoseResource getPoseResource(String name)
    {
        List<PoseResource> choices = poseChoices.get(name);
        if ((choices == null) || (choices.size() == 0)) {
            if (extendedFrom != null) {
                return extendedFrom.getPoseResource(name);
            }
            return null;
        }
        if (choices.size() == 0) {
            return null;
        }
        PoseResource poseResource = choices.get(random.nextInt(choices.size()));
        return poseResource;
    }

    /**
     * Get a Pose for a give event. If there is more than one, one is picked at random.
     * 
     * @param name
     * @return
     * @priority 3
     */
    public Pose getPose(String name)
    {
        PoseResource resource = this.getPoseResource(name);
        return resource == null ? null : resource.pose;
    }

    /**
     * Get the list of all PoseResources for a given event, or null if none are found.
     * 
     * @param name
     * @return
     * @priority 3
     */
    public List<PoseResource> getPoseChoices(String name)
    {
        return poseChoices.get(name);
    }

    // Sound

    /**
     * Add a Sound event. Used by the {@link Editor}
     * 
     * @param name
     * @param poseResource
     * @priority 3
     */
    public void addSound(String name, ManagedSound managedSound)
    {
        List<ManagedSound> choices = soundChoices.get(name);
        if (choices == null) {
            choices = new ArrayList<ManagedSound>();
            soundChoices.put(name, choices);
        }
        choices.add(managedSound);

    }

    /**
     * Add a Pose event. Used by the {@link Editor}
     * 
     * @param name
     * @param poseResource
     * @priority 3
     */
    public ManagedSound addSound(String name, SoundResource soundResource)
    {
        ManagedSound managedSound = new ManagedSound(soundResource);
        this.addSound(name, managedSound);
        return managedSound;
    }

    /**
     * Remove a Pose event. Used by the {@link Editor}
     * 
     * @param name
     * @param poseResource
     * @priority 3
     */
    public void removeSound(String name, ManagedSound value)
    {
        List<ManagedSound> choices = soundChoices.get(name);
        assert (choices.contains(value));
        choices.remove(value);
    }

    /**
     * Get a SoundResource for a give event. If there is more than one, one is picked at random.
     * 
     * @param name
     * @return
     * @priority 3
     */
    public SoundResource getSoundResource(String name)
    {
        ManagedSound cs = getCostumeSound(name);
        if (cs == null) {
            return null;
        } else {
            return cs.soundResource;
        }
    }

    /**
     * Get a ManagedSound for a given event. If there is more than one, one is picked at random.
     * 
     * @param name
     * @return
     * @priority 3
     */
    public ManagedSound getCostumeSound(String name)
    {
        List<ManagedSound> choices = soundChoices.get(name);
        if ((choices == null) || (choices.size() == 0)) {
            if (extendedFrom != null) {
                return extendedFrom.getCostumeSound(name);
            }
            return null;
        }
        ManagedSound costumeSound = choices.get(random.nextInt(choices.size()));
        return costumeSound;
    }

    /**
     * Get a Sound for a given event. If there is more than one, one is picked at random.
     * 
     * @param name
     * @return
     * @priority 3
     */
    public Sound getSound(String name)
    {
        SoundResource resource = this.getSoundResource(name);
        return resource == null ? null : resource.getSound();
    }

    /**
     * Gets a list of all ManagedSounds for a given event, or null if none were found.
     * 
     * @param name
     * @return
     * @priority 3
     */
    public List<ManagedSound> getSoundChoices(String name)
    {
        return soundChoices.get(name);
    }

    // TextStyle

    /**
     * Add a Text Style event. Used by the {@link Editor}
     * 
     * @param name
     * @param poseResource
     * @priority 3
     */
    public void addTextStyle(String name, TextStyle textStyle)
    {
        List<TextStyle> choices = textStyleChoices.get(name);
        if (choices == null) {
            choices = new ArrayList<TextStyle>();
            textStyleChoices.put(name, choices);
        }
        choices.add(textStyle);
    }

    /**
     * Remove a TextStyle event. Used by the {@link Editor}
     * 
     * @param name
     * @param poseResource
     * @priority 3
     */
    public void removeTextStyle(String name, TextStyle value)
    {
        List<TextStyle> choices = textStyleChoices.get(name);
        assert (choices.contains(value));
        choices.remove(value);
    }

    /**
     * Get a TextStyle for a give event. If there is more than one, one is picked at random.
     * 
     * @param name
     * @return
     * @priority 3
     */
    public TextStyle getTextStyle(String name)
    {
        List<TextStyle> choices = textStyleChoices.get(name);
        if ((choices == null) || (choices.size() == 0)) {
            if (extendedFrom != null) {
                return extendedFrom.getTextStyle(name);
            }
            return null;
        }
        TextStyle textStyle = choices.get(random.nextInt(choices.size()));
        return textStyle;
    }

    /**
     * Finds all TextStyles for a given event, or null if none were found.
     * 
     * @param name
     * @return
     * @priority 3
     */
    public List<TextStyle> getTextStyleChoices(String name)
    {
        return textStyleChoices.get(name);
    }

    // Animation

    /**
     * Add an Animation event. Used by the {@link Editor}
     * 
     * @param name
     * @param poseResource
     * @priority 3
     */
    public void addAnimation(String name, AnimationResource animationResource)
    {
        List<AnimationResource> choices = animationChoices.get(name);
        if (choices == null) {
            choices = new ArrayList<AnimationResource>();
            animationChoices.put(name, choices);
        }
        choices.add(animationResource);
    }

    /**
     * Remove an Animation event. Used by the {@link Editor}
     * 
     * @param name
     * @param poseResource
     * @priority 3
     */
    public void removeAnimation(String name, AnimationResource value)
    {
        List<AnimationResource> choices = animationChoices.get(name);
        assert (choices.contains(value));
        choices.remove(value);
    }

    /**
     * Get an AnimationResource for a give event. If there is more than one, one is picked at random.
     * 
     * @param name
     * @return
     * @priority 3
     */
    public AnimationResource getAnimationResource(String name)
    {
        List<AnimationResource> choices = animationChoices.get(name);
        if ((choices == null) || (choices.size() == 0)) {
            if (extendedFrom != null) {
                return extendedFrom.getAnimationResource(name);
            }
            return null;
        }
        AnimationResource animationResource = choices.get(random.nextInt(choices.size()));
        return animationResource;
    }

    /**
     * Get an Animation for a give event. If there is more than one, one is picked at random.
     * 
     * @param name
     * @return
     * @priority 3
     */
    public Animation getAnimation(String name)
    {
        AnimationResource resource = this.getAnimationResource(name);
        return resource == null ? null : resource.animation;
    }

    /**
     * Gets a list of all Animations for a given event, or null if none were found.
     * 
     * @param name
     * @return
     * @priority 3
     */
    public List<AnimationResource> getAnimationChoices(String name)
    {
        return animationChoices.get(name);
    }

    /**
     * Useful while debugging.
     * 
     * @priority 3
     */
    @Override
    public String toString()
    {
        return "Costume " + getName();
    }

    /**
     * Use to sort Costumes. See {@link Costume#getOrder()}.
     * 
     * @priority 5
     */
    public static final Comparator<Costume> orderComparator = new Comparator<Costume>()
    {
        @Override
        public int compare(Costume a, Costume b)
        {
            if (a.getOrder() != b.getOrder()) {
                return a.getOrder() - b.getOrder();
            } else {
                return a.getName().compareTo(b.getName());
            }
        }
    };

    /**
     * Used internally by Itchy.
     * Converts the trick data structure of Costume's events into something that can be handled by Itchy's simplistic
     * {@link PropertiesForm} based dialogs can handle.
     * 
     * @priority 5
     */
    public static class Event implements PropertySubject<Event>
    {
        protected static final List<Property<Event, ?>> event_properties = new ArrayList<Property<Event, ?>>();

        protected static final List<Property<Event, ?>> pose_properties = new ArrayList<Property<Event, ?>>();

        protected static final List<Property<Event, ?>> animation_properties = new ArrayList<Property<Event, ?>>();

        protected static final List<Property<Event, ?>> string_properties = new ArrayList<Property<Event, ?>>();

        protected static final List<Property<Event, ?>> companion_properties = new ArrayList<Property<Event, ?>>();

        protected static final List<Property<Event, ?>> sound_properties = new ArrayList<Property<Event, ?>>();

        protected static final List<Property<Event, ?>> text_style_properties = new ArrayList<Property<Event, ?>>();

        static {
            event_properties.add(new StringProperty<Event>("eventName").allowBlank(false));

            pose_properties.addAll(event_properties);
            pose_properties.add(new PoseResourceProperty<Event>("data").label("Pose"));

            animation_properties.addAll(event_properties);
            animation_properties.add(new AnimationProperty<Event>("data").label("Animation"));

            string_properties.addAll(event_properties);
            string_properties.add(new StringProperty<Event>("data").label("String"));

            companion_properties.addAll(event_properties);
            companion_properties.add(new CostumeProperty<Event>("data").label("Companion"));

            sound_properties.addAll(event_properties);
            sound_properties.add(new SoundProperty<Event>("data.soundResource"));
            sound_properties.add(new IntegerProperty<Event>("data.priority"));
            sound_properties.add(new DoubleProperty<Event>("data.fadeOutSeconds"));
            sound_properties.add(new BooleanProperty<Event>("data.fadeOnDeath"));
            sound_properties.add(new EnumProperty<Event, MultipleRole>("data.multipleRole", MultipleRole.class));

            text_style_properties.addAll(event_properties);
            text_style_properties.add(new FontProperty<Event>("data.font"));
            text_style_properties.add(new IntegerProperty<Event>("data.fontSize"));
            text_style_properties.add(new RGBAProperty<Event>("data.color"));
            text_style_properties.add(new DoubleProperty<Event>("data.xAlignment").hint("0..1"));
            text_style_properties.add(new DoubleProperty<Event>("data.yAlignment").hint("0..1"));
            text_style_properties.add(new IntegerProperty<Event>("data.marginTop"));
            text_style_properties.add(new IntegerProperty<Event>("data.marginRight"));
            text_style_properties.add(new IntegerProperty<Event>("data.marginBottom"));

        }

        @Override
        public List<Property<Event, ?>> getProperties()
        {
            if (data instanceof PoseResource) {
                return pose_properties;
            } else if (data instanceof String) {
                return string_properties;
            } else if (data instanceof ManagedSound) {
                return sound_properties;
            } else if (data instanceof Costume) {
                return companion_properties;
            } else if (data instanceof TextStyle) {
                return text_style_properties;
            } else if (data instanceof AnimationResource) {
                return animation_properties;
            }
            return event_properties;
        }

        public Costume costume;

        public String initialEventName;

        public String eventName;

        public Object initialData;

        public Object data;

        public String type;

        public Event(Costume costume, String eventName, Object object, String type)
        {
            this.costume = costume;
            this.initialEventName = eventName;
            this.eventName = eventName;
            this.initialData = object;
            this.data = object;
            this.type = type;
        }

        public String getResourceName()
        {
            if (data instanceof Named) {
                return ((Named) data).getName();
            } else if (data instanceof TextStyle) {
                return ((TextStyle) data).font.getName();
            } else if (data instanceof ManagedSound) {
                return ((ManagedSound) data).soundResource.getName();
            } else if (data instanceof String) {
                return (String) data;
            } else {
                System.out.println("Unknown type : " + data.getClass().getName());
                return "";
            }
        }

        public void update()
        {
            if (data instanceof PoseResource) {
                costume.removePose(initialEventName, (PoseResource) initialData);
                costume.addPose(eventName, (PoseResource) data);

            } else if (data instanceof String) {
                costume.removeString(initialEventName, (String) initialData);
                costume.addString(eventName, (String) data);

            } else if (data instanceof ManagedSound) {
                costume.removeSound(initialEventName, (ManagedSound) initialData);
                costume.addSound(eventName, (ManagedSound) data);

            } else if (data instanceof Costume) {
                costume.removeCompanion(initialEventName, (Costume) initialData);
                costume.addCompanion(eventName, (Costume) data);

            } else if (data instanceof TextStyle) {
                costume.removeTextStyle(initialEventName, (TextStyle) initialData);
                costume.addTextStyle(eventName, (TextStyle) data);

            } else if (data instanceof AnimationResource) {
                costume.removeAnimation(initialEventName, (AnimationResource) initialData);
                costume.addAnimation(eventName, (AnimationResource) data);
            }
        }

    }
}
