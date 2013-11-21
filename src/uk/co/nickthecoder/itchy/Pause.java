/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

public interface Pause
{
    public boolean isPaused();

    public void togglePause();

    public void pause();

    public void unpause();

    /**
     * Remembers the time that the game was last paused, so that timers which don't count down during pauses can use this time when the game
     * is paused.
     */
    public long pauseTimeMillis();

    /**
     * @return The total time in milliseconds that the game was in a paused state.
     */
    public long totalTimePausedMillis();

}
