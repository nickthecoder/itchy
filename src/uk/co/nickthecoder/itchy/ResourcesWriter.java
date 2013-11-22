/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.co.nickthecoder.itchy.animation.Animation;
import uk.co.nickthecoder.itchy.animation.CompoundAnimation;
import uk.co.nickthecoder.itchy.animation.Frame;
import uk.co.nickthecoder.itchy.animation.FramedAnimation;
import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.itchy.property.PropertySubject;
import uk.co.nickthecoder.itchy.role.NullRole;
import uk.co.nickthecoder.itchy.util.NinePatch;
import uk.co.nickthecoder.itchy.util.StringUtils;
import uk.co.nickthecoder.itchy.util.XMLException;
import uk.co.nickthecoder.itchy.util.XMLWriter;

public class ResourcesWriter extends XMLWriter
{
    private final Resources resources;

    private Set<String> writtenCostumeName;

    public ResourcesWriter( Resources resources )
    {
        this.resources = resources;
    }

    public void write( String filename ) throws Exception
    {
        this.writtenCostumeName = new HashSet<String>();

        this.begin(filename);

        try {

            this.writeResources();

        } finally {
            this.end();
        }
    }

    private void writeResources() throws XMLException
    {
        this.beginTag("resources");

        this.writeGame();
        this.writeFonts();
        this.writeNinePatches();
        this.writePoses();
        this.writeSounds();
        this.writeAnimations();
        this.writeCostumes();
        this.writeScenes();

        this.endTag("resources");
    }

    private void writeGame() throws XMLException
    {
        this.beginTag("game");
        this.writeProperties(this.resources.getGameInfo());
        this.endTag("game");
    }

    private void writeFonts() throws XMLException
    {
        this.beginTag("fonts");

        for (String name : this.resources.fontNames()) {
            FontResource fontResource = this.resources.getFontResource(name);

            this.beginTag("font");
            this.attribute("name", name);
            this.attribute("filename", fontResource.getFilename());
            this.endTag("font");
        }

        this.endTag("fonts");
    }

    private void writeNinePatches() throws XMLException
    {
        this.beginTag("ninePatches");

        for (String name : this.resources.ninePatchNames()) {
            NinePatchResource ninePatchResource = this.resources.getNinePatchResource(name);

            this.beginTag("ninePatch");

            this.attribute("name", name);
            this.attribute("filename", ninePatchResource.getFilename());

            if (ninePatchResource.ninePatch instanceof NinePatch) {
                NinePatch patch = (ninePatchResource.ninePatch);

                this.attribute("top", patch.getMarginTop());
                this.attribute("right", patch.getMarginRight());
                this.attribute("bottom", patch.getMarginBottom());
                this.attribute("left", patch.getMarginLeft());
            }

            this.endTag("ninePatch");
        }

        this.endTag("ninePatches");
    }

    private void writePoses() throws XMLException
    {
        this.beginTag("poses");

        for (String name : this.resources.poseNames()) {
            PoseResource poseResource = this.resources.getPoseResource(name);

            this.beginTag("pose");
            this.attribute("name", name);
            this.attribute("filename", poseResource.getFilename());
            if (poseResource.pose.getDirection() != 0) {
                this.attribute("direction", poseResource.pose.getDirection());
            }
            if (poseResource.pose.getOffsetX() != poseResource.pose.getSurface().getWidth() / 2) {
                this.attribute("offsetX", poseResource.pose.getOffsetX());
            }
            if (poseResource.pose.getOffsetY() != poseResource.pose.getSurface().getHeight() / 2) {
                this.attribute("offsetY", poseResource.pose.getOffsetY());
            }
            this.endTag("pose");
        }

        this.endTag("poses");
    }

    private void writeSounds() throws XMLException
    {
        this.beginTag("sounds");

        for (String name : this.resources.soundNames()) {
            SoundResource soundResource = this.resources.getSoundResource(name);

            this.beginTag("sound");
            this.attribute("name", name);
            this.attribute("filename", soundResource.getFilename());
            this.endTag("sound");
        }

        this.endTag("sounds");
    }

    private void writeAnimations() throws XMLException
    {
        this.beginTag("animations");

        for (String name : this.resources.animationNames()) {
            Animation animation = this.resources.getAnimation(name);

            this.beginTag("animation");
            this.attribute("name", name);

            this.writeAnimation(animation);

            this.endTag("animation");
        }

        this.endTag("animations");
    }

    private String getAnimationTagName( Animation animation )
        throws XMLException
    {
        return animation.getTagName();
    }

    private <S extends PropertySubject<S>> void writeProperties( S subject )
        throws XMLException
    {
        for (AbstractProperty<S, ?> property : subject.getProperties()) {

            try {
                String value = property.getStringValue(subject);
                if (!StringUtils.isBlank(value)) {
                    this.attribute(property.key, value);
                }

            } catch (Exception e) {
                throw new XMLException("Failed to write property : " + property.key);
            }

        }
    }

    private void writeAnimation( Animation animation ) throws XMLException
    {
        String tagName = getAnimationTagName(animation);
        this.beginTag(tagName);

        writeProperties(animation);

        if (animation instanceof FramedAnimation) {
            this.writeFrames(((FramedAnimation) animation).getFrames());
        } else if (animation instanceof CompoundAnimation) {
            this.writeAnimations((CompoundAnimation) animation);
        }

        this.endTag(tagName);

    }

    private void writeFrames( List<Frame> frames ) throws XMLException
    {
        for (Frame frame : frames) {
            this.beginTag("frame");
            this.attribute("pose", this.resources.getPoseName(frame.getPose()));
            this.attribute("delay", frame.getDelay());
            this.endTag("frame");
        }
    }

    private void writeAnimations( CompoundAnimation parent ) throws XMLException
    {
        for (Animation child : parent.children) {
            this.writeAnimation(child);
        }
    }

    private void writeCostumes() throws XMLException
    {
        this.beginTag("costumes");

        for (String name : this.resources.costumeNames()) {

            this.writeCostume(name);
        }

        this.endTag("costumes");

    }

    private void writeCostume( String name ) throws XMLException
    {
        Costume costume = this.resources.getCostume(name);
        String baseName = null;

        Costume extendedCostume = costume.getExtendedFrom();
        if (extendedCostume != null) {

            baseName = this.resources.getCostumeName(extendedCostume);
            if (baseName == null) {
                throw new XMLException("Costume not found : " + extendedCostume);
            }

            if (!this.writtenCostumeName.contains(baseName)) {
                this.writeCostume(baseName);
            }

            this.writeCostume(costume, name, baseName);

        } else {
            this.writeCostume(costume, name, null);
        }
    }

    private void writeCostume( Costume simpleCostume, String name, String baseName )
        throws XMLException
    {
        if (this.writtenCostumeName.contains(name)) {
            return;
        }

        this.beginTag("costume");
        this.attribute("name", name);
        this.attribute("defaultZOrder", simpleCostume.defaultZOrder);

        if (baseName != null) {
            this.attribute("extends", baseName);
        }
        if (!NullRole.class.getName().equals(simpleCostume.roleClassName.name)) {
            this.attribute("role", simpleCostume.roleClassName.name);
        }

        if (CostumeProperties.class != simpleCostume.getProperties().getClass()) {
            this.attribute("properties", simpleCostume.getPropertiesClassName().name);
        }

        this.writeCostumePoses(simpleCostume);
        this.writeCostumeStrings(simpleCostume);
        this.writeCostumeSounds(simpleCostume);
        this.writeCostumeFonts(simpleCostume);
        this.writeCostumeAnimations(simpleCostume);
        this.writeCostumeProperties(simpleCostume);

        this.endTag("costume");

        this.writtenCostumeName.add(name);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void writeCostumeProperties( Costume costume ) throws XMLException
    {
        Object properties = costume.getProperties();

        if (properties.getClass() == CostumeProperties.class) {
            return;
        }

        this.beginTag("properties");
        writeProperties((PropertySubject) properties);
        this.endTag("properties");

    }

    private void writeCostumePoses( Costume costume ) throws XMLException
    {

        for (String name : costume.getPoseNames()) {

            for (PoseResource poseResource : costume.getPoseChoices(name)) {

                // Ignore poses which were GENERATED rather than loaded.
                // For example Fragment, generates poses, which are added to a costume, but should
                // be
                // ignore when saving the resources.
                if (!poseResource.isAnonymous()) {

                    this.beginTag("pose");
                    this.attribute("name", name);

                    this.attribute("pose", poseResource.name);

                    this.endTag("pose");
                }
            }
        }
    }

    private void writeCostumeStrings( Costume costume ) throws XMLException
    {

        for (String name : costume.getStringNames()) {

            for (String str : costume.getStringChoices(name)) {

                this.beginTag("string");
                this.attribute("name", name);
                this.body(str);
                this.endTag("string");

            }
        }
    }

    private void writeCostumeSounds( Costume costume ) throws XMLException
    {

        for (String name : costume.getSoundNames()) {

            for (ManagedSound cs : costume.getSoundChoices(name)) {
                this.beginTag("sound");
                this.attribute("name", name);

                this.attribute("sound", cs.soundResource.name);
                writeProperties(cs);

                this.endTag("sound");
            }

        }
    }

    private void writeCostumeFonts( Costume costume ) throws XMLException
    {

        for (String name : costume.getFontNames()) {

            for (FontResource fontResource : costume.getFontChoices(name)) {
                this.beginTag("font");
                this.attribute("name", name);

                this.attribute("font", fontResource.name);

                this.endTag("font");
            }
        }

    }

    private void writeCostumeAnimations( Costume costume ) throws XMLException
    {

        for (String name : costume.getAnimationNames()) {

            for (AnimationResource animationResource : costume.getAnimationChoices(name)) {
                this.beginTag("animation");
                this.attribute("name", name);

                this.attribute("animation", animationResource.name);

                this.endTag("animation");
            }
        }

    }

    private void writeScenes() throws XMLException
    {
        this.beginTag("scenes");

        for (String name : this.resources.sceneNames()) {
            // SceneResource sceneResource = this.resources.getSceneResource(name);

            this.beginTag("scene");
            this.attribute("name", name);
            this.endTag("scene");
        }

        this.endTag("scenes");
    }

}
