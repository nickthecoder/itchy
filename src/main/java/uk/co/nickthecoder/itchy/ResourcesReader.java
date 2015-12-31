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
import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.itchy.property.PropertySubject;
import uk.co.nickthecoder.itchy.role.PlainRole;
import uk.co.nickthecoder.itchy.script.ScriptManager;
import uk.co.nickthecoder.itchy.util.ClassName;
import uk.co.nickthecoder.itchy.util.NinePatch;
import uk.co.nickthecoder.itchy.util.XMLException;
import uk.co.nickthecoder.itchy.util.XMLTag;
import uk.co.nickthecoder.jame.JameException;
import uk.co.nickthecoder.jame.Surface;

public class ResourcesReader
{
    private final Resources resources;

    private final ScriptManager scriptManager;
    /**
     * true if the resources being read are included from another file.
     */
    public boolean included;

    public ResourcesReader( Resources resources )
    {
        this.resources = resources;
        this.scriptManager = resources.scriptManager;
    }

    public void load( String filename ) throws Exception
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(
            filename)));
        try {
            XMLTag document = XMLTag.openDocument(reader);
            this.resources.setFile(new File(filename));
            this.readResources(document.getTag("resources"));

        } finally {
            reader.close();
        }
    }

    private void readResources( XMLTag resourcesTag ) throws Exception
    {

        for (Iterator<XMLTag> i = resourcesTag.getTags("inputs"); i.hasNext();) {
            XMLTag inputsTag = i.next();
            this.readInputs(inputsTag);
        }
        for (Iterator<XMLTag> i = resourcesTag.getTags("game"); i.hasNext();) {
            XMLTag gameTag = i.next();
            this.readGame(gameTag);
        }
        for (Iterator<XMLTag> i = resourcesTag.getTags("fonts"); i.hasNext();) {
            XMLTag fontsTag = i.next();
            this.readFonts(fontsTag);
        }
        for (Iterator<XMLTag> i = resourcesTag.getTags("sounds"); i.hasNext();) {
            XMLTag soundsTag = i.next();
            this.readSounds(soundsTag);
        }
        for (Iterator<XMLTag> i = resourcesTag.getTags("ninePatches"); i.hasNext();) {
            XMLTag eightPatchesTag = i.next();
            this.readNinePatches(eightPatchesTag);
        }
        for (Iterator<XMLTag> i = resourcesTag.getTags("poses"); i.hasNext();) {
            XMLTag posesTag = i.next();
            this.readPoses(posesTag);
        }
        for (Iterator<XMLTag> i = resourcesTag.getTags("animations"); i.hasNext();) {
            XMLTag animationsTag = i.next();
            this.readAnimations(animationsTag);
        }
        for (Iterator<XMLTag> i = resourcesTag.getTags("costumes"); i.hasNext();) {
            XMLTag costumesTag = i.next();
            this.readCostumes(costumesTag);
        }
        for (Iterator<XMLTag> i = resourcesTag.getTags("scenes"); i.hasNext();) {
            XMLTag scenesTag = i.next();
            this.readScenes(scenesTag);
        }
    }

    private void readGame( XMLTag gameTag ) throws Exception
    {
        GameInfo gameInfo = new GameInfo();
        this.readProperties(gameTag, gameInfo);
        this.resources.setGameInfo(gameInfo);
        this.resources.registry.add(gameInfo.directorClassName);
        Itchy.init(this.resources);
    }

    private void readNinePatches( XMLTag eightPatchesTag ) throws Exception
    {
        for (Iterator<XMLTag> i = eightPatchesTag.getTags("ninePatch"); i.hasNext();) {
            XMLTag eightPatchTag = i.next();

            String name = eightPatchTag.getAttribute("name");
            String filename = eightPatchTag.getAttribute("filename");

            int marginTop = eightPatchTag.getIntAttribute("top");
            int marginRight = eightPatchTag.getIntAttribute("right");
            int marginBottom = eightPatchTag.getIntAttribute("bottom");
            int marginLeft = eightPatchTag.getIntAttribute("left");

            Surface surface = new Surface(this.resources.resolveFilename(filename));

            String middleStr = eightPatchTag.getOptionalAttribute("middle", "tile");
            try {
                NinePatch.Middle middle = NinePatch.Middle.valueOf(middleStr);

                NinePatch ninePatch = new NinePatch(surface, marginTop, marginRight, marginBottom,
                    marginLeft, middle);
                NinePatchResource resource = new NinePatchResource(this.resources, name, filename,
                    ninePatch);
                this.resources.addNinePatch(resource);

            } catch (IllegalArgumentException e) {
                throw new XMLException("Invalid ninePatch middle " + middleStr);
            }

        }

    }

    private void readPoses( XMLTag posesTag ) throws Exception
    {
        for (Iterator<XMLTag> i = posesTag.getTags("pose"); i.hasNext();) {
            XMLTag poseTag = i.next();

            String name = poseTag.getAttribute("name");
            String filename = poseTag.getAttribute("filename");

            PoseResource resource = new PoseResource(this.resources, name, filename);
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

    private void readSounds( XMLTag soundsTag ) throws Exception
    {
        for (Iterator<XMLTag> i = soundsTag.getTags("sound"); i.hasNext();) {
            XMLTag soundTag = i.next();

            String name = soundTag.getAttribute("name");
            String filename = soundTag.getAttribute("filename");

            try {
                SoundResource soundResource = new SoundResource(this.resources, name, filename);
                this.resources.addSound(soundResource);
            } catch (JameException e) {
                throw new XMLException("Failed to load sound file : " + filename);
            }
        }

    }

    private void readAnimations( XMLTag animationsTag ) throws XMLException
    {
        for (Iterator<XMLTag> j = animationsTag.getTags("animation"); j.hasNext();) {
            XMLTag animationTag = j.next();

            String itemName = animationTag.getAttribute("name");

            CompoundAnimation dummyAnimation = new CompoundAnimation(true);
            this.readCompoundAnimation(animationTag, dummyAnimation);

            // But only one animation can be named
            if (dummyAnimation.children.size() != 1) {
                throw new XMLException(
                    "animation tag requires exactly one child");
            }

            AnimationResource ar = new AnimationResource(this.resources, itemName, dummyAnimation.children.get(0));
            this.resources.addAnimation(ar);
        }
    }

    private void readCostumes( XMLTag costumesTag ) throws Exception
    {
        for (Iterator<XMLTag> i = costumesTag.getTags("costume"); i.hasNext();) {
            XMLTag costumeTag = i.next();

            String costumeName = costumeTag.getAttribute("name");

            Costume costume = new Costume();

            costume.defaultZOrder = costumeTag.getOptionalIntAttribute("defaultZOrder", 0);
            costume.showInDesigner = costumesTag.getOptionalBooleanAttribute("showInDesigner", true);
            
            String extendsName = costumeTag.getOptionalAttribute("extends", null);
            if (extendsName != null) {
                Costume base = this.resources.getCostume(extendsName);
                if (base == null) {
                    throw new XMLException("Failed to find base costume : " + extendsName + " for costume : " + costumeName);
                }
                costume.setExtendedFrom(base);

            }

            String roleName = costumeTag.getOptionalAttribute("role", PlainRole.class.getName());
            ClassName roleClassName = new ClassName(Role.class, roleName);

            if (this.resources.checkClassName(roleClassName)) {
                costume.roleClassName = roleClassName;
            } else {
                throw new XMLException("Expected a subclass of Role : " + costume.roleClassName);
            }

            String propertiesName = costumeTag.getOptionalAttribute("properties", CostumeProperties.class.getName());
            ClassName propertiesClassName = new ClassName(CostumeProperties.class, propertiesName);

            if (this.resources.checkClassName(propertiesClassName)) {
                costume.setPropertiesClassName(this.scriptManager, propertiesClassName);

            } else {
                throw new XMLException("Expected a name of a Properties class : " + propertiesClassName);
            }

            for (Iterator<XMLTag> j = costumeTag.getTags("properties"); j.hasNext();) {
                XMLTag propertiesTag = j.next();

                readProperties(propertiesTag, costume.getProperties());
            }

            for (Iterator<XMLTag> j = costumeTag.getTags("pose"); j.hasNext();) {
                XMLTag poseTag = j.next();

                String itemName = poseTag.getAttribute("name");
                String poseName = poseTag.getAttribute("pose");
                PoseResource poseResource = this.resources.getPoseResource(poseName);
                if (poseResource == null) {
                    throw new XMLException("Pose : " + poseName + " not found for costume : " + costumeName);
                }
                costume.addPose(itemName, poseResource);

            }

            for (Iterator<XMLTag> j = costumeTag.getTags("sound"); j.hasNext();) {
                XMLTag soundTag = j.next();

                String itemName = soundTag.getAttribute("name");
                String soundName = soundTag.getAttribute("sound");

                SoundResource soundResource = this.resources.getSoundResource(soundName);
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
                XMLTag stringTag = j.next();

                String itemName = stringTag.getAttribute("name");
                String fontName = stringTag.getAttribute("font");

                FontResource fontResource = this.resources.getFontResource(fontName);
                if (fontResource == null) {
                    throw new XMLException("Font : " + fontName + " not found for costume : " +
                        costumeName);
                }
                costume.addFont(itemName, fontResource);
            }

            for (Iterator<XMLTag> j = costumeTag.getTags("animation"); j.hasNext();) {
                XMLTag animationTag = j.next();

                String itemName = animationTag.getAttribute("name");
                String animationName = animationTag.getAttribute("animation");

                AnimationResource animationResource = this.resources
                    .getAnimationResource(animationName);
                if (animationResource == null) {
                    throw new XMLException("Animation : " + animationName +
                        " not found for costume : " + costumeName);
                }

                costume.addAnimation(itemName, animationResource);
            }

            for (Iterator<XMLTag> j = costumeTag.getTags("companion"); j.hasNext();) {
                XMLTag companionTag = j.next();

                String itemName = companionTag.getAttribute("name");
                String companionName = companionTag.getAttribute("companion");

                List<String> companionNames = costume.companionStringChoices.get( itemName );
                if (companionNames == null) {
                    companionNames = new ArrayList<String>();
                    costume.companionStringChoices.put( itemName,companionNames );
                }
                companionNames.add( companionName );
                // Note, once all costumes have been loaded, costume.costumeStringChoices is
                // used to build the actual map of CostumeResouuces.
            }

            CostumeResource resource = new CostumeResource(this.resources, costumeName, costume);
            this.resources.addCostume(resource);

        }

        for (String costumeName : resources.costumeNames()) {
            Costume costume = resources.getCostume(costumeName);
            for ( String key : costume.companionStringChoices.keySet()) {
                List<String> costumeNames = costume.companionStringChoices.get( key );
                for ( String companionName : costumeNames ) {
                    CostumeResource costumeResource = resources.getCostumeResource( companionName );
                    if ( costumeResource == null) {
                        // TODO How should this be handled?
                        System.err.println( "Companion costume not found : " + companionName );
                    } else {
                        costume.addCompanion(key, costumeResource);
                    }
                }
            }
        }
        
    }

    private void readAnimation( XMLTag tag, Animation animation ) throws XMLException
    {
        animation.setFinishedMessage(tag.getOptionalAttribute("finishedMessage", null));
    }

    private void readCompoundAnimation( XMLTag parentTag, CompoundAnimation animation )
        throws XMLException
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

    public Animation createAnimation( String tagName )
        throws XMLException
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

    private <S extends PropertySubject<S>> void readProperties( XMLTag tag, S subject )
        throws XMLException
    {

        for (AbstractProperty<S, ?> property : subject.getProperties()) {
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
                    throw new XMLException("Failed to parse property : '" + property.key +
                        "'. value : '" + value + "'");
                }
            }
        }

    }

    private void readFramedAnimation( XMLTag parentTag, FramedAnimation animation )
        throws XMLException
    {
        readAnimation(parentTag, animation);

        for (Iterator<XMLTag> k = parentTag.getTags("frame"); k.hasNext();) {
            XMLTag frameTag = k.next();

            int delay = frameTag.getOptionalIntAttribute("delay", 1);
            if (delay < 1) {
                throw new XMLException("Delay must be greater than 0");
            }
            String poseName = frameTag.getAttribute("pose");
            Pose pose = this.resources.getPose(poseName);
            if (pose == null) {
                throw new XMLException("Pose : " + poseName + " not found");
            }
            Frame frame = new Frame(poseName, pose);
            frame.setDelay(delay);
            animation.addFrame(frame);
        }

    }

    private void readFonts( XMLTag fontsTag ) throws Exception
    {
        for (Iterator<XMLTag> i = fontsTag.getTags("font"); i.hasNext();) {
            XMLTag fontTag = i.next();

            String name = fontTag.getAttribute("name");
            String filename = fontTag.getAttribute("filename");

            FontResource fontResource = new FontResource(this.resources, name, filename);
            this.resources.addFont(fontResource);
        }

    }

    private void readScenes( XMLTag scenesTag ) throws Exception
    {
        for (Iterator<XMLTag> i = scenesTag.getTags("scene"); i.hasNext();) {
            XMLTag sceneTag = i.next();

            String name = sceneTag.getAttribute("name");

            SceneResource sceneResource = new SceneResource(this.resources, name);
            this.resources.addScene(sceneResource);
        }
    }

    private void readInputs( XMLTag inputsTag ) throws Exception
    {
        for (Iterator<XMLTag> i = inputsTag.getTags("input"); i.hasNext();) {
            XMLTag inputTag = i.next();

            String name = inputTag.getAttribute("name");
            String keys = inputTag.getOptionalAttribute("keys", "");
            
            Input input = new Input();
            input.parseKeys(keys);
            
            InputResource inputResource = new InputResource(this.resources, name, input);
            this.resources.addInput(inputResource);
        }
    }

}
