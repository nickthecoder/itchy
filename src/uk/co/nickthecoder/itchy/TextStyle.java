/*******************************************************************************
 * Copyright (c) 2014 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.List;

import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.PropertySubject;
import uk.co.nickthecoder.jame.RGBA;

public class TextStyle implements PropertySubject<TextStyle>
{

    private static final List<AbstractProperty<TextStyle, ?>> properties =
        AbstractProperty.<TextStyle> findAnnotations(TextStyle.class);

    public Font font;

    @Property(label = "Font Size")
    public int fontSize;
    
    @Property(label = "Colour")
    public RGBA color = RGBA.WHITE;

    @Property(label = "Top Margin")
    public int marginTop = 0;
    
    @Property(label = "Right Margin")
    public int marginRight = 0;
    
    @Property(label = "Bottom Margin")
    public int marginBottom = 0;
    
    @Property(label = "Left Margin")
    public int marginLeft = 0;
    
    public TextStyle( Font font, int fontSize )
    {
        this.font = font;
        this.fontSize = fontSize;
    }

    public void setMargins( int margin )
    {
        this.setMargins( margin, margin );
    }
    
    public void setMargins( int topBottom, int leftRight )
    {
        this.marginTop = this.marginBottom = topBottom;
        this.marginLeft = this.marginRight = leftRight;
    }
    
    public String toString()
    {
        if (this.font == null) {
            return "TextStyle : null";
        }

        return "TextStyle : " + this.font.getFilename() + "(" + this.fontSize + ")" + this.color.toString();
    }
    
    @Override
    public List<AbstractProperty<TextStyle, ?>> getProperties()
    {
        return properties;
    }

}
