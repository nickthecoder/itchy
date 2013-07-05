package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.jame.JameException;
import uk.co.nickthecoder.jame.JameRuntimeException;
import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.TrueTypeFont;

public abstract class AbstractTextPose implements Pose
{
    private double xAlignment = 0.5;

    private double yAlignment = 0.5;

    private Font font;

    private TrueTypeFont ttf;

    private double fontSize;

    private RGBA color;

    private String text = "";

    
    public AbstractTextPose( Font font, double fontSize )
    {
        // Make RGBA immutable, so we can have static final WHITE.
        this(font, fontSize, new RGBA(255, 255, 255));
    }

    public AbstractTextPose( Font font, double fontSize, RGBA color )
    {
        this.font = font;
        this.fontSize = fontSize;
        this.color = color;
    }


    public String getText()
    {
        return this.text;
    }

    public void setText( String text )
    {
        if (!this.text.equals(text)) {
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
        if (this.font != font) {
            this.font = font;
            this.ttf = null;
            this.clearSurfaceCache();
        }
    }

    public TrueTypeFont getTrueTypeFont()
    {
        try {
            if (this.ttf == null) {
                this.ttf = this.font.getSize((int) (this.fontSize));
            }
            return this.ttf;
        } catch (JameException e) {
            throw new JameRuntimeException(e);
        }
    }

    public double getFontSize()
    {
        return this.fontSize;
    }

    public void setFontSize( double fontSize )
    {
        if ((int) this.fontSize != (int) fontSize) {
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

    /*
     * public void adjustColor( int deltaRed, int deltaGreen, int deltaBlue ) { this.color.r +=
     * deltaRed; this.color.g += deltaGreen; this.color.b += deltaBlue; this.clearSurfaceCache(); }
     */

    public void adjustFontSize( double delta )
    {
        this.setFontSize(this.fontSize + delta);
    }

    public double getXAlignment()
    {
        return this.xAlignment;
    }

    public void setXAlignment( double xAlignment )
    {
        if ((xAlignment < 0) || (xAlignment > 1)) {
            throw new IllegalArgumentException("Alignments must be in the range (0..1)");
        }
        this.xAlignment = xAlignment;
    }

    public double getYAlignment()
    {
        return this.yAlignment;
    }

    public void setYAlignment( double yAlignment )
    {
        if ((yAlignment < 0) || (yAlignment > 1)) {
            throw new IllegalArgumentException("Alignments must be in the range (0..1)");
        }
        this.yAlignment = yAlignment;
    }
    public void setAlignment( double x, double y )
    {
        setXAlignment(x);
        setYAlignment(y);
    }

    @Override
    public int getOffsetX()
    {
        return (int) (this.getWidth() * this.xAlignment);
    }

    @Override
    public int getOffsetY()
    {
        return (int) (this.getHeight() * this.yAlignment);
    }

    @Override
    public double getDirection()
    {
        return 0;
    }

    public abstract int getWidth();

    public abstract int getHeight();

    protected abstract void clearSurfaceCache();

    // protected abstract void ensureCached();

    @Override
    public abstract Surface getSurface();

    @Override
    public abstract boolean changedSinceLastUsed();

}
