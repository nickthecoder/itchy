import uk.co.nickthecoder.itchy.CostumeProperties
import uk.co.nickthecoder.itchy.util.ClassName

import java.util.ArrayList;

// TODO You'll need to import one or more of these...
// import uk.co.nickthecoder.itchy.property.StringProperty;
// import uk.co.nickthecoder.itchy.property.IntegerProperty;
// import uk.co.nickthecoder.itchy.property.BooleanProperty;
// import uk.co.nickthecoder.itchy.property.DoubleProperty;
// import uk.co.nickthecoder.itchy.property.RGBAProperty;
  
public class ${Name} extends CostumeProperties
{

    protected static properties = new ArrayList();
    {
        // TODO declare poroperties here. Note that you must also initialise them in __init__
        // properties.add( StringProperty( "myString" ) )
        // properties.add( IntegerProperty( "myInteger" ) )
        // properties.add( DoubleProperty( "myDouble" ) )
        // properties.add( BooleanProperty( "myBoolean" ) )
        // properties.add( RGBAProperty( "myColor" ).label( "My Colour" ) )
        // properties.add( RGBAProperty( "myOtherColor" ).allowNull().includeAlpha() )
    }
      
    public ${Name}()
    {
        // TODO Initialise your object.
        // this.myString = "Default Value";
        // this.myInteger = 0;
    }



    // Boiler plate code - no need to change this
    public ArrayList getProperties()
    {
        return properties;
    }
    
    // Boiler plate code - no need to change this
    public ClassName getClassName()
    {
        return ClassName( CostumeProperties, "${NAME}.groovy" );
    }

}

