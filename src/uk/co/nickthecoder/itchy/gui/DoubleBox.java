package uk.co.nickthecoder.itchy.gui;

import java.text.DecimalFormat;
import java.text.Format;

import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.jame.Keys;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;

public class DoubleBox extends EntryBox<DoubleBox>
{
    public Format format;

    public DoubleBox( double value )
    {
        this( value, new DecimalFormat( "#.####" ) );
    }

    public DoubleBox( double value, Format format )
    {
        super( format.format( value ) );
        this.format = format;
        this.boxWidth = 10;
    }

    public double getValue()
    {
        return Double.parseDouble( this.getText() );
    }

    @Override
    protected boolean setEntryText( String value )
    {
        if ( value.equals( "" ) || value.equals( "-" ) ) {
        } else {
            try {
                Double.parseDouble( value + "0" );
            } catch ( NumberFormatException e ) {
                return false;
            }
        }
        return super.setEntryText( value );
    }

    public void setValue( double value )
    {
        this.setEntryText( this.format.format( value ) );
    }

    public void adjust( double delta )
    {
        this.setValue( this.getValue() + delta );
    }

    @Override
    public boolean mouseDown( MouseButtonEvent mbe )
    {
        if ( this.hasFocus ) {

            double amount = 1;
            if ( Itchy.singleton.isKeyDown( Keys.LSHIFT ) || Itchy.singleton.isKeyDown( Keys.RSHIFT ) ) {
                amount = 10;
            } else if ( Itchy.singleton.isKeyDown( Keys.LCTRL ) || Itchy.singleton.isKeyDown( Keys.RCTRL ) ) {
                amount = 0.1;
            }

            if ( mbe.button == MouseButtonEvent.BUTTON_WHEELUP ) {
                this.adjust( amount );
                return true;

            } else if ( mbe.button == MouseButtonEvent.BUTTON_WHEELDOWN ) {
                this.adjust( -amount );
                return true;
            }
        }

        return super.mouseDown( mbe );
    }

}
