/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import uk.co.nickthecoder.itchy.animation.Animation;
import uk.co.nickthecoder.itchy.util.NinePatch;
import uk.co.nickthecoder.jame.Sound;
import uk.co.nickthecoder.jame.Surface;

public class Resources extends Loadable
{

    public static List<String> sortNames( Set<String> set )
    {
        if (set == null) {
            return null;
        }
        ArrayList<String> names = new ArrayList<String>(set);
        Collections.sort(names);
        return names;
    }

    private final HashMap<String, SoundResource> sounds;

    private final HashMap<String, FontResource> fonts;

    private final HashMap<String, PoseResource> poses;

    private final HashMap<String, NinePatchResource> ninePatches;

    private final HashMap<String, SceneResource> scenes;

    private final HashMap<String, AnimationResource> animations;

    private final HashMap<String, CostumeResource> costumes;

    private TreeSet<String> behaviourClassNames;

    private TreeSet<String> costumePropertiesClassNames;

    private TreeSet<String> sceneBehaviourClassNames;

    public Resources()
    {
        super();
        this.sounds = new HashMap<String, SoundResource>();
        this.fonts = new HashMap<String, FontResource>();
        this.ninePatches = new HashMap<String, NinePatchResource>();
        this.scenes = new HashMap<String, SceneResource>();

        this.poses = new HashMap<String, PoseResource>();
        this.costumes = new HashMap<String, CostumeResource>();
        this.animations = new HashMap<String, AnimationResource>();

        this.behaviourClassNames = new TreeSet<String>();
        this.costumePropertiesClassNames = new TreeSet<String>();
        this.sceneBehaviourClassNames = new TreeSet<String>();

        this.registerBehaviourClassName(NullBehaviour.class.getName());
        this.registerCostumePropertiesClassName(NoProperties.class.getName());
        this.registerSceneBehaviourClassName(NullSceneBehaviour.class.getName());
    }

    @Override
    public void load() throws Exception
    {
        ResourcesReader loader = new ResourcesReader(this);
        loader.load(getFilename());
    }

    @Override
    protected void actualSave( File file ) throws Exception
    {
        ResourcesWriter writer = new ResourcesWriter(this);
        writer.write(file.getPath());
    }

    @Override
    protected void checkSave( File file ) throws Exception
    {
        Resources resources = new Resources();
        resources.load(file);

        // MORE. Should check that each resource is identical to the other one.

        for (String name : this.animationNames()) {
            if (resources.getAnimation(name) == null) {
                throw new Exception("Animation " + name + " wasn't saved");
            }
        }
        for (String name : this.poseNames()) {
            if (resources.getPose(name) == null) {
                throw new Exception("Pose " + name + " wasn't saved");
            }
        }
        for (String name : this.soundNames()) {
            if (resources.getSound(name) == null) {
                throw new Exception("Sound " + name + " wasn't saved");
            }
        }
        for (String name : this.fontNames()) {
            if (resources.getFont(name) == null) {
                throw new Exception("Font " + name + " wasn't saved");
            }
        }
        for (String name : this.ninePatchNames()) {
            if (resources.getNinePatch(name) == null) {
                throw new Exception("Nine Patch " + name + " wasn't saved");
            }
        }
        for (String name : this.sceneNames()) {
            if (!resources.sceneNames().contains(name)) {
                throw new Exception("Scene " + name + " wasn't saved");
            }
        }
        for (String name : this.costumeNames()) {
            if (resources.getCostume(name) == null) {
                throw new Exception("Costume " + name + " wasn't saved");
            }
        }

    }

    public void renameResource( Object object, String name )
    {
        if (object instanceof SoundResource) {
            this.rename2((SoundResource) object, name);

        } else if (object instanceof FontResource) {
            this.rename2((FontResource) object, name);

        } else if (object instanceof PoseResource) {
            this.rename2((PoseResource) object, name);

        } else if (object instanceof NinePatchResource) {
            this.rename2((NinePatchResource) object, name);

        } else if (object instanceof AnimationResource) {
            this.rename2((AnimationResource) object, name);

        } else if (object instanceof SceneResource) {
            this.rename2((SceneResource) object, name);

        } else if (object instanceof CostumeResource) {
            this.rename2((CostumeResource) object, name);

        } else {
            throw new RuntimeException("Unknown resource type : " + object.getClass().getName());
        }
    }

    // Sounds

    public void addSound( SoundResource soundResource )
    {
        this.sounds.put(soundResource.name, soundResource);
    }

    public void removeSound( String name )
    {
        this.sounds.remove(name);
    }

    public SoundResource getSoundResource( String name )
    {
        return this.sounds.get(name);
    }

    public Sound getSound( String name )
    {
        SoundResource resource = this.sounds.get(name);
        return resource == null ? null : resource.getSound();
    }

    public List<String> soundNames()
    {
        return sortNames(this.sounds.keySet());
    }

    void rename2( SoundResource soundResource, String name )
    {
        this.sounds.remove(soundResource.getName());
        this.sounds.put(name, soundResource);
    }

    public String getSoundName( Sound sound )
    {
        for (String name : this.soundNames()) {
            if (this.getSound(name) == sound) {
                return name;
            }
        }
        return null;
    }

    // Fonts

    public void addFont( FontResource fontResource )
    {
        this.fonts.put(fontResource.getName(), fontResource);
    }

    public void removeFont( String name )
    {
        this.fonts.remove(name);
    }

    public FontResource getFontResource( String name )
    {
        return this.fonts.get(name);
    }

    public FontResource getFontResource( Font font )
    {
        for (FontResource fontResource : this.fonts.values()) {
            if (fontResource.font == font) {
                return fontResource;
            }
        }
        return null;
    }

    public Font getFont( String name )
    {
        FontResource resource = this.fonts.get(name);
        return resource == null ? null : resource.font;
    }

    public List<String> fontNames()
    {
        return sortNames(this.fonts.keySet());
    }

    void rename2( FontResource fontResource, String name )
    {
        this.fonts.remove(fontResource.getName());
        this.fonts.put(name, fontResource);
    }

    public String getFontName( Font font )
    {
        for (String name : this.fontNames()) {
            if (this.getFont(name) == font) {
                return name;
            }
        }
        return null;
    }

    /**
     * @return The default font. At the moment this is a randomly picked font!
     */
    public Font getDefaultFont()
    {
        FontResource fr = this.fonts.values().iterator().next();
        if (fr == null) {
            return null;
        }
        return fr.font;
    }

    // Poses

    public void addPose( PoseResource resource )
    {
        this.poses.put(resource.getName(), resource);
    }

    public void removePose( String name )
    {
        this.poses.remove(name);
    }

    public PoseResource getPoseResource( String name )
    {
        return this.poses.get(name);
    }

    public ImagePose getPose( String name )
    {
        PoseResource resource = this.poses.get(name);
        return resource == null ? null : resource.pose;
    }

    public List<String> poseNames()
    {
        return sortNames(this.poses.keySet());
    }

    void rename2( PoseResource poseResource, String name )
    {
        this.poses.remove(poseResource.getName());
        this.poses.put(name, poseResource);
    }

    public String getPoseName( Pose pose )
    {
        for (String name : this.poseNames()) {
            if (this.getPose(name) == pose) {
                return name;
            }
        }
        return null;
    }

    public PoseResource getPoseResource( Pose pose )
    {
        for (String name : this.poseNames()) {
            if (this.getPose(name) == pose) {
                return this.getPoseResource(name);
            }
        }
        return null;
    }

    public Surface getThumbnail( Pose pose )
    {
        PoseResource resource = this.getPoseResource(pose);
        if (resource == null) {
            return null;
        }
        return resource.getThumbnail();
    }

    // NinePatches

    public void addNinePatch( NinePatchResource ninePatchResource )
    {
        this.ninePatches.put(ninePatchResource.getName(), ninePatchResource);
    }

    public void removeNinePatch( String name )
    {
        this.ninePatches.remove(name);
    }

    public NinePatchResource getNinePatchResource( String name )
    {
        return this.ninePatches.get(name);
    }

    public NinePatch getNinePatch( String name )
    {
        NinePatchResource resource = this.ninePatches.get(name);
        return resource == null ? null : resource.ninePatch;
    }

    public List<String> ninePatchNames()
    {
        return sortNames(this.ninePatches.keySet());
    }

    void rename2( NinePatchResource ninePatchResource, String name )
    {
        this.ninePatches.remove(ninePatchResource.getName());
        this.ninePatches.put(name, ninePatchResource);
    }

    // Costumes

    public void addCostume( CostumeResource resource )
    {
        this.costumes.put(resource.name, resource);
        this.registerBehaviourClassName(resource.costume.behaviourClassName);
        this.registerCostumePropertiesClassName(resource.costume.getPropertiesClassName());
    }

    public void removeCostume( String name )
    {
        this.costumes.remove(name);
    }

    public Costume getCostume( String name )
    {
        CostumeResource resource = this.costumes.get(name);
        return resource == null ? null : resource.costume;
    }

    public CostumeResource getCostumeResource( String name )
    {
        return this.costumes.get(name);
    }

    public List<String> costumeNames()
    {
        return sortNames(this.costumes.keySet());
    }

    public String getCostumeName( Costume costume )
    {
        for (String name : this.costumeNames()) {
            if (this.getCostume(name) == costume) {
                return name;
            }
        }
        return null;
    }

    public CostumeResource getCostumeResource( Costume costume )
    {
        for (String name : this.costumeNames()) {
            if (this.getCostume(name) == costume) {
                return this.costumes.get(name);
            }
        }
        return null;
    }

    void rename2( CostumeResource costumeResource, String name )
    {
        this.costumes.remove(costumeResource.getName());
        this.costumes.put(name, costumeResource);
    }

    public Surface getThumbnail( Costume costume )
    {
        CostumeResource resource = this.getCostumeResource(costume);
        if (resource == null) {
            return null;
        }
        return this.getThumbnail(resource);
    }

    public Surface getThumbnail( CostumeResource resource )
    {
        Pose pose = resource.costume.getPose("default");
        if (pose == null) {
            return null;
        }
        return this.getThumbnail(pose);
    }

    // Animations

    public void addAnimation( AnimationResource ar )
    {
        this.animations.put(ar.getName(), ar);
    }

    public void removeAnimation( String name )
    {
        this.animations.remove(name);
    }

    public Animation getAnimation( String name )
    {
        AnimationResource resource = this.animations.get(name);
        return resource == null ? null : resource.animation;
    }

    public AnimationResource getAnimationResource( String name )
    {
        return this.animations.get(name);
    }

    public List<String> animationNames()
    {
        return sortNames(this.animations.keySet());
    }

    public String getAnimationName( Animation animation )
    {
        for (String name : this.animationNames()) {
            if (this.getAnimation(name) == animation) {
                return name;
            }
        }
        return null;
    }

    void rename2( AnimationResource animationResource, String name )
    {
        this.animations.remove(animationResource.getName());
        this.animations.put(name, animationResource);
    }

    // Scenes

    public void addScene( SceneResource sceneResource )
    {
        this.scenes.put(sceneResource.name, sceneResource);
    }

    public void removeScene( String name )
    {
        this.scenes.remove(name);
    }

    public SceneResource getSceneResource( String name )
    {
        return this.scenes.get(name);
    }

    public Scene getScene( String name ) throws Exception
    {
        SceneResource resource = this.scenes.get(name);
        return resource == null ? null : resource.getScene();
    }

    public List<String> sceneNames()
    {
        return sortNames(this.scenes.keySet());
    }

    void rename2( SceneResource sceneResource, String name )
    {
        this.scenes.remove(sceneResource.getName());
        this.scenes.put(name, sceneResource);
    }

    public boolean registerBehaviourClassName( String className )
    {
        try {
            if (this.behaviourClassNames.contains(className)) {
                return true;
            }
            Class<?> klass = Class.forName(className);
            klass.asSubclass(Behaviour.class);
            this.behaviourClassNames.add(className);
            return true;
        } catch (Exception e) {
            // Do nothing
        }
        return false;
    }

    public boolean registerCostumePropertiesClassName( String className )
    {
        if (this.costumePropertiesClassNames.contains(className)) {
            return true;
        }
        if (NoProperties.isValidClassName(className)) {
            this.costumePropertiesClassNames.add(className);
            return true;
        }
        return false;
    }

    public boolean registerSceneBehaviourClassName( String className )
    {
        try {
            if (this.sceneBehaviourClassNames.contains(className)) {
                return true;
            }
            Class<?> klass = Class.forName(className);
            klass.asSubclass(SceneBehaviour.class);
            this.sceneBehaviourClassNames.add(className);
            return true;

        } catch (Exception e) {
            // Do nothing
        }
        return false;
    }

    public Set<String> getBehaviourClassNames()
    {
        return this.behaviourClassNames;
    }

    public Set<String> getCostumePropertiesClassNames()
    {
        return this.costumePropertiesClassNames;
    }

    public Set<String> getSceneBehaviourClassNames()
    {
        return this.sceneBehaviourClassNames;
    }

}
