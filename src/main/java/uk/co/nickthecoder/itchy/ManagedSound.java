/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.List;

import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.PropertySubject;

public class ManagedSound implements PropertySubject<ManagedSound>
{
    private static final List<AbstractProperty<ManagedSound, ?>> properties =
        AbstractProperty.<ManagedSound> findAnnotations(ManagedSound.class);

    public enum MultipleRole
    {
        PLAY_BOTH, STOP_FIRST, FADE_FIRST, IGNORE_SECOND
    };

    public SoundResource soundResource;

    /**
     * The priority of the sound. The recommended range is 0 (lowest priority) to 3 (highest priority), but you are free to use whatever
     * range you want.
     * 
     * There is a limit on how many sounds can play simultaneously, and when this limit is exceeded high priority sounds will cause low
     * priority sounds to end abruptly. If there are no low priority sounds, then new sounds won't play when the limit is exceeded.
     */
    @Property(label = "Priority", hint = "0=lowest")
    public int priority = 1;

    /**
     * How quickly to fade out the sound when the sound must be cut short.
     */
    @Property(label = "Fade Out (s)")
    public double fadeOutSeconds = 1;

    /**
     * If true, then the sound will fade out automatically when the Actor dies
     */
    @Property(label = "Fade on Death?")
    public boolean fadeOnDeath = false;

    /**
     * What happens if the same sound is played more than once, so that they would overlap? Either both are allowed to play, or the first is
     * stopped, or the second is ignored.
     */
    @Property(label = "Multiple Role")
    public MultipleRole multipleRole = MultipleRole.IGNORE_SECOND;

    public ManagedSound( SoundResource soundResource )
    {
        this.soundResource = soundResource;
    }

    @Override
    public List<AbstractProperty<ManagedSound, ?>> getProperties()
    {
        return properties;
    }

    public int getFadeOutMillis()
    {
        return (int) (1000 * this.fadeOutSeconds);
    }

}