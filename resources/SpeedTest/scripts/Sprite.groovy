import uk.co.nickthecoder.itchy.*
import uk.co.nickthecoder.itchy.property.*
import uk.co.nickthecoder.itchy.role.*
import uk.co.nickthecoder.itchy.util.*

class Sprite extends AbstractRole
{
    public static properties = new ArrayList()
    
	static {
		// TODO declare properties here. Note that you must also add a "def" for each one.
        // e.g.
		// properties.add( new StringProperty( "myString" ).hint( "my greeen label" ) )
	}

    def random = new Random()

	// Constructor
    public Sprite()
    {
    }

    void onBirth()
    {
        // TODO Called soon after the actor is created and after it has been placed on a Stage.
    }

    void tick()
    {
        actor.moveBy( random.nextInt( 11 ) -5, random.nextInt( 11 ) -5 );
    }

    // Boiler plate code - no need to change this
    public ArrayList getProperties()
    {
        return properties
    }
    
    // Boiler plate code - no need to change this
    public ClassName getClassName()
    {
        return new ClassName( Role, "Sprite.groovy" )
    }
}
