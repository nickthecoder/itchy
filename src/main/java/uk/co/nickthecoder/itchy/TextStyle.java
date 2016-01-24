/*******************************************************************************
 * Copyright (c) 2014 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.DoubleProperty;
import uk.co.nickthecoder.itchy.property.IntegerProperty;
import uk.co.nickthecoder.itchy.property.PropertySubject;
import uk.co.nickthecoder.itchy.property.RGBAProperty;
import uk.co.nickthecoder.jame.RGBA;

public class TextStyle implements PropertySubject<TextStyle>, Cloneable
{
    protected static final List<Property<TextStyle, ?>> properties = new ArrayList<Property<TextStyle, ?>>();

    static {
        properties.add(new IntegerProperty<TextStyle>("fontSize"));
        properties.add(new RGBAProperty<TextStyle>("color"));
        properties.add(new DoubleProperty<TextStyle>("xAlignment").hint("0..1"));
        properties.add(new DoubleProperty<TextStyle>("yAlignment").hint("0..1"));
        properties.add(new IntegerProperty<TextStyle>("marginTop"));
        properties.add(new IntegerProperty<TextStyle>("marginRight"));
        properties.add(new IntegerProperty<TextStyle>("marginBottom"));
        properties.add(new IntegerProperty<TextStyle>("marginLeft"));
    }

    protected Font font;

    public int fontSize;

    public RGBA color = RGBA.WHITE;

    public double xAlignment = 0.5;
    
    public double yAlignment = 0.5;
    
    public int marginTop = 0;

    public int marginRight = 0;

    public int marginBottom = 0;

    public int marginLeft = 0;

    
    public TextStyle()
    {
        this(null, 16);
    }

    public TextStyle( Font font, int fontSize )
    {
        this.font = font;
        this.fontSize = fontSize;
    }

    @Override
    public List<Property<TextStyle, ?>> getProperties()
    {
        return properties;
    }
    
    public void setFont( Font font )
    {
        this.font = font;
    }

    /**
     * @return The font if it has been set, or the game's default font otherwise. If the game has no fonts, then null will be returned.
     */
    public Font getFont()
    {
        if (this.font == null) {
            this.font = Itchy.getGame().resources.getDefaultFont();
        }
        return this.font;
    }

    public void setMargins( int margin )
    {
        this.setMargins(margin, margin);
    }

    public void setMargins( int topBottom, int leftRight )
    {
        this.marginTop = this.marginBottom = topBottom;
        this.marginLeft = this.marginRight = leftRight;
    }

    @Override
    public String toString()
    {
        if (this.font == null) {
            return "TextStyle : null";
        }

        return "TextStyle : " + this.font.getFile() + "(" + this.fontSize + ")" + this.color.toString();
    }

    @Override
    public TextStyle clone()
    {
        try {
            TextStyle result = (TextStyle) super.clone();
            return result;
        } catch (CloneNotSupportedException e) {
            // I hate CloneNotSupportedExceptions!
            throw new RuntimeException(e);
        }
    }

}
