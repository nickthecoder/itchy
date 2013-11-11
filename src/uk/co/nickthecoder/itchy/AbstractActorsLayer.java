/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

public abstract class AbstractActorsLayer extends AbstractLayer implements ActorsLayer, MouseListener
{
    /**
     * The list of actors visible on this layer. The order of the list is the order they will be
     * drawn, and therefore determines the z-order. The first item is draw first, and is therefore
     * bottom-most.
     */
    protected TreeSet<Actor> actors = new TreeSet<Actor>(new ZOrderComparactor());
    private List<Actor> actorMouseListeners = null;

    private MouseListener actorsMouseListener = new ActorsMouseListener();
        
    protected List<MouseListener> mouseListeners;
        

    /**
     * The actor that requested to capture the mouse events.
     */
    private MouseListener mouseOwner;

    /**
     * Used by the editor - can actors be placed onto this layer? This is handy if you have a layer
     * for non-game objects, for example a layer for a mini-map, control panel, dash boards etc.
     */
    public boolean locked = false;

    /**
     * Any actors with an alpha less than this will use this as their alpha. Used by the
     * SceneDesigner to reveal invisible actors.
     */
    public int minimumAlpha = 0;

    /**
     * Any actors with an alpha greater than this will use this as their alpha. Used by the
     * SceneDesigner to dim the non-active layers.
     */
    public int maximumAlpha = 255;

    
    public AbstractActorsLayer( String name, Rect position )
    {
        super(name, position);
    }

    public boolean isLocked()
    {
        return this.locked;
    }
    
    public Iterator<Actor> iterator()
    {
        return this.actors.iterator();
    }

    public TreeSet<Actor> getActors()
    {
        return this.actors;
    }

    public void add( Actor actor )
    {
        actor.removeFromLayer();
        actor.setLayerAttribute(this);
        this.actors.add(actor);

        if ((this.actorMouseListeners != null) && (actor.getBehaviour() instanceof MouseListener)) {
            this.actorMouseListeners.add(actor);
        }
    }

    public boolean remove( Actor actor )
    {
        if (this.actorMouseListeners != null) {
            this.actorMouseListeners.remove(actor);
        }
        actor.setLayerAttribute(null);
        return this.actors.remove(actor);
    }

    public void addBottom( Actor actor )
    {
        actor.removeFromLayer();
        if (!this.actors.isEmpty()) {
            Actor first = this.actors.first();
            if (first != null) {
                actor.setZOrderAttribute(first.getZOrder() - 1);
            }
        }
        this.add(actor);
    }

    public void addTop( Actor actor )
    {
        actor.removeFromLayer();
        if (!this.actors.isEmpty()) {
            Actor last = this.actors.last();
            if (last != null) {
                actor.setZOrderAttribute(last.getZOrder() + 1);
            }
        }
        this.add(actor);
    }

    public void addBelow( Actor actor, Actor other )
    {
        actor.removeFromLayer();
        actor.setZOrder(other.getZOrder() - 1);
        this.add(actor);
    }

    public void addAbove( Actor actor, Actor other )
    {
        actor.removeFromLayer();
        actor.setZOrder(other.getZOrder() + 1);
        this.add(actor);
    }

    @Override
    public void clear()
    {
        for (Actor actor : new ArrayList<Actor>(getActors())) {
            actor.kill();
        }
        if (this.actorMouseListeners != null) {
            this.actorMouseListeners.clear();
        }
    }

    public void zOrderUp( Actor actor )
    {
        Actor higher = this.actors.higher(actor);
        if (higher != null) {
            actor.setZOrderAttribute(higher.getZOrder() + 1);
            this.add(actor);
        }
    }

    public void zOrderDown( Actor actor )
    {
        Actor lower = this.actors.lower(actor);
        if (lower != null) {
            actor.setZOrderAttribute(lower.getZOrder() - 1);
            this.add(actor);
        }
    }
    
    public void addMouseListener( MouseListener listener, Game game )
    {
        if (this.mouseListeners == null) {
            this.mouseListeners = new ArrayList<MouseListener>();
            game.addMouseListener(this);
        }
        this.mouseListeners.add(listener);
    }

    public void removeMouseListener( MouseListener listener, Game game )
    {
        this.mouseListeners.remove(listener);
        if (this.mouseListeners.size() == 0) {
            this.mouseListeners = null;
            game.removeMouseListener(this);
        }
    }

    protected void adjustMouse( MouseEvent event )
    {
        Rect position = this.getAbsolutePosition();
        event.x -= position.x;

        if (this.yAxisPointsDown) {
            event.y -= this.position.y;
        } else {
            event.y = position.y + this.position.height - event.y;
        }
    }

    public void enableMouseListener( Game game )
    {
        this.actorMouseListeners = new ArrayList<Actor>();
        this.addMouseListener(this.actorsMouseListener, game);
    }

    public void disableMouseListener( Game game )
    {
        this.actorMouseListeners = null;
        this.removeMouseListener(this.actorsMouseListener, game);
        this.actorMouseListeners = null;
    }
    @Override
    public boolean onMouseDown( MouseButtonEvent event )
    {
        if (!this.contains(event.x, event.y)) {
            return false;
        }

        int tx = event.x;
        int ty = event.y;

        this.adjustMouse(event);

        try {
            for (MouseListener listener : this.mouseListeners) {
                if (listener.onMouseDown(event)) {
                    return true;
                }
            }
        } finally {
            event.x = tx;
            event.y = ty;
        }

        return false;
    }

    @Override
    public boolean onMouseUp( MouseButtonEvent event )
    {
        if (!this.contains(event.x, event.y)) {
            return false;
        }

        int tx = event.x;
        int ty = event.y;

        this.adjustMouse(event);

        try {
            for (MouseListener listener : this.mouseListeners) {
                if (listener.onMouseUp(event)) {
                    return true;
                }
            }
        } finally {
            event.x = tx;
            event.y = ty;
        }

        return false;
    }

    @Override
    public boolean onMouseMove( MouseMotionEvent event )
    {

        if (!this.contains(event.x, event.y)) {
            return false;
        }

        int tx = event.x;
        int ty = event.y;

        this.adjustMouse(event);

        try {
            for (MouseListener listener : this.mouseListeners) {
                if (listener.onMouseMove(event)) {
                    return true;
                }
            }
        } finally {
            event.x = tx;
            event.y = ty;
        }

        return false;
    }

    public class ZOrderComparactor implements Comparator<Actor>
    {
        @Override
        public int compare( Actor a, Actor b )
        {
            if (a.getZOrder() == b.getZOrder()) {
                return a.id - b.id;
            } else {
                return a.getZOrder() - b.getZOrder();
            }
        }

    }

    public void captureMouse( MouseListener owner )
    {
        this.mouseOwner = owner;
        Itchy.getGame().captureMouse(this);
    }

    public void releaseMouse( MouseListener owner )
    {
        Itchy.getGame().releaseMouse(this);
        this.mouseOwner = null;
    }

    class ActorsMouseListener implements MouseListener
    {
        @Override
        public boolean onMouseDown( MouseButtonEvent event )
        {
            if (AbstractActorsLayer.this.mouseOwner == null) {
                for (Iterator<Actor> i = AbstractActorsLayer.this.actorMouseListeners.iterator(); i.hasNext();) {
                    Actor actor = i.next();
                    if (actor.getBehaviour() instanceof MouseListener) {
                        if (((MouseListener) actor.getBehaviour()).onMouseDown(event)) {
                            return true;
                        }
                    } else {
                        i.remove();
                    }
                }
            } else {
                return AbstractActorsLayer.this.mouseOwner.onMouseDown(event);
            }
            return false;
        }

        @Override
        public boolean onMouseUp( MouseButtonEvent event )
        {
            if (AbstractActorsLayer.this.mouseOwner == null) {

                for (Iterator<Actor> i = AbstractActorsLayer.this.actorMouseListeners.iterator(); i.hasNext();) {
                    Actor actor = i.next();
                    if (actor.getBehaviour() instanceof MouseListener) {
                        if (((MouseListener) actor.getBehaviour()).onMouseUp(event)) {
                            return true;
                        }
                    } else {
                        i.remove();
                    }
                }

            } else {
                return AbstractActorsLayer.this.mouseOwner.onMouseUp(event);
            }
            return false;
        }

        @Override
        public boolean onMouseMove( MouseMotionEvent event )
        {
            if (AbstractActorsLayer.this.mouseOwner == null) {

                for (Iterator<Actor> i = AbstractActorsLayer.this.actorMouseListeners.iterator(); i.hasNext();) {
                    Actor actor = i.next();
                    if (actor.getBehaviour() instanceof MouseListener) {
                        if (((MouseListener) actor.getBehaviour()).onMouseMove(event)) {
                            return true;
                        }
                    } else {
                        i.remove();
                    }
                }

            } else {
                return AbstractActorsLayer.this.mouseOwner.onMouseMove(event);
            }
            return false;
        }
    }
}
