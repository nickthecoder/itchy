package uk.co.nickthecoder.itchy;

import java.util.HashMap;
import java.util.Set;

import uk.co.nickthecoder.itchy.animation.Animation;
import uk.co.nickthecoder.itchy.util.NinePatch;
import uk.co.nickthecoder.jame.Sound;
import uk.co.nickthecoder.jame.Surface;

public class Resources extends Loadable
{

    private final HashMap<String, SoundResource> sounds;

    private final HashMap<String, FontResource> fonts;

    private final HashMap<String, PoseResource> poses;

    private final HashMap<String, NinePatchResource> ninePatches;

    private final HashMap<String, SceneResource> scenes;

    private final HashMap<String, AnimationResource> animations;

    private final HashMap<String, CostumeResource> costumes;

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
    }

    @Override
    public void load( String filename ) throws Exception
    {
        super.load(filename);
        ResourcesReader loader = new ResourcesReader(this);
        loader.load(filename);
    }

    @Override
    protected void actualSave( String filename ) throws Exception
    {
        ResourcesWriter writer = new ResourcesWriter(this);
        writer.write(filename);
    }

    @Override
    protected void checkSave( String filename ) throws Exception
    {
        Resources resources = new Resources();
        resources.load(filename);

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

    public void rename( Object object, String name )
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

    public Set<String> soundNames()
    {
        return this.sounds.keySet();
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

    public Font getFont( String name )
    {
        FontResource resource = this.fonts.get(name);
        return resource == null ? null : resource.font;
    }

    public Set<String> fontNames()
    {
        return this.fonts.keySet();
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

    public Set<String> poseNames()
    {
        return this.poses.keySet();
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

    public Set<String> ninePatchNames()
    {
        return this.ninePatches.keySet();
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

    public Set<String> costumeNames()
    {
        return this.costumes.keySet();
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

    public Set<String> animationNames()
    {
        return this.animations.keySet();
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

    public Set<String> sceneNames()
    {
        return this.scenes.keySet();
    }

    void rename2( SceneResource sceneResource, String name )
    {
        this.scenes.remove(sceneResource.getName());
        this.scenes.put(name, sceneResource);
    }

}
