/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.EnumProperty;
import uk.co.nickthecoder.itchy.property.FileProperty;
import uk.co.nickthecoder.itchy.property.IntegerProperty;
import uk.co.nickthecoder.itchy.property.PropertySubject;
import uk.co.nickthecoder.itchy.property.RGBAProperty;
import uk.co.nickthecoder.itchy.property.StringProperty;
import uk.co.nickthecoder.itchy.util.NinePatch;
import uk.co.nickthecoder.itchy.util.NinePatch.Middle;
import uk.co.nickthecoder.jame.JameException;
import uk.co.nickthecoder.jame.Surface;

public class NinePatchResource extends NamedResource implements PropertySubject<NinePatchResource>
{
    protected static final List<Property<NinePatchResource, ?>> properties = new ArrayList<Property<NinePatchResource, ?>>();
    
    static {
        properties.add( new StringProperty<NinePatchResource>( "name" ));
        properties.add( new FileProperty<NinePatchResource>( "file" ).aliases( "filename" ) );
        properties.add( new IntegerProperty<NinePatchResource>( "ninePatch.marginTop" ));
        properties.add( new IntegerProperty<NinePatchResource>( "ninePatch.marginRight" ));
        properties.add( new IntegerProperty<NinePatchResource>( "ninePatch.marginBottom" ));
        properties.add( new IntegerProperty<NinePatchResource>( "ninePatch.marginLeft" ));
        properties.add( new EnumProperty<NinePatchResource, Middle>( "ninePatch.middle", Middle.class ));
        properties.add( new RGBAProperty<NinePatchResource>( "ninePatch.backgroundColor" ));
    }
    
    public static final int THUMBNAIL_WIDTH = 100;
    public static final int THUMBNAIL_HEIGHT = 60;

    public NinePatch ninePatch;

    private File file;

    private Surface thumbnail;

    public NinePatchResource( Resources resources, String name, String filename, NinePatch ninePatch )
    {
        super(resources, name);
        this.file = new File(filename);
        this.ninePatch = ninePatch;
    }

    @Override
    public List<Property<NinePatchResource, ?>> getProperties()
    {
        return properties;
    }
    
    public void setFilename( String filename ) throws JameException
    {
        setFile(new File(filename));
    }

    public String getFilename()
    {
        return this.file.getPath();
    }

    public File getFile()
    {
        return this.file;
    }

    public void setFile( File file ) throws JameException
    {
        this.ninePatch.loadImage(this.resources.resolveFilename(file.getPath()));
        this.file = file;
    }

    public Surface getThumbnail()
    {
        if (this.thumbnail == null) {

            Surface full = this.ninePatch.getSurface();
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
