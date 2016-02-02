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

    private final HashMap<String, Costume> costumes;

    private final HashMap<String, SceneStub> scenes;

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
        scriptManager = new ScriptManager(this);
        errorLog = new ErrorLog();

        spriteSheets = new HashMap<String, SpriteSheet>();
        poses = new HashMap<String, PoseResource>();
        animations = new HashMap<String, AnimationResource>();
        ninePatches = new HashMap<String, NinePatch>();

        sounds = new HashMap<String, SoundResource>();
        fonts = new HashMap<String, Font>();

        inputs = new HashMap<String, Input>();
        costumes = new HashMap<String, Costume>();
        scenes = new HashMap<String, SceneStub>();

        layouts = new HashMap<String, Layout>();

        renamedCostumes = new HashMap<String, String>();
        dirty = false;

        game = new Game(this);
    }

    /**
     * Reloads all of the Poses/SpriteSheets etc
     * 
     * @throws JameException
     */
    public void reload() throws JameException
    {
        for (PoseResource poseResource : this.poses.values()) {
            if (poseResource instanceof FilePoseResource) {
                ((FilePoseResource) poseResource).reload();
            }
        }

        for (SpriteSheet spriteSheet : this.spriteSheets.values()) {
            spriteSheet.reload();
        }
    }

    /**
     * Used by the SceneDesigner when testing a scene. It creates a duplicate set of resources, so that a new Game can
     * run the test, leaving the state of the old Game untouched. This is important when the editor is launched from
     * within the game.
     */
    public Resources copy()
    {
        Resources result = new Resources();
        result.sounds.putAll(sounds);
        result.fonts.putAll(fonts);
        result.ninePatches.putAll(ninePatches);
        result.scenes.putAll(scenes);
        result.poses.putAll(poses);
        result.costumes.putAll(costumes);
        result.animations.putAll(animations);
        result.inputs.putAll(inputs);
        result.layouts.putAll(layouts);
        result.renamedCostumes.putAll(renamedCostumes);

        result.setFile(this.getFile());

        Itchy.loadingGame(result.game);
        result.setGameInfo(gameInfo);
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
        return dirty;
    }

    public void dirty()
    {
        dirty = true;
    }

    public GameInfo getGameInfo()
    {
        return gameInfo;
    }

    public void setGameInfo(GameInfo gameInfo)
    {
        assert (this.gameInfo == null);
        this.gameInfo = gameInfo;
        game.init();
    }

    @Override
    public void load() throws Exception
    {
        dirty = false;
        Itchy.loadingGame(game);

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

    public File getImagesDirectory()
    {
        return getDirectory("images");
    }

    public File getSoundsDirectory()
    {
        return getDirectory("sounds");
    }

    public File getFontsDirectory()
    {
        return getDirectory("fonts");
    }

    private File getDirectory(String name)
    {
        File dir = getDirectory();
        File subDir = new File(dir, name);
        if (subDir.exists()) {
            return subDir;
        } else {
            return dir;
        }
    }

    @Override
    protected void checkSave(File file) throws Exception
    {
        Resources resources = new Resources();
        resources.load(file);

        for (String name : this.poseNames()) {
            // We only care about PoseResources that were loaded.
            if (!(this.getPoseResource(name) instanceof DynamicPoseResource)) {
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

    // SpriteSheets

    public void addSpriteSheet(SpriteSheet resource)
    {
        spriteSheets.put(resource.getName(), resource);
    }

    public void removeSpriteSheet(String name)
    {
        spriteSheets.remove(name);
    }

    public SpriteSheet getSpriteSheet(String name)
    {
        return spriteSheets.get(name);
    }

    public List<String> spriteSheetNames()
    {
        return sortNames(spriteSheets.keySet());
    }

    public void renameSpriteSheet(SpriteSheet spriteSheet)
    {
        for (Entry<String, SpriteSheet> entry : spriteSheets.entrySet()) {
            if (entry.getValue() == spriteSheet) {
                spriteSheets.remove(entry.getKey());
                break;
            }
        }
        spriteSheets.put(spriteSheet.getName(), spriteSheet);
    }

    // Poses

    public void addPose(PoseResource resource)
    {
        poses.put(resource.getName(), resource);
    }

    public void removePose(String name)
    {
        poses.remove(name);
    }

    public PoseResource getPoseResource(String name)
    {
        return poses.get(name);
    }

    public ImagePose getPose(String name)
    {
        PoseResource resource = poses.get(name);
        return resource == null ? null : resource.pose;
    }

    public List<String> poseNames()
    {
        return sortNames(poses.keySet());
    }

    void rename2(PoseResource poseResource, String name)
    {
        poses.remove(poseResource.getName());
        poses.put(name, poseResource);
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

    public void renamePose(PoseResource pr)
    {
        for (Entry<String, PoseResource> entry : this.poses.entrySet()) {
            if (entry.getValue() == pr) {
                this.poses.remove(entry.getKey());
                break;
            }
        }
        this.poses.put(pr.getName(), pr);
    }

    // Animations

    public void addAnimation(AnimationResource ar)
    {
        animations.put(ar.getName(), ar);
    }

    public void removeAnimation(String name)
    {
        animations.remove(name);
    }

    public Animation getAnimation(String name)
    {
        AnimationResource resource = animations.get(name);
        return resource == null ? null : resource.animation;
    }

    public AnimationResource getAnimationResource(String name)
    {
        return animations.get(name);
    }

    public List<String> animationNames()
    {
        return sortNames(animations.keySet());
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

    public void renameAnimation(AnimationResource ar)
    {
        for (Entry<String, AnimationResource> entry : animations.entrySet()) {
            if (entry.getValue() == ar) {
                animations.remove(entry.getKey());
                break;
            }
        }
        animations.put(ar.getName(), ar);
    }

    // NinePatches

    public void addNinePatch(NinePatch ninePatch)
    {
        ninePatches.put(ninePatch.getName(), ninePatch);
    }

    public void removeNinePatch(String name)
    {
        ninePatches.remove(name);
    }

    public NinePatch getNinePatch(String name)
    {
        return ninePatches.get(name);
    }

    public List<String> ninePatchNames()
    {
        return sortNames(ninePatches.keySet());
    }

    public void rename(NinePatch ninePatch)
    {
        for (Entry<String, NinePatch> entry : ninePatches.entrySet()) {
            if (entry.getValue() == ninePatch) {
                ninePatches.remove(entry.getKey());
                break;
            }
        }
        ninePatches.put(ninePatch.getName(), ninePatch);
    }

    // Layouts
    public void addLayout(Layout layout)
    {
        layouts.put(layout.getName(), layout);
    }

    public void removeLayout(String name)
    {
        layouts.remove(name);
    }

    public Layout getLayout(String name)
    {
        return layouts.get(name);
    }

    public List<String> layoutNames()
    {
        return sortNames(layouts.keySet());
    }

    public void renameLayout(Layout layout)
    {
        System.out.println("Renaming layout " + layout.name);
        for (Entry<String, Layout> entry : layouts.entrySet()) {
            if (entry.getValue() == layout) {
                layouts.remove(entry.getKey());
                break;
            }
        }
        layouts.put(layout.getName(), layout);
    }

    // Sounds

    public void addSound(SoundResource soundResource)
    {
        sounds.put(soundResource.getName(), soundResource);
    }

    public void removeSound(String name)
    {
        sounds.remove(name);
    }

    public SoundResource getSound(String name)
    {
        return sounds.get(name);
    }

    public List<String> soundNames()
    {
        return sortNames(sounds.keySet());
    }

    public void renameSound(SoundResource soundResource)
    {
        for (Entry<String, SoundResource> entry : sounds.entrySet()) {
            if (entry.getValue() == soundResource) {
                sounds.remove(entry.getKey());
                break;
            }
        }
        sounds.put(soundResource.getName(), soundResource);
    }

    // Fonts

    public void addFont(Font font)
    {
        fonts.put(font.getName(), font);
    }

    public void removeFont(String name)
    {
        fonts.remove(name);
    }

    public Font getFont(String name)
    {
        return fonts.get(name);
    }

    public List<String> fontNames()
    {
        return sortNames(fonts.keySet());
    }

    public void renameFont(Font font)
    {
        for (Entry<String, Font> entry : fonts.entrySet()) {
            if (entry.getValue() == font) {
                fonts.remove(entry.getKey());
                break;
            }
        }
        fonts.put(font.getName(), font);
    }

    /**
     * @return The default font. At the moment this is a randomly picked font! Null if there are no fonts.
     */
    public Font getDefaultFont()
    {
        try {
            Font font = fonts.values().iterator().next();
            return font;
        } catch (Exception e) {
            return null;
        }
    }

    // Inputs

    public void addInput(Input input)
    {
        inputs.put(input.getName(), input);
    }

    public void removeInput(String name)
    {
        inputs.remove(name);
    }

    public Input getInput(String name)
    {
        return inputs.get(name);
    }

    public List<String> inputNames()
    {
        return sortNames(inputs.keySet());
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

    public void renameInput(Input input)
    {
        for (Entry<String, Input> entry : inputs.entrySet()) {
            if (entry.getValue() == input) {
                inputs.remove(entry.getKey());
                break;
            }
        }
        inputs.put(input.getName(), input);
    }

    // Costumes

    public void addCostume(Costume costume)
    {
        costumes.put(costume.getName(), costume);
        registry.add(costume.roleClassName);
    }

    public void removeCostume(String name)
    {
        costumes.remove(name);
    }

    /**
     * Used while loading a resource - if a costume has been renamed since the scene was last saved, then we need to
     * translate from the old name to the new name before getting the costume.
     */
    public String getNewCostumeName(String name)
    {
        String origName = renamedCostumes.get(name);
        return origName == null ? name : origName;
    }

    public Costume getCostume(String name)
    {
        return costumes.get(name);
    }

    public List<String> costumeNames()
    {
        return sortNames(costumes.keySet());
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

    public void renameCostume(Costume costume)
    {
        String newName = costume.getName();
        String oldName = null;
        for (Entry<String, Costume> entry : costumes.entrySet()) {
            if (entry.getValue() == costume) {
                oldName = entry.getKey();
                break;
            }
        }

        if (oldName == null) {
            return;
        }

        if (oldName.equals(newName)) {
            return;
        }

        String origName = oldName;

        // If the costume has been renamed already, we want to map from the ORIGINAL name, not the intermediate name
        // (oldName).
        for (String name : renamedCostumes.keySet()) {
            if (renamedCostumes.get(name).equals(oldName)) {
                origName = name;
                break;
            }
        }

        renamedCostumes.put(origName, newName);

        costumes.remove(oldName);
        costumes.put(newName, costume);
    }

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
     * @deprecated
     */
    @Deprecated
    public Costume getCompanionCostume(Costume sourceCostume, String name)
    {
        System.err.println("Looking in " + sourceCostume.getName() + " for  " + name);
        System.err.println("Using depricate method Resources.getCompanionCostume. Use Costume.getCompanion instead.");
        String costumeName = sourceCostume.getString(name);
        if (costumeName == null) {
            return null;
        }

        Costume result = this.getCostume(costumeName);
        return result;
    }

    public Surface getThumbnail(Costume costume)
    {
        Pose pose = costume.getPose("default");
        if (pose == null) {

            String text = costume.getName();
            TextStyle textStyle = costume.getTextStyle("default");
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

    public void addScene(SceneStub sceneStub)
    {
        scenes.put(sceneStub.getName(), sceneStub);
    }

    public void removeScene(String name)
    {
        scenes.remove(name);
    }

    public SceneStub getScene(String name)
    {
        return scenes.get(name);
    }

    public List<String> sceneNames()
    {
        return sortNames(scenes.keySet());
    }

    public void renameScene(SceneStub sceneStub)
    {
        for (Entry<String, SceneStub> entry : scenes.entrySet()) {
            if (entry.getValue() == sceneStub) {
                scenes.remove(entry.getKey());
                break;
            }
        }
        scenes.put(sceneStub.getName(), sceneStub);
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

        registry.add(className);
        return true;
    }

    public Game getGame()
    {
        return game;
    }

    public boolean renamesPending()
    {
        return !renamedCostumes.isEmpty();
    }

    public void renameSubjectsInScenes() throws Exception
    {
        for (String sceneName : this.sceneNames()) {
            SceneStub sceneStub = this.getScene(sceneName);
            Scene scene = sceneStub.load(false);
            sceneStub.save(scene);
        }
        renamedCostumes.clear();
    }

    public static void dump(String label, Object... data)
    {
        System.out.print(label);
        for (Object d : data) {
            System.out.print(" ");
            System.out.print(d);
        }
        System.out.println();
    }

    public void dump()
    {
        dump("Poses");
        for (String name : this.poseNames()) {
            dump("   ", "[", name, "]", this.poses.get(name));
        }
        dump("");

        dump("SpriteSheets");
        for (String name : this.spriteSheetNames()) {
            SpriteSheet spriteSheet = this.spriteSheets.get(name);
            dump("   ", "[", name, "]", spriteSheet);
            for (Sprite sprite : spriteSheet.getSprites()) {
                dump("       ", sprite);
            }
        }
        dump("");

    }

}
