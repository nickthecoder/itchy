package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uk.co.nickthecoder.jame.JameRuntimeException;
import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.event.MouseEvent;

public class ScrollableLayer extends ActorsLayer
{
    /**
     * The color to fill the whole area before actors are rendered, or null if not fill should take
     * place.
     */
    public RGBA backgroundColor;

    /**
     * The area of the world visible within this scrollable layer. Note, because zooming is not
     * allowed, then the width and height are the same as in super.positionOnScreen.
     */

    public ScrollableLayer( String name, Rect position )
    {
        this(name, position, null);
    }

    public ScrollableLayer( String name, Rect position, RGBA backgroundColor )
    {
        super(name, position);

        this.backgroundColor = backgroundColor;
    }

    public void setBackground( RGBA background )
    {
        this.backgroundColor = background;
    }

    public void ceterOn( Actor actor )
    {
        this.centerOn(actor.getX(), actor.getY());
    }

    public void centerOn( double x, double y )
    {
        this.worldRect.x = x - this.worldRect.width / 2;
        this.worldRect.y = y - this.worldRect.height / 2;
    }

    public void scrollTo( double x, double y )
    {
        this.worldRect.x = x;
        this.worldRect.y = y;
    }

    public void scrollBy( double dx, double dy )
    {
        this.worldRect.x += dx;
        this.worldRect.y += dy;
    }

    @Override
    protected void adjustMouse( MouseEvent event )
    {
        super.adjustMouse(event);
        event.x += this.worldRect.x;
        event.y += this.worldRect.y;
    }

    @Override
    public void render2( Rect clip, Surface destSurface )
    {
        if (this.backgroundColor != null) {
            destSurface.fill(clip, this.backgroundColor);
        }

        int clipLeft = clip.x;
        int clipTop = clip.y;
        int clipWidth = clip.width;
        int clipHeight = clip.height;

        // Where is the world's (0,0) on screen (in screen coordinates)?
        int tx = clipLeft - (int) this.worldRect.x;
        int ty = this.getYAxisPointsDown() ? clipTop - (int) this.worldRect.y : clipTop +
            clipHeight + (int) this.worldRect.y;

        List<Actor> actors = new ArrayList<Actor>();
        actors.addAll(this.actors);

        for (Iterator<Actor> i = this.actors.iterator(); i.hasNext();) {
            Actor actor = i.next();

            if (actor.isDead()) {
                i.remove();
                continue;
            }

            if (actor.getAppearance().getAlpha() < 2) {
                continue;
            }

            if (actor.getAppearance().visibleWithin(this.worldRect)) {

                for (int retry = 0; retry < 5; retry++) {

                    Surface actorSurface = actor.getSurface(); // Ensures the
                                                               // surface has
                                                               // been rendered,
                                                               // and offset_x,y
                                                               // are now valid.

                    // Top left of where the actor needs to be placed on the
                    // screen.
                    // Note the change of sign for "y", because in world
                    // coordinates "down" is negative.
                    int screenX = tx + (int) (actor.getX()) - actor.getAppearance().getOffsetX();
                    int screenY = this.getYAxisPointsDown() ? ty + (int) (actor.getY()) -
                        actor.getAppearance().getOffsetY() : ty - (int) (actor.getY()) -
                        actor.getAppearance().getOffsetY();

                    try {
                        // actorSurface.blit( Itchy.singleton.screen, 0, 0 ); //
                        // DEBUG

                        int width = actorSurface.getWidth();
                        int height = actorSurface.getHeight();
                        int offsetX = 0;
                        int offsetY = 0;

                        // Clip within the layers positionOnScreen
                        if (screenX < clipLeft) {
                            // left
                            offsetX = clipLeft - screenX;
                            screenX += offsetX;
                            width -= offsetY;
                        }
                        if (screenY < clipTop) {
                            // top
                            offsetY = clipTop - screenY;
                            screenY += offsetY;
                            height -= offsetY;
                        }
                        if (screenX + actorSurface.getWidth() > clipLeft + clipWidth) {
                            // right
                            width -= screenX + actorSurface.getWidth() - (clipLeft + clipWidth);
                        }
                        if (screenY + actorSurface.getHeight() > clipTop + clipHeight) {
                            // bottom
                            height -= screenY + actorSurface.getHeight() - (clipTop + clipHeight);
                        }

                        Rect srcRect = new Rect(offsetX, offsetY, width, height);
                        Rect rect = new Rect(screenX, screenY, width, height);

                        int alpha = (int) (actor.getAppearance().getAlpha());
                        if (alpha >= 255) {

                            // Fully opaque (normal behaviour)
                            actorSurface.blit(srcRect, destSurface, rect);

                        } else {

                            if (alpha > 0 /* totally transparent */) {

                                // Semi-transparent
                                // Create a temp surface, and blit the current
                                // contents of the screen onto it
                                Surface tempSurface = new Surface(width, height, false);
                                Rect tempRect = new Rect(0, 0, width, height);
                                destSurface.blit(rect, tempSurface, tempRect);

                                // Now blit the actor onto it
                                Rect tempRect2 = new Rect(offsetX, offsetY, width, height);
                                actorSurface.blit(tempRect2, tempSurface, tempRect);

                                // Now blit the temp surface onto the screen,
                                // with the correct amount of alpha
                                tempSurface.setPerSurfaceAlpha(alpha);
                                // tempSurface.fillRect( 1234567890 );
                                tempSurface.blit(destSurface, screenX, screenY);

                                tempSurface.free();
                            }

                        }
                        break; // Exit from the retry loop, as we've completed
                               // the operation without error.

                    } catch (JameRuntimeException e) {
                        actor.getAppearance().clearCachedSurface();
                        System.err.println("WARNING : attempt #" + retry +
                            " failed to blit surface during ScrollableLayer.render");
                    }
                }
            }
        }
    }

    @Override
    public void destroy()
    {
        this.clear();
    }

}
