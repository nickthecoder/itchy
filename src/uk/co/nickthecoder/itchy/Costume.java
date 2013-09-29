/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import uk.co.nickthecoder.itchy.animation.Animation;
import uk.co.nickthecoder.jame.Sound;

public class Costume
{
    private static final Random random = new Random();

    private Costume extendedFrom;

    private final HashMap<String, List<AnimationResource>> animationChoices;

    private final HashMap<String, List<String>> stringChoices;

    private final HashMap<String, List<ManagedSound>> soundChoices;

    private final HashMap<String, List<PoseResource>> poseChoices;

    private final HashMap<String, List<FontResource>> fontChoices;

    public String behaviourClassName = uk.co.nickthecoder.itchy.NullBehaviour.class.getName();

    private CostumeProperties properties = new CostumeProperties();

    public Costume()
    {
        this(null);
    }

    public Costume( Costume extendsFrom )
    {
        this.extendedFrom = extendsFrom;

        this.stringChoices = new HashMap<String, List<String>>();
        this.soundChoices = new HashMap<String, List<ManagedSound>>();
        this.poseChoices = new HashMap<String, List<PoseResource>>();
        this.fontChoices = new HashMap<String, List<FontResource>>();
        this.animationChoices = new HashMap<String, List<AnimationResource>>();
    }

    public Costume getExtendedFrom()
    {
        return this.extendedFrom;
    }

    public void setExtendedFrom( Costume costume )
    {
        this.extendedFrom = costume;
    }

    public Set<String> getFontNames()
    {
        return this.fontChoices.keySet();
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

    // String

    public void addString( String name, String value )
    {
        List<String> choices = this.stringChoices.get(name);
        if (choices == null) {
            choices = new ArrayList<String>();
            this.stringChoices.put(name, choices);
        }
        choices.add(value);
    }

    public void removeString( String name, String value )
    {
        List<String> choices = this.stringChoices.get(name);
        assert (choices.contains(value));
        choices.remove(value);
    }

    public String getString( String name, String defaultValue )
    {
        String result = this.getString(name);
        if (result == null) {
            return defaultValue;
        }

        return result;
    }

    public String getString( String name )
    {
        List<String> strings = this.stringChoices.get(name);
        if ((strings == null) || (strings.size()==0)) {
            if (this.extendedFrom != null) {
                return this.extendedFrom.getString(name);
            }
            return null;
        }
        String string = strings.get(random.nextInt(strings.size()));
        return string;
    }

    public List<String> getStringChoices( String name )
    {
        return this.stringChoices.get(name);
    }

    // Pose

    public void addPose( String name, PoseResource poseResource )
    {
        List<PoseResource> choices = this.poseChoices.get(name);
        if (choices == null) {
            choices = new ArrayList<PoseResource>();
            this.poseChoices.put(name, choices);
        }
        choices.add(poseResource);
    }

    public void removePose( String name, PoseResource resource )
    {
        List<PoseResource> choices = this.poseChoices.get(name);
        assert (choices.contains(resource));
        choices.remove(resource);
    }

    public PoseResource getPoseResource( String name )
    {
        List<PoseResource> choices = this.poseChoices.get(name);
        if ((choices == null) || (choices.size()==0)) {
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

    public Pose getPose( String name )
    {
        PoseResource resource = this.getPoseResource(name);
        return resource == null ? null : resource.pose;
    }

    public List<PoseResource> getPoseChoices( String name )
    {
        return this.poseChoices.get(name);
    }

    // Sound
    public void addSound( String name, ManagedSound managedSound )
    {
        List<ManagedSound> choices = this.soundChoices.get(name);
        if (choices == null) {
            choices = new ArrayList<ManagedSound>();
            this.soundChoices.put(name, choices);
        }
        choices.add(managedSound);

    }

    public ManagedSound addSound( String name, SoundResource soundResource )
    {
        ManagedSound managedSound = new ManagedSound(soundResource);
        this.addSound(name, managedSound);
        return managedSound;
    }

    public void removeSound( String name, ManagedSound value )
    {
        List<ManagedSound> choices = this.soundChoices.get(name);
        assert (choices.contains(value));
        choices.remove(value);
    }

    public SoundResource getSoundResource( String name )
    {
        ManagedSound cs = getCostumeSound(name);
        if (cs == null) {
            return null;
        } else {
            return cs.soundResource;
        }
    }

    public ManagedSound getCostumeSound( String name )
    {
        List<ManagedSound> choices = this.soundChoices.get(name);
        if ((choices == null) || (choices.size()==0)) {
            if (this.extendedFrom != null) {
                return this.extendedFrom.getCostumeSound(name);
            }
            return null;
        }
        ManagedSound costumeSound = choices.get(random.nextInt(choices.size()));
        return costumeSound;
    }

    public Sound getSound( String name )
    {
        SoundResource resource = this.getSoundResource(name);
        return resource == null ? null : resource.getSound();
    }

    public List<ManagedSound> getSoundChoices( String name )
    {
        return this.soundChoices.get(name);
    }

    // Font

    public void addFont( String name, FontResource fontResource )
    {
        List<FontResource> choices = this.fontChoices.get(name);
        if (choices == null) {
            choices = new ArrayList<FontResource>();
            this.fontChoices.put(name, choices);
        }
        choices.add(fontResource);
    }

    public void removeFont( String name, FontResource value )
    {
        List<FontResource> choices = this.fontChoices.get(name);
        assert (choices.contains(value));
        choices.remove(value);
    }

    public FontResource getFontResource( String name )
    {
        List<FontResource> choices = this.fontChoices.get(name);
        if ((choices == null) || (choices.size()==0)) {
            if (this.extendedFrom != null) {
                return this.extendedFrom.getFontResource(name);
            }
            return null;
        }
        FontResource fontResource = choices.get(random.nextInt(choices.size()));
        return fontResource;
    }

    public Font getFont( String name )
    {
        FontResource resource = this.getFontResource(name);
        return resource == null ? null : resource.font;
    }

    public List<FontResource> getFontChoices( String name )
    {
        return this.fontChoices.get(name);
    }

    // Animation

    public void addAnimation( String name, AnimationResource animationResource )
    {
        List<AnimationResource> choices = this.animationChoices.get(name);
        if (choices == null) {
            choices = new ArrayList<AnimationResource>();
            this.animationChoices.put(name, choices);
        }
        choices.add(animationResource);
    }

    public void removeAnimation( String name, AnimationResource value )
    {
        List<AnimationResource> choices = this.animationChoices.get(name);
        assert (choices.contains(value));
        choices.remove(value);
    }

    public AnimationResource getAnimationResource( String name )
    {
        List<AnimationResource> choices = this.animationChoices.get(name);
        if ((choices == null) || (choices.size()==0)) {
            if (this.extendedFrom != null) {
                return this.extendedFrom.getAnimationResource(name);
            }
            return null;
        }
        AnimationResource animationResource = choices
            .get(random.nextInt(choices.size()));
        return animationResource;
    }

    public Animation getAnimation( String name )
    {
        AnimationResource resource = this.getAnimationResource(name);
        return resource == null ? null : resource.animation;
    }

    public List<AnimationResource> getAnimationChoices( String name )
    {
        return this.animationChoices.get(name);
    }

    public String getPropertiesClassName()
    {
        return this.properties.getClass().getName();
    }

    public void setPropertiesClassName( String value )
    {
        if (!value.equals(getPropertiesClassName())) {
            this.properties = CostumeProperties.createProperties(value);
        }
    }

    public CostumeProperties getProperties()
    {
        return this.properties;
    }

    public void setProperties( CostumeProperties value )
    {
        this.properties = value;
    }

}
