/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.makeup;

public class TransformationData
{
    public int offsetX;

    /**
     * Note, that this is the offset relative to the TOP of the image. This is probably a design flaw, because standard maths
     * has the y axis pointing UPWARDS. Later versions of this code may fix this problem, and therefore any user-defined
     * Makeup objects may need to be recoded.  You have been warned!
     * This mistake was made because Pose has its offset from the top (which may also be a mistake), and that's because all of the
     * drawing primitives have the Y axis pointing down. Note that the GUI components use a "Y-axis down" system, and they also use Pose.
     * Hmmm.
     */
    public int offsetY;

    public int width;

    public int height;

    public void set( int width, int height, int offsetX, int offsetY )
    {
        this.width = width;
        this.height = height;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }
    
    public String toString() {
        return "Transformation Data : " + this.offsetX +"," + this.offsetY + " .. " + this.width + "," + this.height;
    }
}
