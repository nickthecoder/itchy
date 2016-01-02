import uk.co.nickthecoder.itchy.*
import uk.co.nickthecoder.itchy.util.*

public class Menu extends PlainSceneDirector
{
    protected static properties = new ArrayList()
    
    public void onMessage( String message )
    {
        if (message == "resetHighScores") {
            Itchy.game.preferences.clear()
        }
    }

    // Boiler plate code - no need to change this
    public ArrayList getProperties(self)
    {
        return properties
    }
    
    // Boiler plate code - no need to change this
    public ClassName getClassName(self)
    {
        return ClassName( SceneDirector, "Menu.groovy" )
    }

}


