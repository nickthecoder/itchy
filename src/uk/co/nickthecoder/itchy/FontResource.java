/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.io.File;
import java.util.List;

import uk.co.nickthecoder.itchy.util.AbstractProperty;
import uk.co.nickthecoder.itchy.util.Property;

public class FontResource extends NamedResource
{
    public static List<AbstractProperty<FontResource, ?>> properties = AbstractProperty.findAnnotations(FontResource.class);

    private File file;

    public Font font;

    public FontResource( Resources resources, String name, String filename )
    {
        super(resources, name);
        this.file = new File(filename);
        this.font = new Font(this.resources.resolveFilename(filename));
    }

    public void setFilename( String filename )
    {
        this.setFile( new File(filename));
    }
    
    public String getFilename()
    {
        return this.file.getPath();
    }
    
    @Property( label="Filename", aliases="filename")
    public File getFile()
    {
        return this.file;
    }
    
    public void setFile( File file )
    {
        this.file = file;
        this.font = new Font(this.resources.resolveFilename(this.file.getPath()));
    }
}
