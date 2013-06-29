package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.jame.JameException;
import uk.co.nickthecoder.jame.JameRuntimeException;
import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.TrueTypeFont;

public class TextPose implements Pose
{
    private Surface surface;

    private double xAlignment = 0.5;

    private double yAlignment = 0.5;

    private Font font;

    private double fontSize;

    private RGBA color;

    private String text;

    private boolean changed = false;

    public TextPose( String text, Font font, double fontSize )
    {
        this( text, font, fontSize, new RGBA( 255, 255, 255 ) );
    }

    public TextPose( String text, Font font, double fontSize, RGBA color )
    {
        this.text = text;
        this.color = color;
        this.font = font;
        this.fontSize = fontSize;
    }

    public String getText()
    {
        return this.text;
    }

    public void setText( String text )
    {
        if ( ! this.text.equals( text ) ) {
            this.text = text;
            this.clearSurfaceCache();
        }

    }
    public Font getFont()
    {
        return this.font;
    }
    public void setFont( Font font )
    {
        if ( this.font != font ) {
            this.font = font;
            this.clearSurfaceCache();
        }
    }

    public double getFontSize()
    {
        return this.fontSize;
    }

    public void setFontSize( double fontSize )
    {
        if ( (int) this.fontSize != (int) fontSize ) {
            this.clearSurfaceCache();
        }
        this.fontSize = fontSize;
    }

    public RGBA getColor()
    {
        return this.color;
    }

    public void setColor( RGBA color )
    {
        this.color = color;
        this.clearSurfaceCache();
    }

    public void adjustColor( int deltaRed, int deltaGreen, int deltaBlue )
    {
        this.color.r += deltaRed;
        this.color.g += deltaGreen;
        this.color.b += deltaBlue;
        this.clearSurfaceCache();
    }

    public void adjustFontSize( double delta )
    {
        this.setFontSize( this.fontSize + delta );
    }

    private void clearSurfaceCache()
    {
        this.changed = true;

        if ( this.surface != null ) {
            this.surface.free();
        }
        this.surface = null;
    }

    public double getXAlignment()
    {
        return this.xAlignment;
    }

    public void setXAlignment( double xAlignment )
    {
        if ( ( xAlignment < 0 ) || ( xAlignment > 1 ) ) {
            throw new IllegalArgumentException( "Alignments must be in the range (0..1)" );
        }
        this.xAlignment = xAlignment;
    }

    public double getYAlignment()
    {
        return this.yAlignment;
    }

    public void setYAlignment( double yAlignment )
    {
        if ( ( yAlignment < 0 ) || ( yAlignment > 1 ) ) {
            throw new IllegalArgumentException( "Alignments must be in the range (0..1)" );
        }
        this.yAlignment = yAlignment;
    }

    // TODO Allow alignment to be set to the font baseline.

    @Override
    public int getOffsetX()
    {
        this.ensureCached();
        return (int) ( this.surface.getWidth() * this.xAlignment );
    }

    @Override
    public int getOffsetY()
    {
        this.ensureCached();
        return (int) ( this.surface.getHeight() * this.yAlignment );
    }

    private void ensureCached()
    {
        if ( this.surface == null ) {
        	try {
        		TrueTypeFont ttf = this.font.getSize( (int) this.fontSize );
        		this.surface = ttf.renderBlended( this.text, this.color );
        	} catch (JameException e) {
        		throw new JameRuntimeException( e );
        	}
        }
    }

    @Override
    public double getDirection()
    {
        return 0;
    }

    @Override
    public Surface getSurface()
    {
        this.ensureCached();
        return this.surface;
    }

    @Override
    public boolean changedSinceLastUsed()
    {
        return this.changed;
    }

    @Override
    public void used()
    {
        this.changed = false;
    }

}
