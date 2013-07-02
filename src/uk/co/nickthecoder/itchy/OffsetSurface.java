package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.jame.Surface;

public interface OffsetSurface
{
    /**
     * Along with getOffsetY(), defines the key coordinate of the pose. When a Pose is drawn on
     * screen at x,y, the top left of the Pose will be drawn at ( x-offsetX, y-offsetY )
     */
    public int getOffsetX();

    public int getOffsetY();

    public Surface getSurface();

}
