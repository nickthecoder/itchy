/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.animation;

import uk.co.nickthecoder.itchy.Pose;

/**
 * One frame of an Animation
 */
public class Frame implements Cloneable
{
    private final String poseName;

    private final Pose pose;

    private int delay;

    public Frame( String poseName, Pose pose )
    {
        this.poseName = poseName;
        this.pose = pose;
        this.delay = 0;
    }

    public String getPoseName()
    {
        return this.poseName;
    }

    public Pose getPose()
    {
        return this.pose;
    }

    public void setDelay( int frameCount )
    {
        this.delay = frameCount;
    }

    public int getDelay()
    {
        return this.delay;
    }

    public Frame copy()
    {
        try {
            return (Frame) this.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
