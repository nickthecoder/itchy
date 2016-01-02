import uk.co.nickthecoder.itchy.*
import uk.co.nickthecoder.itchy.property.*
import uk.co.nickthecoder.itchy.role.*
import uk.co.nickthecoder.itchy.util.*

class ${Name} extends AbstractRole
{
    public static properties = new ArrayList()
    
	static {
		// TODO declare properties here. Note that you must also add a "def" for each one.
		// properties.add( new StringProperty( "myString" ) )
	}

	// Constructor
    public ${NAME}()
    {
    }

    void onBirth()
    {
        // TODO Called soon after the actor is created and after it has been placed on a Stage.
    }

    void tick()
    {
        // TODO Called 60 times a second, and is where all the good stuff belongs!
    }

    // TODO Other methods include :
    // onSceneCreated, onDetach, onKill, onMouseDown, onMouseUp, onMouseMove


    // Boiler plate code - no need to change this
    public ArrayList getProperties()
    {
        return properties
    }
    
    // Boiler plate code - no need to change this
    public ClassName getClassName()
    {
        return new ClassName( Role, "${NAME}.groovy" )
    }
}
