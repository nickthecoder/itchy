/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.LinkedList;
import java.util.List;

import uk.co.nickthecoder.itchy.property.DoubleProperty;
import uk.co.nickthecoder.itchy.property.IntegerProperty;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.StringProperty;
import uk.co.nickthecoder.jame.Surface;

public abstract class PoseResource implements Thumbnailed, Named
{
    protected static List<Property<PoseResource, ?>> properties = new LinkedList<Property<PoseResource, ?>>();

    static {
        properties.add( new StringProperty<PoseResource>( "name" ).allowBlank(false));
        properties.add( new DoubleProperty<PoseResource>( "pose.direction" ) );
        properties.add( new IntegerProperty<PoseResource>( "pose.offsetX" ) );
        properties.add( new IntegerProperty<PoseResource>( "pose.offsetY" ) );
    }

    public static final int THUMBNAIL_WIDTH = 50;
    public static final int THUMBNAIL_HEIGHT = 50;

    public String name;
    
    public ImagePose pose;

    private Surface thumbnail;

    PoseResource( String name )
    {
        this.name = name;
    }
    
    public String getName()
    {
        return this.name;
    }
    
    public void setName( String name )
    {
        this.name = name;
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
    
    protected void resetThumbnail()
    {
        this.thumbnail = null;
    }

    public String toString()
    {
        return "PoseResource name:'" + name + "' " + pose.toString();
    }
    
}
