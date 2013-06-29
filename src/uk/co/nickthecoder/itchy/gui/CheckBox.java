package uk.co.nickthecoder.itchy.gui;

import uk.co.nickthecoder.jame.event.MouseButtonEvent;

public class CheckBox extends ClickableContainer
{
    private boolean value;

    private final ImageComponent image;

    public CheckBox( boolean value )
    {
        super();
        this.image = new ImageComponent();
        this.addChild( this.image );

        this.setValue( value );
        this.type = "checkbox";

    }

    public CheckBox()
    {
        this( false );
    }

    public boolean getValue()
    {
        return this.value;
    }

    public final void setValue( boolean value )
    {
        if ( this.value != value ) {
            this.value = value;
            if ( this.value ) {
                this.addStyle( "checked" );
            } else {
                this.removeStyle( "checked" );
            }
            this.invalidate();
        }
        this.image.setVisible( value );
    }

    @Override
    public int getNaturalWidth()
    {
        return this.image.getRequiredHeight() + this.image.getMarginLeft() + this.image.getMarginRight()
            + this.getPaddingLeft() + this.getPaddingRight();
    }

    @Override
    public int getNaturalHeight()
    {
        return this.image.getRequiredWidth() + this.image.getMarginTop() + this.image.getMarginBottom()
            + this.getPaddingTop() + this.getPaddingBottom();
    }

    @Override
    public void onClick( MouseButtonEvent mbe )
    {
        this.setValue( !this.getValue() );
    }
}
