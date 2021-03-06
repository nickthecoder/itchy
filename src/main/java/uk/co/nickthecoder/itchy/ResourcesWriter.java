/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.co.nickthecoder.itchy.animation.Animation;
import uk.co.nickthecoder.itchy.animation.CompoundAnimation;
import uk.co.nickthecoder.itchy.animation.Frame;
import uk.co.nickthecoder.itchy.animation.FramedAnimation;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.PropertySubject;
import uk.co.nickthecoder.itchy.role.PlainRole;
import uk.co.nickthecoder.itchy.util.NinePatch;
import uk.co.nickthecoder.itchy.util.StringUtils;
import uk.co.nickthecoder.itchy.util.XMLException;
import uk.co.nickthecoder.itchy.util.XMLWriter;

public class ResourcesWriter extends XMLWriter
{
    private final Resources resources;

    private Set<String> writtenCostumeName;

    public ResourcesWriter(Resources resources)
    {
        this.resources = resources;
    }

    public void write(String filename) throws Exception
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
        this.writeSpriteSheets();
        this.writePoses();
        this.writeNinePatches();
        this.writeFonts();
        this.writeSounds();
        this.writeAnimations();
        this.writeCostumes();
        this.writeScenes();
        this.writeInputs();
        this.writeLayouts();

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
            Font font = this.resources.getFont(name);

            this.beginTag("font");
            this.writeProperties(font);
            this.endTag("font");
        }

        this.endTag("fonts");
    }

    private void writeNinePatches() throws XMLException
    {
        this.beginTag("ninePatches");

        for (String name : this.resources.ninePatchNames()) {
            NinePatch ninePatch = this.resources.getNinePatch(name);

            this.beginTag("ninePatch");

            this.writeProperties(ninePatch);

            this.endTag("ninePatch");
        }

        this.endTag("ninePatches");
    }

    private void writeSpriteSheets() throws XMLException
    {
        this.beginTag("spriteSheets");

        for (String name : this.resources.spriteSheetNames()) {
            SpriteSheet spriteSheet = this.resources.getSpriteSheet(name);
            this.beginTag("spriteSheet");
            this.writeProperties(spriteSheet);

            List<Sprite> sprites = new ArrayList<Sprite>();
            sprites.addAll(spriteSheet.getSprites());
            Collections.sort(sprites);

            for (Sprite sprite : sprites) {
                this.beginTag("sprite");
                this.writeProperties(sprite);
                this.endTag("sprite");
            }

            this.endTag("spriteSheet");
        }

        this.endTag("spriteSheets");
    }

    private void writePoses() throws XMLException
    {
        this.beginTag("poses");

        for (String name : this.resources.poseNames()) {
            PoseResource poseResource = this.resources.getPoseResource(name);
            if (poseResource instanceof FilePoseResource) {
                FilePoseResource filePoseResource = (FilePoseResource) poseResource;
                this.beginTag("pose");
                this.attribute("name", name);
                this.attribute("filename", filePoseResource.getFilename());
                if (filePoseResource.pose.getDirection() != 0) {
                    this.attribute("direction", filePoseResource.pose.getDirection());
                }
                if (filePoseResource.pose.getOffsetX() != filePoseResource.pose.getSurface().getWidth() / 2) {
                    this.attribute("offsetX", filePoseResource.pose.getOffsetX());
                }
                if (filePoseResource.pose.getOffsetY() != filePoseResource.pose.getSurface().getHeight() / 2) {
                    this.attribute("offsetY", filePoseResource.pose.getOffsetY());
                }
                this.endTag("pose");
            }
        }

        this.endTag("poses");
    }

    private void writeSounds() throws XMLException
    {
        this.beginTag("sounds");

        for (String name : this.resources.soundNames()) {
            SoundResource soundResource = this.resources.getSound(name);

            this.beginTag("sound");
            this.writeProperties(soundResource);
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

    private String getAnimationTagName(Animation animation)
        throws XMLException
    {
        return animation.getTagName();
    }

    private <S extends PropertySubject<S>> void writeProperties(S subject)
        throws XMLException
    {
        for (Property<S, ?> property : subject.getProperties()) {

            try {
                String value = property.getStringValue(subject);
                if (!StringUtils.isBlank(value)) {
                    this.attribute(property.key, value);
                }

            } catch (Exception e) {
                e.printStackTrace();
                throw new XMLException("Failed to write property : " + property.key);
            }

        }
    }

    private void writeAnimation(Animation animation) throws XMLException
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

    private void writeFrames(List<Frame> frames) throws XMLException
    {
        for (Frame frame : frames) {
            this.beginTag("frame");
            this.attribute("pose", this.resources.getPoseName(frame.getPose()));
            this.attribute("delay", frame.getDelay());
            this.attribute("dx", frame.dx);
            this.attribute("dy", frame.dy);
            this.endTag("frame");
        }
    }

    private void writeAnimations(CompoundAnimation parent) throws XMLException
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

    private void writeCostume(String name) throws XMLException
    {
        Costume costume = this.resources.getCostume(name);
        String baseName = null;

        Costume extendedCostume = costume.getExtendedFrom();
        if (extendedCostume != null) {

            baseName = this.resources.getCostumeName(extendedCostume);
            if (baseName == null) {
                throw new XMLException("Extended Costume not found : " + baseName);
            }

            if (!this.writtenCostumeName.contains(baseName)) {
                this.writeCostume(baseName);
            }

            this.writeCostume(costume, name, baseName);

        } else {
            this.writeCostume(costume, name, null);
        }
    }

    private void writeCostume(Costume costume, String name, String baseName)
        throws XMLException
    {

        if (this.writtenCostumeName.contains(name)) {
            return;
        }

        this.beginTag("costume");
        this.attribute("name", name);
        this.attribute("defaultZOrder", costume.defaultZOrder);
        this.attribute("order", costume.getOrder());
        this.attribute("showInDesigner", costume.showInDesigner);

        if (baseName != null) {
            this.attribute("extends", baseName);
        }
        if (!PlainRole.class.getName().equals(costume.roleClassName.name)) {
            this.attribute("role", costume.roleClassName.name);
        }

        this.writeCostumePoses(costume);
        this.writeCostumeStrings(costume);
        this.writeCostumeSounds(costume);
        this.writeCostumeFonts(costume);
        this.writeCostumeAnimations(costume);
        this.writeCostumeCompanions(costume);
        this.writeCostumeProperties(costume);

        this.endTag("costume");

        this.writtenCostumeName.add(name);
    }

    private void writeCostumeProperties(Costume costume) throws XMLException
    {
        CostumeFeatures cp = costume.getCostumeFeatures();
        if (cp.getProperties().size() > 0) {
            this.beginTag("properties");
            writeProperties(cp);
            this.endTag("properties");
        }
    }

    private void writeCostumePoses(Costume costume) throws XMLException
    {

        for (String name : costume.getPoseNames()) {

            for (PoseResource poseResource : costume.getPoseChoices(name)) {

                // Ignore poses which were GENERATED rather than loaded.
                // For example Fragment, generates poses, which are added to a costume, but should
                // be ignore when saving the resources.
                if (resources.getPoseResource(poseResource.getName()) != null) {

                    this.beginTag("pose");
                    this.attribute("name", name);

                    this.attribute("pose", poseResource.name);

                    this.endTag("pose");
                }
            }
        }
    }

    private void writeCostumeStrings(Costume costume) throws XMLException
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

    private void writeCostumeSounds(Costume costume) throws XMLException
    {

        for (String name : costume.getSoundNames()) {

            for (ManagedSound cs : costume.getSoundChoices(name)) {
                this.beginTag("sound");
                this.attribute("name", name);

                this.attribute("sound", cs.soundResource.getName());
                writeProperties(cs);

                this.endTag("sound");
            }

        }
    }

    private void writeCostumeFonts(Costume costume) throws XMLException
    {

        for (String name : costume.getTextStyleNames()) {

            for (TextStyle textStyle : costume.getTextStyleChoices(name)) {
                this.beginTag("font");
                this.attribute("name", name);

                Font font = textStyle.font;
                this.attribute("font", font.getName());
                this.writeProperties(textStyle);

                this.endTag("font");
            }
        }

    }

    private void writeCostumeAnimations(Costume costume) throws XMLException
    {

        for (String name : costume.getAnimationNames()) {

            for (AnimationResource animationResource : costume.getAnimationChoices(name)) {
                this.beginTag("animation");
                this.attribute("name", name);
                this.attribute("animation", animationResource.getName());
                this.endTag("animation");
            }
        }

    }

    private void writeCostumeCompanions(Costume costume) throws XMLException
    {
        for (String name : costume.getCompanionNames()) {
            for (Costume companion : costume.getCompanionChoices(name)) {
                this.beginTag("companion");
                this.attribute("name", name);
                this.attribute("companion", companion.getName());
                this.endTag("companion");
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

    private void writeInputs() throws XMLException
    {
        this.beginTag("inputs");

        for (String name : this.resources.inputNames()) {
            Input input = this.resources.getInput(name);

            this.beginTag("input");
            this.writeProperties(input);
            this.endTag("input");
        }

        this.endTag("inputs");
    }

    private void writeLayouts() throws XMLException
    {
        this.beginTag("layouts");

        for (String name : this.resources.layoutNames()) {
            Layout layout = this.resources.getLayout(name);

            this.beginTag("layout");
            this.writeProperties(layout);

            for (Layer layer : layout.getLayersByZOrder()) {
                this.beginTag("layer");

                this.writeProperties(layer);

                View view = layer.getView();
                if ((view != null) && (view.getProperties().size() > 0)) {
                    this.beginTag("view");
                    this.writeProperties(view);
                    this.endTag("view");
                }
                if (view instanceof StageView) {
                    StageView stageView = (StageView) view;
                    Stage stage = stageView.getStage();
                    if ((stage != null) && (stage.getProperties().size() > 0)) {
                        this.beginTag("stage");
                        this.writeProperties(stage);
                        this.endTag("stage");
                    }
                    if ((stage != null) && (stage.getStageConstraint().getProperties().size() > 0)) {
                        this.beginTag("stageConstraint");
                        this.writeProperties(stage.getStageConstraint());
                        this.endTag("stageConstraint");
                    }
                }

                this.endTag("layer");
            }
            this.endTag("layout");
        }

        this.endTag("layouts");
    }

}
