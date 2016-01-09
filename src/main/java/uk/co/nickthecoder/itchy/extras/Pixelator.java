package uk.co.nickthecoder.itchy.extras;

import uk.co.nickthecoder.itchy.ImagePose;
import uk.co.nickthecoder.itchy.Pose;
import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;

/**
 * Scales an image, and where pixels are a different colour from their neighbour, add an embossed effect.
 */
public class Pixelator
{
    public RGBA highlight;

    public RGBA lowlight;

    public int scale;

    public Pixelator(int scale, RGBA highlight, RGBA lowlight)
    {
        this.scale = scale;
        this.highlight = highlight;
        this.lowlight = lowlight;
    }


    public ImagePose pixelate(Pose pose)
    {
        return pixelate(pose, false);
    }
    
    public ImagePose pixelate(Pose pose, boolean flip)
    {
        ImagePose result = new ImagePose(pixelate(pose.getSurface(), flip));
        result.setOffsetX(pose.getOffsetX() * scale);
        if (flip) {
            result.setOffsetX(result.getSurface().getWidth() - result.getOffsetX());
        }
        result.setOffsetY(pose.getOffsetY() * scale);
        return result;
    }

    public Surface pixelate(Surface source )
    {
        return pixelate(source, false);
    }
    
    public Surface pixelate(Surface source, boolean flip )
    {
        // Make a scaled copy
        if (flip) {
            source = source.zoom(-1, 1, false );
        }
        Surface result = source.rotoZoom(0, scale, false);

        // Apply highlights and lowlights on top of the scaled image
        // wherever the colour changes.

        Surface high = new Surface(scale, scale, true);
        high.fill(highlight);
        Surface low = new Surface(scale, scale, true);
        low.fill(lowlight);

        Rect hRect = new Rect(0, 0, scale, 1);
        Rect hRectShort = new Rect(0, 0, scale - 1, 1);
        Rect vRect = new Rect(0, 0, 1, scale);
        Rect vRectShort = new Rect(0, 0, 1, scale - 1);
        Rect vRectDoubleShort = new Rect(0, 0, 1, scale - 2);

        for (int x = 0; x < source.getWidth(); x++) {
            for (int y = 0; y < source.getHeight(); y++) {
                RGBA rgba = source.getPixelRGBA(x, y);
                if (rgba.a > 10 ) {
                    long color = source.getPixelColor(x, y);
                    boolean top = changedColor(source, x, y - 1, color);
                    boolean left = changedColor(source, x - 1, y, color);
                    boolean bottom = changedColor(source, x, y + 1, color);
                    boolean right = changedColor(source, x + 1, y, color);
    
                    int bodge = 1; // Why do I need to add 1 to all x,y values ?
                    if (top) {
                        high.blit(hRect, result, bodge + x * scale, 1 + y * scale);
                    }
                    if (left) {
                        if (top) {
                            high.blit(vRectShort, result, bodge + x * scale, 1 + y * scale + 1);
                        } else {
                            high.blit(vRect, result, bodge + x * scale, 1 + y * scale);
                        }
                    }
                    if (bottom) {
                        if (left) {
                            low.blit(hRectShort, result, bodge + x * scale + 1, bodge + y * scale + scale - 1);
                        } else {
                            low.blit(hRect, result, bodge + x * scale, bodge + y * scale + scale - 1);
                        }
                    }
                    if (right) {
                        if (top) {
                            if (bottom) {
                                low.blit(vRectDoubleShort, result, bodge + x * scale + scale - 1, bodge + y * scale + 1);
                            } else {
                                low.blit(vRectShort, result, bodge + x * scale + scale - 1, bodge + y * scale + 1);
                            }
                        } else {
                            if (bottom) {
                                low.blit(vRectShort, result, bodge + x * scale + scale - 1, bodge + y * scale);
                            } else {
                                low.blit(vRect, result, bodge + x * scale + scale - 1, bodge + y * scale);
                            }
                        }
                   }
                }
            }
        }

        return result;
    }

    protected boolean changedColor(Surface source, int x, int y, long color)
    {
        if ((x < 0) || (y < 0) || (x >= source.getWidth()) || (y >= source.getHeight())) {
            return true;
        }
        long other = source.getPixelColor(x, y);
        return other != color;
    }
}
