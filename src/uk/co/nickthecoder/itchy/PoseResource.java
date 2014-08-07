/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.io.File;
import java.util.List;

import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.jame.JameException;
import uk.co.nickthecoder.jame.Surface;

public class PoseResource extends NamedResource implements Thumbnailed
{
    public static List<AbstractProperty<PoseResource, ?>> properties = AbstractProperty.findAnnotations(PoseResource.class);

    public static final int THUMBNAIL_WIDTH = 50;
    public static final int THUMBNAIL_HEIGHT = 50;

    private File file;

    @Property(label = "Pose", recurse = true)
    public ImagePose pose;

    @Property(label = "Shared")
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

    @Property(label = "Filename", sortOrder = -1, aliases = "filename")
    public File getFile()
    {
        return this.file;
    }

    public void setFile( File file ) throws JameException
    {
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
