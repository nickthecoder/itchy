package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.List;

public class Scene
{
    public List<SceneActor> sceneActors;

    public boolean showMouse = true;
    
    
    public Scene()
    {
        this.sceneActors = new ArrayList<SceneActor>();
    }

    public void create( ActorsLayer layer, boolean designMode )
    {
        for (SceneActor sceneActor : this.sceneActors) {
            Actor actor = sceneActor.createActor(designMode);
            layer.add(actor);
            
            if (actor.getActivationDelay() == 0) {
                actor.activate();
            } else if (actor.getActivationDelay() > 0) {
                actor.activateAfter( actor.getActivationDelay() );
                //actor.activate();
            }
        }
    }

    public Scene copy()
    {
        Scene result = new Scene();
        result.showMouse = this.showMouse;
        
        for (SceneActor sceneActor : this.sceneActors) {
            result.sceneActors.add(sceneActor.copy());
        }

        return result;
    }

}
