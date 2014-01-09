/*******************************************************************************
 * Copyright (c) 2014 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.Set;

/**
 * Checks for collisions within a world where the left edge is joined to the right,
 * and/or the top edge is joined to the bottom.
 * <p>
 * As well as wrapping the stage, this is also "wrapped" in the sense that it wraps another
 * collision strategy which actual performs the collision tests.
 */
public class WrappedCollisionStrategy implements CollisionStrategy
{
    public CollisionStrategy wrappedCollisionStrategy;
    
    public Wrapped wrapped;
    
    public WrappedCollisionStrategy( Wrapped wrapped )
    {
        this( wrapped, new BruteForceCollisionStrategy() );
    }
    
    public WrappedCollisionStrategy( Wrapped wrapped, CollisionStrategy cs )
    {
        super();
        this.wrapped = wrapped;
        this.wrappedCollisionStrategy = cs;
    }


    private static final String[] EMPTY = {};
    
    @Override
    public Set<Role> collisions( Actor actor, String... includeTags )
    {
        return collisions(actor, includeTags, EMPTY );
    }
    
    @Override
    public Set<Role> collisions( Actor actor, String[] includeTags, String[] excludeTags )
    {
        wrapped.normalise(actor);

        Set<Role> result = collisions2( actor, includeTags, excludeTags );
        
        if (wrapped.overlappingLeft(actor)) {
            actor.setX( actor.getX() + wrapped.getWidth() );
            result.addAll( collisions2(actor, includeTags, excludeTags) );
            actor.setX( actor.getX() - wrapped.getWidth() );
        }
        
        if (wrapped.overlappingRight(actor)) {
            actor.setX( actor.getX() - wrapped.getWidth() );
            result.addAll( collisions2(actor, includeTags, excludeTags) );   
            actor.setX( actor.getX() + wrapped.getWidth() );
        }
        
        return result;
    }
    
    public Set<Role> collisions2( Actor actor, String[] includeTags, String[] excludeTags )
    {
        Set<Role> result = wrappedCollisionStrategy.collisions( actor, includeTags, excludeTags );
        
        if (wrapped.overlappingBottom(actor)) {
            actor.setY( actor.getY() + wrapped.getHeight() );
            result.addAll( wrappedCollisionStrategy.collisions(actor, includeTags, excludeTags) );            
            actor.setY( actor.getY() - wrapped.getHeight() );
        }

        if (wrapped.overlappingTop(actor)) {
            actor.setY( actor.getY() - wrapped.getHeight() );
            result.addAll( wrappedCollisionStrategy.collisions(actor, includeTags, excludeTags) );            
            actor.setY( actor.getY() - wrapped.getHeight() );
        }
        
        return result;
    }

    @Override
    public void update()
    {
        this.wrappedCollisionStrategy.update();
    }

    @Override
    public void remove()
    {
        this.wrappedCollisionStrategy.remove();
    }

}