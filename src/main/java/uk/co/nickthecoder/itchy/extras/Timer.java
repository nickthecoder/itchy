/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.extras;

import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.util.Util;

/**
 * Keeps track of the last time something was used, and how long it takes before it can be used again.
 * 
 * Useful for bullets, which have a maximum period between shots.
 * 
 * Note, this class automatically takes into account when the game is paused. e.g. if a timer is set for 1 minute, and
 * the game is paused for 2 minutes part way through, then the timer will finish after 3 minutes.
 * To disable this behaviour, use setPauseAware(false).
 */
public class Timer
{
    /**
     * The timer period in milliseconds
     */
    public int period;

    public int minimumPeriod;

    public int maximumPeriod;

    /**
     * The time the the timer was started. It will be ready at startTime + period.
     */
    private long startTime;

    private boolean pauseAware = true;
    

    public static Timer createTimerSeconds( double seconds )
    {
        return new Timer((int) (seconds * 1000));
    }

    public static Timer createTimerSeconds( double from, double to )
    {
        return new Timer((int) (from * 1000), (int) (to * 1000));
    }

    private Timer( int period )
    {
        this.minimumPeriod = period;
        this.maximumPeriod = period;
        this.period = period;
        this.reset();
    }

    private Timer( int from, int to )
    {
        this.minimumPeriod = from;
        this.maximumPeriod = to;
        this.period = from;
        this.reset();
    }

    public void setPauseAware( boolean value )
    {
        this.pauseAware = value;
        this.reset();
    }

    public boolean getPauseAware()
    {
        return this.pauseAware;
    }

    private long currentTimeMillis()
    {
        if (this.pauseAware) {
            return Itchy.getGame().gameTimeMillis();
        } else {
            return System.currentTimeMillis();
        }
    }

    public boolean isFinished()
    {
        return currentTimeMillis() > this.startTime + this.period;
    }

    /**
     * The proportion of the time still remaining.
     * 
     * @return 0 to 1 (0: the timer has finished, 1: the timer has only just started).
     */
    public double getRemainder()
    {
        double result = (this.startTime + this.period - currentTimeMillis()) /
            (double) this.period;

        if (result > 1) {
            return 1;
        } else {
            return result;
        }
    }

    /**
     * The proportion of the time elapsed so far.
     * 
     * @return 0 to 1 (0: the time has just started, 1: the time has finished).
     */
    public double getProgress()
    {
        double result = (currentTimeMillis() - this.startTime) /
            (double) this.period;

        if (result > 1) {
            return 1;
        } else {
            return result;
        }
    }

    public final void reset()
    {
        if (this.minimumPeriod != this.maximumPeriod) {
            this.period = (int) Util.randomBetween(this.minimumPeriod, this.maximumPeriod);
        }
        this.startTime = currentTimeMillis();
    }

    @Override
    public String toString()
    {
        return "Timer : " + this.period + " progress  " + getProgress() + " finished ? " + isFinished();
    }
}
