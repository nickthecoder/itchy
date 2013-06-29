package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.List;

public class Scene
{
    public List<SceneActor> sceneActors;

    public Scene()
    {
        this.sceneActors = new ArrayList<SceneActor>();
    }

    public void create( ActorsLayer layer, boolean designMode )
    {
        for ( SceneActor sceneActor : this.sceneActors ) {
            Actor actor = sceneActor.createActor( designMode );
            layer.add( actor );
            actor.activate();
        }
    }

    public Scene copy()
    {
        Scene result = new Scene();
        for ( SceneActor sceneActor : this.sceneActors ) {
            result.sceneActors.add( sceneActor.copy() );
        }

        return result;
    }

}
