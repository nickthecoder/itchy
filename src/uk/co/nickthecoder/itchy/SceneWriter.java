/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.itchy.util.XMLException;
import uk.co.nickthecoder.itchy.util.XMLWriter;
import uk.co.nickthecoder.jame.RGBA;

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
            throw new XMLException("Failed to get scene");
        }

        this.beginTag("scene");
        this.attribute("showMouse", this.scene.showMouse);


        for ( Scene.SceneLayer sceneLayer : this.scene.getSceneLayers() ) {
            
            if ( !sceneLayer.isEmpty() ) {
                this.beginTag("layer");
                this.attribute("name",sceneLayer.name);
                
                this.writeActors(sceneLayer);
    
                this.endTag("layer");
            }
        }            


        this.endTag("scene");
    }

    private void writeActors(Scene.SceneLayer sceneLayer) throws XMLException
    {
        for (SceneActor sceneActor : sceneLayer.getSceneActors()) {

            if (sceneActor instanceof CostumeSceneActor) {

                CostumeSceneActor csa = (CostumeSceneActor) sceneActor;
                this.beginTag("actor");
                this.attribute("costume", this.sceneResource.resources.getCostumeName(csa.costume));

                if ((csa.costume.behaviourClassName == null) ||
                        (!csa.costume.behaviourClassName.equals(csa.behaviourClassName))) {
                    this.attribute("behaviour", sceneActor.behaviourClassName);
                }
                this.writeSceneActorAttributes(sceneActor);

                this.endTag("actor");

            } else if (sceneActor instanceof TextSceneActor) {
                TextSceneActor tsa = (TextSceneActor) sceneActor;
                this.beginTag("text");
                this.attribute("text", tsa.text);
                this.attribute("font", this.sceneResource.resources.getFontName(tsa.font));
                this.attribute("size", tsa.fontSize);
                this.attribute("color", tsa.color.getRGBCode());
                if (!NullBehaviour.class.getName().equals(sceneActor.behaviourClassName)) {
                    this.attribute("behaviour", sceneActor.behaviourClassName);
                }
                this.writeSceneActorAttributes(sceneActor);

                this.endTag("text");
            }

        }

        // this.endTag( "actors" );
    }

    private void writeSceneActorAttributes( SceneActor sceneActor ) throws XMLException
    {
        this.attribute("x", sceneActor.x);
        this.attribute("y", sceneActor.y);
        this.attribute("direction", sceneActor.direction);
        this.attribute("startEvent", sceneActor.startEvent);

        if (sceneActor.colorize != null) {
            this.attribute("colorize", sceneActor.colorize.getRGBACode());
        }

        if (sceneActor.scale != 1) {
            this.attribute("scale", sceneActor.scale);
        }

        if (sceneActor.activationDelay != 0) {
            this.attribute("activationDelay", sceneActor.activationDelay);
        }

        for (String key : sceneActor.customProperties.keySet()) {
            if( key.equals("shootable") ) {
                System.out.println("Saving shootable");
            }
            Object value = sceneActor.customProperties.get(key);

            String stringValue = this.getPropertyValue(value);
            if (stringValue != null) {
                this.beginTag("property");
                this.attribute("name", key);
                this.attribute("value", stringValue);
                this.endTag("property");
            }
        }

    }

    private String getPropertyValue( Object value )
    {
        if ((value instanceof String) || (value instanceof Boolean)  || (value instanceof Double) || (value instanceof Integer)) {
            return String.valueOf(value);

        } else if (value instanceof RGBA) {
            return ((RGBA) value).getRGBACode();

        } else if (value instanceof Font) {

            Font font = (Font) value;
            String fontName = this.sceneResource.resources.getFontName(font);
            return fontName;
        }

        return null;
    }
}
