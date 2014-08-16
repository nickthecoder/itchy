/*******************************************************************************
 * Copyright (c) 2014 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.List;

import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.PropertySubject;
import uk.co.nickthecoder.jame.RGBA;

public class TextStyle implements PropertySubject<TextStyle>, Cloneable
{

    private static final List<AbstractProperty<TextStyle, ?>> properties =
        AbstractProperty.<TextStyle> findAnnotations(TextStyle.class);

    protected Font font;

    @Property(label = "Font Size", sortOrder=1)
    public int fontSize;

    @Property(label = "Colour", sortOrder=20)
    public RGBA color = RGBA.WHITE;


    @Property(label= "Align X", hint="0..1", sortOrder=25)
    public double xAlignment = 0.5;
    
    @Property(label= "Align Y", hint="0..1", sortOrder=25)
    public double yAlignment = 0.5;
    
    @Property(label = "Top Margin", sortOrder=30)
    public int marginTop = 0;

    @Property(label = "Right Margin", sortOrder=31)
    public int marginRight = 0;

    @Property(label = "Bottom Margin", sortOrder=32)
    public int marginBottom = 0;

    @Property(label = "Left Margin", sortOrder=33)
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

        return "TextStyle : " + this.font.getFilename() + "(" + this.fontSize + ")" + this.color.toString();
    }

    @Override
    public List<AbstractProperty<TextStyle, ?>> getProperties()
    {
        return properties;
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
