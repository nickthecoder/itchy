package uk.co.nickthecoder.itchy.gui;

import java.util.HashMap;

import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.SoundResource;

public abstract class SoundPicker extends Picker<SoundResource>
{

    public static HashMap<String, SoundResource> createSoundsHashMap()
    {
        Resources resources = Itchy.getGame().resources;
        HashMap<String, SoundResource> sounds = new HashMap<String, SoundResource>();
        for (String name : resources.soundNames()) {
            SoundResource soundResource = resources.getSound(name);
            sounds.put(name, soundResource);
        }
        return sounds;
    }

    public SoundPicker()
    {
        super( "Pick Sound", createSoundsHashMap() );
    }

    public SoundPicker(SoundResource selected)
    {
        super( "Pick Sound", createSoundsHashMap(), selected );
    }

}
