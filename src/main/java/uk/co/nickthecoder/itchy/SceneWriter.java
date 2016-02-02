/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.io.File;

import uk.co.nickthecoder.itchy.editor.SceneDesignerRole;
import uk.co.nickthecoder.itchy.makeup.Makeup;
import uk.co.nickthecoder.itchy.makeup.NullMakeup;
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
        this.writeProperties(this.scene);

        this.writeProperties("sceneDirector", this.scene.getSceneDirector());

        for (Layer layer : scene.layout.getLayersByZOrder()) {
            
            this.beginTag("layer");
            this.attribute("name", layer.name);

            this.writeLayerProperties(layer);

            Stage stage = layer.getStage();
            if (stage != null) {
                this.writeActors(stage);
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
            this.writeChangedProperties("view", view, templateView);
        }
        
        if (view instanceof StageView) {
            
            StageView stageView = (StageView) view;
            StageView templateStageView = (StageView) templateView;
            
            Stage stage = stageView.getStage();
            Stage templateStage = templateStageView.getStage();
            
            if ((stage != null) && (stage.getProperties().size() > 0)) {
                this.writeChangedProperties("stage", stage, templateStage);
            }
            if ((stage != null) && (stage.getStageConstraint().getProperties().size() > 0)) {
                this.writeChangedProperties("stageConstraint", stage.getStageConstraint(), templateStage.getStageConstraint());
            }
        }
        
    }

    private void writeActors(Stage stage) throws XMLException
    {
        for (Actor sceneActor : stage.getActors()) {

            Role role = sceneActor.getRole();
            if (role instanceof SceneDesignerRole) {
                role = ((SceneDesignerRole)role).actualRole;
            }
            
            if (sceneActor.getAppearance().getPose() instanceof TextPose) {
                this.beginTag("text");

                if (sceneActor.getCostume() != null) {
                    this.attribute("costume", sceneActor.getCostume().getName());
                }

                this.writeSceneActorAttributes(sceneActor, role);
                this.endTag("text");
            
            } else {
                
                this.beginTag("actor");
                this.attribute("costume", sceneActor.getCostume().getName());
                
                this.writeSceneActorAttributes(sceneActor, role);
                
                this.endTag("actor");

            }

        }

    }

    private void writeSceneActorAttributes(Actor sceneActor, Role role) throws XMLException
    {
        writeProperties( sceneActor );
        writeProperties( sceneActor.getAppearance() );
        
        if (PlainRole.class != role.getClass()) {
            this.attribute("role", role.getClassName().name);
        }
        
        Makeup makeup = sceneActor.getAppearance().getMakeup();
        if (NullMakeup.class != makeup.getClass()) {
            this.attribute("makeup", makeup.getClassName().name);
        }
        
        this.writeProperties("role", role);
        this.writeProperties("makeup", sceneActor.getAppearance().getMakeup());

    }


    private <S extends PropertySubject<S>> void writeProperties(String tagName, S subject)
        throws XMLException
    {
        if ( subject.getProperties().size() > 0 ) {
            this.beginTag(tagName);
            this.writeProperties(subject);
            this.endTag(tagName);
        }
    }
    
    private <S extends PropertySubject<S>> void writeProperties(S subject)
        throws XMLException
    {
        for (Property<S, ?> property : subject.getProperties()) {

            try {
                String value = property.getStringValue(subject);
                if (! property.isDefaultValue( subject )) {
                    this.attribute(property.key, value);
                }

            } catch (Exception e) {
                e.printStackTrace();
                throw new XMLException("Failed to write property : " + property.key);
            }

        }
    }
    

    private <S extends PropertySubject<S>> void writeChangedProperties(String tagName, S subject, S template)
        throws XMLException
    {
        boolean startedTag = false;
        
        for (Property<S, ?> property : subject.getProperties()) {

            try {
                String value = property.getStringValue(subject);
                String value2 = property.getStringValue(template);
                if (!value.equals(value2)) {
                    if (!startedTag) {
                        this.beginTag(tagName);
                        startedTag = true;
                    }
                    this.attribute(property.key, value);
                }

            } catch (Exception e) {
                e.printStackTrace();
                throw new XMLException("Failed to write property : " + property.key);
            }

        }
        if ( startedTag ) {
            this.endTag( tagName );
        }
        
    }

}
