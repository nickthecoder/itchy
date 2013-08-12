/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.extras;

import uk.co.nickthecoder.itchy.util.Util;

/**
 * Keeps track of the last time something was used, and how long it takes before it can be used
 * again.
 * 
 * Useful for bullets, which have a maximum period between shots.
 */
public class Recharge
{
    /**
     * The recharge period in milliseconds
     */
    public int period;
    
    public int minimumPeriod;
    
    public int maximumPeriod;

    /**
     * The time the the recharge was started. It will be ready at startTime + period.
     */
    private long startTime;

    
    public static Recharge createRechargeSeconds( double seconds )
    {
        return new Recharge((int) (seconds * 1000));
    }

    public static Recharge createRechargeSeconds( double from, double to )
    {
        return new Recharge((int) (from * 1000), (int) (to*1000));
    }

    public Recharge( int period )
    {
        this.minimumPeriod = period;
        this.maximumPeriod = period;
        this.period = period;
        this.reset();
    }
    public Recharge( int from, int to )
    {
        this.minimumPeriod = from;
        this.maximumPeriod = to;
        this.period = from;
        this.reset();
    }

    public boolean isCharged()
    {
        return System.currentTimeMillis() > this.startTime + this.period;
    }

    /**
     * The amount of charge
     * 
     * @return 0 for fully discharged, 1 for fully charged.
     */
    public double getCharge()
    {
        double result = (System.currentTimeMillis() - (this.startTime + this.period)) /
            (double) this.period;

        if (result > 1) {
            return 1;
        } else {
            return result;
        }
    }
    
    public final void reset()
    {
        if ( this.minimumPeriod != this.maximumPeriod ) {
            this.period = (int) Util.randomBetween(this.minimumPeriod,  this.maximumPeriod);
        }
        this.startTime = System.currentTimeMillis();
    }
}
