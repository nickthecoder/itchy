/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.NamedSubject;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.property.EnumProperty;
import uk.co.nickthecoder.itchy.property.FileProperty;
import uk.co.nickthecoder.itchy.property.IntegerProperty;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.RGBAProperty;
import uk.co.nickthecoder.itchy.property.StringProperty;
import uk.co.nickthecoder.jame.JameException;
import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;

/**
 * Divide an image into a 3x3 grid, and you have nine patches, which can be used to draw a larger image. Imagine we have
 * an image of a plain
 * button background, and we want to draw a much larger button.
 * 
 * Take the corners, and copy them into the corners of the destination image. Now take each side, and copy it multiple
 * times so that it
 * reaches from one corner to the other. We are just left with the centre to complete the final image. We could tile the
 * source's centre
 * into the destination centre, but for some images, a flat fill is all that's needed.
 */
public class NinePatch extends ImageRenderable implements NamedSubject<NinePatch>
{

    public static final int THUMBNAIL_WIDTH = 100;

    public static final int THUMBNAIL_HEIGHT = 60;

    protected static final List<Property<NinePatch, ?>> properties = new ArrayList<Property<NinePatch, ?>>();

    static {
        properties.add(new StringProperty<NinePatch>("name").allowBlank(false));
        properties.add(new FileProperty<NinePatch>("file").aliases("filename"));
        properties.add(new IntegerProperty<NinePatch>("top").access("marginTop"));
        properties.add(new IntegerProperty<NinePatch>("right").access("marginRight"));
        properties.add(new IntegerProperty<NinePatch>("bottom").access("marginBottom"));
        properties.add(new IntegerProperty<NinePatch>("left").access("marginLeft"));
        properties.add(new EnumProperty<NinePatch, Middle>("middle", Middle.class));
        properties.add(new RGBAProperty<NinePatch>("backgroundColor").allowNull(true));
    }

    @Override
    public List<Property<NinePatch, ?>> getProperties()
    {
        return properties;
    }

    /**
     * All of the ways that the center can be rendered.
     */
    public enum Middle
    {
        empty, tile, fill
    };
    
    
    private String name;

    private File file;

    private Surface thumbnail;

    public int marginTop;

    public int marginRight;

    public int marginBottom;

    public int marginLeft;

    /**
     * How should the middle of the destination image be drawn?
     */
    public Middle middle;

    /**
     * If {@link #middle} is <code>Middle.fill</code>, then this is the colour used to draw the middle.
     */
    public RGBA backgroundColor;

    /**
     * If a background color isn't specified, then use the middle pixel of the nine patch as the background color.
     */
    private RGBA midColor;
    
    public NinePatch()
    {
        middle = Middle.tile;
    }

    public int getMinimumWidth()
    {
        return this.marginLeft + this.marginRight;
    }

    public int getMinimumHeight()
    {
        return this.marginTop + this.marginBottom;
    }

    public int getMarginTop()
    {
        return this.marginTop;
    }

    public int getMarginRight()
    {
        return this.marginRight;
    }

    public int getMarginBottom()
    {
        return this.marginBottom;
    }

    public int getMarginLeft()
    {
        return this.marginLeft;
    }

    public void setFile(File file) throws JameException
    {
        this.loadImage(Resources.getCurrentResources().resolveFilename(file.getPath()));
        this.file = file;
        this.midColor = null;
    }

    public File getFile()
    {
        return this.file;
    }

    public Surface getThumbnail()
    {
        if (this.thumbnail == null) {

            Surface full = this.getSurface();
            if ((full.getWidth() > THUMBNAIL_WIDTH) || (full.getHeight() > THUMBNAIL_HEIGHT)) {
                double scale = Math.min(THUMBNAIL_WIDTH / (double) full.getWidth(),
                    THUMBNAIL_HEIGHT / (double) full.getHeight());
                this.thumbnail = full.zoom(scale, scale, true);
            } else {
                this.thumbnail = full;
            }

        }
        return this.thumbnail;
    }

    /**
     * Creates a new Surface of the given size and renders this NinePatch onto it.
     * 
     * @param width
     *            The width of the new Surface.
     * @param height
     *            The height of the new Surface
     * @return A new Surface, of the given size, with an alpha channel.
     */
    public Surface createSurface(int width, int height)
    {
        Surface surface = new Surface(width, height, true);
        this.render(surface);
        return surface;
    }

    /**
     * Draws the nine patch onto <code>result</code>.
     * 
     * @param result
     *            The destination surface. It is assumed to be a blank image.
     */
    @Override
    public void render(Surface result)
    {
        Rect srcRect;
        Rect destRect;

        int blankHeight = this.surface.getHeight() - this.marginTop - this.marginBottom;
        int blankWidth = this.surface.getWidth() - this.marginLeft - this.marginRight;
        int midY = (this.marginTop + result.getHeight() - this.marginBottom) / 2;
        int midX = (this.marginLeft + result.getWidth() - this.marginRight) / 2;

        // TODO setAlphaEnabled 
        //this.surface.setAlphaEnabled(false);

        if (this.middle == Middle.fill) {
            // flat fill the middle

            RGBA bg = this.backgroundColor;
            if (bg == null) {
                if (this.midColor == null) {
                    int x = (this.surface.getWidth() - marginLeft - marginRight) / 2 + marginLeft;
                    int y = (this.surface.getHeight() - marginTop - marginBottom) / 2 + marginTop;
                    this.midColor = surface.getPixelRGBA(x, y);                
                }
                bg = this.midColor;
            }
            Rect rect = new Rect(this.marginLeft, this.marginTop, result.getWidth() -
                this.marginLeft - this.marginRight, result.getHeight() - this.marginTop -
                this.marginBottom);
            result.fill(rect, bg);
        
            
        } else if (this.middle == Middle.tile) {
            // tile the middle

            srcRect = new Rect(this.marginLeft, this.marginTop, blankWidth, blankHeight);
            destRect = new Rect(0, 0, this.marginLeft, blankHeight);
            for (int x = midX; x < result.getWidth(); x += blankWidth) {
                for (int y = midY; y < result.getHeight(); y += blankHeight) {
                    destRect.x = x;
                    destRect.y = y;
                    this.surface.blit(srcRect, result, destRect);
                    destRect.y = midY * 2 - y - blankHeight;
                    this.surface.blit(srcRect, result, destRect);

                    destRect.x = midX * 2 - x - blankWidth;
                    destRect.y = y;
                    this.surface.blit(srcRect, result, destRect);
                    destRect.y = midY * 2 - y - blankHeight;
                    this.surface.blit(srcRect, result, destRect);
                }
            }
        }

        // left edge
        srcRect = new Rect(0, this.marginTop, this.marginLeft, blankHeight);
        destRect = new Rect(0, 0, this.marginLeft, blankHeight);
        for (int y = midY; y < result.getHeight(); y += blankHeight) {
            destRect.y = y;
            this.surface.blit(srcRect, result, destRect);
            destRect.y = midY * 2 - y - blankHeight;
            this.surface.blit(srcRect, result, destRect);
        }

        // right edge
        srcRect = new Rect(this.surface.getWidth() - this.marginRight, this.marginTop,
            this.marginRight, blankHeight);
        destRect = new Rect(result.getWidth() - this.marginRight, 0, this.marginRight, blankHeight);
        for (int y = midY; y < result.getHeight(); y += blankHeight) {
            destRect.y = y;
            this.surface.blit(srcRect, result, destRect);
            destRect.y = midY * 2 - y - blankHeight;
            this.surface.blit(srcRect, result, destRect);
        }

        // top edge
        srcRect = new Rect(this.marginLeft, 0, blankWidth, this.marginTop);
        destRect = new Rect(0, 0, blankWidth, this.marginTop);
        for (int x = midX; x < result.getWidth(); x += blankWidth) {
            destRect.x = x;
            this.surface.blit(srcRect, result, destRect);
            destRect.x = midX * 2 - x - blankWidth;
            this.surface.blit(srcRect, result, destRect);
        }

        // bottom edge
        srcRect = new Rect(this.marginLeft, this.surface.getHeight() - this.marginBottom,
            blankWidth, this.marginBottom);
        destRect = new Rect(0, result.getHeight() - this.marginBottom, blankWidth,
            this.marginBottom);
        for (int x = midX; x < result.getWidth(); x += blankWidth) {
            destRect.x = x;
            this.surface.blit(srcRect, result, destRect);
            destRect.x = midX * 2 - x - blankWidth;
            this.surface.blit(srcRect, result, destRect);
        }

        // top left corner
        srcRect = new Rect(0, 0, this.marginLeft, this.marginTop);
        destRect = new Rect(0, 0, this.marginLeft, this.marginTop);
        this.surface.blit(srcRect, result, destRect);

        // top right corner
        srcRect = new Rect(this.surface.getWidth() - this.marginRight, 0, this.marginRight,
            this.marginTop);
        destRect = new Rect(result.getWidth() - this.marginRight, 0, this.marginRight,
            this.marginTop);
        this.surface.blit(srcRect, result, destRect);

        // bottom left corner
        srcRect = new Rect(0, this.surface.getHeight() - this.marginBottom, this.marginLeft,
            this.marginBottom);
        destRect = new Rect(0, result.getHeight() - this.marginBottom, this.marginLeft,
            this.marginBottom);
        this.surface.blit(srcRect, result, destRect);

        // bottom right corner
        srcRect = new Rect(this.surface.getWidth() - this.marginRight, this.surface.getHeight() -
            this.marginBottom, this.marginRight, this.marginBottom);
        destRect = new Rect(result.getWidth() - this.marginRight, result.getHeight() -
            this.marginBottom, this.marginRight, this.marginBottom);
        this.surface.blit(srcRect, result, destRect);

        // TODO setAlphaEnabled
        //this.surface.setAlphaEnabled(true);

    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public void setName(String name)
    {
        this.name = name;
    }

}
