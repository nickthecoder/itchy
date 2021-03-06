/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uk.co.nickthecoder.itchy.animation.Animation;
import uk.co.nickthecoder.itchy.animation.CompoundAnimation;
import uk.co.nickthecoder.itchy.animation.Frame;
import uk.co.nickthecoder.itchy.animation.FramedAnimation;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.PropertySubject;
import uk.co.nickthecoder.itchy.role.PlainRole;
import uk.co.nickthecoder.itchy.util.ClassName;
import uk.co.nickthecoder.itchy.util.NinePatch;
import uk.co.nickthecoder.itchy.util.XMLException;
import uk.co.nickthecoder.itchy.util.XMLTag;
import uk.co.nickthecoder.jame.Rect;

public class ResourcesReader
{
    private final Resources resources;

    /**
     * true if the resources being read are included from another file.
     */
    public boolean included;

    public ResourcesReader(Resources resources)
    {
        this.resources = resources;
    }

    public void load(String filename) throws Exception
    {
        Resources.currentlyLoading = this.resources;
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
        try {
            XMLTag document = XMLTag.openDocument(reader);
            this.resources.setFile(new File(filename));
            this.readResources(document.getTag("resources", true));

        } finally {
            Resources.currentlyLoading = null;
            reader.close();
        }
    }

    private void readResources(XMLTag resourcesTag) throws Exception
    {

        if (!resources.client) {
            for (Iterator<XMLTag> i = resourcesTag.getTags("game"); i.hasNext();) {
                XMLTag gameTag = i.next();
                this.readGame(gameTag);
            }
        }

        GameInfo gameInfo = this.resources.getGameInfo();
        if (gameInfo == null) {
            gameInfo = new GameInfo();
            resources.setGameInfo(gameInfo);
            ;
        }
        Director director = this.resources.game.getDirector();
        if (director == null) {
            director = new PlainDirector();
        }

        director.onMessage(Director.GAME_INFO_LOADED);

        if (!resources.client) {
            for (Iterator<XMLTag> i = resourcesTag.getTags("inputs"); i.hasNext();) {
                XMLTag inputsTag = i.next();
                this.readInputs(inputsTag);
            }
            director.onMessage(Director.INPUTS_LOADED);
        }

        for (Iterator<XMLTag> i = resourcesTag.getTags("fonts"); i.hasNext();) {
            XMLTag fontsTag = i.next();
            this.readFonts(fontsTag);
        }
        director.onMessage(Director.FONTS_LOADED);

        for (Iterator<XMLTag> i = resourcesTag.getTags("sounds"); i.hasNext();) {
            XMLTag soundsTag = i.next();
            this.readSounds(soundsTag);
        }
        director.onMessage(Director.SOUNDS_LOADED);

        for (Iterator<XMLTag> i = resourcesTag.getTags("ninePatches"); i.hasNext();) {
            XMLTag eightPatchesTag = i.next();
            this.readNinePatches(eightPatchesTag);
        }
        director.onMessage(Director.NINE_PATCHES_LOADED);

        for (Iterator<XMLTag> i = resourcesTag.getTags("spriteSheets"); i.hasNext();) {
            XMLTag spriteSheetsTag = i.next();
            this.readSpriteSheets(spriteSheetsTag);
        }
        director.onMessage(Director.SPRITE_SHEETS_LOADED);

        for (Iterator<XMLTag> i = resourcesTag.getTags("poses"); i.hasNext();) {
            XMLTag posesTag = i.next();
            this.readPoses(posesTag);
        }
        director.onMessage(Director.POSES_LOADED);

        for (Iterator<XMLTag> i = resourcesTag.getTags("animations"); i.hasNext();) {
            XMLTag animationsTag = i.next();
            this.readAnimations(animationsTag);
        }
        director.onMessage(Director.ANIMIATIONS_LOADED);

        for (Iterator<XMLTag> i = resourcesTag.getTags("costumes"); i.hasNext();) {
            XMLTag costumesTag = i.next();
            this.readCostumes(costumesTag);
        }
        director.onMessage(Director.COSTUMES_LOADED);

        if (!resources.client) {

            for (Iterator<XMLTag> i = resourcesTag.getTags("layouts"); i.hasNext();) {
                XMLTag layoutsTag = i.next();
                this.readLayouts(layoutsTag);
            }
            /*
             * For backwards compatibility (when layouts didn't exist), create a default layout, with a single layer.
             */
            if (this.resources.layoutNames().size() == 0) {
                Layout layout = new Layout();
                layout.setName("default");
                Layer layer = new Layer();
                layer.setName("main");
                layer.position = new Rect(0, 0, gameInfo.width, gameInfo.height);
                layout.addLayer(layer);
                resources.addLayout(layout);
            }
            director.onMessage(Director.LAYOUTS_LOADED);

            for (Iterator<XMLTag> i = resourcesTag.getTags("scenes"); i.hasNext();) {
                XMLTag scenesTag = i.next();
                this.readScenes(scenesTag);
            }
            director.onMessage(Director.SCENES_LOADED);
        }

        director.onMessage(Director.LOADED);
    }

    private void readGame(XMLTag gameTag) throws Exception
    {
        GameInfo gameInfo = new GameInfo();
        this.readProperties(gameTag, gameInfo);
        this.resources.setGameInfo(gameInfo);
        this.resources.registry.add(gameInfo.directorClassName);

        this.resources.game.setDirector(gameInfo.createDirector(this.resources));

        Itchy.init(this.resources);
    }

    private void readNinePatches(XMLTag eightPatchesTag) throws Exception
    {
        for (Iterator<XMLTag> i = eightPatchesTag.getTags("ninePatch"); i.hasNext();) {
            XMLTag eightPatchTag = i.next();

            NinePatch ninePatch = new NinePatch();
            this.readProperties(eightPatchTag, ninePatch);
            this.resources.addNinePatch(ninePatch);

        }

    }

    private void readSpriteSheets(XMLTag spriteSheetsTag) throws Exception
    {
        for (Iterator<XMLTag> i = spriteSheetsTag.getTags("spriteSheet"); i.hasNext();) {
            XMLTag spriteSheetTag = i.next();

            SpriteSheet spriteSheet = new SpriteSheet();
            this.readProperties(spriteSheetTag, spriteSheet);

            for (Iterator<XMLTag> j = spriteSheetTag.getTags("sprite"); j.hasNext();) {
                XMLTag spriteTag = j.next();
                String spriteName = spriteTag.getAttribute("name");
                Sprite sprite = new Sprite(spriteSheet, spriteName);
                this.readProperties(spriteTag, sprite);
                spriteSheet.addSprite(sprite);
                this.resources.addPose(sprite);
            }
            this.resources.addSpriteSheet(spriteSheet);
        }

    }

    private void readPoses(XMLTag posesTag) throws Exception
    {
        for (Iterator<XMLTag> i = posesTag.getTags("pose"); i.hasNext();) {
            XMLTag poseTag = i.next();

            String name = poseTag.getAttribute("name");
            File file = new File(poseTag.getAttribute("filename"));

            PoseResource resource = new FilePoseResource(name, file);
            ImagePose pose = resource.pose;
            this.resources.addPose(resource);
            pose.setDirection(poseTag.getOptionalDoubleAttribute("direction", 0));

            if (poseTag.hasAttribute("offsetX")) {
                pose.setOffsetX(poseTag.getIntAttribute("offsetX"));
            }
            if (poseTag.hasAttribute("offsetY")) {
                pose.setOffsetY(poseTag.getIntAttribute("offsetY"));
            }
        }

    }

    private void readSounds(XMLTag soundsTag) throws Exception
    {
        for (Iterator<XMLTag> i = soundsTag.getTags("sound"); i.hasNext();) {
            XMLTag soundTag = i.next();

            SoundResource soundResource = new SoundResource();
            // Don't actually LOAD the sounds on a server, as they are only needed on the clients.
            if (resources.server) {
                String name = soundTag.getAttribute("name");
                soundResource.setName(name);
            } else {
                this.readProperties(soundTag, soundResource);
            }
            this.resources.addSound(soundResource);

        }

    }

    private void readAnimations(XMLTag animationsTag) throws XMLException
    {
        for (Iterator<XMLTag> j = animationsTag.getTags("animation"); j.hasNext();) {
            XMLTag animationTag = j.next();

            CompoundAnimation dummyAnimation = new CompoundAnimation(true);
            this.readCompoundAnimation(animationTag, dummyAnimation);

            // But only one animation can be named
            if (dummyAnimation.children.size() != 1) {
                throw new XMLException("animation tag requires exactly one child");
            }

            AnimationResource ar = new AnimationResource();
            this.readProperties(animationTag, ar);
            ar.animation = dummyAnimation.children.get(0);
            this.resources.addAnimation(ar);
        }
    }

    private void readCostumes(XMLTag costumesTag) throws Exception
    {
        for (Iterator<XMLTag> i = costumesTag.getTags("costume"); i.hasNext();) {
            XMLTag costumeTag = i.next();

            String costumeName = costumeTag.getAttribute("name");

            Costume costume = new Costume();

            int order = costumeTag.getOptionalIntAttribute("order", 0);
            costume.defaultZOrder = costumeTag.getOptionalIntAttribute("defaultZOrder", 0);
            costume.showInDesigner = costumesTag.getOptionalBooleanAttribute("showInDesigner", true);

            String extendsName = costumeTag.getOptionalAttribute("extends", null);
            if (extendsName != null) {
                Costume base = this.resources.getCostume(extendsName);
                if (base == null) {
                    throw new XMLException("Failed to find base costume : " + extendsName + " for costume : "
                        + costumeName);
                }
                costume.setExtendedFrom(base);

            }

            // Don't set the role, because we don't want arbitrary scripts running on the client.
            if (!resources.client) {
                String roleName = costumeTag.getOptionalAttribute("role", PlainRole.class.getName());
                ClassName roleClassName = new ClassName(Role.class, roleName);

                if (roleClassName.isValid(resources.scriptManager)) {
                    costume.roleClassName = roleClassName;
                } else {
                    throw new XMLException("Costume " + costumeName + ". Expected a subclass of Role : '"
                        + roleClassName + "'");
                }
            }

            for (Iterator<XMLTag> j = costumeTag.getTags("pose"); j.hasNext();) {
                XMLTag poseTag = j.next();

                String itemName = poseTag.getAttribute("name");
                String poseName = poseTag.getAttribute("pose");
                PoseResource poseResource = this.resources.getPoseResource(poseName);
                if (poseResource == null) {
                    throw new XMLException("Pose : '" + poseName + "' not found for costume : " + costumeName);
                }
                costume.addPose(itemName, poseResource);

            }

            for (Iterator<XMLTag> j = costumeTag.getTags("sound"); j.hasNext();) {
                XMLTag soundTag = j.next();

                String itemName = soundTag.getAttribute("name");
                String soundName = soundTag.getAttribute("sound");

                SoundResource soundResource = this.resources.getSound(soundName);
                if (soundResource == null) {
                    throw new XMLException("Sound : " + soundName + " not found for costume : " + costumeName);
                }
                ManagedSound managedSound = new ManagedSound(soundResource);
                readProperties(soundTag, managedSound);

                costume.addSound(itemName, managedSound);
            }

            for (Iterator<XMLTag> j = costumeTag.getTags("string"); j.hasNext();) {
                XMLTag stringTag = j.next();

                String itemName = stringTag.getAttribute("name");
                String string = stringTag.getBody();
                costume.addString(itemName, string);
            }

            for (Iterator<XMLTag> j = costumeTag.getTags("font"); j.hasNext();) {
                XMLTag fontTag = j.next();

                String itemName = fontTag.getAttribute("name");
                String fontName = fontTag.getAttribute("font");

                Font font = this.resources.getFont(fontName);
                if (font == null) {
                    throw new XMLException("Font : " + fontName + " not found for costume : " + costumeName);
                }
                TextStyle textStyle = new TextStyle(font, 14);
                this.readProperties(fontTag, textStyle);
                costume.addTextStyle(itemName, textStyle);
            }

            for (Iterator<XMLTag> j = costumeTag.getTags("animation"); j.hasNext();) {
                XMLTag animationTag = j.next();

                String itemName = animationTag.getAttribute("name");
                String animationName = animationTag.getAttribute("animation");

                AnimationResource animationResource = this.resources.getAnimationResource(animationName);
                if (animationResource == null) {
                    throw new XMLException("Animation : " + animationName + " not found for costume : " + costumeName);
                }

                costume.addAnimation(itemName, animationResource);
            }

            for (Iterator<XMLTag> j = costumeTag.getTags("companion"); j.hasNext();) {
                XMLTag companionTag = j.next();

                String itemName = companionTag.getAttribute("name");
                String companionName = companionTag.getAttribute("companion");

                List<String> companionNames = costume.companionStringChoices.get(itemName);
                if (companionNames == null) {
                    companionNames = new ArrayList<String>();
                    costume.companionStringChoices.put(itemName, companionNames);
                }
                companionNames.add(companionName);
                // Note, once all costumes have been loaded, costume.costumeStringChoices is
                // used to build the actual map of CostumeResouuces.
            }

            for (Iterator<XMLTag> j = costumeTag.getTags("properties"); j.hasNext();) {
                XMLTag propertiesTag = j.next();

                readProperties(propertiesTag, costume.getCostumeFeatures());
            }

            costume.setOrder(order);
            costume.setName(costumeName);
            this.resources.addCostume(costume);

        }

        for (String costumeName : resources.costumeNames()) {
            Costume costume = resources.getCostume(costumeName);
            for (String key : costume.companionStringChoices.keySet()) {
                List<String> costumeNames = costume.companionStringChoices.get(key);
                for (String companionName : costumeNames) {
                    Costume companion = resources.getCostume(companionName);
                    if (companion == null) {
                        throw new XMLException("Companion costume not found : " + companionName);
                    } else {
                        costume.addCompanion(key, companion);
                    }
                }
            }
        }

    }

    private void readAnimation(XMLTag tag, Animation animation) throws XMLException
    {
        animation.setFinishedMessage(tag.getOptionalAttribute("finishedMessage", null));
    }

    private void readCompoundAnimation(XMLTag parentTag, CompoundAnimation animation) throws XMLException
    {
        readAnimation(parentTag, animation);

        for (Iterator<XMLTag> j = parentTag.getTags(); j.hasNext();) {

            XMLTag tag = j.next();

            Animation child = createAnimation(tag.getName());
            readProperties(tag, child);

            if (child instanceof CompoundAnimation) {
                readCompoundAnimation(tag, (CompoundAnimation) child);

            } else if (child instanceof FramedAnimation) {
                readFramedAnimation(tag, (FramedAnimation) child);
            }

            animation.addAnimation(child);

        }
    }

    public Animation createAnimation(String tagName) throws XMLException
    {
        if (tagName == "compound") {
            return new CompoundAnimation(true);
        }
        Animation animation = Itchy.registry.getAnimationByTagName(tagName);
        if (animation == null) {
            throw new XMLException("Unknown animation : " + tagName);
        }
        return animation;
    }

    private <S extends PropertySubject<S>> void readProperties(XMLTag tag, S subject) throws XMLException
    {
        for (Property<S, ?> property : subject.getProperties()) {
            String value = tag.getOptionalAttribute(property.key, null);
            if (value == null) {
                for (String alias : property.aliases) {
                    value = tag.getOptionalAttribute(alias, null);
                    if (value != null) {
                        break;
                    }
                }
            }
            if (value != null) {
                try {
                    property.setValueByString(subject, value);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new XMLException("Failed to parse property : '" + property.key + "'. value : '" + value + "'");
                }
            }
        }

    }

    private void readFramedAnimation(XMLTag parentTag, FramedAnimation animation) throws XMLException
    {
        readAnimation(parentTag, animation);

        for (Iterator<XMLTag> k = parentTag.getTags("frame"); k.hasNext();) {
            XMLTag frameTag = k.next();

            int delay = frameTag.getOptionalIntAttribute("delay", 1);
            if (delay < 0) {
                throw new XMLException("Delay cannot  be negative");
            }
            double dx = frameTag.getOptionalDoubleAttribute("dx", 0);
            double dy = frameTag.getOptionalDoubleAttribute("dy", 0);

            String poseName = frameTag.getAttribute("pose");
            Pose pose = this.resources.getPose(poseName);
            if (pose == null) {
                throw new XMLException("Pose : '" + poseName + "' not found");
            }
            Frame frame = new Frame(poseName, pose);
            frame.delay = delay;
            frame.dx = dx;
            frame.dy = dy;
            animation.addFrame(frame);
        }

    }

    private void readFonts(XMLTag fontsTag) throws Exception
    {
        for (Iterator<XMLTag> i = fontsTag.getTags("font"); i.hasNext();) {
            XMLTag fontTag = i.next();

            Font font = new Font();
            this.readProperties(fontTag, font);
            this.resources.addFont(font);
        }

    }

    private void readLayouts(XMLTag layoutsTag) throws Exception
    {
        for (Iterator<XMLTag> i = layoutsTag.getTags("layout"); i.hasNext();) {
            XMLTag layoutTag = i.next();

            Layout layout = new Layout();

            for (Iterator<XMLTag> j = layoutTag.getTags("layer"); j.hasNext();) {
                XMLTag layerTag = j.next();
                Layer layer = new Layer();
                this.readProperties(layerTag, layer);
                layout.addLayer(layer);

                XMLTag viewTag = layerTag.getTag("view", false);
                if (viewTag != null) {
                    this.readProperties(viewTag, layer.getView());
                }

                Stage stage = layer.getStage();
                if (stage != null) {
                    XMLTag stageTag = layerTag.getTag("stage", false);
                    if (stageTag != null) {
                        this.readProperties(stageTag, stage);
                    }

                    XMLTag stageConstraintTag = layerTag.getTag("stageConstraint", false);
                    if (stageConstraintTag != null) {
                        this.readProperties(stageConstraintTag, stage.getStageConstraint());
                    }
                }
            }
            this.readProperties(layoutTag, layout);

            this.resources.addLayout(layout);
        }

    }

    private void readScenes(XMLTag scenesTag) throws Exception
    {
        for (Iterator<XMLTag> i = scenesTag.getTags("scene"); i.hasNext();) {
            XMLTag sceneTag = i.next();

            SceneStub sceneStub = new SceneStub();
            this.readProperties(sceneTag, sceneStub);
            this.resources.addScene(sceneStub);
        }
    }

    private void readInputs(XMLTag inputsTag) throws Exception
    {
        for (Iterator<XMLTag> i = inputsTag.getTags("input"); i.hasNext();) {
            XMLTag inputTag = i.next();

            Input input = new Input();
            this.readProperties(inputTag, input);

            this.resources.addInput(input);
        }
    }

}
