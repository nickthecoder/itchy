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
import uk.co.nickthecoder.itchy.util.Property;
import uk.co.nickthecoder.itchy.util.StringUtils;
import uk.co.nickthecoder.jame.RGBA;

public class Scene
{
    @Property(label = "Show Mouse")
    public boolean showMouse = true;

    @Property(label = "Scene Behaviour", baseClass = SceneBehaviour.class, sortOrder = 90)
    public ClassName sceneBehaviourClassName;

    @Property(label="Background Colour")
    public RGBA backgroundColor = RGBA.BLACK;
    
    private List<SceneLayer> sceneLayers;

    private HashMap<String, SceneLayer> layersMap;

    public SceneBehaviour sceneBehaviour;

    public Scene()
    {
        this.sceneLayers = new ArrayList<SceneLayer>();
        this.layersMap = new HashMap<String, SceneLayer>();
        this.sceneBehaviourClassName = new ClassName(PlainSceneBehaviour.class.getName());

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
        Stage best = game.stages.get(0);
        
        for (Stage stage : game.stages) {
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

        if (StringUtils.isBlank(this.sceneBehaviourClassName.name)) {
            result = new PlainSceneBehaviour();
        } else {
            if (resources.isValidScript(this.sceneBehaviourClassName)) {
                result = Resources.getScriptManager().createSceneBehaviour(this.sceneBehaviourClassName);
            } else {
                Class<?> klass = Class.forName(this.sceneBehaviourClassName.name);
                result = (SceneBehaviour) klass.newInstance();
            }
        }
        // Copy the sceneBehaviour properties
        if (this.sceneBehaviour != null) {
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

        public void create( Stage stage, Resources resources, boolean designMode )
        {

            for (SceneActor sceneActor : this.sceneActors) {
                Actor actor = sceneActor.createActor(resources, designMode);
                stage.add(actor);

                if (!designMode) {
                    if (actor.getActivationDelay() == 0) {
                    } else if (actor.getActivationDelay() > 0) {
                        actor.activateAfter(actor.getActivationDelay());
                    }
                }
            }
        }

    }

}
