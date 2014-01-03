/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import uk.co.nickthecoder.itchy.animation.Animation;
import uk.co.nickthecoder.itchy.role.Companion;
import uk.co.nickthecoder.itchy.script.ScriptManager;
import uk.co.nickthecoder.itchy.util.ClassName;
import uk.co.nickthecoder.itchy.util.NinePatch;
import uk.co.nickthecoder.jame.JameException;
import uk.co.nickthecoder.jame.RGBA;
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

    public static File getResourceFileFromDirectory( File directory )
    {
        return new File(directory, directory.getName() + ".itchy");
    }

    public final Game game;

    public final ScriptManager scriptManager;

    private GameInfo gameInfo;

    private final HashMap<String, SoundResource> sounds;

    private final HashMap<String, FontResource> fonts;

    private final HashMap<String, PoseResource> poses;

    private final HashMap<String, NinePatchResource> ninePatches;

    private final HashMap<String, SceneResource> scenes;

    private final HashMap<String, AnimationResource> animations;

    private final HashMap<String, CostumeResource> costumes;

    private HashMap<String, String> renamedCostumes;

    public ErrorLog errorLog;

    public final Registry registry = new Registry(Itchy.registry);

    public Resources()
    {
        super();
        this.scriptManager = new ScriptManager(this);
        this.errorLog = new ErrorLog();

        this.sounds = new HashMap<String, SoundResource>();
        this.fonts = new HashMap<String, FontResource>();
        this.ninePatches = new HashMap<String, NinePatchResource>();
        this.scenes = new HashMap<String, SceneResource>();

        this.poses = new HashMap<String, PoseResource>();
        this.costumes = new HashMap<String, CostumeResource>();
        this.animations = new HashMap<String, AnimationResource>();

        this.renamedCostumes = new HashMap<String, String>();

        this.game = new Game(this);
    }

    public String getId()
    {
        String name = this.getFile().getName();
        int dot = name.lastIndexOf('.');
        if (dot > 0) {
            return name.substring(0, dot);
        } else {
            return name;
        }
    }

    public GameInfo getGameInfo()
    {
        return this.gameInfo;
    }

    public void setGameInfo( GameInfo gameInfo )
    {
        assert (this.gameInfo == null);
        this.gameInfo = gameInfo;
        this.game.init();
    }

    @Override
    public void load() throws Exception
    {
        Itchy.loadingGame(this.game);
        ResourcesReader loader = new ResourcesReader(this);
        loader.load(getFilename());

        this.game.setDirector(this.gameInfo.createDirector(this));
        Itchy.loadingGame(null);
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

    public boolean has( NamedResource object )
    {
        if (object instanceof SoundResource) {
            return this.getSoundResource(object.name) == object;

        } else if (object instanceof FontResource) {
            return this.getFontResource(object.name) == object;

        } else if (object instanceof PoseResource) {
            return this.getPoseResource(object.name) == object;

        } else if (object instanceof NinePatchResource) {
            return this.getNinePatchResource(object.name) == object;

        } else if (object instanceof AnimationResource) {
            return this.getAnimationResource(object.name) == object;

        } else if (object instanceof CostumeResource) {
            return this.getCostumeResource(object.name) == object;

        }
        return false;
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
     * @return The default font. At the moment this is a randomly picked font! Null if there are no fonts.
     */
    public Font getDefaultFont()
    {
        try {
            FontResource fr = this.fonts.values().iterator().next();
            return fr.font;
        } catch (Exception e) {
            return null;
        }
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
        this.registry.add(resource.getCostume().roleClassName);
        this.registry.add(resource.getCostume().getPropertiesClassName());
    }

    public void removeCostume( String name )
    {
        this.costumes.remove(name);
    }

    /**
     * Used while loading a resource - if a costume has been renamed since the scene was last saved, then we need to translate from the old
     * name to the new name before getting the costume.
     */
    public String getNewCostumeName( String name )
    {
        String origName = this.renamedCostumes.get(name);
        return origName == null ? name : origName;
    }

    public Costume getCostume( String name )
    {
        CostumeResource resource = this.costumes.get(name);
        return resource == null ? null : resource.getCostume();
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

    void rename2( CostumeResource costumeResource, String newName )
    {
        String oldName = costumeResource.getName();
        String origName = oldName;

        // If the costume has been renamed already, we want to map from the ORIGINAL name, not the intermediate name (oldName).
        for (String name : this.renamedCostumes.keySet()) {
            if (this.renamedCostumes.get(name).equals(oldName)) {
                origName = name;
                break;
            }
        }

        this.renamedCostumes.put(origName, newName);

        this.costumes.remove(oldName);
        this.costumes.put(newName, costumeResource);
    }

    /**
     * Looks for a named string, and then uses that to search for another costume.
     * <p>
     * For example, create two ship costumes named bigShip and smallShip, and two bullets named redBullet and greenBullet. Now create a
     * string within the bigShip : "bullet" -> "redBullet", and another within the small ship : "bullet" -> "greenBullet". Now we can get a
     * get the appropriate bullet costume for a ship : <code>resources.getCompananionCostume( myShipActor.getCostume(), "bullet" )</code>
     * <p>
     * This can be useful when using {@link Companion#costume(Costume)}; for example we may create a bullet like so : <code> 
     * <pre>
     * new Projectile()
     *     .costume( Itchy.getGame().resources.getCompanionCostume( getActor().getCostume(), "bullet" )
     *     .offsetForwards(20)
     *     .speed(5)
     *     .createActor().activate();
     * </pre>
     * </code>
     * <p>
     * Note, you will probably use a sub-class of Projectile, as you'll want it to check for collisions etc.
     * <p>
     * By creating multiple strings all named "bullet" within a ship's costume, the code above will randomly pick from the named costumes.
     * 
     * 
     * @param sourceCostume
     *        The costume who's strings are search to find the name of another costume.
     * @param name
     *        The name of the String to look up, which is then used as a costume name. Note, this is NOT a costume name, it is the name of a
     *        String.
     * @return The costume that you were searching for, or null if none was found.
     */
    public Costume getCompanionCostume( Costume sourceCostume, String name )
    {
        String costumeName = sourceCostume.getString(name);
        if (costumeName == null) {
            return null;
        }

        Costume result = this.getCostume(costumeName);
        return result;
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
        Pose pose = resource.getCostume().getPose("default");
        if (pose == null) {

            String text = resource.getCostume().getString("default");
            if (text != null) {
                Font font = resource.getCostume().getFont("default");
                if (font == null) {
                    font = this.getDefaultFont();
                }
                if (font == null) {
                    return null;
                }
                return this.getThumbnail(font, text);
            }

            return null;
        }
        return this.getThumbnail(pose);
    }

    public Surface getThumbnail( Font font, String text )
    {
        try {
            return font.getSize(16).renderBlended(text, RGBA.BLACK);
        } catch (JameException e) {
            return null;
        }
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

    public List<String> sceneNames()
    {
        return sortNames(this.scenes.keySet());
    }

    void rename2( SceneResource sceneResource, String name )
    {
        this.scenes.remove(sceneResource.getName());
        this.scenes.put(name, sceneResource);
    }

    public boolean isValidScript( String name )
    {
        if (!ScriptManager.isScript(name)) {
            return false;
        }

        File file = new File(resolveFilename("scripts" + File.separator + name));
        return (file.exists());
    }

    public boolean isValidScript( ClassName className )
    {
        return isValidScript(className.name);
    }

    public boolean checkClassName( ClassName className )
    {
        try {
            if (isValidScript(className)) {
                // Do nothing
            } else {
                Class<?> klass = Class.forName(className.name);
                if (klass == null) {
                    return false;
                }
                klass.asSubclass(className.baseClass);
            }
        } catch (Exception e) {
            return false;
        }

        this.registry.add(className);
        return true;
    }

    public Game getGame()
    {
        return this.game;
    }

    public boolean renamesPending()
    {
        return !this.renamedCostumes.isEmpty();
    }

    public void loadSaveAllScenes()
        throws Exception
    {
        for (String sceneName : this.sceneNames()) {
            SceneResource sceneResource = this.getSceneResource(sceneName);
            sceneResource.load();
            sceneResource.save();
            sceneResource.unloadScene();
        }
        this.renamedCostumes.clear();
    }

}
