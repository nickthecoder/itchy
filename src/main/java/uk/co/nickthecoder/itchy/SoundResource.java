/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.FileProperty;
import uk.co.nickthecoder.itchy.property.PropertySubject;
import uk.co.nickthecoder.itchy.property.StringProperty;
import uk.co.nickthecoder.jame.JameException;
import uk.co.nickthecoder.jame.Sound;

public class SoundResource implements NamedSubject<SoundResource>, PropertySubject<SoundResource>
{
    protected static final List<Property<SoundResource, ?>> properties = new ArrayList<Property<SoundResource, ?>>();

    static {
        properties.add( new StringProperty<SoundResource>( "name" ).allowBlank(false));
        properties.add( new FileProperty<SoundResource>( "file" ).aliases( "filename" ) );
    }

    private String name;
    
    private File file;

    private Sound sound;

    public SoundResource()
    {
    }

    @Override
    public List<Property<SoundResource, ?>> getProperties()
    {
        return properties;
    }
    
    public File getFile()
    {
        return this.file;
    }

    public void setFile( File file ) throws JameException
    {
        if (this.sound != null) {
            this.sound.free();
        }
        this.sound = new Sound(Itchy.getGame().resources.resolveFilename(file.getPath()));
        this.file = file;
    }

    public Sound getSound()
    {
        return this.sound;
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

}
