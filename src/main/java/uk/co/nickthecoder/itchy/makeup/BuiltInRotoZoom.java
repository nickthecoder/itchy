/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.makeup;

import uk.co.nickthecoder.itchy.Appearance;
import uk.co.nickthecoder.itchy.OffsetSurface;
import uk.co.nickthecoder.itchy.SimpleOffsetSurface;
import uk.co.nickthecoder.jame.Surface;

public class BuiltInRotoZoom extends BuiltInMakeup
{
    public BuiltInRotoZoom( Appearance appearance )
    {
        super(appearance);
    }

    @Override
    public OffsetSurface apply( OffsetSurface os )
    {
        double dirDiff = this.appearance.getDirection() - this.appearance.getPose().getDirection();
        if (((int) dirDiff) == 0) {
            return this.appearance.scaleMakeup.apply(os);
        }
        double scale = this.appearance.getScale();

        // Find out where the old offset was relative to the CENTER of the image.
        double odx = os.getOffsetX() - os.getSurface().getWidth() / 2.0;
        double ody = os.getOffsetY() - os.getSurface().getHeight() / 2.0;

        Surface rotated = os.getSurface().rotoZoom(dirDiff, scale, true);

        double dirRadians = dirDiff / 180.0 * Math.PI;
        double cosa = Math.cos(-dirRadians);
        double sina = Math.sin(-dirRadians);
        // Calculate were (odx,ody) is using the new coordinate system.
        double ndy = odx * sina + ody * cosa;
        double ndx = odx * cosa - ody * sina;

        return new SimpleOffsetSurface(
            rotated,
            (int) (rotated.getWidth() / 2.0 + ndx * scale),
            (int) (rotated.getHeight() / 2.0 + ndy * scale));
    }

    @Override
    public void applyGeometry( TransformationData os )
    {
        double dirDiff = this.appearance.getDirection() - this.appearance.getPose().getDirection();
        if (((int) dirDiff) == 0) {
            this.appearance.scaleMakeup.applyGeometry(os);
            return;
        }
        double scale = this.appearance.getScale();

        double mathOffsetY = os.height - os.offsetY; // Lets use a proper Y-Axis points up system, and swap it back at the end.

        // Find out where the old offset was relative to the CENTER of the image.
        double odx = os.offsetX - os.width / 2.0;
        double ody = mathOffsetY - os.height / 2.0;

        double dirRadians = dirDiff / 180.0 * Math.PI;
        double cosa = Math.cos(dirRadians);
        double sina = Math.sin(dirRadians);

        // Calculate were (odx,ody) is using the new coordinate system.
        double ndy = odx * sina + ody * cosa;
        double ndx = odx * cosa - ody * sina;

        int width = (int) (Math.round(Math.abs(os.width * cosa) + Math.abs(os.height * sina)) * scale);
        int height = (int) (Math.round(Math.abs(os.height * cosa) + Math.abs(os.width * sina)) * scale);

        os.set(width,
            height,
            (int) (width / 2.0 + ndx * scale),
            (int) (height - (height / 2.0 + ndy * scale)) // Swapped back so the yoffset is from the TOP of the image
        );

    }
}
