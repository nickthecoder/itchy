package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.GraphicsContext;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.gui.AbstractComponent;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.util.NinePatch;
import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;

public class EditNinePatch extends EditNamedSubject<NinePatch>
{

    public EditNinePatch(Resources resources, ListNinePatches list, NinePatch subject, boolean adding)
    {
        super(resources, list, subject, adding);
    }

    @Override
    protected String getSubjectName()
    {
        return "Input";
    }

    @Override
    protected NinePatch getSubjectByName(String name)
    {
        return resources.getNinePatch(name);
    }

    @Override
    protected void add()
    {
        resources.addNinePatch(subject);
    }

    @Override
    protected void rename()
    {
        resources.rename(subject);
    }
    
    @Override
    protected Component createForm()
    {
        super.createForm();
        ExplodedImage img = new ExplodedImage();
        form.grid.addRow("Image", img);
        
        return form.container;
    }

    public class ExplodedImage extends AbstractComponent
    {
        private final Surface surface;

        private final int spacing = 10;

        private int backgroundIndex = 0;

        private final RGBA[] backgrounds = new RGBA[] {
            null, new RGBA(0, 0, 0), new RGBA(255, 255, 255), new RGBA(128, 128, 128) };

        public ExplodedImage()
        {
            surface = subject.getSurface();
        }

        @Override
        public int getNaturalWidth()
        {
            return surface.getWidth() + spacing * 2;
        }

        @Override
        public int getNaturalHeight()
        {
            return surface.getHeight() + spacing * 2;
        }

        @Override
        public void onMouseDown(MouseButtonEvent mbe)
        {
            if (mbe.button == 1) {
                backgroundIndex++;
                if (backgroundIndex >= backgrounds.length) {
                    backgroundIndex = 0;
                }
                this.invalidate();
                mbe.stopPropagation();
            }
            super.onMouseDown(mbe);
        }


        @Override
        public void render(GraphicsContext gc)
        {
            RGBA background = backgrounds[backgroundIndex];
            if (background == null) {
                this.renderBackground(gc);
            } else {
                Rect whole = new Rect(0, 0, surface.getWidth() + spacing * 2, surface.getHeight() + spacing * 2);
                gc.fill(whole, background);
            }

            int top = subject.getMarginTop();
            int right = subject.getMarginRight();
            int bottom = subject.getMarginBottom();
            int left = subject.getMarginLeft();
            
            int width = surface.getWidth();
            int height = surface.getHeight();

            // Top left
            Rect srcRect = new Rect(0, 0, left, top);
            gc.blit(surface, srcRect, 0, 0, Surface.BlendMode.NONE);

            // Top edge
            srcRect = new Rect(left, 0, width - left - right, top);
            gc.blit(surface, srcRect, left + spacing, 0, Surface.BlendMode.NONE);

            // Top right
            srcRect = new Rect(width - right, 0, right, top);
            gc.blit(surface, srcRect, width - right + spacing * 2, 0, Surface.BlendMode.NONE);

            // Left Edge
            srcRect = new Rect(0, top, left, height - top - bottom);
            gc.blit(surface, srcRect, 0, top + spacing, Surface.BlendMode.NONE);

            // Center
            srcRect = new Rect(left, top, width - left - right, height - top - bottom);
            gc.blit(surface, srcRect, left + spacing, top + spacing, Surface.BlendMode.NONE);

            // Right Edge
            srcRect = new Rect(width - right, top, right, height - top - bottom);
            gc.blit(surface, srcRect, width - right + spacing * 2, top + spacing, Surface.BlendMode.NONE);

            // Bottom Left
            srcRect = new Rect(0, height - bottom, left, bottom);
            gc.blit(surface, srcRect, 0, height - bottom + spacing * 2, Surface.BlendMode.NONE);

            // Bottom edge
            srcRect = new Rect(left, height - bottom, width - left - right, bottom);
            gc.blit(surface, srcRect, left + spacing, height - bottom + spacing * 2, Surface.BlendMode.NONE);

            // Bottom right
            srcRect = new Rect(width - right, height - bottom, right, bottom);
            gc.blit(surface, srcRect, width - right + spacing * 2, height - bottom + spacing * 2,
                Surface.BlendMode.NONE);
        }
    }

}
