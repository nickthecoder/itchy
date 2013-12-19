/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.Surface.BlendMode;
import uk.co.nickthecoder.jame.TrueTypeFont;

public class MultiLineTextPose extends AbstractTextPose
{
    private Surface surface;

    private boolean fixedSize = false;

    private int fixedWidth;

    private int fixedHeight;

    public MultiLineTextPose( Font font, double fontSize )
    {
        this(font, fontSize, new RGBA(255, 255, 255));
    }

    public MultiLineTextPose( Font font, double fontSize, RGBA color )
    {
        super(font, fontSize, color);
    }

    @Override
    protected void changedImage()
    {
        super.changedImage();

        if (this.surface != null) {
            this.surface.free();
        }
        this.surface = null;
    }

    private void ensureCached()
    {
        if (this.surface == null) {
            drawSurface();
        }
    }

    private void drawSurface()
    {
        TrueTypeFont ttf = this.getTrueTypeFont();
        int lineHeight = ttf.getLineHeight();

        String lines[] = this.getText().split("\\r?\\n");
        Surface[] surfaces = new Surface[lines.length];

        int maxWidth = 0;
        for (int i = 0; i < surfaces.length; i++) {
            surfaces[i] = ttf.renderBlended(lines[i], this.getColor());
            if (surfaces[i].getWidth() > maxWidth) {
                maxWidth = surfaces[i].getWidth();
            }
        }

        int textHeight = lines.length * lineHeight;
        int height = this.fixedSize ? this.fixedHeight : textHeight;
        int width = this.fixedSize ? this.fixedWidth : maxWidth;

        this.surface = new Surface(width, height, true);

        for (int i = 0; i < surfaces.length; i++) {
            int x = (int) ((width - surfaces[i].getWidth()) * getXAlignment());
            int y = (int) ((height - textHeight) * getYAlignment()) + i * lineHeight;

            surfaces[i].blit(this.surface, x, y, BlendMode.COMPOSITE);
            surfaces[i].free();
        }
    }

    @Override
    public Surface getSurface()
    {
        this.ensureCached();
        return this.surface;
    }

    public void autoSize()
    {
        this.fixedSize = false;
    }

    public void setSize( int width, int height )
    {
        this.fixedSize = true;
        this.fixedWidth = width;
        this.fixedHeight = height;
    }

    public void setSizeInCharacters( int across, int down )
    {
        int em = getTrueTypeFont().sizeText("M");
        this.setSize(em * across, getTrueTypeFont().getLineHeight() * down);
    }

    @Override
    public int getWidth()
    {
        if (this.fixedSize) {
            return this.fixedWidth;
        }

        this.ensureCached();
        return this.surface.getWidth();
    }

    @Override
    public int getHeight()
    {
        if (this.fixedSize) {
            return this.fixedHeight;
        }

        this.ensureCached();
        return this.surface.getHeight();
    }

    @Override
    public boolean isShared()
    {
        return true;
    }

}
