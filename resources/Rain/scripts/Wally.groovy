import uk.co.nickthecoder.itchy.*
import uk.co.nickthecoder.itchy.util.*

import uk.co.nickthecoder.jame.event.*

class Wally extends AbstractRole implements ViewMouseListener
{
    public static properties = new ArrayList()
    
    // Poke (mouse click) the wally walking across out view, to make him/her jump.   
    public void onMouseDown(MouseListenerView view, MouseButtonEvent event)
    {
        if (actor.pixelOverlap(event.x, event.y)) {
            actor.event("boo", null, Actor.AnimationEvent.PARALLEL)
            event.stopPropagation()
        }
    }

    public void onMouseUp(MouseListenerView view, MouseButtonEvent event)
    {
    }

    public void onMouseMove(MouseListenerView view, MouseMotionEvent event)
    {
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

