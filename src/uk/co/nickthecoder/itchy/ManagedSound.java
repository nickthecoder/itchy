/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

public class ManagedSound
{
    public enum MultipleBehaviour {
        PLAY_BOTH, STOP_FIRST, FADE_FIRST, IGNORE_SECOND
    };
    
    public SoundResource soundResource;

    /**
     * The priority of the sound. The recommended range is 0 (lowest priority) to 3 (highest priority),
     * but you are free to use whatever range you want.
     * 
     * There is a limit on how many sounds can play simultaneously, and when this limit is exceeded
     * high priority sounds will cause low priority sounds to end abruptly. If there are no low priority
     * sounds, then new sounds won't play when the limit is exceeded.
     */
    public int priority = 1;

    /**
     * How quickly to fade out the sound when the sound must be cut short.
     */
    public double fadeOutSeconds = 1;

    /**
     * If true, then the sound will fade out automatically when the Actor dies
     */
    public boolean fadeOnDeath = false;
    
    /**
     * What happens if the same sound is played more than once, so that they would overlap?
     * Either both are allowed to play, or the first is stopped, or the second is ignored.
     */
    public MultipleBehaviour multipleBehaviour = MultipleBehaviour.IGNORE_SECOND;
    

    public ManagedSound( SoundResource soundResource )
    {
        this.soundResource = soundResource;
    }

    public int getFadeOutMillis()
    {
        return (int) (1000 * this.fadeOutSeconds);
    }

}