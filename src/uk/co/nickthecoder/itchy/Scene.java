package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Scene
{
    private List<SceneLayer> sceneLayers;

    private HashMap<String, SceneLayer> layersMap;

    public boolean showMouse = true;

    public Scene()
    {
        this.sceneLayers = new ArrayList<SceneLayer>();
        this.layersMap = new HashMap<String, SceneLayer>();

        this.createLayer("default");
    }

    public SceneLayer createLayer( String name )
    {
        SceneLayer layer = new SceneLayer(name);
        addSceneLayer(layer);
        return layer;
    }

    private void addSceneLayer( SceneLayer sceneLayer )
    {
        this.sceneLayers.add(sceneLayer);
        this.layersMap.put(sceneLayer.getName(), sceneLayer);
    }

    public void create( ActorsLayer layer, boolean designMode )
    {
        for ( SceneLayer sceneLayer : this.sceneLayers ) {
            sceneLayer.create( layer, designMode );
        }
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

    class SceneLayer
    {
        String name;

        private List<SceneActor> sceneActors;

        public SceneLayer( String name )
        {
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
        
        public void create( ActorsLayer layer, boolean designMode )
        {
            for (SceneActor sceneActor : this.sceneActors) {
                Actor actor = sceneActor.createActor(designMode);
                layer.add(actor);
    
                if (!designMode) {
                    if (actor.getActivationDelay() == 0) {
                        actor.activate();
                    } else if (actor.getActivationDelay() > 0) {
                        actor.activateAfter(actor.getActivationDelay());
                        // actor.activate();
                    }
                }
            }
        }

    }

}
