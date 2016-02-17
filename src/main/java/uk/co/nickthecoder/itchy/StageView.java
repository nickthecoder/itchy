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

import uk.co.nickthecoder.itchy.property.BooleanProperty;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

public class StageView extends AbstractScrollableView implements StageListener, MouseListener, MouseListenerView
{
    protected static final List<Property<View, ?>> properties = new ArrayList<Property<View, ?>>();

    static {
        properties.addAll(AbstractScrollableView.properties);
        properties.add(new BooleanProperty<View>("enableMouse"));
    }

    @Override
    public List<Property<View, ?>> getProperties()
    {
        return properties;
    }

    private Stage stage;

    public int minimumAlpha = 0;

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

    public StageView()
    {
        super();
    }

    public StageView(Rect position, Stage stage)
    {
        super(position);
        this.stage = stage;
        if (stage != null) {
            this.stage.addStageListener(this);
        }
    }

    public void setStage(Stage stage)
    {
        this.stage = stage;
        if (stage != null) {
            this.stage.addStageListener(this);
        }
    }

    public Stage getStage()
    {
        return this.stage;
    }

    public boolean getEnableMouse()
    {
        return this.roleMouseListeners != null;
    }

    public void setEnableMouse(boolean value)
    {
        if (value) {
            enableMouseListener(Itchy.getGame());
        } else {
            disableMouseListener(Itchy.getGame());
        }
    }

    @Override
    public void render(GraphicsContext gc)
    {
        // This is where we would like to draw onto the surface, without taking into account the
        // clipping parentClip.
        GraphicsContext mygc = gc.window(this.position);
        mygc.ox -= this.worldRect.x;
        mygc.oy += this.position.height + this.worldRect.y;

        render2(mygc);
    }

    @Override
    public void render2(GraphicsContext gc)
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

    protected void render(GraphicsContext gc, Actor actor, int alpha)
    {
        gc.render(actor, alpha);
    }

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

    @Override
    public void disableMouseListener(Game game)
    {
        if (this.roleMouseListeners != null) {
            this.roleMouseListeners.clear();
        }
        this.roleMouseListeners = null;
    }

    @Override
    public void captureMouse(ViewMouseListener owner)
    {
        this.mouseOwner = owner;
        Itchy.getGame().captureMouse(this);
    }

    @Override
    public void releaseMouse(ViewMouseListener owner)
    {
        this.mouseOwner = null;
        Itchy.getGame().releaseMouse(this);
    }

    private int oldX;
    private int oldY;

    public boolean adjustMouse(MouseEvent event)
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

    public void unadjustMouse(MouseEvent event)
    {
        event.x = this.oldX;
        event.y = this.oldY;
    }

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
