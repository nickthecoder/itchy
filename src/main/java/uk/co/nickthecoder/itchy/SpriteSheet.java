package uk.co.nickthecoder.itchy;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import uk.co.nickthecoder.itchy.property.FileProperty;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.PropertySubject;
import uk.co.nickthecoder.itchy.property.StringProperty;
import uk.co.nickthecoder.jame.JameException;
import uk.co.nickthecoder.jame.Surface;

public class SpriteSheet extends NamedResource implements PropertySubject<SpriteSheet>
{
    protected static List<Property<SpriteSheet, ?>> properties = new LinkedList<Property<SpriteSheet, ?>>();
        
    private static final int THUMBNAIL_WIDTH = 80;
    private static final int THUMBNAIL_HEIGHT = 80;

    private File file;
    
    private Surface surface;
    
    private Surface thumbnail;
    
    static {
        properties.add( new StringProperty<SpriteSheet>( "name" ));
        properties.add( new FileProperty<SpriteSheet>( "file" ).aliases( "filename" ));
    }
    
    private Set<Sprite> sprites;

    public SpriteSheet(Resources resources, String name)
    {
        super(resources, name);
        this.sprites = new TreeSet<Sprite>();
    }
    
    public Surface getSurface()
    {
        return this.surface;
    }
    
    public Set<Sprite> getSprites()
    {
        return this.sprites;
    }
    
    public void addSprite( Sprite sprite )
    {
        this.sprites.add( sprite );
    }
    
    public void setFilename( String filename ) throws JameException
    {
        setFile( new File(filename) );
    }
    
    public String getFilename()
    {
        return this.file.getPath();
    }
    
    public void setFile( File file ) throws JameException
    {
        this.file = file;
        this.surface = new Surface(this.resources.resolveFile(file));
    }
    
    public File getFile()
    {
        return file;
    }

    public Surface getThumbnail()
    {
        if (this.thumbnail == null) {

            if ((surface.getWidth() > THUMBNAIL_WIDTH) || (surface.getHeight() > THUMBNAIL_HEIGHT)) {
                double scale = Math.min(THUMBNAIL_WIDTH / (double) surface.getWidth(),
                    THUMBNAIL_HEIGHT / (double) surface.getHeight());
                this.thumbnail = surface.zoom(scale, scale, true);
            } else {
                this.thumbnail = surface;
            }

        }
        return this.thumbnail;
    }
    
    @Override
    public List<Property<SpriteSheet, ?>> getProperties()
    {
        return properties;
    }
}
