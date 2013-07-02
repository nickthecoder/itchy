package uk.co.nickthecoder.itchy;

import java.util.HashMap;

import uk.co.nickthecoder.jame.JameException;
import uk.co.nickthecoder.jame.TrueTypeFont;

/**
 * Keeps track of a True Type Font at various point sizes. To render some text to a surface
 * construct a Font object with the path to the .ttf file, call "getSize" for the point size you
 * require, and then call the returned SDLTrueTypeFont's renderTextBlended (or renderTextSolid).
 */
public class Font
{
    private String filename;

    private HashMap<Integer, TrueTypeFont> sizes;

    public Font( String filename )
    {
        this.filename = filename;
        this.sizes = new HashMap<Integer, TrueTypeFont>();
    }

    public TrueTypeFont getSize( int size ) throws JameException
    {
        Integer key = new Integer(size);
        if (this.sizes.containsKey(key)) {
            return this.sizes.get(key);
        }
        TrueTypeFont ttf = new TrueTypeFont(this.filename, size);
        this.sizes.put(key, ttf);
        return ttf;
    }
}
