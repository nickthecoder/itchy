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

    @Override
    public List<Property<Costume, ?>> getProperties()
    {
        return properties;
    }

    private String name;

    private Costume extendedFrom;

    public ClassName roleClassName = new ClassName(Role.class, uk.co.nickthecoder.itchy.role.PlainRole.class.getName());

    public int defaultZOrder;

    public boolean showInDesigner = true;

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
     */
    public HashMap<String, List<String>> companionStringChoices;

    private static final Random random = new Random();

    public Costume()
    {
        this(null);
    }

    public Costume(Costume extendsFrom)
    {
        extendedFrom = extendsFrom;

        stringChoices = new HashMap<String, List<String>>();
        soundChoices = new HashMap<String, List<ManagedSound>>();
        poseChoices = new HashMap<String, List<PoseResource>>();
        textStyleChoices = new HashMap<String, List<TextStyle>>();
        animationChoices = new HashMap<String, List<AnimationResource>>();
        companionChoices = new HashMap<String, List<Costume>>();
        companionStringChoices = new HashMap<String, List<String>>();
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void setName(String name)
    {
        this.name = name;
    }

    public int getOrder()
    {
        return order;
    }

    public void setOrder(int order)
    {
        this.order = order;
    }

    public String getExtendedFromName()
    {
        Costume base = this.getExtendedFrom();
        if (base == null) {
            return null;
        } else {
            return Itchy.getGame().resources.getCostumeName(base);
        }
    }

    public Surface getThumbnail()
    {
        return Itchy.getGame().resources.getThumbnail(this);
    }

    public Actor createActor(String startEvent)
    {
        Actor actor = new Actor(this, startEvent);
        actor.setZOrder(this.defaultZOrder);
        Role role;
        try {
            role = AbstractRole.createRole(Itchy.getGame().resources, roleClassName);

        } catch (Exception e) {
            role = new PlainRole();
            e.printStackTrace();
        }
        actor.setRole(role);
        actor.event(startEvent);

        return actor;
    }

    private String roleClassNameForCostumeFeatures;

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
                Role dummyRole = AbstractRole.createRole(Itchy.getGame().resources, roleClassName);
                costumeFeatures = dummyRole.createCostumeFeatures(this);
            } catch (Exception e) {
                e.printStackTrace();
                costumeFeatures = new CostumeFeatures(this);
            }
        }
        return costumeFeatures;
    }

    public Costume getExtendedFrom()
    {
        return extendedFrom;
    }

    public void setExtendedFrom(Costume costume)
    {
        extendedFrom = costume;
    }

    public Set<String> getTextStyleNames()
    {
        return textStyleChoices.keySet();
    }

    public List<String> getStringNames()
    {
        return Resources.sortNames(stringChoices.keySet());
    }

    public List<String> getPoseNames()
    {
        return Resources.sortNames(poseChoices.keySet());
    }

    public List<String> getSoundNames()
    {
        return Resources.sortNames(soundChoices.keySet());
    }

    public List<String> getAnimationNames()
    {
        return Resources.sortNames(animationChoices.keySet());
    }

    public List<String> getCompanionNames()
    {
        return Resources.sortNames(companionChoices.keySet());
    }

    // String

    public void addString(String name, String value)
    {
        List<String> choices = stringChoices.get(name);
        if (choices == null) {
            choices = new ArrayList<String>();
            stringChoices.put(name, choices);
        }
        choices.add(value);
    }

    public void removeString(String name, String value)
    {
        List<String> choices = stringChoices.get(name);
        assert (choices.contains(value));
        choices.remove(value);
    }

    public String getString(String name, String defaultValue)
    {
        String result = this.getString(name);
        if (result == null) {
            return defaultValue;
        }

        return result;
    }

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

    public List<String> getStringChoices(String name)
    {
        return stringChoices.get(name);
    }

    // Companion (Costume)

    public void addCompanion(String name, Costume costume)
    {
        List<Costume> choices = companionChoices.get(name);
        if (choices == null) {
            choices = new ArrayList<Costume>();
            companionChoices.put(name, choices);
        }
        choices.add(costume);
    }

    public void removeCompanion(String name, Costume costume)
    {
        List<Costume> choices = companionChoices.get(name);
        assert (choices.contains(costume));
        choices.remove(costume);
    }

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

    public List<Costume> getCompanionChoices(String name)
    {
        return companionChoices.get(name);
    }

    // Pose

    public void addPose(String name, PoseResource poseResource)
    {
        List<PoseResource> choices = poseChoices.get(name);
        if (choices == null) {
            choices = new ArrayList<PoseResource>();
            poseChoices.put(name, choices);
        }
        choices.add(poseResource);
    }

    public void removePose(String name, PoseResource resource)
    {
        List<PoseResource> choices = poseChoices.get(name);
        assert (choices.contains(resource));
        choices.remove(resource);
    }

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

    public Pose getPose(String name)
    {
        PoseResource resource = this.getPoseResource(name);
        return resource == null ? null : resource.pose;
    }

    public List<PoseResource> getPoseChoices(String name)
    {
        return poseChoices.get(name);
    }

    // Sound
    public void addSound(String name, ManagedSound managedSound)
    {
        List<ManagedSound> choices = soundChoices.get(name);
        if (choices == null) {
            choices = new ArrayList<ManagedSound>();
            soundChoices.put(name, choices);
        }
        choices.add(managedSound);

    }

    public ManagedSound addSound(String name, SoundResource soundResource)
    {
        ManagedSound managedSound = new ManagedSound(soundResource);
        this.addSound(name, managedSound);
        return managedSound;
    }

    public void removeSound(String name, ManagedSound value)
    {
        List<ManagedSound> choices = soundChoices.get(name);
        assert (choices.contains(value));
        choices.remove(value);
    }

    public SoundResource getSoundResource(String name)
    {
        ManagedSound cs = getCostumeSound(name);
        if (cs == null) {
            return null;
        } else {
            return cs.soundResource;
        }
    }

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

    public Sound getSound(String name)
    {
        SoundResource resource = this.getSoundResource(name);
        return resource == null ? null : resource.getSound();
    }

    public List<ManagedSound> getSoundChoices(String name)
    {
        return soundChoices.get(name);
    }

    // TextStyle

    public void addTextStyle(String name, TextStyle textStyle)
    {
        List<TextStyle> choices = textStyleChoices.get(name);
        if (choices == null) {
            choices = new ArrayList<TextStyle>();
            textStyleChoices.put(name, choices);
        }
        choices.add(textStyle);
    }

    public void removeTextStyle(String name, TextStyle value)
    {
        List<TextStyle> choices = textStyleChoices.get(name);
        assert (choices.contains(value));
        choices.remove(value);
    }

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

    /*
     * public Font getFont( String name ) { FontResource resource = this.getFontResource(name); return resource == null
     * ? null : resource.font; }
     */

    public List<TextStyle> getTextStyleChoices(String name)
    {
        return textStyleChoices.get(name);
    }

    // Animation

    public void addAnimation(String name, AnimationResource animationResource)
    {
        List<AnimationResource> choices = animationChoices.get(name);
        if (choices == null) {
            choices = new ArrayList<AnimationResource>();
            animationChoices.put(name, choices);
        }
        choices.add(animationResource);
    }

    public void removeAnimation(String name, AnimationResource value)
    {
        List<AnimationResource> choices = animationChoices.get(name);
        assert (choices.contains(value));
        choices.remove(value);
    }

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

    public Animation getAnimation(String name)
    {
        AnimationResource resource = this.getAnimationResource(name);
        return resource == null ? null : resource.animation;
    }

    public List<AnimationResource> getAnimationChoices(String name)
    {
        return animationChoices.get(name);
    }

    public Costume copy(Resources resource)
    {
        try {
            Costume result = (Costume) super.clone();

            result.roleClassName = new ClassName(roleClassName.baseClass, roleClassName.name);

            // result.costumeProperties = this.costumeProperties.copy();

            result.animationChoices = new HashMap<String, List<AnimationResource>>();
            for (String eventName : animationChoices.keySet()) {
                List<AnimationResource> list = new ArrayList<AnimationResource>();
                list.addAll(animationChoices.get(eventName));
                result.animationChoices.put(eventName, list);
            }

            result.stringChoices = new HashMap<String, List<String>>();
            for (String eventName : stringChoices.keySet()) {
                List<String> list = new ArrayList<String>();
                list.addAll(stringChoices.get(eventName));
                result.stringChoices.put(eventName, list);
            }

            result.soundChoices = new HashMap<String, List<ManagedSound>>();
            for (String eventName : soundChoices.keySet()) {
                List<ManagedSound> list = new ArrayList<ManagedSound>();
                list.addAll(soundChoices.get(eventName));
                result.soundChoices.put(eventName, list);
            }

            result.poseChoices = new HashMap<String, List<PoseResource>>();
            for (String eventName : poseChoices.keySet()) {
                List<PoseResource> list = new ArrayList<PoseResource>();
                list.addAll(poseChoices.get(eventName));
                result.poseChoices.put(eventName, list);
            }

            result.textStyleChoices = new HashMap<String, List<TextStyle>>();
            for (String eventName : textStyleChoices.keySet()) {
                List<TextStyle> list = new ArrayList<TextStyle>();
                list.addAll(textStyleChoices.get(eventName));
                result.textStyleChoices.put(eventName, list);
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString()
    {
        return "Costume " + getName();
    }

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
                return ((TextStyle)data).font.getName();
            } else if (data instanceof ManagedSound) {
                return ((ManagedSound)data).soundResource.getName();
            } else if (data instanceof String) {
                return (String)data;
            } else {
                System.out.println( "Unknown type : " + data.getClass().getName() );
                return "";
            }
        }

        public void update()
        {
            if (data instanceof PoseResource) {
                costume.removePose(initialEventName,  (PoseResource) initialData); 
                costume.addPose(eventName,  (PoseResource) data); 
                
            } else if (data instanceof String) {
                costume.removeString(initialEventName,  (String) initialData); 
                costume.addString(eventName,  (String) data); 
                
            } else if (data instanceof ManagedSound) {
                costume.removeSound(initialEventName,  (ManagedSound) initialData); 
                costume.addSound(eventName,  (ManagedSound) data); 
                
            } else if (data instanceof Costume) {
                costume.removeCompanion(initialEventName,  (Costume) initialData); 
                costume.addCompanion(eventName,  (Costume) data); 
                
            } else if (data instanceof TextStyle) {
                costume.removeTextStyle(initialEventName,  (TextStyle) initialData); 
                costume.addTextStyle(eventName,  (TextStyle) data); 
                
            } else if (data instanceof AnimationResource) {
                costume.removeAnimation(initialEventName,  (AnimationResource) initialData); 
                costume.addAnimation(eventName,  (AnimationResource) data); 
            }
        }

    }
}
