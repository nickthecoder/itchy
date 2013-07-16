/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.animation;

public class BounceProfile implements Profile
{
    @Override
    public double amount( double amount )
    {
        return amount < 0.5 ? (0.5 - amount) * 2 : (amount - 0.5) * -2;
    }
}
