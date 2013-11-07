/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.List;

import uk.co.nickthecoder.itchy.util.AbstractProperty;
import uk.co.nickthecoder.itchy.util.Property;
import uk.co.nickthecoder.jame.JameException;
import uk.co.nickthecoder.jame.Surface;

public class PoseResource extends NamedResource
{
    public static List<AbstractProperty<PoseResource, ?>> properties = 
        AbstractProperty.findAnnotations(PoseResource.class);

    public static final int THUMBNAIL_WIDTH = 50;
    public static final int THUMBNAIL_HEIGHT = 50;

    @Property(label="Filename")
    public String filename;

    @Property(label="Pose", recurse=true)
    public ImagePose pose;

    @Property(label="Shared")
    public boolean shared;
    
    private Surface thumbnail;

    public PoseResource( Resources resources, String name, String filename ) throws JameException
    {
        super(resources, name);
        this.filename = filename;
        this.pose = new ImagePose(this.resources.resolveFilename(filename));
    }

    public PoseResource( ImagePose pose )
    {
        super();
        this.pose = pose;
        this.filename = null;
    }

    public void setFilename( String filename ) throws JameException
    {
        this.pose = new ImagePose(this.resources.resolveFilename(filename));
        this.filename = filename;
    }

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
