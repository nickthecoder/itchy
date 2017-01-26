/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import uk.co.nickthecoder.itchy.editor.Editor;
import uk.co.nickthecoder.itchy.editor.SceneDesigner;
import uk.co.nickthecoder.itchy.property.BooleanProperty;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

/**
 * StageViews draw all of the {@link Actor}s that are on a {@link Stage}.
 * There can be many StageViews, for a single Stage. For example, a two player game with a split-screen will have a
 * StageView for each player.
 */
public class StageView extends AbstractScrollableView implements StageListener, MouseListener, MouseListenerView
{
    protected static final List<Property<View, ?>> properties = new ArrayList<Property<View, ?>>();

    static {
        properties.addAll(AbstractView.properties);
        properties.add(new BooleanProperty<View>("enableMouse"));
    }

    /**
     * Used internally by Itchy.
     * 
     * @priority 5
     */
    @Override
    public List<Property<View, ?>> getProperties()
    {
        return properties;
    }

    private Stage stage;

    /**
     * Setting this greater than zero will cause Actors that would normally be invisible to become visible.
     * This is used in the {@link SceneDesigner} to reveal Actors, that would otherwise be hidden.
     */
    public int minimumAlpha = 0;

    /**
     * Setting this lower than the default of 255, will cause all Actors to be semi-transparent.
     * This is used in the {@link SceneDesigner} to dim chosen layers.
     */
    public int maximumAlpha = 255;

    /**
     * A list of Roles of Actors on this view's Stage, who implements ViewMouseListener. They are expecting to hear
     * events with their x,y
     * coordinates adjusted to their world coordinates.
     */
    private Set<ViewMouseListener> roleMouseListeners = null;

    /**
     * The actor that requested to capture the mouse events.
     */
    private ViewMouseListener mouseOwner;

    /**
     * Used internally by Itchy.
     * If you wish to create StageView's dynamically, then use the other constructor : {@link #StageView(Rect, Stage)}.
     * 
     * @priority 5
     */
    public StageView()
    {
        super();
    }

    /**
     * Create a new StageView
     * 
     * @param position
     * @param stage
     */
    public StageView(Rect position, Stage stage)
    {
        super(position);
        this.stage = stage;
        if (stage != null) {
            this.stage.addStageListener(this);
        }
    }

    /**
     * Used internally by Itchy. You game code should set the stage in the constructor ({link
     * {@link #StageView(Rect, Stage)}, and then leave it unchanged.
     * 
     * @param stage
     * @priority 5
     */
    public void setStage(Stage stage)
    {
        this.stage = stage;
        if (stage != null) {
            this.stage.addStageListener(this);
        }
    }

    /**
     * A simple getter.
     * 
     * @return
     */
    public Stage getStage()
    {
        return this.stage;
    }

    /**
     * @return True is Actors on this View's Stage receieve mouse events.
     * @priority 3
     */
    public boolean getEnableMouse()
    {
        return this.roleMouseListeners != null;
    }

    /**
     * For performance reasons, the Actors on a Stage don't normally receive mouse events. So if a Stage has buttons, or
     * other Actors that need to receive mouse events, then call this method.
     * <p>
     * For Views created in the {@link Editor}, there is a tick box to enable/disable the mouse in the "View" tab of the
     * "Edit Layer" dialog.
     * 
     * @param value
     */
    public void setEnableMouse(boolean value)
    {
        if (value) {
            enableMouseListener(Itchy.getGame());
        } else {
            disableMouseListener(Itchy.getGame());
        }
    }

    /**
     * For performance reasons, the Actors on a Stage don't normally receive mouse events. So if a Stage has buttons, or
     * other Actors that need to receive mouse events, then call this method.
     * <p>
     * For Views created in the {@link Editor}, there is a tick box to enable/disable the mouse in the "View" tab of the
     * "Edit Layer" dialog.
     * 
     * @param game
     */
    @Override
    public void enableMouseListener(Game game)
    {
        this.roleMouseListeners = new HashSet<ViewMouseListener>();
        for (Actor actor : this.stage.getActors()) {
            Role role = actor.getRole();
            if (role instanceof ViewMouseListener) {
                this.roleMouseListeners.add((ViewMouseListener) role);
            }
        }
    }

    /**
     * Disables mouse events for Actors on the View's Stage.
     * </p>
     * For Views created in the {@link Editor}, there is a tick box to enable/disable the mouse in the "View" tab of the
     * "Edit Layer" dialog.
     * 
     * @priority 3
     */
    @Override
    public void disableMouseListener(Game game)
    {
        if (this.roleMouseListeners != null) {
            this.roleMouseListeners.clear();
        }
        this.roleMouseListeners = null;
    }

    /**
     * Used internally by Itchy.
     * 
     * @priority 5
     */
    @Override
    public GraphicsContext adjustGraphicsContext(GraphicsContext gc)
    {
        // This is where we would like to draw onto the surface, without taking into account the
        // clipping parentClip.
        GraphicsContext mygc = gc.window(this.position);
        mygc.ox -= this.worldRect.x;
        mygc.oy += this.position.height + this.worldRect.y;

        return mygc;
    }

    /**
     * Used internally by Itchy.
     * 
     * @priority 3
     */
    @Override
    public void render(GraphicsContext gc)
    {
        for (Iterator<Actor> i = this.stage.iterator(); i.hasNext();) {

            Actor actor = i.next();

            try {

                if (actor.isDead()) {
                    i.remove();
                    continue;
                }

                // Don't render actors that are invisible (or very nearly invisible)
                if ((actor.getAppearance().getAlpha() > 1) || (this.minimumAlpha > 1)) {
                    if (actor.getAppearance().visibleWithin(this.worldRect)) {

                        int alpha = (int) (actor.getAppearance().getAlpha());
                        if (alpha < this.minimumAlpha) {
                            alpha = this.minimumAlpha;
                        }
                        if (alpha > this.maximumAlpha) {
                            alpha = this.maximumAlpha;
                        }

                        if (alpha > 0) {
                            render(gc, actor, alpha);
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Failed to render " + actor);
            }
        }
    }

    /**
     * Renders a single Actor
     * 
     * @param gc
     * @param actor
     * @param alpha
     * @priority 3
     */
    protected void render(GraphicsContext gc, Actor actor, int alpha)
    {
        gc.render(actor, alpha);
    }

    /**
     * Used internally by Itchy.
     * 
     * @priority 3
     */
    @Override
    public void render(NewGraphicsContext gc)
    {
        for (Iterator<Actor> i = this.stage.iterator(); i.hasNext();) {

            Actor actor = i.next();

            try {

                if (actor.isDead()) {
                    i.remove();
                    continue;
                }

                // Don't render actors that are invisible (or very nearly invisible)
                if ((actor.getAppearance().getAlpha() > 1) || (this.minimumAlpha > 1)) {
                    if (actor.getAppearance().visibleWithin(this.worldRect)) {

                        int alpha = (int) (actor.getAppearance().getAlpha());
                        if (alpha < this.minimumAlpha) {
                            alpha = this.minimumAlpha;
                        }
                        if (alpha > this.maximumAlpha) {
                            alpha = this.maximumAlpha;
                        }

                        if (alpha > 0) {
                            render(gc, actor, alpha);
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Failed to render " + actor);
            }
        }
    }

    /**
     * Renders a single Actor
     * 
     * @param gc
     * @param actor
     * @param alpha
     * @priority 3
     */
    protected void render(NewGraphicsContext gc, Actor actor, int alpha)
    {
        gc.render(actor, alpha);
    }


    /**
     * Part of the {@link StageListener} interface, used to keep track of which Actors need to receive mouse events.
     * 
     * @priority 5
     */
    @Override
    public void onAdded(Stage stage, Actor actor)
    {
        Role role = actor.getRole();
        if ((this.roleMouseListeners != null) && (role instanceof ViewMouseListener)) {
            ViewMouseListener listener = ((ViewMouseListener) role);
            if (listener.isMouseListener()) {
                this.roleMouseListeners.add(listener);
            }
        }
    }

    /**
     * Part of the {@link StageListener} interface, used to keep track of which Actors need to receive mouse events.
     * 
     * @priority 5
     */
    @Override
    public void onChangedRole(Stage stage, Actor actor)
    {
        Role role = actor.getRole();
        if ((this.roleMouseListeners != null) && (role instanceof ViewMouseListener)) {
            ViewMouseListener listener = ((ViewMouseListener) role);
            if (listener.isMouseListener()) {
                this.roleMouseListeners.add(listener);
            }
        }
    }

    /**
     * Part of the {@link StageListener} interface, used to keep track of which Actors need to receive mouse events.
     * 
     * @priority 5
     */
    @Override
    public void onRemoved(Stage stage, Actor actor)
    {
        Role role = actor.getRole();
        if ((this.roleMouseListeners != null) && (role instanceof ViewMouseListener)) {
            ViewMouseListener listener = ((ViewMouseListener) role);
            if (listener.isMouseListener()) {
                this.roleMouseListeners.remove(role);
            }
        }
    }

    /**
     * Capturing a mouse means that mouse events won't be sent to any other mouse listeners until the mouse is released.
     * Typically used during a drag operation;
     * This method is called when the mouse button is pressed, and {@link #releaseMouse(ViewMouseListener)} is called
     * when the mouse button is released.
     * 
     * @see #releaseMouse(ViewMouseListener)
     */
    @Override
    public void captureMouse(ViewMouseListener owner)
    {
        this.mouseOwner = owner;
        Itchy.getGame().captureMouse(this);
    }

    /**
     * Release the mouse after capturing it.
     * 
     * @see #captureMouse(ViewMouseListener)
     */
    @Override
    public void releaseMouse(ViewMouseListener owner)
    {
        this.mouseOwner = null;
        Itchy.getGame().releaseMouse(this);
    }

    private int oldX;
    private int oldY;

    /**
     * This is a horrible bodge - it adjust the x,y values in a MouseEvent, such that they are transformed from being
     * screen
     * coordinates to world coordinates. It is on my todo list to implement this in a better, clean way.
     * 
     * @param event
     * @return true if the mouse is within this view's rectangle.
     * @priority 5
     */
    public boolean adjustMouse(MouseEvent event)
    {
        // Store actual x,y so it can be restored by unadjustMouse
        this.oldX = event.x;
        this.oldY = event.y;

        // Calculate the position within the view, with (0,0) as the bottom left of the viewport.
        event.x -= position.x;
        event.y = position.y + position.height - event.y;
        boolean result = ((event.x >= 0) && (event.x < position.width) && (event.y >= 0) && (event.y < position.height));

        // Take scroll into affect.
        event.x += this.worldRect.x;
        event.y += this.worldRect.y;

        return result;
    }

    /**
     * Repalce's the mouses x,y values to their correct values.
     * 
     * @see #adjustMouse(MouseEvent)
     * @param event
     * @priority 5
     */
    public void unadjustMouse(MouseEvent event)
    {
        event.x = this.oldX;
        event.y = this.oldY;
    }

    /**
     * Checks if the mouse is within the view's rectangle, and if so, forwards the event to all ViewMouseListeners
     * (i.e. all of the Actors' Roles that are expecting mouse events.
     */
    @Override
    public void onMouseDown(MouseButtonEvent event)
    {
        if (this.roleMouseListeners == null) {
            return;
        }

        try {
            if (!adjustMouse(event)) {
                return;
            }

            if (this.mouseOwner == null) {
                for (ViewMouseListener vml : this.roleMouseListeners) {
                    vml.onMouseDown(StageView.this, event);
                }
            } else {
                this.mouseOwner.onMouseDown(StageView.this, event);
            }

        } finally {
            unadjustMouse(event);
        }
    }

    /**
     * Checks if the mouse is within the view's rectangle, and if so, forwards the event to all ViewMouseListeners
     * (i.e. all of the Actors' Roles that are expecting mouse events.
     */

    @Override
    public void onMouseUp(MouseButtonEvent event)
    {
        if (this.roleMouseListeners == null) {
            return;
        }

        try {
            if (!adjustMouse(event)) {
                return;
            }

            if (this.mouseOwner == null) {
                for (ViewMouseListener vml : this.roleMouseListeners) {
                    vml.onMouseUp(StageView.this, event);
                }
            } else {
                this.mouseOwner.onMouseUp(StageView.this, event);
            }

        } finally {
            unadjustMouse(event);
        }
    }

    /**
     * Checks if the mouse is within the view's rectangle, and if so, forwards the event to all ViewMouseListeners
     * (i.e. all of the Actors' Roles that are expecting mouse events.
     */
    @Override
    public void onMouseMove(MouseMotionEvent event)
    {
        if (this.roleMouseListeners == null) {
            return;
        }

        try {
            if (!adjustMouse(event)) {
                return;
            }

            if (this.mouseOwner == null) {
                for (ViewMouseListener vml : this.roleMouseListeners) {
                    vml.onMouseMove(StageView.this, event);
                }
            } else {
                this.mouseOwner.onMouseMove(StageView.this, event);
            }

        } finally {
            unadjustMouse(event);
        }
    }

}
