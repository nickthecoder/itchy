/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

public abstract class ActorsLayer extends Layer
{

    /**
     * The list of actors visible on this layer. The order of the list is the order they will be
     * drawn, and therefore determines the z-order. The first item is draw first, and is therefore
     * bottom-most.
     */
    protected LinkedList<Actor> actors = new LinkedList<Actor>();
    protected List<Actor> readOnlyActors = Collections.unmodifiableList(this.actors);
    private MouseListener mouseListener = null;
    private final List<Actor> actorMouseListeners = new ArrayList<Actor>();

    public ActorsLayer( String name, Rect position )
    {
        super(name,position);
    }

    public Iterator<Actor> iterator()
    {
        return this.actors.iterator();
    }

    public List<Actor> getActors()
    {
        return this.readOnlyActors;
    }

    public void add( Actor actor )
    {
        actor.removeFromLayer();
        actor.setLayerAttribute(this);
        this.actors.add(actor);

        if (actor.getBehaviour() instanceof MouseListener) {
            this.actorMouseListeners.add(actor);
        }
    }

    public boolean remove( Actor actor )
    {
        this.actorMouseListeners.remove(actor);
        actor.setLayerAttribute(null);
        return this.actors.remove(actor);
    }

    public void addBottom( Actor actor )
    {
        actor.removeFromLayer();
        actor.setLayerAttribute(this);
        this.actors.add(0, actor);
    }

    public void addBelow( Actor actor, Actor other )
    {
        actor.removeFromLayer();
        actor.setLayerAttribute(this);
        int index = this.actors.indexOf(other);
        if (index < 0) {
            this.actors.add(0, other);
        } else {
            this.actors.add(index, actor);
        }
    }

    public void addAbove( Actor actor, Actor other )
    {
        actor.removeFromLayer();
        actor.setLayerAttribute(this);
        int index = this.actors.indexOf(other);
        if (index < 0) {
            this.actors.add(other);
        } else {
            this.actors.add(index + 1, actor);
        }
    }

    @Override
    public void clear()
    {
        for (Actor actor : new ArrayList<Actor>(this.getActors())) {
            actor.kill();
        }
        this.actorMouseListeners.clear();
    }

    public void deactivateAll()
    {
        for (Actor actor : this.getActors()) {
            actor.deactivate();
        }
    }

    public void zOrderUp( Actor actor )
    {
        int index = this.actors.indexOf(actor);
        if (index < this.actors.size() - 1) {
            Actor other = this.actors.get(index + 1);
            this.actors.set(index, other);
            this.actors.set(index + 1, actor);
        }
    }

    public void zOrderDown( Actor actor )
    {
        int index = this.actors.indexOf(actor);
        if (index > 0) {
            Actor other = this.actors.get(index - 1);
            this.actors.set(index, other);
            this.actors.set(index - 1, actor);
        }
    }

    public void zOrderTop( Actor actor )
    {
        this.actors.remove(actor);
        this.actors.add(actor);
    }

    public void zOrderBottom( Actor actor )
    {
        this.actors.remove(actor);
        this.actors.add(0, actor);
    }

    public void enableMouseListener(Game game)
    {
        if (this.mouseListener != null) {
            return;
        }

        this.mouseListener = new MouseListener() {
            @Override
            public boolean onMouseDown( MouseButtonEvent event )
            {
                for (Iterator<Actor> i = ActorsLayer.this.actorMouseListeners.iterator(); i
                    .hasNext();) {
                    Actor actor = i.next();
                    if (actor.getBehaviour() instanceof MouseListener) {
                        if (((MouseListener) actor.getBehaviour()).onMouseDown(event)) {
                            return true;
                        }
                    } else {
                        i.remove();
                    }
                }
                return false;
            }

            @Override
            public boolean onMouseUp( MouseButtonEvent event )
            {
                for (Iterator<Actor> i = ActorsLayer.this.actorMouseListeners.iterator(); i
                    .hasNext();) {
                    Actor actor = i.next();
                    if (actor.getBehaviour() instanceof MouseListener) {
                        if (((MouseListener) actor.getBehaviour()).onMouseUp(event)) {
                            return true;
                        }
                    } else {
                        i.remove();
                    }
                }
                return false;
            }

            @Override
            public boolean onMouseMove( MouseMotionEvent event )
            {
                for (Iterator<Actor> i = ActorsLayer.this.actorMouseListeners.iterator(); i
                    .hasNext();) {
                    Actor actor = i.next();
                    if (actor.getBehaviour() instanceof MouseListener) {
                        if (((MouseListener) actor.getBehaviour()).onMouseMove(event)) {
                            return true;
                        }
                    } else {
                        i.remove();
                    }
                }
                return false;
            }

        };
        this.addMouseListener(this.mouseListener, game);
    }

    public void disableMouseListener(Game game)
    {
        if (this.mouseListener != null) {
            this.removeMouseListener(this.mouseListener,game);
            this.mouseListener = null;
        }
    }

}
