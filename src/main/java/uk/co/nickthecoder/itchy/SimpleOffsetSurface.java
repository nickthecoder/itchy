/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.jame.Renderer;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.Texture;

public class SimpleOffsetSurface implements OffsetSurface
{
    public int offsetX;

    public int offsetY;

    public Surface surface;
    
    private Texture texture;

    public boolean shared;

    public SimpleOffsetSurface( OffsetSurface other )
    {
        this.offsetX = other.getOffsetX();
        this.offsetY = other.getOffsetY();
        this.surface = other.getSurface();
        this.shared = other.isShared();
    }

    public SimpleOffsetSurface( Surface surface, int x, int y )
    {
        this.offsetX = x;
        this.offsetY = y;
        this.surface = surface;
        this.shared = false;
    }

    @Override
    public int getOffsetX()
    {
        return this.offsetX;
    }

    @Override
    public int getOffsetY()
    {
        return this.offsetY;
    }

    @Override
    public Surface getSurface()
    {
        return this.surface;
    }

    @Override
    public boolean isShared()
    {
        return this.shared;
    }

    @Override
    public Texture getTexture(Renderer renderer)
    {
        if ( texture == null ) {
            texture = new Texture( renderer, getSurface() );
        }
        return texture;
    }

}
