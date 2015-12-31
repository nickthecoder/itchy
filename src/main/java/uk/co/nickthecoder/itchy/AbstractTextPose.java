/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.jame.JameException;
import uk.co.nickthecoder.jame.JameRuntimeException;
import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.TrueTypeFont;

public abstract class AbstractTextPose implements Pose
{
    private double xAlignment = 0.5;

    private double yAlignment = 0.5;

    private Font font;

    private TrueTypeFont ttf;

    private double fontSize;

    private RGBA color;

    private String text = "";

    protected int changeId = 0;

    protected Appearance appearance;


    public AbstractTextPose( TextStyle textStyle )
    {
        this.font = textStyle.font;
        this.fontSize = textStyle.fontSize;
        this.color = textStyle.color;
    }
    
    public AbstractTextPose( Font font, double fontSize )
    {
        this(font, fontSize, new RGBA(255, 255, 255));
    }

    public AbstractTextPose( Font font, double fontSize, RGBA color )
    {
        this.font = font;
        this.fontSize = fontSize;
        this.color = color;
    }

    public String getText()
    {
        return this.text;
    }

    public void setText( String text )
    {
        if (!this.text.equals(text)) {
            this.text = text;
            changedShape();
        }
    }

    public Font getFont()
    {
        return this.font;
    }

    public void setFont( Font font )
    {
        if (this.font != font) {
            this.font = font;
            this.ttf = null;
            changedShape();
        }
    }

    public TrueTypeFont getTrueTypeFont()
    {
        try {
            if (this.ttf == null) {
                this.ttf = this.font.getSize((int) (this.fontSize));
            }
            return this.ttf;
        } catch (JameException e) {
            throw new JameRuntimeException(e);
        }
    }

    public double getFontSize()
    {
        return this.fontSize;
    }

    public void setFontSize( double fontSize )
    {
        if ((int) this.fontSize != (int) fontSize) {
            this.ttf = null;
            changedShape();
        }
        this.fontSize = fontSize;
    }

    public RGBA getColor()
    {
        return this.color;
    }

    public void setColor( RGBA color )
    {
        this.color = color;
        changedImage();
    }

    /*
     * public void adjustColor( int deltaRed, int deltaGreen, int deltaBlue ) { this.color.r +=
     * deltaRed; this.color.g += deltaGreen; this.color.b += deltaBlue; this.clearSurfaceCache(); }
     */

    public void adjustFontSize( double delta )
    {
        this.setFontSize(this.fontSize + delta);
    }

    public double getXAlignment()
    {
        return this.xAlignment;
    }

    public void setXAlignment( double xAlignment )
    {
        if ((xAlignment < 0) || (xAlignment > 1)) {
            throw new IllegalArgumentException("Alignments must be in the range (0..1)");
        }
        this.xAlignment = xAlignment;
        this.changeId += 1;
        changedShape();
    }

    public double getYAlignment()
    {
        return this.yAlignment;
    }

    public void setYAlignment( double yAlignment )
    {
        if ((yAlignment < 0) || (yAlignment > 1)) {
            throw new IllegalArgumentException("Alignments must be in the range (0..1)");
        }
        this.yAlignment = yAlignment;
        this.changeId += 1;
        changedShape();
    }

    public void setAlignment( double x, double y )
    {
        setXAlignment(x);
        setYAlignment(y);
    }

    @Override
    public int getOffsetX()
    {
        return (int) (this.getWidth() * this.xAlignment);
    }

    @Override
    public int getOffsetY()
    {
        return (int) (this.getHeight() * this.yAlignment);
    }

    @Override
    public double getDirection()
    {
        return 0;
    }

    public abstract int getWidth();

    public abstract int getHeight();

    protected void changedShape()
    {
        this.changedImage();
        if (this.appearance != null) {
            this.appearance.invalidateShape();
        }
    }

    protected void changedImage()
    {
        this.changeId++;
    }

    @Override
    public abstract Surface getSurface();

    @Override
    public int getChangeId()
    {
        return this.changeId;
    }

    @Override
    public void attach( Appearance appearance )
    {
        this.appearance = appearance;
    }
}
