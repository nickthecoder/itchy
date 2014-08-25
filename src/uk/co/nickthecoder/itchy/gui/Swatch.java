/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Surface;

public class Swatch extends PlainContainer
{
    private Surface opaqueSwatch;

    private Surface transparentSwatch;

    public Swatch()
    {
        this(200, 30);
    }

    public Swatch( int width, int height )
    {
        this.type = "swatch";

        this.opaqueSwatch = new Surface(100, 30, true);
        this.transparentSwatch = new Surface(100, 30, true);
        addChild(new ImageComponent(this.opaqueSwatch));
        addChild(new ImageComponent(this.transparentSwatch));

    }

    public Swatch link( final TextBox textBox )
    {
        textBox.addChangeListener(new ComponentChangeListener() {
            @Override
            public void changed()
            {
                try {
                    setColor(RGBA.parse(textBox.getText()));
                } catch (Exception e) {
                }
            }

        });
        return this;
    }

    public void setSwatchColor( RGBA color )
    {
        RGBA opaque = new RGBA(color.r, color.g, color.b);
        this.transparentSwatch.fill(color);
        this.opaqueSwatch.fill(opaque);
    }
}
