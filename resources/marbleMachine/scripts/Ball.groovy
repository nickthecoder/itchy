import uk.co.nickthecoder.itchy.*
import uk.co.nickthecoder.itchy.property.*
import uk.co.nickthecoder.itchy.role.*
import uk.co.nickthecoder.itchy.util.*

class Ball extends AbstractRole
{
    public static properties = new ArrayList()
    
    public radius = 17
    public vy = 0.0
    public vx = 0.0

    // Constructor
    public Ball()
    {
        
    }

    void onBirth()
    {
        addTag("ball")
    }

    void tick()
    {
        smallMovement()
        smallMovement()
        smallMovement()
    }
    
    void smallMovement()
    {
        actor.moveBy( vx, vy )
        vy -= 0.1
        if ( vy > 5 ) {
            vy = 5
        }
        // Check if any pieces can capture the ball
        for (Role role : collisions("piece") ) {
            if ( role.checkCollision( this ) ) {
                break
            }
        }

    }

    // Boiler plate code - no need to change this
    public ArrayList getProperties()
    {
        return properties
    }
    
    // Boiler plate code - no need to change this
    public ClassName getClassName()
    {
        return new ClassName( Role, "Ball.groovy" )
    }
}
