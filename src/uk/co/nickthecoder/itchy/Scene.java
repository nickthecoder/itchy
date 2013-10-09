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

import uk.co.nickthecoder.itchy.util.AbstractProperty;
import uk.co.nickthecoder.itchy.util.ClassName;
import uk.co.nickthecoder.itchy.util.StringUtils;

public class Scene
{
    private List<SceneLayer> sceneLayers;

    private HashMap<String, SceneLayer> layersMap;

    public SceneBehaviour sceneBehaviour;
    
    public boolean showMouse = true;

    public ClassName sceneBehaviourName;

    private List<Actor> activateList;

    public Scene()
    {
        this.sceneLayers = new ArrayList<SceneLayer>();
        this.layersMap = new HashMap<String, SceneLayer>();
        
        this.createSceneLayer("default");
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

    public void create( ActorsLayer layer, Resources resources, boolean designMode )
    {
        this.activateList = new ArrayList<Actor>();

        for (SceneLayer sceneLayer : this.sceneLayers) {
            sceneLayer.create(layer, resources, designMode);
        }

        for (Actor actor : this.activateList) {
            actor.activate();
        }
        this.activateList.clear();
        this.activateList = null;
    }

    public void create( CompoundLayer layer, Resources resources, boolean designMode )
    {
        this.activateList = new ArrayList<Actor>();

        for (SceneLayer sceneLayer : this.sceneLayers) {
            String name = sceneLayer.name;

            sceneLayer.create(findLayer(layer, name), resources, designMode);
        }

        for (Actor actor : this.activateList) {
            actor.activate();
        }
        this.activateList.clear();
        this.activateList = null;
    }

    private ActorsLayer findLayer( CompoundLayer parent, String name )
    {
        Layer best = null;

        for (Layer childLayer : parent.getChildren()) {
            if ((childLayer instanceof ActorsLayer) && (!childLayer.locked)) {
                if (name.equals(childLayer.getName())) {
                    return (ActorsLayer) childLayer;
                }
                best = childLayer;
            }
        }

        return (ActorsLayer) best;
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

        for (SceneLayer sceneLayer : this.sceneLayers) {
            SceneLayer newSceneLayer = sceneLayer.copy();
            result.addSceneLayer(newSceneLayer);
        }

        return result;
    }

    public SceneBehaviour createSceneBehaviour( Resources resources )
        throws Exception
    {
        SceneBehaviour result;
        
        if (StringUtils.isBlank(this.sceneBehaviourName)) {
            result = new NullSceneBehaviour();
        } else {
            if (resources.scriptManager.isValidScript(this.sceneBehaviourName)) {
                result = resources.scriptManager.createSceneBehaviour(this.sceneBehaviourName);
            } else {
                Class<?> klass = Class.forName(this.sceneBehaviourName.name);
                result = (SceneBehaviour) klass.newInstance();
            }
        }
        // Copy the sceneBehaviour properties
        if ( this.sceneBehaviour != null ) {
            for (AbstractProperty<SceneBehaviour, ?> property : result.getProperties()) {
                property.setValue(result, property.getValue(this.sceneBehaviour));
            }
        }
        return result;
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

        public void create( ActorsLayer layer, Resources resources, boolean designMode )
        {
            layer.reset();

            for (SceneActor sceneActor : this.sceneActors) {
                Actor actor = sceneActor.createActor(resources, designMode);
                layer.add(actor);

                if (!designMode) {
                    if (actor.getActivationDelay() == 0) {
                        Scene.this.activateList.add(actor);
                    } else if (actor.getActivationDelay() > 0) {
                        actor.activateAfter(actor.getActivationDelay());
                    }
                }
            }
        }

    }

}
