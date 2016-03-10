package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.StringProperty;

public class RedirectSceneDirector extends PlainSceneDirector
{
    protected static final List<Property<SceneDirector, ?>> properties = new ArrayList<Property<SceneDirector, ?>>();
    
    static {
        properties.add( new StringProperty<SceneDirector>( "sceneName").aliases("redirectSceneName") );
    }

    public String sceneName = "";
    
    @Override
    public List<Property<SceneDirector, ?>> getProperties()
    {
        return properties;
    }

    @Override
    public void onLoaded()
    {
        Itchy.getGame().startScene( this.sceneName );
    }
}
