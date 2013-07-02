package uk.co.nickthecoder.itchy.gui;

import uk.co.nickthecoder.itchy.GraphicsContext;
import uk.co.nickthecoder.jame.Surface;

public abstract class SurfaceComponent extends Component
{
    protected Surface plainSurface;

    @Override
    public int getNaturalHeight()
    {
        return this.getPlainSurface().getHeight();
    }

    @Override
    public int getNaturalWidth()
    {
        return this.getPlainSurface().getWidth();
    }

    public Surface getPlainSurface()
    {
        if (this.plainSurface == null) {
            this.createPlainSurface();
        }

        return this.plainSurface;
    }

    protected void clearPlainSurface()
    {
        if (this.plainSurface != null) {
            this.plainSurface.free();
            this.plainSurface = null;
        }
        this.invalidate();
    }

    @Override
    protected void render( GraphicsContext gc )
    {
        super.render(gc);
        Surface plainSurface = this.getPlainSurface();

        gc.blit(plainSurface, 0, 0, Surface.BlendMode.COMPOSITE);

    }

    protected abstract void createPlainSurface();

}
