/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import uk.co.nickthecoder.itchy.animation.AlphaAnimation;
import uk.co.nickthecoder.itchy.animation.Animation;
import uk.co.nickthecoder.itchy.animation.CompoundAnimation;
import uk.co.nickthecoder.itchy.animation.ForwardsAnimation;
import uk.co.nickthecoder.itchy.animation.Frame;
import uk.co.nickthecoder.itchy.animation.FramedAnimation;
import uk.co.nickthecoder.itchy.animation.MoveAnimation;
import uk.co.nickthecoder.itchy.animation.ScaleAnimation;
import uk.co.nickthecoder.itchy.animation.TurnAnimation;
import uk.co.nickthecoder.itchy.util.AbstractProperty;
import uk.co.nickthecoder.itchy.util.NinePatch;
import uk.co.nickthecoder.itchy.util.PropertySubject;
import uk.co.nickthecoder.itchy.util.XMLException;
import uk.co.nickthecoder.itchy.util.XMLTag;
import uk.co.nickthecoder.jame.Surface;

public class ResourcesReader
{
    private final Resources resources;

    /**
     * true if the resources being read are included from another file.
     */
    public boolean included;

    public ResourcesReader( Resources resources )
    {
        this.resources = resources;
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
        String className = gameTag.getAttribute("class");
        resources.setGameClassName( className );
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

            SoundResource soundResource = new SoundResource(this.resources, name, filename);
            this.resources.addSound(soundResource);
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
                    "animation tag requires exactly one child (pingPong, frames, sequence, parallel etc)");
            }

            AnimationResource ar = new AnimationResource(this.resources, itemName,
                dummyAnimation.children.get(0));
            this.resources.addAnimation(ar);
        }
    }

    private void readCostumes( XMLTag costumesTag ) throws Exception
    {
        for (Iterator<XMLTag> i = costumesTag.getTags("costume"); i.hasNext();) {
            XMLTag costumeTag = i.next();

            String costumeName = costumeTag.getAttribute("name");

            Costume costume = new Costume();

            String extendsName = costumeTag.getOptionalAttribute("extends", null);
            if (extendsName != null) {
                Costume base = this.resources.getCostume(extendsName);
                if (base == null) {
                    throw new XMLException("Failed to find base costume : " + extendsName +
                        " for costume : " + costumeName);
                }
                costume.setExtendedFrom(base);

            }

            String behaviourClassName = costumeTag.getOptionalAttribute("behaviour",
                NullBehaviour.class.getName());
            if (this.resources.registerBehaviourClassName(behaviourClassName)) {
                costume.behaviourClassName = behaviourClassName;
            } else {
                throw new XMLException("Expected a subclass of Behaviour : " +
                    costume.behaviourClassName);
            }

            String propertiesClassName = costumeTag.getOptionalAttribute("properties",
                CostumeProperties.class.getName());
            if (this.resources.registerCostumePropertiesClassName(propertiesClassName)) {
                costume.setPropertiesClassName(propertiesClassName);

            } else {
                throw new XMLException("Expected a name of a Properties class : " +
                    propertiesClassName);
            }

            
            for (Iterator<XMLTag> j = costumeTag.getTags("properties"); j.hasNext();) {
                XMLTag propertiesTag = j.next();
                
                readProperties( propertiesTag, costume.getProperties());
            }

            for (Iterator<XMLTag> j = costumeTag.getTags("pose"); j.hasNext();) {
                XMLTag poseTag = j.next();

                String itemName = poseTag.getAttribute("name");
                String poseName = poseTag.getAttribute("pose");
                PoseResource poseResource = this.resources.getPoseResource(poseName);
                if (poseResource == null) {
                    throw new XMLException("Pose : " + poseName + " not found for costume : " +
                        costumeName);
                }
                costume.addPose(itemName, poseResource);

            }

            for (Iterator<XMLTag> j = costumeTag.getTags("sound"); j.hasNext();) {
                XMLTag soundTag = j.next();

                String itemName = soundTag.getAttribute("name");
                String soundName = soundTag.getAttribute("sound");

                SoundResource soundResource = this.resources.getSoundResource(soundName);
                if (soundResource == null) {
                    throw new XMLException("Sound : " + soundName + " not found for costume : " +
                        costumeName);
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

            CostumeResource resource = new CostumeResource(this.resources, costumeName, costume);
            this.resources.addCostume(resource);

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
        if (tagName.equals("compound")) {
            return new CompoundAnimation(true);

        } else if (tagName.equals("parallel")) {
            return new CompoundAnimation(false);

        } else if (tagName.equals("sequence")) {
            return new CompoundAnimation(true);

        } else if (tagName.equals("pingPong")) {
            return new FramedAnimation();

        } else if (tagName.equals("frames")) {
            return new FramedAnimation();

        } else if (tagName.equals("move")) {
            return new MoveAnimation();

        } else if (tagName.equals("forwards")) {
            return new ForwardsAnimation();

        } else if (tagName.equals("alpha")) {
            return new AlphaAnimation();

        } else if (tagName.equals("turn")) {
            return new TurnAnimation();

        } else if (tagName.equals("scale")) {
            return new ScaleAnimation();

        } else {
            throw new XMLException("Unknown animation : " + tagName);
        }
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
                throw new XMLException("Delay must be greater than 1");
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

}
