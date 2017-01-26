package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Renderer;
import uk.co.nickthecoder.jame.Texture;

/**
 * A context for hardware accelerated rendering. Used to draw to a window (or to an intermediate Texture).
 * It can keep track of offsets, and clipping rectangles, so that drawing can be specified in coordinates
 * relevant to the view in questing, rather than in window coordinates.
 */
public class NewGraphicsContext
{
    /**
     * The position on the destination surface which corresponds to the world's origin. For example, if ox is 10, and an
     * image is blitted at
     * x=30, then the image will be blitted at x=40 on the destination surface.
     */
    protected int ox;

    protected int oy;

    /**
     * The clipping rectangle of the destination surface (not in world coordinates).
     */
    protected Rect clip;

    public Renderer renderer;

    protected int width;

    protected int height;

    public NewGraphicsContext(Renderer renderer, int width, int height)
    {
        this.width = width;
        this.height = height;
        this.renderer = renderer;
        this.clip = new Rect(0, 0, width, height);
    }

    protected NewGraphicsContext duplicate()
    {
        return new NewGraphicsContext(renderer, width, height);
    }

    public NewGraphicsContext window(Rect rect)
    {
        // TODO Use *real*, hardware based clipping, rather than just remembering a clipping rectangle.
        
        NewGraphicsContext result = duplicate();
        result.ox = this.ox + rect.x;
        result.oy = this.oy + rect.y;

        result.clip = new Rect(result.ox, result.oy, rect.width, rect.height);

        int dx = (result.clip.x + result.clip.width) - (this.clip.x + this.clip.width);
        int dy = (result.clip.y + result.clip.height) - (this.clip.y + this.clip.height);

        if (dx > 0) {
            result.clip.width -= dx;
        }
        if (dy > 0) {
            result.clip.height -= dy;
        }

        dx = this.clip.x - result.clip.x;
        if (dx > 0) {
            result.clip.x += dx;
            result.clip.width -= dx;
        }
        dy = this.clip.y - result.clip.y;
        if (dy > 0) {
            result.clip.y += dy;
            result.clip.height -= dy;
        }

        return result;
    }

    protected Rect adjustRect(Rect rect)
    {
        Rect result = new Rect(rect.x + this.ox, rect.y + this.oy, rect.width, rect.height);
        return result;
    }

    public void render(Actor actor, int alpha)
    {
        Appearance appearance = actor.getAppearance();
        Pose pose = appearance.getPose();

        Texture texture = pose.getTexture(renderer);

        int x = (int) actor.getX() - appearance.getOffsetX();
        int y = (int) -actor.getY() - appearance.getOffsetY();

        // TODO Perform scale and rotate, clip and offset
        // TODO y+height is a quick bodge instead of the correct clip and offset
        renderer.copy(texture, x, y+height);

    }

    public void fill(Rect rect, RGBA color)
    {
        Rect destRect = this.adjustRect(rect);

        if (destRect != null) {
            renderer.setDrawColor(color);
            renderer.fillRect(rect);
        }
    }
}
