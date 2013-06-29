package uk.co.nickthecoder.drunkinvaders;

import java.util.List;

import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.Font;
import uk.co.nickthecoder.itchy.MouseListener;
import uk.co.nickthecoder.itchy.TextPose;
import uk.co.nickthecoder.itchy.util.FontProperty;
import uk.co.nickthecoder.itchy.util.IntegerProperty;
import uk.co.nickthecoder.itchy.util.Property;
import uk.co.nickthecoder.itchy.util.RGBAProperty;
import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

public class PickLevelBehaviour extends Behaviour implements MouseListener
{
    public int levelNumber;

    public Font font;

    public int fontSize = 22;

    public RGBA fontColor = new RGBA( 255, 255, 255 );

    public RGBA shadowColor = new RGBA( 0,0,0 );

    @Override
    protected void addProperties( List<Property<Behaviour, ?>> list )
    {
        super.addProperties( list );
        list.add( new IntegerProperty<Behaviour>( "Level", "levelNumber" ) );
        list.add( new FontProperty<Behaviour>( "Font", "font" ) );
        list.add( new IntegerProperty<Behaviour>( "Font Size", "fontSize" ) );
        list.add( new RGBAProperty<Behaviour>( "Font Color", "fontColor", false, false ) );
    }

    @Override
    public void init()
    {
        if ( DrunkInvaders.singleton.completedLevel( this.levelNumber ) ) {
            this.actor.event( "completed" );
        }

        if ( this.font != null ) {
            TextPose shadowPose = new TextPose( String.valueOf( this.levelNumber ), this.font, this.fontSize, this.shadowColor );

            TextPose textPose = new TextPose( String.valueOf( this.levelNumber ), this.font, this.fontSize, this.fontColor );
            this.actor.getAppearance().superimpose( shadowPose, 2, 2 );
            this.actor.getAppearance().superimpose( textPose, 0, 0 );
        }
    }

    @Override
    public void tick()
    {
    }

    @Override
    public boolean onMouseDown( MouseButtonEvent event )
    {
        if ( this.actor.contains( event.x, event.y ) ) {
            DrunkInvaders.singleton.play( this.levelNumber );
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
