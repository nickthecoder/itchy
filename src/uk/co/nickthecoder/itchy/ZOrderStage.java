/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

/**
 * Uses the Z Order attribute on Actor to order the actors front to back. The actor's with the lower Z Order are below those with a higher Z
 * Order. If two actors share the same Z Order, then the newest Actor will be above.
 */
public class ZOrderStage extends AbstractStage
{
    /**
     * The list of actors visible on this layer. The order of the list is the order they will be drawn, and therefore determines the
     * z-order. The first item is drawn first, and is therefore bottom-most.
     */
    protected TreeSet<Actor> actors = new TreeSet<Actor>(new ZOrderComparactor());
    
    public ZOrderStage( String name )
    {
        super(name);
    }

    @Override
    public Iterator<Actor> iterator()
    {
        return this.actors.iterator();
    }

    @Override
    public List<Actor> getActors()
    {
        List<Actor> result = new ArrayList<Actor>(this.actors.size());
        result.addAll(this.actors);
        return result;
    }
    
    @Override
    public void clear()
    {
        for (Actor actor : new ArrayList<Actor>(getActors())) {
            actor.kill();
        }
    }

    @Override
    public void add( Actor actor )
    {
        if (actor.getStage() != null) {
            actor.getStage().remove(actor);
        }
        actor.setStageAttribute(this);
        this.actors.add(actor);

        super.add(actor);        
    }    

    @Override
    public void remove( Actor actor )
    {
        actor.setStageAttribute(null);
        this.actors.remove(actor);

        super.remove(actor);
    }

    public void addBottom( Actor actor )
    {
        actor.removeFromStage();
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
        actor.removeFromStage();
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
        actor.removeFromStage();
        actor.setZOrder(other.getZOrder() - 1);
        this.add(actor);
    }

    public void addAbove( Actor actor, Actor other )
    {
        actor.removeFromStage();
        actor.setZOrder(other.getZOrder() + 1);
        this.add(actor);
    }

    public void zOrderUp( Actor actor )
    {
        Actor higher = this.actors.higher(actor);
        if (higher != null) {
            actor.removeFromStage();
            actor.setZOrderAttribute(higher.getZOrder() + 1);
            this.add(actor);
        }
    }

    public void zOrderDown( Actor actor )
    {
        Actor lower = this.actors.lower(actor);
        if (lower != null) {
            actor.removeFromStage();
            actor.setZOrderAttribute(lower.getZOrder() - 1);
            this.add(actor);
        }
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

    @Override
    public Stage createDesignStage()
    {
        ZOrderStage result = (ZOrderStage) super.createDesignStage();
        result.actors = new TreeSet<Actor>(new ZOrderComparactor());

        return result;
    }

    private List<Actor> tempList = new ArrayList<Actor>();
    
    @Override
    public void tick()
    {
        this.tempList.addAll( this.actors );
        
        for (Actor actor : this.tempList ) {
            
            if (actor.isDead()) {
                this.actors.remove(actor);
            } else {
                this.singleTick(actor);
            }
        }
        
        this.tempList.clear();
    }
    
    /**
     * If subclasses want to perform different behaviour for a single actor's tick, then
     * this method can be overridden.
     */
    protected void singleTick(Actor actor)
    {
        actor.tick();        
    }

}
