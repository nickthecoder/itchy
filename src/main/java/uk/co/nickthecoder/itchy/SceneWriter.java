/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.io.File;

import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.PropertySubject;
import uk.co.nickthecoder.itchy.role.PlainRole;
import uk.co.nickthecoder.itchy.util.XMLException;
import uk.co.nickthecoder.itchy.util.XMLWriter;

public class SceneWriter extends XMLWriter
{
    private Resources resources;

    private Scene scene;

    public SceneWriter(Resources resources, Scene scene)
    {
        this.resources = resources;
        this.scene = scene;
    }

    public void write(File file) throws Exception
    {

        this.begin(file);

        try {

            this.writeScene();

        } finally {
            this.end();
        }
    }

    private void writeScene() throws XMLException
    {
        this.beginTag("scene");
        this.attribute("showMouse", this.scene.showMouse);
        this.attribute("layout", this.scene.layout.name);
        if (!PlainSceneDirector.class.getName().equals(this.scene.getSceneDirectorClassName())) {
            this.attribute("role", this.scene.getSceneDirectorClassName().name);
        }

        writeSceneDirectorProperties();

        //for (Scene.SceneLayer sceneLayer : this.scene.getSceneLayers()) {

        for (Layer layer : scene.layout.getLayersByZOrder()) {
            Scene.SceneLayer sceneLayer = scene.findSceneLayer(layer.name);
            
            this.beginTag("layer");
            this.attribute("name", layer.name);

            this.writeLayerProperties(layer);

            if (sceneLayer != null ) {
                this.writeActors(sceneLayer);
            }

            this.endTag("layer");
        }

        this.endTag("scene");
    }

    private void writeLayerProperties(Layer actualLayer)
        throws XMLException
    {
        Layout templateLayout = this.resources.getLayout(this.scene.layout.name);
        if (templateLayout == null) {
            return;
        }
        Layer templateLayer = templateLayout.findLayer(actualLayer.name);

        if ((actualLayer == null) || (templateLayer == null)) {
            return;
        }

        View view = actualLayer.getView();
        View templateView = templateLayer.getView();
        if ((view != null) && (view.getProperties().size() > 0)) {
            this.beginTag("view");
            this.writeChangedProperties(view, templateView);
            this.endTag("view");
        }
        
        if (view instanceof StageView) {
            
            StageView stageView = (StageView) view;
            StageView templateStageView = (StageView) templateView;
            
            Stage stage = stageView.getStage();
            Stage templateStage = templateStageView.getStage();
            
            if ((stage != null) && (stage.getProperties().size() > 0)) {
                this.beginTag("stage");
                this.writeChangedProperties(stage, templateStage);
                this.endTag("stage");
            }
            if ((stage != null) && (stage.getStageConstraint().getProperties().size() > 0)) {
                this.beginTag("stageConstraint");
                this.writeChangedProperties(stage.getStageConstraint(), templateStage.getStageConstraint());
                this.endTag("stageConstraint");
            }
        }
    }

    private <S extends PropertySubject<S>> void writeChangedProperties(S subject, S template)
        throws XMLException
    {
        for (Property<S, ?> property : subject.getProperties()) {

            try {
                String value = property.getStringValue(subject);
                String value2 = property.getStringValue(template);
                if (!value.equals(value2)) {
                    this.attribute(property.key, value);
                }

            } catch (Exception e) {
                e.printStackTrace();
                throw new XMLException("Failed to write property : " + property.key);
            }

        }
    }

    private void writeSceneDirectorProperties()
        throws XMLException
    {
        this.beginTag("properties");
        for (Property<SceneDirector, ?> property : this.scene.getSceneDirector().getProperties()) {
            try {
                this.beginTag("property");
                this.attribute("name", property.key);
                this.attribute("value", property.getStringValue(this.scene.getSceneDirector()));
                this.endTag("property");
            } catch (Exception e) {
                throw new XMLException("Failed to write sceneDirector property : " + property.key);
            }
        }

        this.endTag("properties");
    }

    private void writeActors(Scene.SceneLayer sceneLayer) throws XMLException
    {
        for (SceneActor sceneActor : sceneLayer.getSceneActors()) {

            if (sceneActor instanceof CostumeSceneActor) {

                CostumeSceneActor csa = (CostumeSceneActor) sceneActor;
                this.beginTag("actor");
                this.attribute("costume", this.resources.getCostumeName(csa.costume));

                if ((csa.costume.roleClassName == null) ||
                    (!csa.costume.roleClassName.name.equals(csa.roleClassName.name))) {
                    this.attribute("role", sceneActor.roleClassName.name);
                }
                this.writeSceneActorAttributes(sceneActor);

                this.writeMakeupAttributes(sceneActor);

                this.endTag("actor");

            } else if (sceneActor instanceof TextSceneActor) {
                TextSceneActor tsa = (TextSceneActor) sceneActor;
                this.beginTag("text");
                this.attribute("text", tsa.text);
                this.attribute("font", tsa.font.getName());
                this.attribute("size", tsa.fontSize);
                this.attribute("color", tsa.color.getRGBCode());
                this.attribute("xAlignment", tsa.xAlignment);
                this.attribute("yAlignment", tsa.yAlignment);
                if (!PlainRole.class.getName().equals(sceneActor.roleClassName.name)) {
                    this.attribute("role", sceneActor.roleClassName.name);
                }
                if (tsa.costume != null) {
                    this.attribute("costume", this.resources.getCostumeName(tsa.costume));
                }

                this.writeSceneActorAttributes(sceneActor);

                this.writeMakeupAttributes(sceneActor);

                this.endTag("text");
            }

        }

        // this.endTag( "actors" );
    }

    private void writeSceneActorAttributes(SceneActor sceneActor) throws XMLException
    {
        if (sceneActor.id != null) {
            this.attribute("id", sceneActor.id);
        }
        this.attribute("x", sceneActor.x);
        this.attribute("y", sceneActor.y);
        this.attribute("direction", sceneActor.direction);
        this.attribute("heading", sceneActor.heading);
        this.attribute("startEvent", sceneActor.startEvent);
        this.attribute("zOrder", sceneActor.zOrder);

        if (sceneActor.alpha != 255) {
            this.attribute("alpha", sceneActor.alpha);
        }

        if (sceneActor.colorize != null) {
            this.attribute("colorize", sceneActor.colorize.getRGBACode());
        }

        if (sceneActor.scale != 1) {
            this.attribute("scale", sceneActor.scale);
        }

        if (sceneActor.activationDelay != 0) {
            this.attribute("activationDelay", sceneActor.activationDelay);
        }

        for (String key : sceneActor.customPropertyStrings.keySet()) {
            String value = sceneActor.customPropertyStrings.get(key);

            if (value != null) {
                this.beginTag("property");
                this.attribute("name", key);
                this.attribute("value", value);
                this.endTag("property");
            }
        }

    }

    private void writeMakeupAttributes(SceneActor sceneActor) throws XMLException
    {
        if (sceneActor.makeupClassName.name.equals(NullMakeup.class.getName())) {
            return;
        }
        this.beginTag("makeup");
        this.attribute("classname", sceneActor.makeupClassName.name);

        for (String key : sceneActor.makeupPropertyStrings.keySet()) {
            String value = sceneActor.makeupPropertyStrings.get(key);

            if (value != null) {
                this.beginTag("property");
                this.attribute("name", key);
                this.attribute("value", value);
                this.endTag("property");
            }
        }
        this.endTag("makeup");
    }

}
