/*******************************************************************************
 * Copyright (c) 2014 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.collision;

import java.util.List;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.Wrapped;
import uk.co.nickthecoder.itchy.util.Filter;

/**
 * Checks for collisions within a world where the left edge is joined to the right,
 * and/or the top edge is joined to the bottom.
 */
public class WrappedCollisionStrategy implements CollisionStrategy
{
    public CollisionStrategy innerCollisionStrategy;
    
    public Wrapped wrapped;
    
    public WrappedCollisionStrategy( Wrapped wrapped )
    {
        this( wrapped, new BruteForceCollisionStrategy() );
    }
    
    public WrappedCollisionStrategy( Wrapped wrapped, CollisionStrategy cs )
    {
        super();
        this.wrapped = wrapped;
        this.innerCollisionStrategy = cs;
    }
    
    @Override
    public List<Role> collisions( Actor actor, String[] tags )
    {
        return collisions(actor, tags, MAX_RESULTS, acceptFilter );
    }

    @Override
    public List<Role> collisions(Actor actor, String[] tags, int maxResults)
    {
        return collisions(actor, tags, maxResults, acceptFilter );
    }
    
    @Override
    public List<Role> collisions( Actor actor, String[] includeTags, int maxResults, Filter<Role> filter )
    {
        wrapped.normalise(actor);

        List<Role> result = collisions2( actor, includeTags, maxResults, filter );
        
        if (result.size() >= maxResults ) {
            return result;
        }
        
        if (wrapped.overlappingLeft(actor)) {
            actor.setX( actor.getX() + wrapped.getWidth() );
            result.addAll( collisions2(actor, includeTags, maxResults - result.size(), filter) );
            actor.setX( actor.getX() - wrapped.getWidth() );
        }
        
        if (result.size() >= maxResults ) {
            return result;
        }
        
        if (wrapped.overlappingRight(actor)) {
            actor.setX( actor.getX() - wrapped.getWidth() );
            result.addAll( collisions2(actor, includeTags, maxResults - result.size(), filter) );   
            actor.setX( actor.getX() + wrapped.getWidth() );
        }
        
        return result;
    }
    
    public List<Role> collisions2( Actor actor, String[] tags, int maxResults, Filter<Role> filter )
    {
        List<Role> result = innerCollisionStrategy.collisions( actor, tags, maxResults, filter );
        
        if (wrapped.overlappingBottom(actor)) {
            actor.setY( actor.getY() + wrapped.getHeight() );
            result.addAll( innerCollisionStrategy.collisions(actor, tags, maxResults, filter) );            
            actor.setY( actor.getY() - wrapped.getHeight() );
        }

        if (wrapped.overlappingTop(actor)) {
            actor.setY( actor.getY() - wrapped.getHeight() );
            result.addAll( innerCollisionStrategy.collisions(actor, tags, maxResults, filter) );            
            actor.setY( actor.getY() + wrapped.getHeight() );
        }
        
        return result;
    }

    @Override
    public void update()
    {
        this.innerCollisionStrategy.update();
    }

    @Override
    public void remove()
    {
        this.innerCollisionStrategy.remove();
    }


}