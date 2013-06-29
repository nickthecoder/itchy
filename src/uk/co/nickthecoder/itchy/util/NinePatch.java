package uk.co.nickthecoder.itchy.util;

import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;

/**
 * Similar to a NinePatch, but the center is left transparent.
 *
 */
public class NinePatch extends ImageRenderable
{
    public enum Middle
    {
        empty, tile, fill
    };

    public int marginTop;
    public int marginRight;
    public int marginBottom;
    public int marginLeft;

    public Middle middle;

    protected RGBA backgroundColor;

    public NinePatch( Surface surface, int marginTop, int marginRight, int marginBottom, int marginLeft )
    {
        this( surface, marginTop, marginRight, marginBottom, marginLeft, Middle.tile );
    }

    public NinePatch( Surface surface, int marginTop, int marginRight, int marginBottom, int marginLeft, Middle middle )
    {
        super( surface );

        this.middle = middle;

        this.marginTop = marginTop;
        this.marginRight = marginRight;
        this.marginBottom = marginBottom;
        this.marginLeft = marginLeft;

        int midY = ( this.marginTop + surface.getHeight() - this.marginBottom ) / 2;
        int midX = ( this.marginLeft + surface.getWidth() - this.marginRight ) / 2;
        this.backgroundColor = surface.getPixelRGBA( midX, midY );
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

    public Surface createSurface( int width, int height )
    {
        Surface surface = new Surface( width, height, true );
        this.render( surface );
        return surface;
    }

    @Override
    public void render( Surface result )
    {
        Rect srcRect;
        Rect destRect;

        int blankHeight = this.surface.getHeight() - this.marginTop - this.marginBottom;
        int blankWidth = this.surface.getWidth() - this.marginLeft - this.marginRight;
        int midY = ( this.marginTop + result.getHeight() - this.marginBottom ) / 2;
        int midX = ( this.marginLeft + result.getWidth() - this.marginRight ) / 2;

        this.surface.setAlphaEnabled( false );

        if ( this.middle == Middle.fill ) {
            // flat fill the middle

            Rect rect = new Rect( this.marginLeft, this.marginTop, result.getWidth() - this.marginLeft
                - this.marginRight, result.getHeight() - this.marginTop - this.marginBottom );
            result.fill( rect, this.backgroundColor );

        } else if ( this.middle == Middle.tile ) {
            // tile the middle

            srcRect = new Rect( this.marginLeft, this.marginTop, blankWidth, blankHeight );
            destRect = new Rect( 0, 0, this.marginLeft, blankHeight );
            for ( int x = midX; x < result.getWidth(); x += blankWidth ) {
                for ( int y = midY; y < result.getHeight(); y += blankHeight ) {
                    destRect.x = x;
                    destRect.y = y;
                    this.surface.blit( srcRect, result, destRect );
                    destRect.y = midY * 2 - y - blankHeight;
                    this.surface.blit( srcRect, result, destRect );

                    destRect.x = midX * 2 - x - blankWidth;
                    destRect.y = y;
                    this.surface.blit( srcRect, result, destRect );
                    destRect.y = midY * 2 - y - blankHeight;
                    this.surface.blit( srcRect, result, destRect );
                }
            }
        }

        // left edge
        srcRect = new Rect( 0, this.marginTop, this.marginLeft, blankHeight );
        destRect = new Rect( 0, 0, this.marginLeft, blankHeight );
        for ( int y = midY; y < result.getHeight(); y += blankHeight ) {
            destRect.y = y;
            this.surface.blit( srcRect, result, destRect );
            destRect.y = midY * 2 - y - blankHeight;
            this.surface.blit( srcRect, result, destRect );
        }

        // right edge
        srcRect = new Rect( this.surface.getWidth() - this.marginRight, this.marginTop, this.marginRight, blankHeight );
        destRect = new Rect( result.getWidth() - this.marginRight, 0, this.marginRight, blankHeight );
        for ( int y = midY; y < result.getHeight(); y += blankHeight ) {
            destRect.y = y;
            this.surface.blit( srcRect, result, destRect );
            destRect.y = midY * 2 - y - blankHeight;
            this.surface.blit( srcRect, result, destRect );
        }

        // top edge
        srcRect = new Rect( this.marginLeft, 0, blankWidth, this.marginTop );
        destRect = new Rect( 0, 0, blankWidth, this.marginTop );
        for ( int x = midX; x < result.getWidth(); x += blankWidth ) {
            destRect.x = x;
            this.surface.blit( srcRect, result, destRect );
            destRect.x = midX * 2 - x - blankWidth;
            this.surface.blit( srcRect, result, destRect );
        }

        // bottom edge
        srcRect = new Rect( this.marginLeft, this.surface.getHeight() - this.marginBottom, blankWidth,
            this.marginBottom );
        destRect = new Rect( 0, result.getHeight() - this.marginBottom, blankWidth, this.marginBottom );
        for ( int x = midX; x < result.getWidth(); x += blankWidth ) {
            destRect.x = x;
            this.surface.blit( srcRect, result, destRect );
            destRect.x = midX * 2 - x - blankWidth;
            this.surface.blit( srcRect, result, destRect );
        }

        // top left corner
        srcRect = new Rect( 0, 0, this.marginLeft, this.marginTop );
        destRect = new Rect( 0, 0, this.marginLeft, this.marginTop );
        this.surface.blit( srcRect, result, destRect );

        // top right corner
        srcRect = new Rect( this.surface.getWidth() - this.marginRight, 0, this.marginRight, this.marginTop );
        destRect = new Rect( result.getWidth() - this.marginRight, 0, this.marginRight, this.marginTop );
        this.surface.blit( srcRect, result, destRect );

        // bottom left corner
        srcRect = new Rect( 0, this.surface.getHeight() - this.marginBottom, this.marginLeft, this.marginBottom );
        destRect = new Rect( 0, result.getHeight() - this.marginBottom, this.marginLeft, this.marginBottom );
        this.surface.blit( srcRect, result, destRect );

        // bottom right corner
        srcRect = new Rect( this.surface.getWidth() - this.marginRight, this.surface.getHeight() - this.marginBottom,
            this.marginRight, this.marginBottom );
        destRect = new Rect( result.getWidth() - this.marginRight, result.getHeight() - this.marginBottom,
            this.marginRight, this.marginBottom );
        this.surface.blit( srcRect, result, destRect );

        this.surface.setAlphaEnabled( true );

    }

}
