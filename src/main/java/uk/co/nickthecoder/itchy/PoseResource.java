/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.BooleanProperty;
import uk.co.nickthecoder.itchy.property.DoubleProperty;
import uk.co.nickthecoder.itchy.property.FileProperty;
import uk.co.nickthecoder.itchy.property.PropertySubject;
import uk.co.nickthecoder.itchy.property.StringProperty;
import uk.co.nickthecoder.jame.JameException;
import uk.co.nickthecoder.jame.Surface;

public class PoseResource extends NamedResource implements Thumbnailed, PropertySubject<PoseResource>
{
    protected static List<Property<PoseResource, ?>> properties = new LinkedList<Property<PoseResource, ?>>();

    static {
        properties.add( new StringProperty<PoseResource>( "name" ));
        properties.add( new FileProperty<PoseResource>( "file" ).aliases( "filename" ));
        properties.add( new BooleanProperty<PoseResource>( "shared" ));
        properties.add( new DoubleProperty<PoseResource>( "pose.direction" ) );
        properties.add( new DoubleProperty<PoseResource>( "pose.offsetX" ) );
        properties.add( new DoubleProperty<PoseResource>( "pose.offsetY" ) );
    }
    
    public static final int THUMBNAIL_WIDTH = 50;
    public static final int THUMBNAIL_HEIGHT = 50;

    private File file;

    public ImagePose pose;

    public boolean shared;

    private Surface thumbnail;

    public PoseResource( Resources resources, String name, String filename ) throws JameException
    {
        super(resources, name);
        this.file = new File(filename);
        this.pose = new ImagePose(this.resources.resolveFilename(filename));
    }

    public PoseResource( ImagePose pose )
    {
        super();
        this.pose = pose;
        this.file = null;
    }

    @Override
    public List<Property<PoseResource, ?>> getProperties()
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
            file = new File( this.resources.makeRelativeFilename(file) );
        }
        
        if (! file.equals(this.file)) { 
            this.pose.load( this.resources.resolveFilename(file.getPath()) );
            this.thumbnail = null;
            // this.pose = new ImagePose(this.resources.resolveFilename(file.getPath()));
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

    @Override
    public Surface getThumbnail()
    {
        if (this.thumbnail == null) {

            Surface full = this.pose.getSurface();
            if ((full.getWidth() > THUMBNAIL_WIDTH) || (full.getHeight() > THUMBNAIL_HEIGHT)) {
                double scale = Math.min(THUMBNAIL_WIDTH / (double) full.getWidth(),
                    THUMBNAIL_HEIGHT / (double) full.getHeight());
                this.thumbnail = full.zoom(scale, scale, true);
            } else {
                this.thumbnail = full;
            }

        }
        return this.thumbnail;
    }
}
