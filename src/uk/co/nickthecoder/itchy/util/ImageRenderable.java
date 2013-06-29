package uk.co.nickthecoder.itchy.util;

import uk.co.nickthecoder.itchy.Renderable;
import uk.co.nickthecoder.jame.JameException;
import uk.co.nickthecoder.jame.Surface;

public abstract class ImageRenderable implements Renderable
{
    protected Surface surface;

    public ImageRenderable( Surface surface )
    {
        this.surface = surface;
    }

    public ImageRenderable( String filename )
    		throws JameException
    {
        this( new Surface( filename ) );
    }

    public void loadImage( String filename )
    		throws JameException
    {
        this.surface = new Surface( filename );
    }

    public Surface getSurface()
    {
        return this.surface;
    }

}
