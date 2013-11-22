/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

public class StageView extends AbstractScrollableView implements StageListener, MouseListener, MouseListenerView
{
    private Stage stage;

    public int minimumAlpha = 0;

    public int maximumAlpha = 255;

    /**
     * A list of Roles of Actors on this view's Stage, who implements ViewMouseListener. They are expecting to hear events with their x,y
     * coordinates adjusted to their world coordinates.
     */
    private Set<ViewMouseListener> roleMouseListeners = null;

    /**
     * The actor that requested to capture the mouse events.
     */
    private ViewMouseListener mouseOwner;

    public StageView( Rect position, Stage stage )
    {
        super(position);
        this.stage = stage;
        this.stage.addStageListener(this);
    }

    public Stage getStage()
    {
        return this.stage;
    }

    @Override
    public void render2( Surface destSurface, Rect clip, final int offsetX, final int offsetY )
    {
        // System.out.println( this.stage.getName() + " clip top  "+ clip.y + " clipHeight " +
        // clip.height + " offsety " + offsetY);

        // Where is the world's (0,0) on screen (in screen coordinates)?
        int tx = offsetX - (int) this.worldRect.x;
        int ty = offsetY + this.position.height + (int) this.worldRect.y;

        for (Iterator<Actor> i = this.stage.iterator(); i.hasNext();) {

            try {

                Actor actor = i.next();

                if (actor.isDead()) {
                    i.remove();
                    continue;
                }

                if ((actor.getAppearance().getAlpha() < 2) && (this.minimumAlpha < 2)) {
                    continue;
                }

                if (actor.getAppearance().visibleWithin(this.worldRect)) {

                    // Ensures the surface has been rendered, and offset_x,y are now valid.
                    Surface actorSurface = actor.getSurface();

                    // Top left of where the actor needs to be placed on the screen.
                    // Note the change of sign for "y", because in world
                    // coordinates "down" is negative.
                    int displayAtX = tx + (int) (actor.getX()) - actor.getAppearance().getOffsetX();
                    int displayAtY = ty - (int) (actor.getY()) - actor.getAppearance().getOffsetY();

                    int width = actorSurface.getWidth();
                    int height = actorSurface.getHeight();
                    int shiftX = 0;
                    int shiftY = 0;

                    // Clip within the layers positionOnScreen
                    if (displayAtX < clip.x) { // left
                        shiftX = clip.x - displayAtX;
                        displayAtX += shiftX;
                    }
                    if (displayAtY < clip.y) { // top
                        shiftY = clip.y - displayAtY;
                        displayAtY += shiftY;
                    }
                    if (displayAtX + actorSurface.getWidth() > clip.x + clip.width) { // right
                        width -= displayAtX + actorSurface.getWidth() - (clip.x + clip.width);
                    }
                    if (displayAtY + actorSurface.getHeight() > clip.y + clip.height) { // bottom
                        height -= displayAtY + actorSurface.getHeight() - (clip.y + clip.height);
                    }

                    if ((height > 0) && (width > 0)) {

                        Rect srcRect = new Rect(shiftX, shiftY, width, height);
                        Rect rect = new Rect(displayAtX, displayAtY, width, height);

                        int alpha = (int) (actor.getAppearance().getAlpha());
                        if (alpha < this.minimumAlpha) {
                            alpha = this.minimumAlpha;
                        }
                        if (alpha > this.maximumAlpha) {
                            alpha = this.maximumAlpha;
                        }
                        if (alpha >= 255) {

                            // Fully opaque (normal role)
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
                                Rect tempRect2 = new Rect(shiftX, shiftY, width, height);
                                actorSurface.blit(tempRect2, tempSurface, tempRect);

                                // Now blit the temp surface onto the screen,
                                // with the correct amount of alpha
                                tempSurface.setPerSurfaceAlpha(alpha);
                                tempSurface.blit(destSurface, displayAtX, displayAtY);

                                tempSurface.free();
                            }

                        }

                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAdded( Stage stage, Actor actor )
    {
        Role role = actor.getRole();
        if (role instanceof ViewMouseListener) {
            ViewMouseListener listener = ((ViewMouseListener) role);
            if (listener.isMouseListener()) {
                this.roleMouseListeners.add(listener);
            }
        }
    }

    @Override
    public void onChangedRole( Stage stage, Actor actor )
    {
        Role role = actor.getRole();
        if (role instanceof ViewMouseListener) {
            ViewMouseListener listener = ((ViewMouseListener) role);
            if (listener.isMouseListener()) {
                this.roleMouseListeners.add(listener);
            }
        }
    }

    @Override
    public void onRemoved( Stage stage, Actor actor )
    {
        Role role = actor.getRole();
        if ((this.roleMouseListeners != null) && (role instanceof ViewMouseListener)) {
            ViewMouseListener listener = ((ViewMouseListener) role);
            if (listener.isMouseListener()) {
                this.roleMouseListeners.remove(role);
            }
        }
    }

    @Override
    public void enableMouseListener( Game game )
    {
        this.roleMouseListeners = new HashSet<ViewMouseListener>();
        // TODO Iterate over all actors, and add them if the roles are ViewMouseListeners
    }

    @Override
    public void disableMouseListener( Game game )
    {
        this.roleMouseListeners.clear();
        this.roleMouseListeners = null;
    }

    @Override
    public void captureMouse( ViewMouseListener owner )
    {
        this.mouseOwner = owner;
        Itchy.getGame().captureMouse(this);
    }

    @Override
    public void releaseMouse( ViewMouseListener owner )
    {
        this.mouseOwner = null;
        Itchy.getGame().releaseMouse(this);
    }

    private int oldX;
    private int oldY;

    public boolean adjustMouse( MouseEvent event )
    {
        // Store actual x,y so it can be restored by unadjustMouse
        this.oldX = event.x;
        this.oldY = event.y;

        // Calculate the position within the view, with (0,0) as the bottom left of the viewport.
        Rect rect = getAbsolutePosition();
        event.x -= rect.x;
        event.y = rect.y + rect.height - event.y;
        boolean result = ((event.x >= 0) && (event.x < rect.width) && (event.y >= 0) && (event.y < rect.height));

        // Take scroll into affect.
        event.x += this.worldRect.x;
        event.y += this.worldRect.y;

        return result;
    }

    public void unadjustMouse( MouseEvent event )
    {
        event.x = this.oldX;
        event.y = this.oldY;
    }

    @Override
    public boolean onMouseDown( MouseButtonEvent event )
    {
        if (this.roleMouseListeners == null) {
            return false;
        }

        try {
            if (!adjustMouse(event)) {
                return false;
            }

            if (this.mouseOwner == null) {
                for (ViewMouseListener vml : this.roleMouseListeners) {
                    if (vml.onMouseDown(StageView.this, event)) {
                        return true;
                    }
                }
            } else {
                return this.mouseOwner.onMouseDown(StageView.this, event);
            }

        } finally {
            unadjustMouse(event);
        }

        return false;
    }

    @Override
    public boolean onMouseUp( MouseButtonEvent event )
    {
        if (this.roleMouseListeners == null) {
            return false;
        }

        try {
            if (!adjustMouse(event)) {
                return false;
            }

            if (this.mouseOwner == null) {
                for (ViewMouseListener vml : this.roleMouseListeners) {
                    if (vml.onMouseUp(StageView.this, event)) {
                        return true;
                    }
                }
            } else {
                return this.mouseOwner.onMouseUp(StageView.this, event);
            }

        } finally {
            unadjustMouse(event);
        }
        return false;
    }

    @Override
    public boolean onMouseMove( MouseMotionEvent event )
    {
        if (this.roleMouseListeners == null) {
            return false;
        }

        try {
            if (!adjustMouse(event)) {
                return false;
            }

            if (this.mouseOwner == null) {
                for (ViewMouseListener vml : this.roleMouseListeners) {
                    if (vml.onMouseMove(StageView.this, event)) {
                        return true;
                    }
                }
            } else {
                return this.mouseOwner.onMouseMove(StageView.this, event);
            }

        } finally {
            unadjustMouse(event);
        }

        return false;

    }

    @Override
    public String toString()
    {
        return "StageView " + this.stage.getName();
    }

}
