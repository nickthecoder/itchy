/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import uk.co.nickthecoder.itchy.animation.AlphaAnimation;
import uk.co.nickthecoder.itchy.animation.CompoundAnimation;
import uk.co.nickthecoder.itchy.animation.ForwardsAnimation;
import uk.co.nickthecoder.itchy.animation.Frame;
import uk.co.nickthecoder.itchy.animation.FramedAnimation;
import uk.co.nickthecoder.itchy.animation.MoveAnimation;
import uk.co.nickthecoder.itchy.animation.NumericAnimation;
import uk.co.nickthecoder.itchy.animation.Profile;
import uk.co.nickthecoder.itchy.animation.ScaleAnimation;
import uk.co.nickthecoder.itchy.animation.TurnAnimation;
import uk.co.nickthecoder.itchy.util.NinePatch;
import uk.co.nickthecoder.itchy.util.XMLException;
import uk.co.nickthecoder.itchy.util.XMLTag;
import uk.co.nickthecoder.jame.Surface;

public class ResourcesReader
{
    private final Resources resources;

    public ResourcesReader( Resources resources )
    {
        this.resources = resources;
    }

    public void load( String filename ) throws Exception
    {
        System.out.println("Loading resources :" + filename);
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(
                filename)));
        try {
            XMLTag document = XMLTag.openDocument(reader);
            this.readResources(document.getTag("resources"));

            System.out.println("Loaded resources file : " + filename);

        } finally {
            reader.close();
        }
    }

    private void readResources( XMLTag resourcesTag ) throws Exception
    {
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

            System.out.println("Loading ninePatch : " + filename);
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

            System.out.println("Loading image : " + filename);
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

            System.out.println("Loading sound : " + filename);
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
            System.out.println("Loading costume " + costumeName);

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

            costume.behaviourClassName = costumeTag.getOptionalAttribute("behaviour",
                    NullBehaviour.class.getName());
            if (!Behaviour.isValidClassName(costume.behaviourClassName)) {
                throw new XMLException("Expected a subclass of Behaviour : " +
                        costume.behaviourClassName);
            }

            for (Iterator<XMLTag> j = costumeTag.getTags("pose"); j.hasNext();) {
                XMLTag poseTag = j.next();

                String itemName = poseTag.getAttribute("name");
                String poseName = poseTag.getAttribute("pose");
                System.out.println("Loading costume pose " + itemName + "," + poseName);
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
                System.out.println("Loading costume sound " + itemName + "," + soundName);

                SoundResource soundResource = this.resources.getSoundResource(soundName);
                if (soundResource == null) {
                    throw new XMLException("Sound : " + soundName + " not found for costume : " +
                            costumeName);
                }
                costume.addSound(itemName, soundResource);
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
                System.out.println("Loading costume font " + itemName + "," + fontName);

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
                System.out.println("Loading costume animation " + itemName + "," + animationName);

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

            System.out.println("Loaded costume " + costumeName);

        }

    }

    private void readCompoundAnimation( XMLTag parentTag, CompoundAnimation animation )
        throws XMLException
    {
        for (Iterator<XMLTag> j = parentTag.getTags(); j.hasNext();) {

            XMLTag tag = j.next();

            if ( tag.getName().equals("parallel") ) {
    
                CompoundAnimation parallel = new CompoundAnimation(false);
                int loops = tag.getIntAttribute("loops");
                parallel.loops = loops;
                this.readCompoundAnimation(tag, parallel);
                animation.addAnimation(parallel);
                
            } else if ( tag.getName().equals("sequence") ) {
    
                CompoundAnimation sequential = new CompoundAnimation(true);
                int loops = tag.getIntAttribute("loops");
                sequential.loops = loops;
                this.readCompoundAnimation(tag, sequential);
                animation.addAnimation(sequential);

            } else if ( tag.getName().equals("pingPong") ) {
    
                FramedAnimation pingPong = new FramedAnimation();
                pingPong.pingPong = true;
                this.readFramedAnimation(tag, pingPong);
                animation.addAnimation(pingPong);

            } else if ( tag.getName().equals("frames") ) {
    
                FramedAnimation frames = new FramedAnimation();
                this.readFramedAnimation(tag, frames);
                animation.addAnimation(frames);

            } else if ( tag.getName().equals("move") ) {

                String profileName = tag.getOptionalAttribute("profile", "unit");
                Profile profile = NumericAnimation.getProfile(profileName);
                if (profile == null) {
                    throw new XMLException("Unknown profile : " + profileName);
                }
                int ticks = tag.getIntAttribute("ticks");
                double dx = tag.getDoubleAttribute("dx");
                double dy = tag.getDoubleAttribute("dy");
                MoveAnimation ani = new MoveAnimation(ticks, profile, dx, dy);
                animation.addAnimation(ani);

            } else if ( tag.getName().equals("forwards") ) {
                    
                String profileName = tag.getOptionalAttribute("profile", "unit");
                Profile profile = NumericAnimation.getProfile(profileName);
                if (profile == null) {
                    throw new XMLException("Unknown profile : " + profileName);
                }
                int ticks = tag.getIntAttribute("ticks");
                double forwards = tag.getDoubleAttribute("forwards");
                double sideways = tag.getDoubleAttribute("sideways");
                ForwardsAnimation ani = new ForwardsAnimation(ticks, profile, forwards, sideways);
                animation.addAnimation(ani);
                
            } else if ( tag.getName().equals("alpha") ) {
    
                String profileName = tag.getOptionalAttribute("profile", "linear");
                Profile profile = NumericAnimation.getProfile(profileName);
                if (profile == null) {
                    throw new XMLException("Unknown profile : " + profileName);
                }
                int ticks = tag.getIntAttribute("ticks");
                double target = tag.getOptionalDoubleAttribute("target", 255);
                AlphaAnimation ani = new AlphaAnimation(ticks, profile, target);
                animation.addAnimation(ani);

            } else if ( tag.getName().equals("turn") ) {
    
                String profileName = tag.getOptionalAttribute("profile", "linear");
                Profile profile = NumericAnimation.getProfile(profileName);
                if (profile == null) {
                    throw new XMLException("Unknown profile : " + profileName);
                }
                int ticks = tag.getIntAttribute("ticks");
                double turn = tag.getOptionalDoubleAttribute("turn", 360);
                TurnAnimation ani = new TurnAnimation(ticks, profile, turn);
                animation.addAnimation(ani);

            } else if ( tag.getName().equals("scale") ) {
    
                String profileName = tag.getOptionalAttribute("profile", "linear");
                Profile profile = NumericAnimation.getProfile(profileName);
                if (profile == null) {
                    throw new XMLException("Unknown profile : " + profileName);
                }
                int ticks = tag.getIntAttribute("ticks");
                double target = tag.getOptionalDoubleAttribute("target", 1);
                ScaleAnimation ani = new ScaleAnimation(ticks, profile, target);
                animation.addAnimation(ani);

            } else {
                System.err.println( "Ignoring animation tag " + tag.getName() );
            }
        }
    }

    private void readFramedAnimation( XMLTag parentTag, FramedAnimation animation )
        throws XMLException
    {
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

            System.out.println("Loading font : " + filename);
            FontResource fontResource = new FontResource(this.resources, name, filename);
            this.resources.addFont(fontResource);
        }

    }

    private void readScenes( XMLTag scenesTag ) throws Exception
    {
        for (Iterator<XMLTag> i = scenesTag.getTags("scene"); i.hasNext();) {
            XMLTag sceneTag = i.next();

            String name = sceneTag.getAttribute("name");
            String filename = sceneTag.getAttribute("filename");

            System.out.println("Loading scene : " + filename);
            SceneResource sceneResource = new SceneResource(this.resources, name, filename);
            this.resources.addScene(sceneResource);
        }
    }

}
