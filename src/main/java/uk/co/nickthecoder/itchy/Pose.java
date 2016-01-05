/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;


/**
 * A Pose is a single image used by an Actor's Appearance. A Pose consists of a single Surface, which holds the image, as well as an offset
 * which defines the key coordinate of the pose. When a Pose is drawn on screen at x,y, the top left of the Pose will be drawn at (
 * x-offsetX, y-offsetY ) Also, if the Actor can be rotated, then the offset will be the point around which the Actor will rotate.
 */
public interface Pose extends OffsetSurface
{    
    /**
     * The direction that the Pose is facing. The default is 0 degrees (facing right). This is useful if you have images which don't face
     * right. For example, a human's bullet in Space Invaders points upwards, and it would be counter intuative to draw it pointing right,
     * so instead, draw it pointing up, and set direction to 90 degrees.
     * 
     * @return The direction that the image is pointing in degrees. 0 degrees is to the right, 90 degrees is straight up.
     */
    public double getDirection();

    /**
     * Appearance needs to know if a Pose has changed since it last rendered it to the screen, because it's processed verion of the pose
     * isn't suitable if the pose itself has changed. Therefore, each time a Pose changes, it should increase its identifier. Appearance
     * will compare the id it got last time with the latest one, and throw away its processed image if they differ.
     */
    public int getChangeId();

    @Override
    public int getOffsetX();

    @Override
    public int getOffsetY();

    public void attach( Appearance appearance );
}
