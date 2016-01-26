package uk.co.nickthecoder.itchy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.property.BooleanProperty;
import uk.co.nickthecoder.itchy.property.FileProperty;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.PropertySubject;
import uk.co.nickthecoder.jame.JameException;

public class FilePoseResource extends PoseResource implements PropertySubject<FilePoseResource>
{
    protected static List<Property<FilePoseResource, ?>> properties = new ArrayList<Property<FilePoseResource, ?>>();

    static {
        Property.addAll( PoseResource.properties, properties );
        properties.add( 1, new FileProperty<FilePoseResource>( "file" ).aliases( "filename" ));
        properties.add( new BooleanProperty<FilePoseResource>( "shared" ));

    }

    @Override
    public List<Property<FilePoseResource, ?>> getProperties()
    {
        return properties;
    }
    
    private File file;

    public boolean shared;

    public FilePoseResource( String name, File file ) throws JameException
    {
        super(name);
        this.file = file;
        this.pose = new ImagePose(Resources.getCurrentResources().resolveFile(file).getPath());
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

    public String toString()
    {
        return "FilePoseResource name:'" + name + "' file:'" + file + "' " + pose.toString();
    }
}
