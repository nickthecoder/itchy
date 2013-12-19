/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.util;

import uk.co.nickthecoder.itchy.Renderable;
import uk.co.nickthecoder.jame.JameException;
import uk.co.nickthecoder.jame.Surface;

public abstract class ImageRenderable implements Renderable
{
    protected Surface surface;

    public ImageRenderable( Surface surface )
    {
        this.surface = surface;
    }

    public ImageRenderable( String filename ) throws JameException
    {
        this(new Surface(filename));
    }

    public void loadImage( String filename ) throws JameException
    {
        this.surface = new Surface(filename);
    }

    public Surface getSurface()
    {
        return this.surface;
    }

}
