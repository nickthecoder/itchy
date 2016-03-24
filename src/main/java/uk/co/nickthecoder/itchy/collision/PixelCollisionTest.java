/*******************************************************************************
 * Copyright (c) 2014 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.collision;

import uk.co.nickthecoder.itchy.Actor;

/**
 * Tests if two Actors' pixels overlap.
 * <p>
 * When one or both Actors have semi-transparent pixels, then a threshold is used to determine if they are deemed to be
 * overlapping. If you set this threshold too low, then {@link #collided(Actor, Actor)} will return true
 * even though the actors look like they are a pixel or two apart. (The edges of otherwise opaque images will usually be
 * semi-transparent).
 * <p>
 * If you set it too high, then it may return false, even though the Actors appear to be overlapping. This is especially
 * true if part of your image is semi-transparent, such as a pane of glass, or a nearly transparent water droplet.
 */
public class PixelCollisionTest implements CollisionTest
{
    /**
     * An instance which can be shared, uses a threshold value of 10.
     */
    public static final PixelCollisionTest instance = new PixelCollisionTest();

    private int threshold;

    /**
     * Uses a default alpha threshold.
     */
    public PixelCollisionTest()
    {
        this(Actor.DEFAULT_ALPHA_THRESHOLD);
    }

    /**
     * 
     * @param threshold
     *            The alpha channel threshold value. A value of 0 consider all pixels, except those that
     *            are fully transparent (i.e. with an alpha value of zero).
     */
    public PixelCollisionTest(int threshold)
    {
        this.threshold = threshold;
    }

    @Override
    public boolean collided(Actor a, Actor b)
    {
        return a.pixelOverlap(b, threshold);
    }

}
