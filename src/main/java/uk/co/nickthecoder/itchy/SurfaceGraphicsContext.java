package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;

public class SurfaceGraphicsContext extends GraphicsContext
{
    private Surface destination;


    public SurfaceGraphicsContext( Surface destination )
    {
        super( destination.getWidth(), destination.getHeight() );
        this.destination = destination;
    }
    
    public void blit( Surface surface, int x, int y, Surface.BlendMode blendMode )
    {
        this.blit(surface, new Rect(0, 0, surface.getWidth(), surface.getHeight()), x, y, blendMode);
    }

    public void blit( Surface surface, Rect origSrcRect, int x, int y, Surface.BlendMode blendMode )
    {
        blit( surface, origSrcRect, x, y, blendMode, 255 );
    }
    
    public void blit( Surface surface, Rect origSrcRect, int x, int y, Surface.BlendMode blendMode, int alpha )
    {
        x += this.ox;
        y += this.oy;

        Rect srcRect = new Rect(origSrcRect);

        int clipRight = (x + srcRect.width) - (this.clip.x + this.clip.width);
        if (clipRight > 0) {
            srcRect.width -= clipRight;
        }

        int clipLeft = this.clip.x - x;
        if (clipLeft > 0) {
            x += clipLeft;
            srcRect.x += clipLeft;
            srcRect.width -= clipLeft;
        }

        if (srcRect.width < 0) {
            return;
        }

        int clipBottom = (y + srcRect.height) - (this.clip.y + this.clip.height);
        if (clipBottom > 0) {
            srcRect.height -= clipBottom;
        }

        int clipTop = this.clip.y - y;
        if (clipTop > 0) {
            y += clipTop;
            srcRect.y += clipTop;
            srcRect.height -= clipTop;
        }

        if (srcRect.height < 0) {
            return;
        }

        if (alpha >= 255) {
            
            surface.blit(srcRect, this.destination, x, y, blendMode);
            
        } else {
            // Semi-transparent
            // Create a temp surface, and blit the current
            // contents of the screen onto it
            int width = srcRect.width;
            int height = srcRect.height;
            
            Surface tempSurface = new Surface(width, height, false);
            Rect tempRect = new Rect(0, 0, width, height);
            Rect destRect = new Rect(x, y, width, height);

            this.destination.blit(destRect, tempSurface, tempRect);
    
            // Now blit the actor onto it
            surface.blit(srcRect, tempSurface, tempRect);
    
            // Now blit the temp surface onto the screen,
            // with the correct amount of alpha
            tempSurface.setPerSurfaceAlpha(alpha);
            tempSurface.blit(this.destination, x, y, blendMode);
    
            tempSurface.free();
        }
    }

    @Override
    public void render(Actor actor, int alpha)
    {
        Surface actorSurface = actor.getSurface();
        Appearance appearance = actor.getAppearance();
        
        int x = (int) actor.getX() - appearance.getOffsetX();
        int y = (int) -actor.getY() - appearance.getOffsetY();
        
        Rect rect = new Rect(0, 0, actorSurface.getWidth(), actorSurface.getHeight());
        
        this.blit(actorSurface, rect, x, y, Surface.BlendMode.NONE, alpha );

    }
    
    protected GraphicsContext duplicate()
    {
        return new SurfaceGraphicsContext(this.destination);
    }
    
    public void fill( Rect rect, RGBA color )
    {
        Rect destRect = this.adjustRect(rect);

        if (destRect != null) {
            this.destination.fill(destRect, color);
        }
    }

    public String toString()
    {
        return "Surface" + super.toString();
    }


}
