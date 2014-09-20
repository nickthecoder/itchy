import uk.co.nickthecoder.itchy.Director
import uk.co.nickthecoder.itchy.AbstractDirector
import uk.co.nickthecoder.itchy.util.ClassName

public class ${Name} extends AbstractDirector
{    
    // Constructor
    public ${Name}()
    {
    }
        
    public void tick()
    {
        // TODO Called 60 times a second
    }


    // Boiler plate code - no need to change this
    public ClassName getClassName()
    {
        return new ClassName( Director, "${NAME}.groovy" )
    }

}

