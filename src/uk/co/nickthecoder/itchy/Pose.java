package uk.co.nickthecoder.itchy;

/**
 * A Pose is a single image used by an Actor's Appearance. A Pose consists of a single Surface,
 * which holds the image, as well as an offset which defines the key coordinate of the pose. When a
 * Pose is drawn on screen at x,y, the top left of the Pose will be drawn at ( x-offsetX, y-offsetY
 * ) Also, if the Actor can be rotated, then the offset will be the point around which the Actor
 * will rotate.
 * 
 * @author nick
 */

public interface Pose extends OffsetSurface
{

    /**
     * The direction that the Pose is facing. The default is 0 degrees (facing right). This is
     * useful if you have images which don't face right. For example, a human's bullet in Space
     * Invaders points upwards, and it would be counter intuative to draw it pointing right, so
     * instead, draw it pointing up, and set direction to 90 degrees.
     * 
     * @return The direction that the image is pointing in degrees. 0 degrees is to the right, 90
     *         degrees is straight up (assuming your Y axis points upwards). See ScrollableLayer's
     *         constructor's yAxisPointsDown parameter.
     */
    public double getDirection();

    /**
     * Called by Appearance to ensure that its surface is up to date. Most types of Pose will be
     * static (i.e. once they are created, they remain the same), and therefore, they will always
     * return false. For those type of Pose which are NOT static, then they can only be used by a
     * single Appearance.
     * 
     * @return true iff the pose has changed since the last time validate was called.
     */
    public boolean changedSinceLastUsed();

    /**
     * Called by Appearance after it has used the results from Pose.getSurface. After used is
     * called, then changedSinceLastUsed will return false until the Pose changes. Note that
     * ImagePoses should never change, so changedSinceLastUsed always returns false, and "used" is a
     * no-op.
     */
    public void used();
}
