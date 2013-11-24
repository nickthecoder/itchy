/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.makeup;

import uk.co.nickthecoder.itchy.Appearance;
import uk.co.nickthecoder.itchy.OffsetSurface;
import uk.co.nickthecoder.itchy.SimpleOffsetSurface;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.Surface.BlendMode;

public class BuiltInClip extends BuiltInMakeup
{
    public BuiltInClip( Appearance appearance )
    {
        super(appearance);
    }

    @Override
    public OffsetSurface apply( OffsetSurface os )
    {
        if (this.appearance.getClip() == null) {
            return os;
        }

        Rect rect = new Rect(0, 0, os.getSurface().getWidth(), os.getSurface().getHeight());
        rect = this.appearance.getClip().intersection(rect);

        if ((rect.width < 0) || (rect.height < 0)) {
            return new SimpleOffsetSurface(new Surface(1, 1, true), 0, 0);

        } else {
            Surface clippedSurface = new Surface(rect.width, rect.height, true);
            os.getSurface().blit(rect, clippedSurface, 0, 0, BlendMode.COMPOSITE);

            return new SimpleOffsetSurface(clippedSurface, os.getOffsetX() - rect.x, os.getOffsetY() - rect.y);
        }
    }

    @Override
    public void applyGeometry( TransformationData src )
    {
        if (this.appearance.getClip() == null) {
            return;
        }
        Rect rect = new Rect(0, 0, src.width, src.height);
        rect = this.appearance.getClip().intersection(rect);

        if ((rect.width < 0) || (rect.height < 0)) {
            src.set(1, 1, 0, 0);

        } else {
            src.set(rect.width, rect.height, src.offsetX - rect.x, src.offsetY - rect.y);
        }
    }

}
