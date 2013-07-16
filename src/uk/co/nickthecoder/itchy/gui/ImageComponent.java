/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import uk.co.nickthecoder.jame.Surface;

public class ImageComponent extends SurfaceComponent
{
    private static Surface dummySurface;

    public static Surface getDummySurface()
    {
        if (dummySurface == null) {
            try {
                dummySurface = new Surface(1, 1, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dummySurface;
    }

    public ImageComponent()
    {
        this(getDummySurface());
    }

    public ImageComponent( Surface surface )
    {
        this.plainSurface = surface;
    }

    @Override
    public String getType()
    {
        return "image";
    }

    public void setImage( Surface surface )
    {
        this.plainSurface = surface;
        if (this.parent != null) {
            this.parent.forceLayout();
        }
        this.invalidate();
    }

    @Override
    public Surface getPlainSurface()
    {
        return this.plainSurface;
    }

    @Override
    protected void clearPlainSurface()
    {
        throw new RuntimeException("You should not clear the plain surface of an ImageComponent");
    }

    @Override
    protected void createPlainSurface()
    {
    }
}
