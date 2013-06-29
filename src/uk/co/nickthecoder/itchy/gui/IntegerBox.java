package uk.co.nickthecoder.itchy.gui;

import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.jame.Keys;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;

public class IntegerBox extends EntryBox<IntegerBox>
{

    public IntegerBox( int value )
    {
        super( Integer.toString( value ) );
        this.addStyle( "integerBox" );
        this.boxWidth = 10;
    }

    public int getValue()
    {
        return Integer.parseInt( this.getText() );
    }

    @Override
    protected boolean setEntryText( String value )
    {
        if ( value.equals( "" ) || value.equals( "-" ) ) {
        } else {
            try {
                Integer.parseInt( value );
            } catch ( NumberFormatException e ) {
                return false;
            }
        }
        return super.setEntryText( value );
    }

    public void setValue( int value )
    {
        this.setEntryText( Integer.toString( value ) );
    }

    public void adjust( int delta )
    {
        this.setValue( this.getValue() + delta );
    }

    @Override
    public boolean mouseDown( MouseButtonEvent mbe )
    {
        if ( this.hasFocus ) {
            if ( mbe.button == MouseButtonEvent.BUTTON_WHEELUP ) {
                this.adjust( Itchy.singleton.isKeyDown( Keys.LSHIFT ) || Itchy.singleton.isKeyDown( Keys.RSHIFT ) ? 10 : 1 );
                return true;

            } else if ( mbe.button == MouseButtonEvent.BUTTON_WHEELDOWN ) {
                this.adjust( Itchy.singleton.isKeyDown( Keys.LSHIFT ) || Itchy.singleton.isKeyDown( Keys.RSHIFT ) ? -10
                    : -1 );
                return true;
            }
        }

        return super.mouseDown( mbe );
    }

}
