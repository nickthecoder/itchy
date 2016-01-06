package uk.co.nickthecoder.itchy;

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
    
    private String filename;
    
    private Surface surface;
    
    static {
        properties.add( new StringProperty<SpriteSheet>( "name" ));
        properties.add( new FileProperty<SpriteSheet>( "file" ).aliases( "filename" ));
    }
    
    private Set<PoseResource> sprites;

    public SpriteSheet(Resources resources, String name)
    {
        super(resources, name);
        this.sprites = new TreeSet<PoseResource>();
    }
    
    public Set<PoseResource> getSprites()
    {
        return this.sprites;
    }
    
    public void setFilename( String filename ) throws JameException
    {
        this.filename = filename;
        this.surface = new Surface(filename);
    }
    
    public String getFilename()
    {
        return filename;
    }
    
    public Surface getSurface()
    {
        return this.surface;
    }
    
    public Surface getThumbnail()
    {
        return null;
    }
    
    @Override
    public List<Property<SpriteSheet, ?>> getProperties()
    {
        return properties;
    }
}
