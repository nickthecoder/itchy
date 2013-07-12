package uk.co.nickthecoder.itchy.extras;

import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.MouseListener;
import uk.co.nickthecoder.itchy.util.Property;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

public class Button extends Behaviour implements MouseListener
{
    @Property(label = "Message")
    public String message = "none";

    @Override
    public void tick()
    {
    }

    @Override
    public boolean onMouseDown( MouseButtonEvent event )
    {
        if (this.actor.contains(event.x, event.y)) {
            Itchy.singleton.getGame().onMessage(this.message);
            return true;
        }
        return false;
    }

    @Override
    public boolean onMouseUp( MouseButtonEvent event )
    {
        return false;
    }

    @Override
    public boolean onMouseMove( MouseMotionEvent event )
    {
        return false;
    }

}
