import uk.co.nickthecoder.itchy.Itchy
import uk.co.nickthecoder.itchy.SceneDirector
import uk.co.nickthecoder.itchy.PlainSceneDirector
import uk.co.nickthecoder.itchy.util.ClassName

public class Menu extends PlainSceneDirector
{
    protected static properties = new ArrayList();
    
    public void onMessage( String message )
    {
        if (message == "resetHighScores") {
            Itchy.getGame().getPreferences().clear();
        }
    }

    // Boiler plate code - no need to change this
    public ArrayList getProperties(self)
    {
        return properties;
    }
    
    // Boiler plate code - no need to change this
    public ClassName getClassName(self)
    {
        return ClassName( SceneDirector, "Menu.groovy" );
    }

}


