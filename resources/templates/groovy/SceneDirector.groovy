import uk.co.nickthecoder.itchy.PlainSceneDirector
import uk.co.nickthecoder.itchy.util.ClassName

// TODO If your SceneDirector has properties, you'll need to import one or more of these...
// import uk.co.nickthecoder.itchy.property.StringProperty
// import uk.co.nickthecoder.itchy.property.IntegerProperty
// import uk.co.nickthecoder.itchy.property.BooleanProperty
// import uk.co.nickthecoder.itchy.property.DoubleProperty
// import uk.co.nickthecoder.itchy.property.RGBAProperty

public class ${Name} extends PlainSceneDirector
{
    protected static properties = new ArrayList()
    /*{
        // TODO declare properties here. Note that you should also initialise them in the constructor.
        // properties.add( new StringProperty( "myString" ) )
        // properties.add( new IntegerProperty( "myInteger" ) )
        // properties.add( new DoubleProperty( "myDouble" ) )
        // properties.add( new BooleanProperty( "myBoolean" ) )
        // properties.add( new RGBAProperty( "myColor" ).label( "My Colour" ) )
        // properties.add( new RGBAProperty( "myOtherColor" ).allowNull().includeAlpha() )
    }*/
    
    // Constructor
    public ${Name}()
    {
        // TODO Initialise your object.
        // self.myString = "Default Value"
        // self.myInteger = 0
    }
        
    public void tick()
    {
        // TODO Called 60 times a second
    }

    // TODO Other methods include :
    // onActivate, onDeactivate(), onMouseDown, onMouseUp, onMouseMove, onKeyDown, onKeyUp, onMessage
    
    // Boiler plate code - no need to change this
    public ArrayList getProperties(self)
    {
        return properties
    }
    
    // Boiler plate code - no need to change this
    public ClassName getClassName(self)
    {
        return new ClassName( CostumeProperties, "${NAME}.groovy" )
    }

}


