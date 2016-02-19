package uk.co.nickthecoder.itchy;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import uk.co.nickthecoder.itchy.util.StringUtils;
import uk.co.nickthecoder.jame.Sound;

public class StandardSoundManager implements SoundManager
{
    private List<SoundEntry> soundEntries;

    public StandardSoundManager()
    {
        this.soundEntries = new LinkedList<SoundEntry>();
    }

    public void tick()
    {
        for (Iterator<SoundEntry> i = this.soundEntries.iterator(); i.hasNext();) {
            SoundEntry entry = i.next();

            if ((entry.managedSound.fadeOnDeath) && (entry.actor != null) && (entry.actor.isDead())) {
                entry.fadeOut();
                continue;
            }

            if (!entry.getSound().isPlaying()) {
                i.remove();
                continue;
            }
        }
    }

    public void play( Actor actor, String eventName, ManagedSound ms )
    {
        if (ms.multipleRole != ManagedSound.MultipleRole.PLAY_BOTH) {
            // If this sound is already playing, then we can't play both of them

            for (Iterator<SoundEntry> i = this.soundEntries.iterator(); i.hasNext();) {
                SoundEntry entry = i.next();

                if ((!entry.fadingOut) && (entry.getSound() == ms.soundResource.getSound())) {
                    // The sound is already playing.

                    if (actor == entry.actor) {
                        if (ms.multipleRole == ManagedSound.MultipleRole.IGNORE_SECOND) {
                            return;

                        } else if (ms.multipleRole == ManagedSound.MultipleRole.FADE_FIRST) {
                            entry.fadeOut();

                        } else { // Must be STOP_FIRST
                            entry.getSound().stop();
                            i.remove();
                        }
                    }
                }
            }
        }

        if (ms.soundResource.getSound().play()) {
            SoundEntry entry = new SoundEntry(actor, eventName, ms);
            this.soundEntries.add(entry);
        } else {
            if (killLowestPriority(ms.priority)) {
                if (ms.soundResource.getSound().play()) {
                    SoundEntry entry = new SoundEntry(actor, eventName, ms);
                    this.soundEntries.add(entry);
                }
            }
        }
    }

    /**
     * Fades out the sound, if it is still playing.
     * 
     * @param actor
     * @param eventName
     */
    public void end( Actor actor, String eventName )
    {
        for (Iterator<SoundEntry> i = this.soundEntries.iterator(); i.hasNext();) {
            SoundEntry entry = i.next();

            if (entry.matches(actor, eventName)) {
                if (entry.managedSound.fadeOutSeconds <= 0) {

                    entry.getSound().stop();
                    i.remove();

                } else {
                    entry.fadeOut();
                }
            }
        }
    }

    public void stopAll()
    {
        for (SoundEntry entry : this.soundEntries) {
            entry.getSound().stop();
        }
        this.soundEntries.clear();
    }

    private boolean killLowestPriority( int belowPriority )
    {
        SoundEntry lowestEntry = null;
        int lowestPriority = belowPriority;

        for (SoundEntry entry : this.soundEntries) {
            if (entry.managedSound.priority < lowestPriority) {
                lowestPriority = entry.managedSound.priority;
                lowestEntry = entry;
            }
        }
        if (lowestEntry == null) {
            return false;

        } else {
            kill(lowestEntry);
            return true;
        }
    }

    private void kill( SoundEntry entry )
    {
        entry.getSound().stop();
        this.soundEntries.remove(entry);
    }

    class SoundEntry
    {
        public Actor actor;

        public String eventName;

        public ManagedSound managedSound;

        public boolean fadingOut = false;

        public SoundEntry( Actor actor, String eventName, ManagedSound managedSound )
        {
            this.actor = actor;
            this.eventName = eventName;
            this.managedSound = managedSound;
        }

        public Sound getSound()
        {
            return this.managedSound.soundResource.getSound();
        }

        public boolean matches( Actor actor, String eventName )
        {
            return (actor == this.actor) && (StringUtils.equals(eventName, this.eventName));
        }

        public void fadeOut()
        {
            if (!this.fadingOut) {
                getSound().fadeOut(this.managedSound.getFadeOutMillis());
                this.fadingOut = true;
            }
        }
    }
}
