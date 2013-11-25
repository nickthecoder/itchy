/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;

/**
 * Renders a solid color over the whole area.
 */
public class RGBAView extends AbstractView
{
    public RGBA color;

    public RGBAView( Rect position, RGBA color )
    {
        super(position);
        this.color = color;
    }

    public boolean isVisible()
    {
        return (this.color.a > 0) && super.isVisible();
    }
    
    @Override
    public void render2( Surface destSurface, Rect clip, int offsetX, int offsetY )
    {
        destSurface.fill(clip, this.color);
    }

    public String toString()
    {
        return "RGBAView " + this.color;
    }
}
