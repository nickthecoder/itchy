/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.extras;

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

    private long chargeTime;


    public Recharge( int period )
    {
        this.period = period;
    }
    
    public boolean isCharged()
    {
        return System.currentTimeMillis() > this.chargeTime + this.period;
    }

    /**
     * The amount of charge
     * 
     * @return 0 for fully discharged, 1 for fully charged.
     */
    public double getCharge()
    {
        double result = (System.currentTimeMillis() - (this.chargeTime + this.period)) /
            (double) this.period;

        if (result > 1) {
            return 1;
        } else {
            return result;
        }
    }

    public void reset()
    {
        this.chargeTime = System.currentTimeMillis();
    }
}
