/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.util;

import uk.co.nickthecoder.itchy.ImagePose;
import uk.co.nickthecoder.itchy.Pose;
import uk.co.nickthecoder.itchy.Renderable;
import uk.co.nickthecoder.jame.Surface;

public class BorderPoseDecorator implements PoseDecorator
{

    private Renderable renderable;

    private int borderTop;
    private int borderRight;
    private int borderBottom;
    private int borderLeft;

    
    public BorderPoseDecorator( NinePatch eightPatch )
    {
        this(
            eightPatch,
            eightPatch.marginTop, 
            eightPatch.marginRight, 
            eightPatch.marginBottom, 
            eightPatch.marginLeft
        ); 
    }
    
    public BorderPoseDecorator( NinePatch eightPatch, int border )
    {
        this(eightPatch, border, border);
    }

    public BorderPoseDecorator( NinePatch eightPatch, int borderTopBottom, int borderLeftRight )
    {
        this(eightPatch, borderTopBottom, borderLeftRight, borderTopBottom, borderLeftRight);
    }

    public BorderPoseDecorator( Renderable renderable, int borderTop, int borderRight,
            int borderBottom, int borderLeft )
    {
        this.renderable = renderable;

        this.borderTop = borderTop;
        this.borderRight = borderRight;
        this.borderBottom = borderBottom;
        this.borderLeft = borderLeft;
    }

    @Override
    public Pose createPose( Pose srcPose )
    {
        Surface srcSurface = srcPose.getSurface();
        Surface surface = new Surface(srcSurface.getWidth() + this.borderLeft + this.borderRight,
                srcSurface.getHeight() + this.borderTop + this.borderBottom, true);

        this.renderable.render(surface);
        srcSurface.blit(surface, this.borderLeft, this.borderRight);

        ImagePose result = new ImagePose(surface);
        result.setOffsetX(srcPose.getOffsetX() + this.borderLeft);
        result.setOffsetY(srcPose.getOffsetY() + this.borderTop);

        return result;
    }
}
