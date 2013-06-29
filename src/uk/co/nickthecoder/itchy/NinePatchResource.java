package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.itchy.util.NinePatch;
import uk.co.nickthecoder.jame.JameException;
import uk.co.nickthecoder.jame.Surface;

public class NinePatchResource extends NamedResource
{

    public static final int THUMBNAIL_WIDTH = 100;
    public static final int THUMBNAIL_HEIGHT = 60;

    public NinePatch ninePatch;

    public String filename;

    private Surface thumbnail;

    public NinePatchResource( Resources resources, String name, String filename, NinePatch ninePatch )
    {
        super( resources, name );
        this.filename = filename;
        this.ninePatch = ninePatch;
    }

    public void setFilename( String filename )
        throws JameException
    {
        this.ninePatch.loadImage( this.resources.resolveFilename( filename ) );
        this.filename = filename;
    }

    public Surface getThumbnail()
    {
        if ( this.thumbnail == null ) {
        	
            Surface full = this.ninePatch.getSurface();
            if ( ( full.getWidth() > THUMBNAIL_WIDTH ) || ( full.getHeight() > THUMBNAIL_HEIGHT ) ) {
                double scale = Math.min( THUMBNAIL_WIDTH / (double) full.getWidth(), THUMBNAIL_HEIGHT
                    / (double) full.getHeight() );
                this.thumbnail = full.zoom( scale, scale, true );
            } else {
                this.thumbnail = full;
            }

        }
        return this.thumbnail;
    }

}
