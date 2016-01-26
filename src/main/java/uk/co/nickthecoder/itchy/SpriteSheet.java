package uk.co.nickthecoder.itchy;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import uk.co.nickthecoder.itchy.property.FileProperty;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.StringProperty;
import uk.co.nickthecoder.jame.JameException;
import uk.co.nickthecoder.jame.Surface;

public class SpriteSheet implements NamedSubject<SpriteSheet>
{
    protected static List<Property<SpriteSheet, ?>> properties = new LinkedList<Property<SpriteSheet, ?>>();

    static {
        properties.add(new StringProperty<SpriteSheet>("name"));
        properties.add(new FileProperty<SpriteSheet>("file").aliases("filename"));
    }

    @Override
    public List<Property<SpriteSheet, ?>> getProperties()
    {
        return properties;
    }

    private static final int THUMBNAIL_WIDTH = 80;
    private static final int THUMBNAIL_HEIGHT = 80;

    private String name;

    private File file;

    private Surface surface;

    private Surface thumbnail;

    private Set<Sprite> sprites;

    public SpriteSheet()
    {
        sprites = new HashSet<Sprite>();
    }

    public SpriteSheet( String name, File file ) throws JameException
    {
        this();
        this.name = name;
        this.setFile( file );
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void setName(String name)
    {
        this.name = name;
    }

    public Surface getSurface()
    {
        return surface;
    }

    public Set<Sprite> getSprites()
    {
        return sprites;
    }

    public void addSprite(Sprite sprite)
    {
        sprites.add(sprite);
    }

    public void removeSprite(Sprite sprite)
    {
        sprites.remove(sprite);
    }

    public final void setFile(File file) throws JameException
    {
        this.file = file;
        surface = new Surface(Itchy.getGame().resources.resolveFile(file));
    }

    public File getFile()
    {
        return file;
    }

    public Surface getThumbnail()
    {
        if (thumbnail == null) {

            if ((surface.getWidth() > THUMBNAIL_WIDTH) || (surface.getHeight() > THUMBNAIL_HEIGHT)) {
                double scale = Math.min(THUMBNAIL_WIDTH / (double) surface.getWidth(),
                    THUMBNAIL_HEIGHT / (double) surface.getHeight());
                thumbnail = surface.zoom(scale, scale, true);
            } else {
                thumbnail = surface;
            }

        }
        return thumbnail;
    }

    public String toString()
    {
        return "SpriteSheet name:'" + name + "' file:'" + file + "' size:" + sprites.size();
    }
}
