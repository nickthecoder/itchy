package uk.co.nickthecoder.drunkinvaders;

import java.util.List;

import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.MouseListener;
import uk.co.nickthecoder.itchy.util.Property;
import uk.co.nickthecoder.itchy.util.StringProperty;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

public class DrunkButtonBehaviour extends Behaviour implements MouseListener
{
    public String action = "none";


    @Override
    protected void addProperties( List<Property<Behaviour, ?>> list )
    {
        super.addProperties( list );
        list.add( new StringProperty<Behaviour>( "Action", "action" ) );
    }

    @Override
    public void tick()
    {
    }

    @Override
    public boolean onMouseDown( MouseButtonEvent event )
    {
        if ( this.actor.contains( event.x, event.y ) ) {
            DrunkInvaders.singleton.action( this.action );
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
