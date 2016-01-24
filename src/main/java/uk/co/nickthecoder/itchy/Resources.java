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
import java.util.Map.Entry;
import java.util.Set;

import uk.co.nickthecoder.itchy.animation.Animation;
import uk.co.nickthecoder.itchy.script.ScriptManager;
import uk.co.nickthecoder.itchy.util.ClassName;
import uk.co.nickthecoder.itchy.util.NinePatch;
import uk.co.nickthecoder.jame.JameException;
import uk.co.nickthecoder.jame.Sound;
import uk.co.nickthecoder.jame.Surface;

public class Resources extends Loadable
{
    public static Resources currentlyLoading = null;
    
    public static Resources getCurrentResources()
    {
        if (currentlyLoading == null) {
            return Itchy.getGame().resources;
        } else {
            return currentlyLoading;
        }
    }

    public static List<String> sortNames(Set<String> set)
    {
        if (set == null) {
            return null;
        }
        ArrayList<String> names = new ArrayList<String>(set);
        Collections.sort(names);
        return names;
    }

    public static File getResourceFileFromDirectory(File directory)
    {
        return new File(directory, directory.getName() + ".itchy");
    }

    public final Game game;

    public final ScriptManager scriptManager;

    private GameInfo gameInfo;

    private final HashMap<String, SpriteSheet> spriteSheets;

    private final HashMap<String, PoseResource> poses;

    private final HashMap<String, AnimationResource> animations;

    private final HashMap<String, NinePatch> ninePatches;

    private final HashMap<String, SoundResource> sounds;

    private final HashMap<String, Font> fonts;

    private final HashMap<String, Input> inputs;

    private final HashMap<String, CostumeResource> costumes;

    private final HashMap<String, SceneResource> scenes;

    private final HashMap<String, Layout> layouts;

    private HashMap<String, String> renamedCostumes;

    public ErrorLog errorLog;

    public final Registry registry = new Registry(Itchy.registry);

    /**
     * True iff any of the resources have been changed since it was loaded, or last saved.
     */
    private boolean dirty;
        
    public Resources()
    {
        super();
        this.scriptManager = new ScriptManager(this);
        this.errorLog = new ErrorLog();

        this.spriteSheets = new HashMap<String, SpriteSheet>();
        this.poses = new HashMap<String, PoseResource>();
        this.animations = new HashMap<String, AnimationResource>();
        this.ninePatches = new HashMap<String, NinePatch>();

        this.sounds = new HashMap<String, SoundResource>();
        this.fonts = new HashMap<String, Font>();

        this.inputs = new HashMap<String, Input>();
        this.costumes = new HashMap<String, CostumeResource>();
        this.scenes = new HashMap<String, SceneResource>();

        this.layouts = new HashMap<String,Layout>();
        
        this.renamedCostumes = new HashMap<String, String>();
        this.dirty = false;
        
        this.game = new Game(this);
    }

    /**
     * Used by the SceneDesigner when testing a scene. It creates a duplicate set of resources, so that a new Game can
     * run the test, leaving the state of the old Game untouched. This is important when the editor is launched from
     * within the game.
     */
    public Resources copy()
    {
        Resources result = new Resources();
        result.sounds.putAll(this.sounds);
        result.fonts.putAll(this.fonts);
        result.ninePatches.putAll(this.ninePatches);
        result.scenes.putAll(this.scenes);
        result.poses.putAll(this.poses);
        result.costumes.putAll(this.costumes);
        result.animations.putAll(this.animations);
        result.inputs.putAll(this.inputs);
        result.layouts.putAll(this.layouts);
        result.renamedCostumes.putAll(this.renamedCostumes);
        
        result.setFile(this.getFile());

        Itchy.loadingGame(result.game);
        result.setGameInfo(this.gameInfo);
        result.game.setDirector(result.getGameInfo().createDirector(result));
        Itchy.loadingGame(null);

        return result;
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

    public boolean isDirty() 
    {
        return this.dirty;
    }
    
    public void dirty()
    {
        this.dirty = true;
    }
    
    public GameInfo getGameInfo()
    {
        return this.gameInfo;
    }

    public void setGameInfo(GameInfo gameInfo)
    {
        assert (this.gameInfo == null);
        this.gameInfo = gameInfo;
        this.game.init();
    }

    @Override
    public void load() throws Exception
    {
        this.dirty = false;
        Itchy.loadingGame(this.game);

        ResourcesReader loader = new ResourcesReader(this);
        loader.load(getFilename());

        Itchy.loadingGame(null);
    }

    @Override
    protected void actualSave(File file) throws Exception
    {
        ResourcesWriter writer = new ResourcesWriter(this);
        writer.write(file.getPath());
    }

    @Override
    protected void checkSave(File file) throws Exception
    {
        Resources resources = new Resources();
        resources.load(file);

        for (String name : this.poseNames()) {
            // We only care about PoseResources that were loaded.
            if (! (this.getPoseResource(name) instanceof DynamicPoseResource) ) {
                if (resources.getPose(name) == null) {
                    throw new Exception("Pose " + name + " wasn't saved");
                }
            }
        }
        for (String name : this.animationNames()) {
            if (resources.getAnimation(name) == null) {
                throw new Exception("Animation " + name + " wasn't saved");
            }
        }
        for (String name : this.ninePatchNames()) {
            if (resources.getNinePatch(name) == null) {
                throw new Exception("Nine Patch " + name + " wasn't saved");
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
        for (String name : this.layoutNames()) {
            if (resources.getLayout(name) == null) {
                throw new Exception("Layout " + name + " wasn't saved");
            }
        }

    }

    public boolean has(NamedResource object)
    {
        if (object instanceof SpriteSheet) {
            return this.getSpriteSheet(object.name) == object;

        } else if (object instanceof PoseResource) {
            return this.getPoseResource(object.name) == object;

        } else if (object instanceof AnimationResource) {
            return this.getAnimationResource(object.name) == object;

        } else if (object instanceof SoundResource) {
            return this.getSoundResource(object.name) == object;

        } else if (object instanceof CostumeResource) {
            return this.getCostumeResource(object.name) == object;
        }
        
        return false;
    }

    public void renameResource(Object object, String name)
    {

        if (object instanceof SpriteSheet) {
            this.rename2((SpriteSheet) object, name);

        } else if (object instanceof PoseResource) {
            this.rename2((PoseResource) object, name);

        } else if (object instanceof SoundResource) {
            this.rename2((SoundResource) object, name);

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

    // SpriteSheets
   

    public void addSpriteSheet(SpriteSheet resource)
    {
        this.spriteSheets.put(resource.getName(), resource);
    }

    public void removeSpriteSheet(String name)
    {
        this.spriteSheets.remove(name);
    }


    public SpriteSheet getSpriteSheet(String name)
    {
        return this.spriteSheets.get(name);
    }

    public List<String> spriteSheetNames()
    {
        return sortNames(this.spriteSheets.keySet());
    }

    void rename2(SpriteSheet spriteSheet, String name)
    {
        this.spriteSheets.remove(spriteSheet.getName());
        this.spriteSheets.put(name, spriteSheet);
    }
    
    // Poses

    public void addPose(PoseResource resource)
    {
        this.poses.put(resource.getName(), resource);
    }

    public void removePose(String name)
    {
        this.poses.remove(name);
    }

    public PoseResource getPoseResource(String name)
    {
        return this.poses.get(name);
    }

    public ImagePose getPose(String name)
    {
        PoseResource resource = this.poses.get(name);
        return resource == null ? null : resource.pose;
    }

    public List<String> poseNames()
    {
        return sortNames(this.poses.keySet());
    }

    void rename2(PoseResource poseResource, String name)
    {
        this.poses.remove(poseResource.getName());
        this.poses.put(name, poseResource);
    }

    public String getPoseName(Pose pose)
    {
        for (String name : this.poseNames()) {
            if (this.getPose(name) == pose) {
                return name;
            }
        }
        return null;
    }

    public PoseResource getPoseResource(Pose pose)
    {
        for (String name : this.poseNames()) {
            if (this.getPose(name) == pose) {
                return this.getPoseResource(name);
            }
        }
        return null;
    }

    public Surface getThumbnail(Pose pose)
    {
        PoseResource resource = this.getPoseResource(pose);
        if (resource == null) {
            return null;
        }
        return resource.getThumbnail();
    }

    // Animations

    public void addAnimation(AnimationResource ar)
    {
        this.animations.put(ar.getName(), ar);
    }

    public void removeAnimation(String name)
    {
        this.animations.remove(name);
    }

    public Animation getAnimation(String name)
    {
        AnimationResource resource = this.animations.get(name);
        return resource == null ? null : resource.animation;
    }

    public AnimationResource getAnimationResource(String name)
    {
        return this.animations.get(name);
    }

    public List<String> animationNames()
    {
        return sortNames(this.animations.keySet());
    }

    public String getAnimationName(Animation animation)
    {
        for (String name : this.animationNames()) {
            if (this.getAnimation(name) == animation) {
                return name;
            }
        }
        return null;
    }

    void rename2(AnimationResource animationResource, String name)
    {
        this.animations.remove(animationResource.getName());
        this.animations.put(name, animationResource);
    }

    // NinePatches

    public void addNinePatch(NinePatch ninePatch)
    {
        this.ninePatches.put(ninePatch.getName(), ninePatch);
    }

    public void removeNinePatch(String name)
    {
        this.ninePatches.remove(name);
    }

    public NinePatch getNinePatch(String name)
    {
        return this.ninePatches.get(name);
    }

    public List<String> ninePatchNames()
    {
        return sortNames(this.ninePatches.keySet());
    }

    public void rename(NinePatch ninePatch)
    {
        for (Entry<String, NinePatch> entry : this.ninePatches.entrySet() ) {
            if (entry.getValue() == ninePatch) {
                this.ninePatches.remove(entry.getKey());
                break;
            }
        }
        this.ninePatches.put(ninePatch.getName(), ninePatch);
    }

    // Layouts
    public void addLayout(Layout layout)
    {
        this.layouts.put(layout.getName(), layout);
    }

    public void removeLayout(String name)
    {
        this.layouts.remove(name);
    }

    public Layout getLayout(String name)
    {
        return this.layouts.get(name);
    }

    public List<String> layoutNames()
    {
        return sortNames(this.layouts.keySet());
    }

    void rename(Layout layout)
    {
        for (Entry<String, Layout> entry : this.layouts.entrySet() ) {
            if (entry.getValue() == layout) {
                this.layouts.remove(entry.getKey());
                break;
            }
        }
        this.layouts.put(layout.getName(), layout);
    }

    
    // Sounds

    public void addSound(SoundResource soundResource)
    {
        this.sounds.put(soundResource.name, soundResource);
    }

    public void removeSound(String name)
    {
        this.sounds.remove(name);
    }

    public SoundResource getSoundResource(String name)
    {
        return this.sounds.get(name);
    }

    public Sound getSound(String name)
    {
        SoundResource resource = this.sounds.get(name);
        return resource == null ? null : resource.getSound();
    }

    public List<String> soundNames()
    {
        return sortNames(this.sounds.keySet());
    }

    void rename2(SoundResource soundResource, String name)
    {
        this.sounds.remove(soundResource.getName());
        this.sounds.put(name, soundResource);
    }

    public String getSoundName(Sound sound)
    {
        for (String name : this.soundNames()) {
            if (this.getSound(name) == sound) {
                return name;
            }
        }
        return null;
    }

    // Fonts

    public void addFont(Font font)
    {
        this.fonts.put(font.getName(), font);
    }

    public void removeFont(String name)
    {
        this.fonts.remove(name);
    }

    public Font getFont(String name)
    {
        return this.fonts.get(name);
    }


    public List<String> fontNames()
    {
        return sortNames(this.fonts.keySet());
    }


    public void rename(Font font)
    {
        for (Entry<String, Font> entry : this.fonts.entrySet() ) {
            if (entry.getValue() == font) {
                this.fonts.remove(entry.getKey());
                break;
            }
        }
        this.fonts.put(font.getName(), font);
    }


    /**
     * @return The default font. At the moment this is a randomly picked font! Null if there are no fonts.
     */
    public Font getDefaultFont()
    {
        try {
            Font font = this.fonts.values().iterator().next();
            return font;
        } catch (Exception e) {
            return null;
        }
    }

    // Inputs

    public void addInput(Input input)
    {
        this.inputs.put(input.getName(), input);
    }

    public void removeInput(String name)
    {
        this.inputs.remove(name);
    }

    public Input getInput(String name)
    {
        return this.inputs.get(name);
    }

    public List<String> inputNames()
    {
        return sortNames(this.inputs.keySet());
    }

    public String getInputName(Input input)
    {
        for (String name : this.inputNames()) {
            if (this.getInput(name) == input) {
                return name;
            }
        }
        return null;
    }

    public void rename(Input input)
    {
        for (Entry<String, Input> entry : this.inputs.entrySet() ) {
            if (entry.getValue() == input) {
                this.inputs.remove(entry.getKey());
                break;
            }
        }
        this.inputs.put(input.getName(), input);
    }

    // Costumes

    public void addCostume(CostumeResource resource)
    {
        this.costumes.put(resource.name, resource);
        this.registry.add(resource.getCostume().roleClassName);
    }

    public void removeCostume(String name)
    {
        this.costumes.remove(name);
    }

    /**
     * Used while loading a resource - if a costume has been renamed since the scene was last saved, then we need to
     * translate from the old name to the new name before getting the costume.
     */
    public String getNewCostumeName(String name)
    {
        String origName = this.renamedCostumes.get(name);
        return origName == null ? name : origName;
    }

    public Costume getCostume(String name)
    {
        CostumeResource resource = this.costumes.get(name);
        return resource == null ? null : resource.getCostume();
    }

    public CostumeResource getCostumeResource(String name)
    {
        return this.costumes.get(name);
    }

    public List<String> costumeNames()
    {
        return sortNames(this.costumes.keySet());
    }

    public String getCostumeName(Costume costume)
    {
        for (String name : this.costumeNames()) {
            if (this.getCostume(name) == costume) {
                return name;
            }
        }
        return null;
    }

    public CostumeResource getCostumeResource(Costume costume)
    {
        for (String name : this.costumeNames()) {
            if (this.getCostume(name) == costume) {
                return this.costumes.get(name);
            }
        }
        return null;
    }

    void rename2(CostumeResource costumeResource, String newName)
    {
        if (costumeResource.getName().equals(newName)) {
            return;
        }

        String oldName = costumeResource.getName();
        String origName = oldName;

        // If the costume has been renamed already, we want to map from the ORIGINAL name, not the intermediate name
        // (oldName).
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

    // TODO Deprecate Resources.getCompanionCostume - Update all games.
    /**
     * This will be deprecated soon. Use Costume.getCopmanion instead.
     *
     * Looks for a named string, and then uses that to search for another costume.
     * <p>
     * For example, create two ship costumes named bigShip and smallShip, and two bullets named redBullet and
     * greenBullet. Now create a string within the bigShip : "bullet" -> "redBullet", and another within the small ship
     * : "bullet" -> "greenBullet". Now we can get a get the appropriate bullet costume for a ship :
     * <code>resources.getCompananionCostume( myShipActor.getCostume(), "bullet" )</code>
     * <p>
     * By creating multiple strings all named "bullet" within a ship's costume, the code above will randomly pick from
     * the named costumes.
     * 
     * @param sourceCostume
     *            The costume who's strings are search to find the name of another costume.
     * @param name
     *            The name of the String to look up, which is then used as a costume name. Note, this is NOT a costume
     *            name, it is the name of a String.
     * @return The costume that you were searching for, or null if none was found.
     */
    public Costume getCompanionCostume(Costume sourceCostume, String name)
    {
        String costumeName = sourceCostume.getString(name);
        if (costumeName == null) {
            return null;
        }

        Costume result = this.getCostume(costumeName);
        return result;
    }

    public Surface getThumbnail(Costume costume)
    {
        CostumeResource resource = this.getCostumeResource(costume);
        if (resource == null) {
            return null;
        }
        return this.getThumbnail(resource);
    }

    public Surface getThumbnail(CostumeResource resource)
    {
        Pose pose = resource.getCostume().getPose("default");
        if (pose == null) {

            String text = resource.getCostume().getString("default");
            if (text == null) {
                text = resource.name;
            }
            TextStyle textStyle = resource.getCostume().getTextStyle("default");
            if (textStyle == null) {
                return null;
            }
            return this.getThumbnail(textStyle, text);
        }
        return this.getThumbnail(pose);
    }

    public Surface getThumbnail(TextStyle textStyle, String text)
    {
        int fontSize = textStyle.fontSize;
        if (fontSize > 30) {
            fontSize = 30;
        }

        try {
            return textStyle.font.getSize(fontSize).renderBlended(text, textStyle.color);
        } catch (JameException e) {
            return null;
        }
    }

    /**
     * Creates the role object defined by a costume. If the costume is badly defined, then null is returned.
     */
    public Role createRole(Costume costume)
    {
        try {
            return AbstractRole.createRole(this, costume.roleClassName);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Creates an Actor based on a Costume, and places it on a stage.
     */
    public Actor createActor(Costume costume, Stage stage)
    {
        Role role = this.createRole(costume);
        Actor actor = new Actor(costume);
        actor.setRole(role);
        actor.setZOrder(costume.defaultZOrder);
        stage.add(actor);

        return actor;
    }

    // Scenes

    public void addScene(SceneResource sceneResource)
    {
        this.scenes.put(sceneResource.name, sceneResource);
    }

    public void removeScene(String name)
    {
        this.scenes.remove(name);
    }

    public SceneResource getSceneResource(String name)
    {
        return this.scenes.get(name);
    }

    public List<String> sceneNames()
    {
        return sortNames(this.scenes.keySet());
    }

    void rename2(SceneResource sceneResource, String name)
    {
        this.scenes.remove(sceneResource.getName());
        this.scenes.put(name, sceneResource);
    }

    public boolean isValidScript(String name)
    {
        if (!ScriptManager.isScript(name)) {
            return false;
        }

        File file = new File(resolveFilename("scripts" + File.separator + name));
        return (file.exists());
    }

    public boolean isValidScript(ClassName className)
    {
        return isValidScript(className.name);
    }

    public boolean checkClassName(ClassName className)
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

    public void loadSaveAllScenes() throws Exception
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
