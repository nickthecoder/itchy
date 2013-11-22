/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.TrueTypeFont;

public class TextPose extends AbstractTextPose
{
    private Surface surface;

    
    public TextPose( String text, Font font, double fontSize )
    {
        this(text, font, fontSize, new RGBA(255, 255, 255));
    }

    public TextPose( String text, Font font, double fontSize, RGBA color )
    {
        super( font, fontSize, color );
        this.setText( text );
    }
    
    protected void clearSurfaceCache()
    {
        super.clearSurfaceCache();

        if (this.surface != null) {
            this.surface.free();
        }
        this.surface = null;
    }

    private void ensureCached()
    {
        if (this.surface == null) {
            TrueTypeFont ttf = getTrueTypeFont();
            this.surface = ttf.renderBlended(this.getText(), this.getColor());
        }
    }

    @Override
    public Surface getSurface()
    {
        this.ensureCached();
        return this.surface;
    }

    @Override
    public int getWidth()
    {
        this.ensureCached();
        return this.surface.getWidth();
    }

    @Override
    public int getHeight()
    {
        this.ensureCached();
        return this.surface.getHeight();
    }

    /**
     * Sets the y alignment, so that it uses the baseline of the text as the reference line.
     * Use this to line up text sensibly.
     */
    public void setBaselineAlignment()
    {
        setYAlignment( this.getTrueTypeFont().getAscent() / (double) this.getTrueTypeFont().getHeight() );
    }

    @Override
    public boolean isShared()
    {
        return true;
    }
}
