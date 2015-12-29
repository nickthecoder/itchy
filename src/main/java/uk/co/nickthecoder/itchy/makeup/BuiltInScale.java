/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.makeup;

import uk.co.nickthecoder.itchy.Appearance;
import uk.co.nickthecoder.itchy.OffsetSurface;
import uk.co.nickthecoder.itchy.SimpleOffsetSurface;
import uk.co.nickthecoder.jame.Surface;

public class BuiltInScale extends BuiltInMakeup
{
    public BuiltInScale( Appearance appearance )
    {
        super(appearance);
    }

    @Override
    public OffsetSurface apply( OffsetSurface os )
    {
        double scale = this.appearance.getScale();

        if (scale == 1.0) {
            return os;
        }

        if (scale <= 0) {
            return new SimpleOffsetSurface(new Surface(1, 1, true), 0, 0);
        }

        Surface scaled = os.getSurface().zoom(scale, scale, true);

        return new SimpleOffsetSurface(
            scaled,
            (int) (os.getOffsetX() * scale),
            (int) (os.getOffsetY() * scale));
    }

    @Override
    public void applyGeometry( TransformationData src )
    {
        double scale = this.appearance.getScale();

        if (scale == 1.0) {
            return;
        }

        if (scale <= 0) {
            src.set(1, 1, 0, 0);
            return;
        }

        int width = (int) (src.width * scale);
        int height = (int) (src.height * scale);
        src.set(width, height, (int) (src.offsetX * scale), (int) (src.offsetY * scale));
    }

}
