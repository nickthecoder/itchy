package uk.co.nickthecoder.itchy.gui;

import uk.co.nickthecoder.itchy.Font;

import uk.co.nickthecoder.jame.JameException;
import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.TrueTypeFont;

public class Label extends SurfaceComponent
{
    private String text;

    public Label( String text )
    {
        this.text = text;
        this.type = "label";
    }

    public String getText()
    {
        return text;
    }

    public void setText( String text )
    {
        if ( this.text.equals( text ) ) {
        } else {
            this.text = text;
            this.clearPlainSurface();
            this.invalidate();
            if ( this.parent != null ) {
                this.parent.forceLayout();
            }
        }
    }

    public void setFontSize( int value )
    {
        super.setFontSize( value );
        this.clearPlainSurface();
    }

    public void setFont( Font font )
    {
        super.setFont( font );
        this.clearPlainSurface();
    }

    public void setColor( RGBA color )
    {
        super.setColor( color );
        this.clearPlainSurface();
    }

    protected void createPlainSurface()
    {
    	try {
    		TrueTypeFont ttf = this.getFont().getSize( this.getFontSize() );
    		this.plainSurface = ttf.renderBlended( this.text, this.getColor() );
    	} catch (JameException e) {
    		e.printStackTrace();
    	}
    }

    public String toString()
    {
        return super.toString() + " : " + this.text;
    }
}
