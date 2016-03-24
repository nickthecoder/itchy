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

/**
 * Uses True Type Fonts to render text.
 * There are two concrete classes {@link TextPose} and {@link MultiLineTextPose}. TextPose can only draw a single line
 * of text, but is quicker, and simpler.
 */
public abstract class AbstractTextPose implements Pose
{
    private double xAlignment = 0.5;

    private double yAlignment = 0.5;

    private Font font;

    private TrueTypeFont ttf;

    private double fontSize;

    private RGBA color;

    private String text = "";

    /**
     * Used internally by Itchy for speed optimisation.
     */
    protected int changeId = 0;

    protected Appearance appearance;

    /**
     * Create a TextPose using the font, fontSize and color within the textStyle.
     * 
     * @param textStyle
     */
    public AbstractTextPose(TextStyle textStyle)
    {
        this.font = textStyle.font;
        this.fontSize = textStyle.fontSize;
        this.color = textStyle.color;
    }

    /**
     * Create white text.
     * 
     * @param font
     * @param fontSize
     */
    public AbstractTextPose(Font font, double fontSize)
    {
        this(font, fontSize, new RGBA(255, 255, 255));
    }

    public AbstractTextPose(Font font, double fontSize, RGBA color)
    {
        this.font = font;
        this.fontSize = fontSize;
        this.color = color;
    }

    /**
     * A simple getter.
     * 
     * @return
     */
    public String getText()
    {
        return this.text;
    }

    /**
     * A simple setter.
     * 
     * @param text
     */
    public void setText(String text)
    {
        if (!this.text.equals(text)) {
            this.text = text;
            changedShape();
        }
    }

    /**
     * A simple getter
     * 
     * @return
     */
    public Font getFont()
    {
        return this.font;
    }

    /**
     * A simple setter.
     * 
     * @param font
     */
    public void setFont(Font font)
    {
        if (this.font != font) {
            this.font = font;
            this.ttf = null;
            changedShape();
        }
    }

    /**
     * A simple getter
     * 
     * @return
     */
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

    /**
     * A simple getter.
     * 
     * @return
     */
    public double getFontSize()
    {
        return this.fontSize;
    }

    /**
     * A simple setter.
     * 
     * @param fontSize
     */
    public void setFontSize(double fontSize)
    {
        if ((int) this.fontSize != (int) fontSize) {
            this.ttf = null;
            changedShape();
        }
        this.fontSize = fontSize;
    }

    /**
     * A simple getter.
     */
    public RGBA getColor()
    {
        return this.color;
    }

    /**
     * A simple setter.
     * 
     * @param color
     */
    public void setColor(RGBA color)
    {
        this.color = color;
        changedImage();
    }

    /**
     * The X alignment (0 = left aligned, 0.5 = centered, 1 = right aligned).
     * 
     * @return
     */
    public double getXAlignment()
    {
        return this.xAlignment;
    }

    /**
     * The X alignment (0 = left aligned, 0.5 = centered, 1 = right aligned).
     * 
     * @param xAlignment
     */
    public void setXAlignment(double xAlignment)
    {
        if ((xAlignment < 0) || (xAlignment > 1)) {
            throw new IllegalArgumentException("Alignments must be in the range (0..1)");
        }
        this.xAlignment = xAlignment;
        this.changeId += 1;
        changedShape();
    }

    /**
     * The Y alignment (0 = top aligned, 1 = bottom aligned).
     * 
     * @return
     */
    public double getYAlignment()
    {
        return this.yAlignment;
    }

    /**
     * The Y alignment (0 = top aligned, 1 = bottom aligned).
     * 
     * @param yAlignment
     */
    public void setYAlignment(double yAlignment)
    {
        if ((yAlignment < 0) || (yAlignment > 1)) {
            throw new IllegalArgumentException("Alignments must be in the range (0..1)");
        }
        this.yAlignment = yAlignment;
        this.changeId += 1;
        changedShape();
    }

    /**
     * Set the X and Y alignments
     * 
     * @param x
     *            0 = left aligned, 0.5 = centered, 1 = right aligned.
     * @param y
     *            0 = top, 1 = bottom
     */
    public void setAlignment(double x, double y)
    {
        setXAlignment(x);
        setYAlignment(y);
    }

    /**
     * Calculated from the X Alignment, and the width of the text.
     * 
     * @priority 3
     */
    @Override
    public int getOffsetX()
    {
        return (int) (this.getWidth() * this.xAlignment);
    }

    /**
     * Calculated from the Y Alignment, and the height of the text.
     * 
     * @priority 3
     */
    @Override
    public int getOffsetY()
    {
        return (int) (this.getHeight() * this.yAlignment);
    }

    /**
     * @return 0
     * @priority 3
     */
    @Override
    public double getDirection()
    {
        return 0;
    }

    /**
     * @return The width of the text
     * @priority 3
     */
    public abstract int getWidth();

    /**
     * @return The height of the text
     * @priority 3
     */
    public abstract int getHeight();

    /**
     * Used internally; called when the text has changed, and the surface needs to be re-rendered.
     * 
     * @priority 5
     */
    protected void changedShape()
    {
        this.changedImage();
        if (this.appearance != null) {
            this.appearance.invalidateShape();
        }
    }

    /**
     * Used internally; called when the text has changed, and the surface needs to be re-rendered.
     * 
     * @priority 5
     */
    protected void changedImage()
    {
        this.changeId++;
    }

    /**
     * Gets the bitmap (surface) for this text.
     * 
     * @priority 3
     */
    @Override
    public abstract Surface getSurface();

    /**
     * Used internally as part of the optimisation.
     * 
     * @priority 5
     */
    @Override
    public int getChangeId()
    {
        return this.changeId;
    }

    /**
     * Used internally by Itchy.
     * 
     * @priority 5
     */
    @Override
    public void attach(Appearance appearance)
    {
        this.appearance = appearance;
    }
}
