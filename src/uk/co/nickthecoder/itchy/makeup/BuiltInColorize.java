/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.makeup;

import uk.co.nickthecoder.itchy.Appearance;
import uk.co.nickthecoder.itchy.OffsetSurface;
import uk.co.nickthecoder.itchy.SimpleOffsetSurface;
import uk.co.nickthecoder.jame.Surface;

public class BuiltInColorize extends BuiltInMakeup
{
    public BuiltInColorize( Appearance appearance )
    {
        super(appearance);
    }
    
    @Override
    public OffsetSurface apply( OffsetSurface os )
    {
        if (this.appearance.getColorize() == null) {
            return os;
        }

        Surface colorSurface = new Surface(os.getSurface().getWidth(), os.getSurface().getHeight(), true);
        Surface result = os.getSurface();
        // if (os.isShared()) {
        result = result.copy();
        // }
        colorSurface.fill(this.appearance.getColorize());
        colorSurface.blit(result);
        colorSurface.free();

        return new SimpleOffsetSurface(result, os.getOffsetX(), os.getOffsetY());
    }

    @Override
    public void applyGeometry( TransformationData src )
    {
    }
}
