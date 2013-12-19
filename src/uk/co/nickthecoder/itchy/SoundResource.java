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
import uk.co.nickthecoder.jame.Sound;

public class SoundResource extends NamedResource
{
    public static List<AbstractProperty<SoundResource, ?>> properties = AbstractProperty.findAnnotations(SoundResource.class);

    public File file;

    private Sound sound;

    public SoundResource( Resources resources, String name, String filename ) throws JameException
    {
        super(resources, name);
        this.sound = new Sound(this.resources.resolveFilename(filename));
        this.file = new File(filename);
    }

    @Property(label = "filename", aliases = "filename")
    public File getFile()
    {
        return this.file;
    }

    public void setFile( File file ) throws JameException
    {
        this.sound.free();
        this.sound = new Sound(this.resources.resolveFilename(file.getPath()));
        this.file = file;
    }

    public String getFilename()
    {
        return this.file.getPath();
    }

    public void setFilename( String filename ) throws JameException
    {
        setFile(new File(filename));
    }

    public Sound getSound()
    {
        return this.sound;
    }

}
