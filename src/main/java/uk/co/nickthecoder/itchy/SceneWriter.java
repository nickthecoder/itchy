/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.itchy.role.PlainRole;
import uk.co.nickthecoder.itchy.util.XMLException;
import uk.co.nickthecoder.itchy.util.XMLWriter;

public class SceneWriter extends XMLWriter
{
    private final SceneResource sceneResource;
    private Scene scene;

    public SceneWriter( SceneResource sceneResource )
    {
        this.sceneResource = sceneResource;
    }

    public void write( String filename ) throws Exception
    {

        this.begin(filename);

        try {

            this.writeScene();

        } finally {
            this.end();
        }
    }

    private void writeScene() throws XMLException
    {
        try {
            this.scene = this.sceneResource.getScene();
        } catch (Exception e) {
            throw new XMLException("Failed to get the scene");
        }
        this.beginTag("scene");
        this.attribute("showMouse", this.scene.showMouse);
        if (!PlainSceneDirector.class.getName().equals(this.scene.sceneDirectorClassName)) {
            this.attribute("role", this.scene.sceneDirectorClassName.name);
        }
        this.attribute("background", this.scene.backgroundColor.toString());

        writeSceneDirectorProperties();

        for (Scene.SceneLayer sceneLayer : this.scene.getSceneLayers()) {

            if (!sceneLayer.isEmpty()) {
                this.beginTag("layer");
                this.attribute("name", sceneLayer.name);

                this.writeActors(sceneLayer);

                this.endTag("layer");
            }
        }

        this.endTag("scene");
    }

    private void writeSceneDirectorProperties()
        throws XMLException
    {

        this.beginTag("properties");
        for (AbstractProperty<SceneDirector, ?> property : this.scene.sceneDirector.getProperties()) {
            try {
                this.beginTag("property");
                this.attribute("name", property.key);
                this.attribute("value", property.getStringValue(this.scene.sceneDirector));
                this.endTag("property");
            } catch (Exception e) {
                throw new XMLException("Failed to write sceneDirector property : " + property.key);
            }
        }

        this.endTag("properties");
    }

    private void writeActors( Scene.SceneLayer sceneLayer ) throws XMLException
    {
        for (SceneActor sceneActor : sceneLayer.getSceneActors()) {

            if (sceneActor instanceof CostumeSceneActor) {

                CostumeSceneActor csa = (CostumeSceneActor) sceneActor;
                this.beginTag("actor");
                this.attribute("costume", this.sceneResource.resources.getCostumeName(csa.costume));

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
                this.attribute("font", this.sceneResource.resources.getFontName(tsa.font));
                this.attribute("size", tsa.fontSize);
                this.attribute("color", tsa.color.getRGBCode());
                this.attribute("xAlignment", tsa.xAlignment);
                this.attribute("yAlignment", tsa.yAlignment);
                if (!PlainRole.class.getName().equals(sceneActor.roleClassName.name)) {
                    this.attribute("role", sceneActor.roleClassName.name);
                }
                if (tsa.costume != null) {
                    this.attribute("costume", this.sceneResource.resources.getCostumeName(tsa.costume));
                }

                this.writeSceneActorAttributes(sceneActor);

                this.writeMakeupAttributes(sceneActor);

                this.endTag("text");
            }

        }

        // this.endTag( "actors" );
    }

    private void writeSceneActorAttributes( SceneActor sceneActor ) throws XMLException
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

    private void writeMakeupAttributes( SceneActor sceneActor ) throws XMLException
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