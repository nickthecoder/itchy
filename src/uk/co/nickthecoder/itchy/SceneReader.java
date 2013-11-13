/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

import uk.co.nickthecoder.itchy.editor.SceneDesignerBehaviour;
import uk.co.nickthecoder.itchy.util.AbstractProperty;
import uk.co.nickthecoder.itchy.util.ClassName;
import uk.co.nickthecoder.itchy.util.XMLException;
import uk.co.nickthecoder.itchy.util.XMLTag;
import uk.co.nickthecoder.jame.RGBA;

public class SceneReader
{
    private final Resources resources;

    private Scene scene;

    public SceneReader( Resources resources )
    {
        this.resources = resources;
    }

    public Scene load( String filename ) throws Exception
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(
            filename)));
        this.scene = new Scene();
        try {
            XMLTag document = XMLTag.openDocument(reader);
            this.readScene(document.getTag("scene"));

        } finally {
            reader.close();
        }
        return this.scene;
    }

    private void readScene( XMLTag sceneTag ) throws Exception
    {
        this.scene.showMouse = sceneTag.getOptionalBooleanAttribute("showMouse", true);
        String background = sceneTag.getOptionalAttribute("background",  "#000");
        try {
            this.scene.backgroundColor = RGBA.parse(background);
        } catch (Exception e) {
            throw new XMLException("Illegal colour : " + background);
        }
        
        this.scene.sceneBehaviourClassName = new ClassName(sceneTag.getOptionalAttribute("behaviour",
            PlainSceneBehaviour.class.getName()));

        // For old versions without multiple layers, the actor tags are directly within the scene
        // tag.
        readLayer(sceneTag, this.scene.getDefaultSceneLayer());

        this.scene.sceneBehaviour = this.scene.createSceneBehaviour(this.resources);
        
        for (Iterator<XMLTag> i = sceneTag.getTags("properties"); i.hasNext();) {
            XMLTag propertiesTag = i.next();
            this.readProperties(propertiesTag);
        }
        
        
        // For new versions, the scene tag has a set of layer tags, and the layer tags have the
        // actors.
        for (Iterator<XMLTag> i = sceneTag.getTags("layer"); i.hasNext();) {
            XMLTag layerTag = i.next();
            String name = layerTag.getAttribute("name");
            Scene.SceneLayer sceneLayer = this.scene.createSceneLayer(name);
            this.readLayer(layerTag, sceneLayer);
        }

    }

    private void readProperties( XMLTag propertiesTag )
        throws Exception
    {
        List<AbstractProperty<SceneBehaviour,?>> properties = this.scene.sceneBehaviour.getProperties();
        
        for (Iterator<XMLTag> i = propertiesTag.getTags("property"); i.hasNext();) {
            XMLTag propertyTag = i.next();
            String name = propertyTag.getAttribute("name");
            String value = propertyTag.getAttribute("value");
            
            AbstractProperty<SceneBehaviour,?> property = findProperty( properties, name );
            if ( property == null) {
                throw new Exception( "Didn't find SceneBehaviour property : " + name );
            }
            property.setValueByString(this.scene.sceneBehaviour, value);
        }
        
    }
    
    private AbstractProperty<SceneBehaviour,?> findProperty(List<AbstractProperty<SceneBehaviour,?>> properties, String name )
    {
        for (AbstractProperty<SceneBehaviour,?> property : properties) {
            if ( property.key.equals( name ) ) {
                return property;
            }
        }
        return null;
    }

    
    private void readLayer( XMLTag parentTag, Scene.SceneLayer sceneLayer ) throws Exception
    {
        for (Iterator<XMLTag> i = parentTag.getTags(); i.hasNext();) {
            XMLTag tag = i.next();
            if (tag.getName() == "actor") {
                this.readActor(tag, sceneLayer);
            } else if (tag.getName() == "text") {
                this.readText(tag, sceneLayer);
            }
        }
    }

    private void readActor( XMLTag actorTag, Scene.SceneLayer sceneLayer ) throws Exception
    {
        String costumeName = actorTag.getAttribute("costume");
        Costume costume = this.resources.getCostume(costumeName);
        if (costume == null) {
            throw new XMLException("Costume not found : " + costumeName);
        }

        CostumeSceneActor sceneActor = new CostumeSceneActor(costume);
        sceneActor.behaviourClassName = costume.behaviourClassName;
        this.readSceneActorAttributes(actorTag, sceneActor);

        sceneLayer.add(sceneActor);
    }

    private void readText( XMLTag textTag, Scene.SceneLayer sceneLayer ) throws Exception
    {
        String fontName = textTag.getAttribute("font");
        int fontSize = textTag.getIntAttribute("size");
        String text = textTag.getAttribute("text");
        Font font = this.resources.getFont(fontName);

        if (font == null) {
            throw new XMLException("Font not found : " + fontName);
        }

        TextSceneActor sceneActor = new TextSceneActor(font, fontSize, text);
        sceneActor.behaviourClassName = new ClassName(NullBehaviour.class.getName());

        String costumeName = textTag.getOptionalAttribute("costume", null);
        if (costumeName != null) {
            Costume costume = this.resources.getCostume(costumeName);
            if (costume == null) {
                throw new XMLException("Costume not found : " + costumeName);
            }
            sceneActor.costume = costume;
        }

        String colorString = textTag.getOptionalAttribute("color", "#ffffff");
        try {
            sceneActor.color = RGBA.parse(colorString);
        } catch (Exception e) {
            throw new XMLException("Illegal color : " + colorString);
        }
        double xAlignment = textTag.getOptionalDoubleAttribute("xAlignment", 0.5);
        double yAlignment = textTag.getOptionalDoubleAttribute("yAlignment", 0.5);
        sceneActor.xAlignment = xAlignment;
        sceneActor.yAlignment = yAlignment;

        this.readSceneActorAttributes(textTag, sceneActor);

        sceneLayer.add(sceneActor);
    }

    /**
     * For backwards compatibility, if the zOrder isn't specified, then the default zOrder
     * increases by 1 for each actor read. Therefore, the actors will have increasing zOrders.
     */
    private int defaultZOrder = 0;
    
    private void readSceneActorAttributes( XMLTag actorTag, SceneActor sceneActor )
        throws Exception
    {
        defaultZOrder += 1;
        
        sceneActor.x = actorTag.getIntAttribute("x");
        sceneActor.y = actorTag.getIntAttribute("y");
        sceneActor.zOrder = actorTag.getOptionalIntAttribute("zOrder", defaultZOrder);
        sceneActor.alpha = actorTag.getOptionalDoubleAttribute("alpha", 255);
        sceneActor.direction = actorTag.getOptionalDoubleAttribute("direction", 0);
        sceneActor.heading = actorTag.getOptionalDoubleAttribute("heading", sceneActor.direction);
        sceneActor.scale = actorTag.getOptionalDoubleAttribute("scale", 1);
        sceneActor.activationDelay = actorTag.getOptionalDoubleAttribute("activationDelay", 0);
        sceneActor.startEvent = actorTag.getOptionalAttribute("startEvent", "default");

        if (actorTag.hasAttribute("colorize")) {
            try {
                sceneActor.colorize = RGBA.parse(actorTag.getAttribute("colorize"));
            } catch (Exception e) {
                throw new XMLException("Illegal color : " + actorTag.getAttribute("colorize"));
            }
        }

        if (actorTag.hasAttribute("behaviour")) {
            ClassName className = new ClassName(actorTag.getAttribute("behaviour"));

            if (this.resources.registerBehaviourClassName(className.name)) {
                sceneActor.behaviourClassName = className;
            }
        }

        Actor actor = sceneActor.createActor(this.resources, true);

        for (Iterator<XMLTag> i = actorTag.getTags("property"); i.hasNext();) {
            XMLTag tag = i.next();

            String name = tag.getAttribute("name");
            String value = tag.getAttribute("value");
            setProperty(sceneActor, actor, name, value);
        }
    }

    private void setProperty( SceneActor sceneActor, Actor actor, String name, String value )
        throws Exception
    {
        SceneDesignerBehaviour sdb = (SceneDesignerBehaviour) actor.getBehaviour();

        for (AbstractProperty<Behaviour, ?> property : sdb.actualBehaviour.getProperties()) {
            if (property.key.equals(name)) {
                sceneActor.customProperties.put(property.key, property.parse(value));
                return;
            }
        }

        for (AbstractProperty<Behaviour, ?> property : sdb.actualBehaviour.getProperties()) {
            for (String alias : property.aliases) {
                if (alias.equals(name)) {
                    sceneActor.customProperties.put(property.key, property.parse(value));
                    return;
                }
            }
        }
        System.err.println("Ignoring unknown property : " + name);
    }

}
