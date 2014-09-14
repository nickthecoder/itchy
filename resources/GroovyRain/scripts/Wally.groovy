import uk.co.nickthecoder.itchy.Role
import uk.co.nickthecoder.itchy.Actor
import uk.co.nickthecoder.itchy.AbstractRole
import uk.co.nickthecoder.itchy.ViewMouseListener
import uk.co.nickthecoder.itchy.MouseListenerView
import uk.co.nickthecoder.itchy.util.ClassName

import uk.co.nickthecoder.jame.event.MouseButtonEvent
import uk.co.nickthecoder.jame.event.MouseMotionEvent

class Wally extends AbstractRole implements ViewMouseListener
{
    public static properties = new ArrayList()
    
    // Poke (mouse click) the wally walking across out view, to make him/her jump.   
    public boolean onMouseDown(MouseListenerView view, MouseButtonEvent event)
    {
        if (getActor().pixelOverlap(event.x, event.y)) {
            getActor().event("boo", null, Actor.AnimationEvent.PARALLEL)
            return true
        }
        return false
    }

    public boolean onMouseUp(MouseListenerView view, MouseButtonEvent event)
    {
        return false
    }

    public boolean onMouseMove(MouseListenerView view, MouseMotionEvent event)
    {
        return false
    }
  
    public boolean isMouseListener()
    {
        return true;
    }

    // Boiler plate code - no need to change this
    public ArrayList getProperties()
    {
        return properties
    }
    
    // Boiler plate code - no need to change this
    public ClassName getClassName()
    {
        return new ClassName( Role, "Wally.groovy" )
    }
    
}

