/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
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

    private final HashMap<String, List<SoundResource>> soundChoices;

    private final HashMap<String, List<PoseResource>> poseChoices;

    private final HashMap<String, List<FontResource>> fontChoices;

    public String behaviourClassName = uk.co.nickthecoder.itchy.NullBehaviour.class.getName();

    public Costume()
    {
        this(null);
    }

    public Costume( Costume extendsFrom )
    {
        this.extendedFrom = extendsFrom;

        this.stringChoices = new HashMap<String, List<String>>();
        this.soundChoices = new HashMap<String, List<SoundResource>>();
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

    public String getString( String name, String defaultValue )
    {
        String result = this.getString(name);
        if ( result == null) {
            return defaultValue;
        }
        
        return result;
    }
    
    public String getString( String name )
    {
        List<String> strings = this.stringChoices.get(name);
        if (strings == null) {
            if (this.extendedFrom != null) {
                return this.extendedFrom.getString(name);
            }
            return null;
        }
        String string = strings.get(random.nextInt(strings.size()));
        return string;
    }
    
    public int getInt( String name, int defaultValue )
    {
        try {
            return Integer.parseInt( getString(name) );
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public double getDouble( String name, double defaultValue )
    {
        try {
            return Double.parseDouble( getString(name) );
        } catch (Exception e) {
            return defaultValue;
        }
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
        if (choices == null) {
            if (this.extendedFrom != null) {
                return this.extendedFrom.getPoseResource(name);
            }
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

    public void addSound( String name, SoundResource soundResource )
    {
        List<SoundResource> choices = this.soundChoices.get(name);
        if (choices == null) {
            choices = new ArrayList<SoundResource>();
            this.soundChoices.put(name, choices);
        }
        choices.add(soundResource);
    }

    public SoundResource getSoundResource( String name )
    {
        List<SoundResource> choices = this.soundChoices.get(name);
        if (choices == null) {
            if (this.extendedFrom != null) {
                return this.extendedFrom.getSoundResource(name);
            }
            return null;
        }
        SoundResource soundResource = choices.get(random.nextInt(choices.size()));
        return soundResource;
    }

    public Sound getSound( String name )
    {
        SoundResource resource = this.getSoundResource(name);
        return resource == null ? null : resource.getSound();
    }

    public List<SoundResource> getSoundChoices( String name )
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

    public FontResource getFontResource( String name )
    {
        List<FontResource> fontList = this.fontChoices.get(name);
        if (fontList == null) {
            if (this.extendedFrom != null) {
                return this.extendedFrom.getFontResource(name);
            }
            return null;
        }
        FontResource fontResource = fontList.get(random.nextInt(fontList.size()));
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

    public AnimationResource getAnimationResource( String name )
    {
        List<AnimationResource> animationList = this.animationChoices.get(name);
        if (animationList == null) {
            if (this.extendedFrom != null) {
                return this.extendedFrom.getAnimationResource(name);
            }
            return null;
        }
        AnimationResource animationResource = animationList
                .get(random.nextInt(animationList.size()));
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

}
