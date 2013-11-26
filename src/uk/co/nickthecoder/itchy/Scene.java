/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.util.ClassName;
import uk.co.nickthecoder.itchy.util.StringUtils;
import uk.co.nickthecoder.jame.RGBA;

public class Scene
{
    @Property(label = "Show Mouse")
    public boolean showMouse = true;

    @Property(label = "Scene Director", baseClass = SceneDirector.class, sortOrder = 90)
    public ClassName sceneDirectorClassName;

    @Property(label="Background Colour")
    public RGBA backgroundColor = RGBA.BLACK;
    
    private List<SceneLayer> sceneLayers;

    private HashMap<String, SceneLayer> layersMap;

    public SceneDirector sceneDirector;

    public Scene()
    {
        this.sceneLayers = new ArrayList<SceneLayer>();
        this.layersMap = new HashMap<String, SceneLayer>();
        this.sceneDirectorClassName = new ClassName(SceneDirector.class, PlainSceneDirector.class.getName());
    }

    public List<SceneLayer> getSceneLayers()
    {
        return this.sceneLayers;
    }

    public SceneLayer createSceneLayer( String name )
    {
        SceneLayer layer = new SceneLayer(name);
        addSceneLayer(layer);
        return layer;
    }

    public SceneLayer getDefaultSceneLayer()
    {
        return this.sceneLayers.get(0);
    }

    private void addSceneLayer( SceneLayer sceneLayer )
    {
        this.sceneLayers.add(sceneLayer);
        this.layersMap.put(sceneLayer.getName(), sceneLayer);
    }

    public void clear()
    {
        for (SceneLayer sceneLayer : this.sceneLayers) {
            sceneLayer.clear();
        }
        this.sceneLayers.clear();
    }
    
    public void create( Stage layer, Resources resources, boolean designMode )
    {
        for (SceneLayer sceneLayer : this.sceneLayers) {
            sceneLayer.create(layer, resources, designMode);
        }
    }

    public void create( Game game, boolean designMode )
    {
        for (SceneLayer sceneLayer : this.sceneLayers) {
            String name = sceneLayer.name;

            sceneLayer.create( findStage(game, name), game.resources, designMode);
        }
    }

    private Stage findStage( Game game, String name )
    {
        Stage best = game.getStages().get(0);
        
        for (Stage stage : game.getStages()) {
            if (!stage.isLocked()) {
                
                if (name.equals(stage.getName())) {
                    return stage;
                }
                best = stage;
            }
        }

        return best;
    }

    public void add( SceneActor sceneActor )
    {
        this.sceneLayers.get(0).add(sceneActor);
    }

    public List<SceneActor> getSceneActors()
    {
        return this.sceneLayers.get(0).getSceneActors();
    }

    public Scene copy()
    {
        Scene result = new Scene();
        result.showMouse = this.showMouse;
        result.backgroundColor = this.backgroundColor;
        result.sceneDirectorClassName = this.sceneDirectorClassName;
        result.sceneDirector = this.sceneDirector;

        for (SceneLayer sceneLayer : this.sceneLayers) {
            SceneLayer newSceneLayer = sceneLayer.copy();
            result.addSceneLayer(newSceneLayer);
        }

        return result;
    }

    public SceneDirector createSceneDirector( Resources resources )
        throws Exception
    {
        SceneDirector result;

        if (StringUtils.isBlank(this.sceneDirectorClassName.name)) {
            result = new PlainSceneDirector();
        } else {
            if (resources.isValidScript(this.sceneDirectorClassName)) {
                result = Itchy.getGame().getScriptManager().createSceneDirector(this.sceneDirectorClassName);
            } else {
                Class<?> klass = Class.forName(this.sceneDirectorClassName.name);
                result = (SceneDirector) klass.newInstance();
            }
        }
        // Copy the sceneDirector properties
        if (this.sceneDirector != null) {
            for (AbstractProperty<SceneDirector, ?> property : result.getProperties()) {
                property.setValue(result, property.getValue(this.sceneDirector));
            }
        }
        return result;
    }

    public boolean uses( FontResource fontResource )
    {
        for (SceneLayer layer : this.sceneLayers) {
            if ( layer.uses( fontResource )) {
                return true;
            }
        }
        return false;
    }
    
    public boolean uses( CostumeResource costumeResource )
    {
        for (SceneLayer layer : this.sceneLayers) {
            if ( layer.uses( costumeResource )) {
                return true;
            }
        }
        return false;
    }
    
    public void debug()
    {
        System.err.println("Scene");
        for (SceneLayer layer : this.sceneLayers) {
            System.err.println( "Layer : " + layer.name + " Actors " + layer.sceneActors.size() );
        }
    }
    
    public class SceneLayer
    {
        String name;

        private List<SceneActor> sceneActors;

        public SceneLayer( String name )
        {
            this.name = name;
            this.sceneActors = new ArrayList<SceneActor>();
        }

        public String getName()
        {
            return this.name;
        }

        public void add( SceneActor sceneActor )
        {
            this.sceneActors.add(sceneActor);
        }

        public List<SceneActor> getSceneActors()
        {
            return Collections.unmodifiableList(this.sceneActors);
        }

        public void clear()
        {
            this.sceneActors.clear();
        }
        
        public SceneLayer copy()
        {
            SceneLayer result = new SceneLayer(this.name);
            for (SceneActor sceneActor : this.sceneActors) {
                result.sceneActors.add(sceneActor.copy());
            }
            return result;
        }

        public boolean isEmpty()
        {
            return this.sceneActors.size() == 0;
        }

        public void create( Stage stage, Resources resources, boolean designMode )
        {
            for (SceneActor sceneActor : this.sceneActors) {
                Actor actor = sceneActor.createActor(resources, designMode);
                stage.add(actor);
            }
        }

        public boolean uses( FontResource fontResource )
        {
            for (SceneActor sceneActor : this.sceneActors) {
                if (sceneActor instanceof TextSceneActor) {
                    if (((TextSceneActor) sceneActor).font == fontResource.font) {
                        return true;
                    }
                }
            }
            return false;
        }
        
        public boolean uses( CostumeResource costumeResource )
        {
            for (SceneActor sceneActor : this.sceneActors) {
                if (sceneActor.costume == costumeResource.getCostume()) {
                    return true;
                }
            }
            return false;
        }
    }

}
