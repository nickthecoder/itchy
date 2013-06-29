package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.jame.JameException;
import uk.co.nickthecoder.jame.Surface;

public class PoseResource extends NamedResource
{
    public static final int THUMBNAIL_WIDTH = 50;
    public static final int THUMBNAIL_HEIGHT = 50;

    public ImagePose pose;

    public String filename;

    private Surface thumbnail;

    public PoseResource( Resources resources, String name, String filename )
    		throws JameException
    {
        super( resources, name );
        this.filename = filename;
        this.pose = new ImagePose( this.resources.resolveFilename( filename ) );
    }

    public void setFilename( String filename )
    		throws JameException
    {
        this.pose = new ImagePose( this.resources.resolveFilename( filename ) );
        this.filename = filename;
    }

    public Surface getThumbnail()
    {
        if ( this.thumbnail == null ) {

        	Surface full = this.pose.getSurface();
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
