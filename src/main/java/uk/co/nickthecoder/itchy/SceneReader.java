/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

import uk.co.nickthecoder.itchy.makeup.Makeup;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.role.PlainRole;
import uk.co.nickthecoder.itchy.util.ClassName;
import uk.co.nickthecoder.itchy.util.XMLException;
import uk.co.nickthecoder.itchy.util.XMLTag;
import uk.co.nickthecoder.jame.RGBA;

public class SceneReader
{
    private final Resources resources;

    private Scene scene;

    public SceneReader(Resources resources)
    {
        this.resources = resources;
    }

    public Scene load(String filename) throws Exception
    {
        return this.load(new File(filename));
    }

    public Scene load(File file) throws Exception
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        this.scene = new Scene();
        try {
            XMLTag document = XMLTag.openDocument(reader);
            this.readScene(document.getTag("scene"));

        } finally {
            reader.close();
        }
        return this.scene;
    }

    private void readScene(XMLTag sceneTag) throws Exception
    {
        this.scene.showMouse = sceneTag.getOptionalBooleanAttribute("showMouse", true);
        String background = sceneTag.getOptionalAttribute("background", "#000");
        try {
            this.scene.backgroundColor = RGBA.parse(background);
        } catch (Exception e) {
            throw new XMLException("Illegal colour : " + background);
        }
        String layoutName = sceneTag.getOptionalAttribute("layout", "default");
        Layout layout = resources.getLayout(layoutName);
        this.scene.layout = layout.clone();

        String sceneDirectorName = sceneTag.getOptionalAttribute("role", PlainSceneDirector.class.getName());
        this.scene.setSceneDirectorClassName( new ClassName(SceneDirector.class, sceneDirectorName) );


        for (Iterator<XMLTag> i = sceneTag.getTags("properties"); i.hasNext();) {
            XMLTag propertiesTag = i.next();
            this.readProperties(propertiesTag);
        }

        for (Iterator<XMLTag> i = sceneTag.getTags("layer"); i.hasNext();) {
            XMLTag layerTag = i.next();
            String name = layerTag.getAttribute("name");
            Scene.SceneLayer sceneLayer = this.scene.createSceneLayer(name);
            this.readLayer(layerTag, sceneLayer);
        }

    }

    private void readProperties(XMLTag propertiesTag)
        throws Exception
    {
        List<Property<SceneDirector, ?>> properties = this.scene.getSceneDirector().getProperties();

        for (Iterator<XMLTag> i = propertiesTag.getTags("property"); i.hasNext();) {
            XMLTag propertyTag = i.next();
            String name = propertyTag.getAttribute("name");
            String value = propertyTag.getAttribute("value");

            Property<SceneDirector, ?> property = findProperty(properties, name);
            if (property == null) {
                throw new Exception("Didn't find SceneDirector property : " + name);
            }
            property.setValueByString(this.scene.getSceneDirector(), value);
        }

    }

    private Property<SceneDirector, ?> findProperty(List<Property<SceneDirector, ?>> properties, String name)
    {
        for (Property<SceneDirector, ?> property : properties) {
            if (property.key.equals(name)) {
                return property;
            }
        }
        return null;
    }

    private void readLayer(XMLTag parentTag, Scene.SceneLayer sceneLayer) throws Exception
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

    private void readActor(XMLTag actorTag, Scene.SceneLayer sceneLayer) throws Exception
    {
        String costumeName = actorTag.getAttribute("costume");
        costumeName = this.resources.getNewCostumeName(costumeName);
        Costume costume = this.resources.getCostume(costumeName);
        if (costume == null) {
            throw new XMLException("Costume not found : " + costumeName);
        }

        CostumeSceneActor sceneActor = new CostumeSceneActor(costume);
        sceneActor.roleClassName = costume.roleClassName;
        this.readSceneActorAttributes(actorTag, sceneActor);

        try {
            XMLTag makeupTag = actorTag.getTag("makeup");
            this.readMakeup(makeupTag, sceneActor);
        } catch (Exception e) {
            // Do nothing
        }

        sceneLayer.add(sceneActor);
    }

    private void readMakeup(XMLTag makeupTag, SceneActor sceneActor)
        throws Exception
    {
        ClassName className = new ClassName(Makeup.class, makeupTag.getAttribute("classname"));
        sceneActor.makeupClassName = className;
        Makeup makeup = Appearance.createMakeup(className);

        for (Iterator<XMLTag> i = makeupTag.getTags("property"); i.hasNext();) {
            XMLTag tag = i.next();

            String name = tag.getAttribute("name");
            String value = tag.getAttribute("value");
            setMakeupProperty(sceneActor, makeup, name, value);
        }
    }

    private void setMakeupProperty(SceneActor sceneActor, Makeup makeup, String name, String value)
        throws Exception
    {
        sceneActor.makeupPropertyStrings.put(name, value);
    }

    private void readText(XMLTag textTag, Scene.SceneLayer sceneLayer) throws Exception
    {
        String fontName = textTag.getAttribute("font");
        int fontSize = textTag.getIntAttribute("size");
        String text = textTag.getAttribute("text");
        Font font = this.resources.getFont(fontName);

        if (font == null) {
            throw new XMLException("Font not found : " + fontName);
        }

        TextSceneActor sceneActor = new TextSceneActor(font, fontSize, text);
        sceneActor.roleClassName = new ClassName(Role.class, PlainRole.class.getName());

        String costumeName = textTag.getOptionalAttribute("costume", null);
        costumeName = this.resources.getNewCostumeName(costumeName);
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

        try {
            XMLTag makeupTag = textTag.getTag("makeup");
            this.readMakeup(makeupTag, sceneActor);
        } catch (Exception e) {
            // Do nothing
        }

        sceneLayer.add(sceneActor);
    }

    /**
     * For backwards compatibility, if the zOrder isn't specified, then the default zOrder increases by 1 for each actor
     * read. Therefore,
     * the actors will have increasing zOrders.
     */
    private int defaultZOrder = 0;

    private void readSceneActorAttributes(XMLTag actorTag, SceneActor sceneActor)
        throws Exception
    {
        this.defaultZOrder += 1;

        sceneActor.id = actorTag.getOptionalAttribute("id", null);
        sceneActor.x = actorTag.getIntAttribute("x");
        sceneActor.y = actorTag.getIntAttribute("y");
        sceneActor.zOrder = actorTag.getOptionalIntAttribute("zOrder", this.defaultZOrder);
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

        if (actorTag.hasAttribute("role")) {
            ClassName className = new ClassName(Role.class, actorTag.getAttribute("role"));

            if (this.resources.checkClassName(className)) {
                sceneActor.roleClassName = className;
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

    private void setProperty(SceneActor sceneActor, Actor actor, String name, String value)
        throws Exception
    {
        sceneActor.customPropertyStrings.put(name, value);
    }

}
