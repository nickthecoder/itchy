import uk.co.nickthecoder.itchy.*
import uk.co.nickthecoder.itchy.property.*
import uk.co.nickthecoder.itchy.role.*
import uk.co.nickthecoder.itchy.util.*

class Ramp extends Piece
{
    public static properties = new ArrayList()
    
    def boolean mirror = false
        
    // Constructor
    public Ramp()
    {
    }
   
    void onBirth()
    { // Center = 60, 55 : 18,28  21,10
        edges.add( [-25,  26 ,  50,51,  0 ] ) // Ramp
        edges.add( [-42,  27 , -25,26,  1 ] ) // Tilt (18,28)
        edges.add( [-42,  27 , -40,45,  0 ] ) // Cup (21, 10)
        super.onBirth()
    }

    // Boiler plate code - no need to change this
    public ArrayList getProperties()
    {
        return properties
    }
    
    // Boiler plate code - no need to change this
    public ClassName getClassName()
    {
        return new ClassName( Role, "Ramp.groovy" )
    }
}

