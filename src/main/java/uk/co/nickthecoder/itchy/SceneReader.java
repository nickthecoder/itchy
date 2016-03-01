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

import uk.co.nickthecoder.itchy.editor.SceneDesignerRole;
import uk.co.nickthecoder.itchy.makeup.Makeup;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.PropertySubject;
import uk.co.nickthecoder.itchy.role.PlainRole;
import uk.co.nickthecoder.itchy.util.ClassName;
import uk.co.nickthecoder.itchy.util.XMLException;
import uk.co.nickthecoder.itchy.util.XMLTag;

public class SceneReader
{
    private final Resources resources;

    private Scene scene;

    private boolean design = false;

    public SceneReader(Resources resources, boolean design)
    {
        this.design = design;
        this.resources = resources;
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
        readProperties(sceneTag, this.scene);
        
        String layoutName = sceneTag.getOptionalAttribute("layout", "default");
        layoutName = this.resources.getNewLayoutName(layoutName);
        this.scene.layout = this.resources.getLayout( layoutName ).clone();
        
        if (this.scene.layout == null) {
            this.scene.layout = resources.getLayout("default");
        }
        if (this.scene.layout == null) {
            System.err.println ("Creating a blank layout, as one was not found");
            this.scene.layout = new Layout();
            Layer layer = new Layer();
            layer.setName("default");
            layer.setViewClassName(new ClassName(View.class, StageView.class.getName()));
            this.scene.layout.addLayer(new Layer());
        } else {
            this.scene.layout = this.scene.layout.clone();
        }

        if (sceneTag.hasTag("properties")) {
            // The old way
            this.oldReadSceneDirectorProperties(sceneTag.getTag("properties"));
        } else {
            // The good way
            readProperties(sceneTag, "sceneDirector", scene.getSceneDirector());
        }

        for (Iterator<XMLTag> i = sceneTag.getTags("layer"); i.hasNext();) {
            XMLTag layerTag = i.next();
            String name = layerTag.getAttribute("name");

            this.readLayerProperties(layerTag, name);

            Layer layer = this.scene.layout.findSafeLayer(name);
            if (layer == null) {
                throw new XMLException( "Cannot find layer " + name );
            }
            this.readLayer(layerTag, layer);
        }

    }

    private void readLayerProperties(XMLTag layerTag, String name)
        throws XMLException
    {
        Layout layout = this.scene.layout;
        Layer layer = layout.findLayer(name);
        if (layer == null) {
            return;
        }

        XMLTag viewTag = layerTag.getTag("view", false);
        if (viewTag != null) {
            readProperties(viewTag, layer.getView());
        }
        XMLTag stageTag = layerTag.getTag("stage", false);
        if (stageTag != null) {
            readProperties(stageTag, layer.getStage());
        }
        XMLTag stageConstraintTag = layerTag.getTag("stageConstraint", false);
        if (stageConstraintTag != null) {
            readProperties(stageConstraintTag, layer.getStage().getStageConstraint());
        }
    }

    private void readLayer(XMLTag parentTag, Layer layer) throws Exception
    {
        Stage stage = layer.getStage();
        for (Iterator<XMLTag> i = parentTag.getTags(); i.hasNext();) {
            XMLTag tag = i.next();
            if (tag.getName() == "actor") {
                this.readActor(tag, stage);
            } else if (tag.getName() == "text") {
                this.readText(tag, stage);
            }
        }
    }

    private void readActor(XMLTag actorTag, Stage stage) throws Exception
    {
        String costumeName = actorTag.getAttribute("costume");
        costumeName = this.resources.getNewCostumeName(costumeName);
        Costume costume = this.resources.getCostume(costumeName);
        if (costume == null) {
            throw new XMLException("Costume not found : " + costumeName);
        }

        Actor sceneActor = new Actor(costume);
        this.readSceneActorAttributes(actorTag, sceneActor);

        stage.add(sceneActor);
    }

    private void readText(XMLTag textTag, Stage stage) throws Exception
    {
        String fontName = textTag.getAttribute("font");
        fontName = this.resources.getNewFontName(fontName);
        Font font = this.resources.getFont(fontName);

        if (font == null) {
            throw new XMLException("Font not found : " + fontName);
        }

        // Use default text, and font size for now. They will be read later.
        TextPose textPose = new TextPose("", font, 14);

        Actor sceneActor = new Actor(textPose);

        String costumeName = textTag.getOptionalAttribute("costume", null);
        costumeName = this.resources.getNewCostumeName(costumeName);
        if (costumeName != null) {
            Costume costume = this.resources.getCostume(costumeName);
            if (costume == null) {
                throw new XMLException("Costume not found : " + costumeName);
            }
            sceneActor.setCostume(costume);
        }

        this.readSceneActorAttributes(textTag, sceneActor);

        stage.add(sceneActor);
    }

    private void readSceneActorAttributes(XMLTag actorTag, Actor sceneActor)
        throws Exception
    {
        String roleClassNameString = actorTag.getOptionalAttribute("role", null);
        if (roleClassNameString == null) {
            if (sceneActor.getCostume() != null) {
                roleClassNameString = sceneActor.getCostume().roleClassName.name;
            } else {
                roleClassNameString = PlainRole.class.getName();
            }
        }
        ClassName roleClassName = new ClassName(Role.class, roleClassNameString);
        Role role = (Role) roleClassName.createInstance(resources);
        
        this.readProperties(actorTag, sceneActor);
        this.readProperties(actorTag, sceneActor.getAppearance());
        
        if (design) {
            sceneActor.setRole(new SceneDesignerRole(role));
        } else {
            double delay = actorTag.getOptionalDoubleAttribute("activationDelay", 0);
            sceneActor.setRole(new DelayedActivation(delay, role));
        }

        readRole(actorTag, role);
        readMakeup(actorTag, sceneActor);

    }

    private void readRole(XMLTag actorTag, Role role) throws Exception
    {
        if (actorTag.hasTag("role")) {
            this.readProperties(actorTag.getTag("role"), role);
        } else {
            // Do it the old way...

            List<Property<Role, ?>> properties = role.getProperties();

            for (Iterator<XMLTag> i = actorTag.getTags("property"); i.hasNext();) {
                XMLTag propertyTag = i.next();
                String name = propertyTag.getAttribute("name");
                String value = propertyTag.getAttribute("value");

                Property<Role, ?> property = findProperty(properties, name);
                if (property == null) {
                    throw new Exception("Didn't find Role property : " + name + " for " + role);
                } else {
                    property.setValueByString(role, value);
                }
            }

        }
    }

    private void readMakeup(XMLTag actorTag, Actor sceneActor)
        throws Exception
    {
        // The new way is for the makeup class name to be on the "actor" tag.
        if (actorTag.hasAttribute("makeup")) {
            ClassName className = new ClassName(Makeup.class, actorTag.getAttribute("makeup"));
            Makeup makeup = (Makeup) className.createInstance(resources);
            sceneActor.getAppearance().setMakeup(makeup);
        }

        if (actorTag.hasTag("makeup")) {
            XMLTag makeupTag = actorTag.getTag("makeup");

            if (makeupTag.hasAttribute("classname")) {
                // Makeup class name used to be on the makeup tag. This is the old way.
                ClassName className = new ClassName(Makeup.class, makeupTag.getAttribute("classname"));
                Makeup makeup = (Makeup) className.createInstance(resources);
                sceneActor.getAppearance().setMakeup(makeup);
            }

            Makeup makeup = sceneActor.getAppearance().getMakeup();

            // New way to read the makeup properties.
            this.readProperties(makeupTag, sceneActor.getAppearance().getMakeup());

            // Old way to read the makeup properties.
            for (Iterator<XMLTag> i = makeupTag.getTags("property"); i.hasNext();) {
                XMLTag tag = i.next();

                String name = tag.getAttribute("name");
                String value = tag.getAttribute("value");

                for (Property<Makeup, ?> property : makeup.getProperties()) {
                    if (property.key.equals(name)) {
                        property.setValueByString(makeup, value);
                    }
                }
            }
        }

    }

    private <S extends PropertySubject<S>> void readProperties(XMLTag tag, String tagName, S subject)
        throws XMLException
    {
        if (tag.hasTag(tagName)) {
            readProperties(tag.getTag(tagName), subject);
        }
    }

    private <S extends PropertySubject<S>> void readProperties(XMLTag tag, S subject) throws XMLException
    {
        for (Property<S, ?> property : subject.getProperties()) {
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
                    throw new XMLException("Failed to parse property : '" + property.key + "'. value : '" + value + "'");
                }
            }
        }

    }

    /**
     * For backwards compatibility only.
     */
    private void oldReadSceneDirectorProperties(XMLTag propertiesTag)
        throws Exception
    {
        List<Property<SceneDirector, ?>> properties = this.scene.getSceneDirector().getProperties();

        for (Iterator<XMLTag> i = propertiesTag.getTags("property"); i.hasNext();) {
            XMLTag propertyTag = i.next();
            String name = propertyTag.getAttribute("name");
            String value = propertyTag.getAttribute("value");

            Property<SceneDirector, ?> property = findProperty(properties, name);
            if (property == null) {
                throw new Exception("Didn't find sceneDirector property : " + name);
            }
            property.setValueByString(this.scene.getSceneDirector(), value);
        }

    }

    private <S> Property<S, ?> findProperty(List<Property<S, ?>> properties, String name)
    {
        for (Property<S, ?> property : properties) {
            if (property.key.equals(name)) {
                return property;
            }
        }
        return null;
    }

}
