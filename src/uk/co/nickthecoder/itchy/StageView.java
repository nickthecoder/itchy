/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uk.co.nickthecoder.jame.JameRuntimeException;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

public class StageView extends AbstractScrollableView implements StageListener, MouseListenerView
{
    private Stage stage;

    public int minimumAlpha = 0;

    public int maximumAlpha = 255;

    /**
     * A list of Behaviours of Actors on this view's Stage, who implements ViewMouseListener. They
     * are expecting to hear events with their x,y coordinates adjusted to their world coordinates.
     */
    private List<ViewMouseListener> behaviourMouseListeners = null;

    /**
     * The mouse listener which hears mouse events directly from Game, with x.y coordinates in the
     * display's coordinates. actorsMouseListener converts those coordinates to those of the world,
     * and sends the message to the list <code>behaviourMouseListeners</code>
     */
    private MouseListener forwardingMouseListener;

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
        final int clipLeft = clip.x;
        final int clipTop = clip.y;
        final int clipWidth = clip.width;
        final int clipHeight = clip.height;

        // Where is the world's (0,0) on screen (in screen coordinates)?
        // TODO I think this is the -ve of what the above comment says.
        int tx = offsetX - (int) this.worldRect.x;
        int ty = this.getYAxisPointsDown() ? offsetY - (int) this.worldRect.y : offsetY + this.position.height + (int) this.worldRect.y;

        for (Iterator<Actor> i = this.stage.iterator(); i.hasNext();) {
            Actor actor = i.next();

            if (actor.isDead()) {
                i.remove();
                continue;
            }

            if ((actor.getAppearance().getAlpha() < 2) && (this.minimumAlpha < 2)) {
                continue;
            }

            if (actor.getAppearance().visibleWithin(this.worldRect)) {

                // TODO this has never failed, don't need retries.
                for (int retry = 0; retry < 5; retry++) {

                    // Ensures the surface has been rendered, and offset_x,y are now valid.
                    Surface actorSurface = actor.getSurface();

                    // Top left of where the actor needs to be placed on the screen.
                    // Note the change of sign for "y", because in world
                    // coordinates "down" is negative.
                    int displayAtX = tx + (int) (actor.getX()) - actor.getAppearance().getOffsetX();
                    int displayAtY = this.getYAxisPointsDown() ?
                        ty + (int) (actor.getY()) - actor.getAppearance().getOffsetY() :
                        ty - (int) (actor.getY()) - actor.getAppearance().getOffsetY();

                    try {

                        int width = actorSurface.getWidth();
                        int height = actorSurface.getHeight();
                        int shiftX = 0;
                        int shiftY = 0;

                        // Clip within the layers positionOnScreen
                        if (displayAtX < clipLeft) { // left
                            shiftX = clipLeft - displayAtX;
                            displayAtX += shiftX;
                        }
                        if (displayAtY < clipTop) { // top
                            shiftY = clipTop - displayAtY;
                            displayAtY += shiftY;
                        }
                        if (displayAtX + actorSurface.getWidth() > clipLeft + clipWidth) { // right
                            width -= displayAtX + actorSurface.getWidth() - (clipLeft + clipWidth);
                        }
                        if (displayAtY + actorSurface.getHeight() > clipTop + clipHeight) { // bottom
                            height -= displayAtY + actorSurface.getHeight() - (clipTop + clipHeight);
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
                                    Rect tempRect2 = new Rect(shiftX, shiftY, width, height);
                                    actorSurface.blit(tempRect2, tempSurface, tempRect);

                                    // Now blit the temp surface onto the screen,
                                    // with the correct amount of alpha
                                    tempSurface.setPerSurfaceAlpha(alpha);
                                    tempSurface.blit(destSurface, displayAtX, displayAtY);

                                    tempSurface.free();
                                }

                            }
                            break; // Exit from the retry loop, as we've completed
                                   // the operation without error.
                        }

                    } catch (JameRuntimeException e) {
                        actor.getAppearance().clearCachedSurface();
                        System.err.println("WARNING : attempt #" + retry + " failed to blit surface during ScrollableLayer.render");
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void onAdded( Stage stage, Actor actor )
    {
        Behaviour behaviour = actor.getBehaviour();
        if (behaviour instanceof ViewMouseListener) {
            this.behaviourMouseListeners.add((ViewMouseListener) behaviour);
        }
    }

    @Override
    public void onRemoved( Stage stage, Actor actor )
    {
        Behaviour behaviour = actor.getBehaviour();
        if (behaviour instanceof ViewMouseListener) {
            this.behaviourMouseListeners.remove(behaviour);
        }
    }

    private void adjustMouse( MouseEvent event )
    {
        Rect position = this.getAbsolutePosition();
        event.x -= position.x;

        if (this.yAxisPointsDown) {
            event.y -= this.position.y;
        } else {
            event.y = position.y + this.position.height - event.y;
        }
        // TODO - Need to take the scroll offset into account.
    }

    @Override
    public void enableMouseListener( Game game )
    {
        this.behaviourMouseListeners = new ArrayList<ViewMouseListener>();
        this.forwardingMouseListener = new ForwardingMouseListener();
        // TODO Don't do this, implement MouseListener instead.
        game.addMouseListener(this.forwardingMouseListener);
        // TODO Iterate over all actors, and add them if the behaviours are ViewMouseListeners
    }

    @Override
    public void disableMouseListener( Game game )
    {
        this.behaviourMouseListeners.clear();
        this.behaviourMouseListeners = null;
        this.forwardingMouseListener = null;
        game.removeMouseListener(this.forwardingMouseListener);
    }

    @Override
    public void captureMouse( ViewMouseListener owner )
    {
        if (this.forwardingMouseListener!= null) {
            this.mouseOwner = owner;
            Itchy.getGame().captureMouse(this.forwardingMouseListener);
        } else {
            throw new RuntimeException( "This StageView has not been enabled as a MouseListener");
        }
    }

    @Override
    public void releaseMouse( ViewMouseListener owner )
    {
        this.mouseOwner = null;
        Itchy.getGame().releaseMouse(this.forwardingMouseListener);
    }

    /**
     * Converts the mouse event's x,y from device coordinates into world coordinates and then
     * forwards the MouseEvent to all of the Actors' Behaviours (within this Stage), that implement
     * MouseListener.
     */
    class ForwardingMouseListener implements MouseListener
    {
        @Override
        public boolean onMouseDown( MouseButtonEvent event )
        {
            if (!contains(event.x, event.y)) {
                return false;
            }

            int tx = event.x; // Save the real mouse event coords
            int ty = event.y;

            adjustMouse(event);

            try {

                if (StageView.this.mouseOwner == null) {
                    for (ViewMouseListener vml : StageView.this.behaviourMouseListeners) {
                        if (vml.onMouseDown(StageView.this, event)) {
                            return true;
                        }
                    }
                } else {
                    return StageView.this.mouseOwner.onMouseDown(StageView.this, event);
                }

            } finally {
                event.x = tx; // Restore the real mouse event coords
                event.y = ty;
            }

            return false;
        }

        @Override
        public boolean onMouseUp( MouseButtonEvent event )
        {
            if (!contains(event.x, event.y)) {
                return false;
            }

            int tx = event.x; // Save the real mouse event coords
            int ty = event.y;

            adjustMouse(event);

            try {

                if (StageView.this.mouseOwner == null) {
                    for (ViewMouseListener vml : StageView.this.behaviourMouseListeners) {
                        if (vml.onMouseUp(StageView.this, event)) {
                            return true;
                        }
                    }
                } else {
                    return StageView.this.mouseOwner.onMouseUp(StageView.this, event);
                }

            } finally {
                event.x = tx; // Restore the real mouse event coords
                event.y = ty;
            }

            return false;
        }

        @Override
        public boolean onMouseMove( MouseMotionEvent event )
        {
            if (!contains(event.x, event.y)) {
                return false;
            }

            int tx = event.x; // Save the real mouse event coords
            int ty = event.y;

            adjustMouse(event);
            try {

                if (StageView.this.mouseOwner == null) {
                    for (ViewMouseListener vml : StageView.this.behaviourMouseListeners) {
                        if (vml.onMouseMove(StageView.this, event)) {
                            return true;
                        }
                    }
                } else {
                    return StageView.this.mouseOwner.onMouseMove(StageView.this, event);
                }

            } finally {
                event.x = tx; // Restore the real mouse event coords
                event.y = ty;
            }

            return false;

        }
    }

}
