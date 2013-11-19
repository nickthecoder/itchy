/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.animation;

import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;

public abstract class AbstractEase implements Ease
{
    public static RGBA THUMBNAIL_COLOR = new RGBA( 241, 163, 12 );
    public static final int THUMBNAIL_SIZE = 50;
    
    private Surface thumbnail;
    
    @Override
    public abstract double amount( double amount );

    @Override
    public Surface getThumbnail()
    {
        if ( this.thumbnail == null) {
            this.thumbnail = generateThumbnail();
        }
        return this.thumbnail;
    }
    
    public String getName()
    {
        return Itchy.registry.getEaseName(this);
    }

    /**
     * If the ease changes, then call this to cause its thumbnail to be regenerated when it is
     * next requested.
     */
    public void resetThumbnail()
    {
        this.thumbnail = null;
    }
    
    protected Surface generateThumbnail()
    {
        // We create an over-sized image, and the shrink it to make anit aliased lines.
        int size = 200;
        int margin = 25; // Extra top and bottom for eases with overshoot.
        int dotSize = 10;
        Rect dot = new Rect( 0,0, dotSize, dotSize);
        
        Surface surface = new Surface( size + dotSize, size + dotSize + margin * 2, true );
        
        //surface.fill( new RGBA( 255,255,255,30 ) );
        //surface.fill( new Rect( dotSize/2, dotSize/2 + margin, size, size ), new RGBA( 255,0,0, 30 ));
        
        for ( double x = 0; x <= 1; x += 1.0/size ) {
            double y = this.amount(x);
            
            dot.x = (int) (x * size);
            dot.y = (int) ((1-y) * size) + margin;
            surface.fill( dot, THUMBNAIL_COLOR );
        }

        double scale = (double) THUMBNAIL_SIZE / surface.getWidth();
        Surface thumbnail = surface.zoom(scale, scale, true);
        
        surface.free();
        
        return thumbnail;
    }
}
