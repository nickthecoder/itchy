import uk.co.nickthecoder.itchy.*
import uk.co.nickthecoder.itchy.property.*
import uk.co.nickthecoder.itchy.role.*
import uk.co.nickthecoder.itchy.util.*

class Crossover extends Piece
{
    public static properties = new ArrayList()

    // Constructor
    public Crossover()
    {
    }

    void onBirth()
    {
        edges.add( [-50,-50 ,  50,-50, 0] ) // Base
        edges.add( [ 0,  30 , -25, 10, 0] ) // Inside left
        edges.add( [ 0,  30 ,  25, 10, 0] ) // Inside right
        
        edges.add( [-50,-10 , -65,  4, 0] ) // Left bottom
        edges.add( [-48, 45 , -65,  4, 0] ) // Left top
        edges.add( [-48, 45 , -52, 49, 0] ) // Left end
        edges.add( [ 50,-10 ,  65,  4, 0] ) // Right bottom
        edges.add( [ 48, 45 ,  65,  4, 0] ) // Right top
        edges.add( [ 48, 45 ,  52, 49, 0] ) // Right end
        edges.add( [ -7, 17 ,  -7, 75, 0] ) // Divider Left 
        edges.add( [  7, 17 ,   7, 75, 0] ) // Divider Right

        super.onBirth()
    }

    void tick()
    {
    }

    // Boiler plate code - no need to change this
    public ArrayList getProperties()
    {
        return properties
    }
    
    // Boiler plate code - no need to change this
    public ClassName getClassName()
    {
        return new ClassName( Role, "Crossover.groovy" )
    }
}
