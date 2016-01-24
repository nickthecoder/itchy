/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.co.nickthecoder.itchy.property.FileProperty;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.StringProperty;
import uk.co.nickthecoder.jame.JameException;
import uk.co.nickthecoder.jame.TrueTypeFont;

/**
 * Keeps track of a True Type Font at various point sizes. To render some text to a surface construct a Font object with
 * the path to the
 * .ttf file, call "getSize" for the point size you require, and then call the returned SDLTrueTypeFont's
 * renderTextBlended (or
 * renderTextSolid).
 */
public class Font implements NamedSubject<Font> 
{
    protected static final List<Property<Font, ?>> properties = new ArrayList<Property<Font, ?>>();

    static {
        properties.add(new StringProperty<Font>("name"));
        properties.add(new FileProperty<Font>("file").aliases("filename"));
    }

    @Override
    public List<Property<Font, ?>> getProperties()
    {
        return properties;
    }
    private String name;
    
    private File file;

    private File resolvedFile;
    
    private HashMap<Integer, TrueTypeFont> sizes;

    public Font()
    {
        this.sizes = new HashMap<Integer, TrueTypeFont>();
    }

    public TrueTypeFont getSize(int size) throws JameException
    {
        Integer key = new Integer(size);
        if (this.sizes.containsKey(key)) {
            return this.sizes.get(key);
        }
        TrueTypeFont ttf = new TrueTypeFont(resolvedFile.getPath(), size);
        this.sizes.put(key, ttf);
        return ttf;
    }

    public File getFile()
    {
        return this.file;
    }

    public void setFile( File file )
    {
        this.file = file;
        this.resolvedFile = Resources.getCurrentResources().resolveFile(file);
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
