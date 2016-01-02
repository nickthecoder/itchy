import uk.co.nickthecoder.itchy.*
import uk.co.nickthecoder.itchy.property.*
import uk.co.nickthecoder.itchy.util.*

public class ${Name} extends PlainSceneDirector
{
    public static properties = new ArrayList()

	static {
		// TODO declare properties here. Note that you must also add a "def" for each one.
        // properties.add( new StringProperty( "myString" ) )
    }

    // Constructor
    public ${Name}()
    {
        // TODO Initialise your object.
    }
        
    public void tick()
    {
        // TODO Called 60 times a second
    }

    // TODO Other methods include :
    // onActivate, onDeactivate(), onMouseDown, onMouseUp, onMouseMove, onKeyDown, onKeyUp, onMessage
    
    // Boiler plate code - no need to change this
    public ArrayList getProperties()
    {
        return properties
    }
    
    // Boiler plate code - no need to change this
    public ClassName getClassName()
    {
        return new ClassName( CostumeProperties, "${NAME}.groovy" )
    }
}
