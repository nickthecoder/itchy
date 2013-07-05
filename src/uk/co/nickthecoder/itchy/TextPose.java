package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.TrueTypeFont;

public class TextPose extends AbstractTextPose
{
    private Surface surface;

    private boolean changed = false;

    
    public TextPose( String text, Font font, double fontSize )
    {
        this(text, font, fontSize, new RGBA(255, 255, 255));
    }

    public TextPose( String text, Font font, double fontSize, RGBA color )
    {
        super( font, fontSize, color );
        this.setText( text );
    }
    
    protected void clearSurfaceCache()
    {
        this.changed = true;

        if (this.surface != null) {
            this.surface.free();
        }
        this.surface = null;
    }

    private void ensureCached()
    {
        if (this.surface == null) {
            TrueTypeFont ttf = getTrueTypeFont();
            this.surface = ttf.renderBlended(this.getText(), this.getColor());
        }
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

    @Override
    public int getWidth()
    {
        this.ensureCached();
        return this.surface.getWidth();
    }

    @Override
    public int getHeight()
    {
        this.ensureCached();
        return this.surface.getHeight();
    }

}
