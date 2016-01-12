/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import uk.co.nickthecoder.itchy.animation.Animation;
import uk.co.nickthecoder.itchy.role.PlainRole;
import uk.co.nickthecoder.itchy.util.ClassName;
import uk.co.nickthecoder.jame.Sound;

public class Costume implements Cloneable
{
    private static final Random random = new Random();

    private Costume extendedFrom;

    public ClassName roleClassName = new ClassName(Role.class, uk.co.nickthecoder.itchy.role.PlainRole.class.getName());

    public int defaultZOrder;

    public boolean showInDesigner = true;

    private CostumeProperties costumeProperties;

    private HashMap<String, List<AnimationResource>> animationChoices;

    private HashMap<String, List<String>> stringChoices;

    private HashMap<String, List<ManagedSound>> soundChoices;

    private HashMap<String, List<PoseResource>> poseChoices;

    private HashMap<String, List<TextStyle>> textStyleChoices;

    /**
     * A Costume can link to other costumes by a String key. These are called
     * "companions", because they tend to be used to create companion objects.
     * For example, "Space Miner" (asteroids) uses this when one "rock" breaks
     * up into smaller rocks. The smaller rocks are the companions of the larger
     * one.
     */
    private HashMap<String, List<CostumeResource>> companionChoices;

    /**
     * When loading a costume, we may have circular references, which can only
     * be resolved when all costumes have been loaded. This stores the list of
     * names of costumes during the load phase, which are then converted into
     * companionChoices at the end.
     */
    public HashMap<String, List<String>> companionStringChoices;

    public Costume()
    {
        this(null);
    }

    public Costume(Costume extendsFrom)
    {
        this.extendedFrom = extendsFrom;

        this.stringChoices = new HashMap<String, List<String>>();
        this.soundChoices = new HashMap<String, List<ManagedSound>>();
        this.poseChoices = new HashMap<String, List<PoseResource>>();
        this.textStyleChoices = new HashMap<String, List<TextStyle>>();
        this.animationChoices = new HashMap<String, List<AnimationResource>>();
        this.companionChoices = new HashMap<String, List<CostumeResource>>();
        this.companionStringChoices = new HashMap<String, List<String>>();
    }

    public Actor createActor(String startEvent)
    {
        Actor actor = new Actor(this, startEvent);
        Role role;
        try {
            role = AbstractRole.createRole(Itchy.getGame().resources, this.roleClassName);

        } catch (Exception e) {
            role = new PlainRole();
            e.printStackTrace();
        }
        actor.setRole(role);
        actor.event(startEvent);

        return actor;
    }

    // TODO REMOVE Costume.getProperties
    @Deprecated
    public CostumeProperties getProperties()
    {
        System.err.println("Called deprecated method : Costume.getCostumeProperties. Use getCostumeProperties instead.");
        try {
            throw new RuntimeException();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getCostumeProperties();
    }

    private String roleClassNameForCostumeProperties;

    public CostumeProperties getCostumeProperties()
    {
        // If the Role has changed since we last created the costume properties, then we can't use
        // the old costumeProperties.
        if ((this.roleClassNameForCostumeProperties != null)
                        && (! this.roleClassNameForCostumeProperties.equals(this.roleClassName.name))) {
            this.costumeProperties = null;
        }

        if (this.costumeProperties == null) {
            try {
                roleClassNameForCostumeProperties = this.roleClassName.name;
                Role dummyRole = AbstractRole.createRole(Itchy.getGame().resources, this.roleClassName);
                this.costumeProperties = dummyRole.createCostumeProperties();
            } catch (Exception e) {
                e.printStackTrace();
                this.costumeProperties = new CostumeProperties();
            }
            this.costumeProperties.setCostume( this );
        }
        return this.costumeProperties;
    }

    public Costume getExtendedFrom()
    {
        return this.extendedFrom;
    }

    public void setExtendedFrom(Costume costume)
    {
        this.extendedFrom = costume;
    }

    public Set<String> getTextStyleNames()
    {
        return this.textStyleChoices.keySet();
    }

    public List<String> getStringNames()
    {
        return Resources.sortNames(this.stringChoices.keySet());
    }

    public List<String> getPoseNames()
    {
        return Resources.sortNames(this.poseChoices.keySet());
    }

    public List<String> getSoundNames()
    {
        return Resources.sortNames(this.soundChoices.keySet());
    }

    public List<String> getAnimationNames()
    {
        return Resources.sortNames(this.animationChoices.keySet());
    }

    public List<String> getCompanionNames()
    {
        return Resources.sortNames(this.companionChoices.keySet());
    }

    // String

    public void addString(String name, String value)
    {
        List<String> choices = this.stringChoices.get(name);
        if (choices == null) {
            choices = new ArrayList<String>();
            this.stringChoices.put(name, choices);
        }
        choices.add(value);
    }

    public void removeString(String name, String value)
    {
        List<String> choices = this.stringChoices.get(name);
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
        List<String> strings = this.stringChoices.get(name);
        if ((strings == null) || (strings.size() == 0)) {
            if (this.extendedFrom != null) {
                return this.extendedFrom.getString(name);
            }
            return null;
        }
        String string = strings.get(random.nextInt(strings.size()));
        return string;
    }

    public List<String> getStringChoices(String name)
    {
        return this.stringChoices.get(name);
    }

    // Companion (Costume)

    public void addCompanion(String name, CostumeResource costumeResource)
    {
        List<CostumeResource> choices = this.companionChoices.get(name);
        if (choices == null) {
            choices = new ArrayList<CostumeResource>();
            this.companionChoices.put(name, choices);
        }
        choices.add(costumeResource);
    }

    public void removeCompanion(String name, CostumeResource costumeResource)
    {
        List<CostumeResource> choices = this.companionChoices.get(name);
        assert (choices.contains(costumeResource));
        choices.remove(costumeResource);
    }

    public CostumeResource getCompanionResource(String name)
    {
        List<CostumeResource> choices = this.companionChoices.get(name);
        if ((choices == null) || (choices.size() == 0)) {
            if (this.extendedFrom != null) {
                return this.extendedFrom.getCompanionResource(name);
            }
            return null;
        }
        if (choices.size() == 0) {
            return null;
        }
        CostumeResource costumeResource = choices.get(random.nextInt(choices.size()));
        return costumeResource;
    }

    public Costume getCompanion(String name)
    {
        CostumeResource resource = getCompanionResource(name);
        return resource == null ? null : resource.getCostume();
    }

    public List<CostumeResource> getCompanionChoices(String name)
    {
        return this.companionChoices.get(name);
    }

    // Pose

    public void addPose(String name, PoseResource poseResource)
    {
        List<PoseResource> choices = this.poseChoices.get(name);
        if (choices == null) {
            choices = new ArrayList<PoseResource>();
            this.poseChoices.put(name, choices);
        }
        choices.add(poseResource);
    }

    public void removePose(String name, PoseResource resource)
    {
        List<PoseResource> choices = this.poseChoices.get(name);
        assert (choices.contains(resource));
        choices.remove(resource);
    }

    public PoseResource getPoseResource(String name)
    {
        List<PoseResource> choices = this.poseChoices.get(name);
        if ((choices == null) || (choices.size() == 0)) {
            if (this.extendedFrom != null) {
                return this.extendedFrom.getPoseResource(name);
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
        return this.poseChoices.get(name);
    }

    // Sound
    public void addSound(String name, ManagedSound managedSound)
    {
        List<ManagedSound> choices = this.soundChoices.get(name);
        if (choices == null) {
            choices = new ArrayList<ManagedSound>();
            this.soundChoices.put(name, choices);
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
        List<ManagedSound> choices = this.soundChoices.get(name);
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
        List<ManagedSound> choices = this.soundChoices.get(name);
        if ((choices == null) || (choices.size() == 0)) {
            if (this.extendedFrom != null) {
                return this.extendedFrom.getCostumeSound(name);
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
        return this.soundChoices.get(name);
    }

    // TextStyle

    public void addTextStyle(String name, TextStyle textStyle)
    {
        List<TextStyle> choices = this.textStyleChoices.get(name);
        if (choices == null) {
            choices = new ArrayList<TextStyle>();
            this.textStyleChoices.put(name, choices);
        }
        choices.add(textStyle);
    }

    public void removeTextStyle(String name, TextStyle value)
    {
        List<TextStyle> choices = this.textStyleChoices.get(name);
        assert (choices.contains(value));
        choices.remove(value);
    }

    public TextStyle getTextStyle(String name)
    {
        List<TextStyle> choices = this.textStyleChoices.get(name);
        if ((choices == null) || (choices.size() == 0)) {
            if (this.extendedFrom != null) {
                return this.extendedFrom.getTextStyle(name);
            }
            return null;
        }
        TextStyle textStyle = choices.get(random.nextInt(choices.size()));
        return textStyle;
    }

    /*
     * public Font getFont( String name ) { FontResource resource =
     * this.getFontResource(name); return resource == null ? null :
     * resource.font; }
     */

    public List<TextStyle> getTextStyleChoices(String name)
    {
        return this.textStyleChoices.get(name);
    }

    // Animation

    public void addAnimation(String name, AnimationResource animationResource)
    {
        List<AnimationResource> choices = this.animationChoices.get(name);
        if (choices == null) {
            choices = new ArrayList<AnimationResource>();
            this.animationChoices.put(name, choices);
        }
        choices.add(animationResource);
    }

    public void removeAnimation(String name, AnimationResource value)
    {
        List<AnimationResource> choices = this.animationChoices.get(name);
        assert (choices.contains(value));
        choices.remove(value);
    }

    public AnimationResource getAnimationResource(String name)
    {
        List<AnimationResource> choices = this.animationChoices.get(name);
        if ((choices == null) || (choices.size() == 0)) {
            if (this.extendedFrom != null) {
                return this.extendedFrom.getAnimationResource(name);
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
        return this.animationChoices.get(name);
    }

    public Costume copy(Resources resource)
    {
        try {
            Costume result = (Costume) super.clone();

            result.roleClassName = new ClassName(this.roleClassName.baseClass, this.roleClassName.name);

            // result.costumeProperties = this.costumeProperties.copy();

            result.animationChoices = new HashMap<String, List<AnimationResource>>();
            for (String eventName : this.animationChoices.keySet()) {
                List<AnimationResource> list = new ArrayList<AnimationResource>();
                list.addAll(this.animationChoices.get(eventName));
                result.animationChoices.put(eventName, list);
            }

            result.stringChoices = new HashMap<String, List<String>>();
            for (String eventName : this.stringChoices.keySet()) {
                List<String> list = new ArrayList<String>();
                list.addAll(this.stringChoices.get(eventName));
                result.stringChoices.put(eventName, list);
            }

            result.soundChoices = new HashMap<String, List<ManagedSound>>();
            for (String eventName : this.soundChoices.keySet()) {
                List<ManagedSound> list = new ArrayList<ManagedSound>();
                list.addAll(this.soundChoices.get(eventName));
                result.soundChoices.put(eventName, list);
            }

            result.poseChoices = new HashMap<String, List<PoseResource>>();
            for (String eventName : this.poseChoices.keySet()) {
                List<PoseResource> list = new ArrayList<PoseResource>();
                list.addAll(this.poseChoices.get(eventName));
                result.poseChoices.put(eventName, list);
            }

            result.textStyleChoices = new HashMap<String, List<TextStyle>>();
            for (String eventName : this.textStyleChoices.keySet()) {
                List<TextStyle> list = new ArrayList<TextStyle>();
                list.addAll(this.textStyleChoices.get(eventName));
                result.textStyleChoices.put(eventName, list);
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String toString()
    {
        try {
            CostumeResource cr = Itchy.getGame().resources.getCostumeResource(this);
            return "Costume " + cr.getName();
        } catch (Exception e) {
            return "Costume (not named in resources)";
        }
    }
}
