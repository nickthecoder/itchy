import uk.co.nickthecoder.itchy.CostumeProperties
import uk.co.nickthecoder.itchy.util.ClassName

import uk.co.nickthecoder.itchy.property.DoubleProperty

public class DropProperties extends CostumeProperties
{
    protected static properties = new ArrayList()
    
    static {
        properties.add( new DoubleProperty( 'speedFactor' ) )
    }

    def speedFactor = 1

    // Boiler plate code - no need to change this
    public ArrayList getProperties()
    {
        return properties;
    }
    
    // Boiler plate code - no need to change this
    public ClassName getClassName()
    {
        return ClassName( CostumeProperties, 'DropProperties.groovy' );
    }

}

