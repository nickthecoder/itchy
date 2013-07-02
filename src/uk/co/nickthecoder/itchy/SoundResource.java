package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.jame.JameException;
import uk.co.nickthecoder.jame.Sound;

public class SoundResource extends NamedResource
{
    public String filename;

    private Sound sound;

    public SoundResource( Resources resources, String name, String filename ) throws JameException
    {
        super(resources, name);
        this.sound = new Sound(this.resources.resolveFilename(filename));
        this.filename = filename;
    }

    public void setFilename( String filename ) throws JameException
    {
        this.sound.free();
        this.sound = new Sound(this.resources.resolveFilename(filename));
        this.filename = filename;
    }

    public Sound getSound()
    {
        return this.sound;
    }

}
