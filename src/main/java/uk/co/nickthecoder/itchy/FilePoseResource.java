package uk.co.nickthecoder.itchy;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import uk.co.nickthecoder.itchy.property.BooleanProperty;
import uk.co.nickthecoder.itchy.property.DoubleProperty;
import uk.co.nickthecoder.itchy.property.FileProperty;
import uk.co.nickthecoder.itchy.property.IntegerProperty;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.PropertySubject;
import uk.co.nickthecoder.itchy.property.StringProperty;
import uk.co.nickthecoder.jame.JameException;

public class FilePoseResource extends PoseResource implements PropertySubject<FilePoseResource>
{
    protected static List<Property<FilePoseResource, ?>> properties = new LinkedList<Property<FilePoseResource, ?>>();

    static {
        properties.add( new StringProperty<FilePoseResource>( "name" ));
        properties.add( new FileProperty<FilePoseResource>( "file" ).aliases( "filename" ));
        properties.add( new BooleanProperty<FilePoseResource>( "shared" ));
        properties.add( new DoubleProperty<FilePoseResource>( "pose.direction" ) );
        properties.add( new IntegerProperty<FilePoseResource>( "pose.offsetX" ) );
        properties.add( new IntegerProperty<FilePoseResource>( "pose.offsetY" ) );
    }
    
    private File file;

    public FilePoseResource( String name, String filename ) throws JameException
    {
        super(name);
        this.file = new File(filename);
        this.pose = new ImagePose(Resources.getCurrentResources().resolveFilename(filename));
    }

    @Override
    public List<Property<FilePoseResource, ?>> getProperties()
    {
        return properties;
    }

    public File getFile()
    {
        return this.file;
    }

    public void setFile( File file ) throws JameException
    {
        if (file.isAbsolute()) {
            // Lets try to make file relative to the resources directory.
            file = new File( Resources.getCurrentResources().makeRelativeFilename(file) );
        }
        
        if (! file.equals(this.file)) { 
            this.pose.load( Resources.getCurrentResources().resolveFilename(file.getPath()) );
            this.resetThumbnail();
            this.file = file;
        }
    }

    public String getFilename()
    {
        return this.file.getPath();
    }

    public void setFilename( String filename ) throws JameException
    {
        setFile(new File(filename));
    }

}
