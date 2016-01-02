import uk.co.nickthecoder.itchy.*
import uk.co.nickthecoder.itchy.property.*
import uk.co.nickthecoder.itchy.role.*
import uk.co.nickthecoder.itchy.util.*

public class ${Name} extends CostumeProperties
{
    public static properties = new ArrayList()
	
    static {
        // TODO declare properties here. Note that you must also add a "def" for each one.
        // properties.add( new StringProperty( "myString" ) )
    }

    public ${Name}()
    {
        // TODO Initialise your object.
        // this.myString = "Default Value";
    }


    // Boiler plate code - no need to change this
    public ArrayList getProperties()
    {
        return properties
    }

    // Boiler plate code - no need to change this
    public ClassName getClassName()
    {
        return ClassName( CostumeProperties, "${NAME}.groovy" )
    }
}
