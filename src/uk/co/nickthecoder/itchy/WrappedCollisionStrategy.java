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

    @Override
    public Set<Role> overlapping( Actor actor, String[] includeTags, String[] excludeTags )
    {
        wrapped.normalise(actor);

        Set<Role> result = overlapping2( actor, includeTags, excludeTags );
        
        if (wrapped.overlappingLeft(actor)) {
            actor.setX( actor.getX() + wrapped.getWidth() );
            result.addAll( overlapping2(actor, includeTags, excludeTags) );
            actor.setX( actor.getX() - wrapped.getWidth() );
        }
        
        if (wrapped.overlappingRight(actor)) {
            actor.setX( actor.getX() - wrapped.getWidth() );
            result.addAll( overlapping2(actor, includeTags, excludeTags) );   
            actor.setX( actor.getX() + wrapped.getWidth() );
        }
        
        return result;
    }
    
    public Set<Role> overlapping2( Actor actor, String[] includeTags, String[] excludeTags )
    {
        Set<Role> result = wrappedCollisionStrategy.overlapping( actor, includeTags, excludeTags );
        
        if (wrapped.overlappingBottom(actor)) {
            actor.setY( actor.getY() + wrapped.getHeight() );
            result.addAll( wrappedCollisionStrategy.overlapping(actor, includeTags, excludeTags) );            
            actor.setY( actor.getY() - wrapped.getHeight() );
        }
        
        if (wrapped.overlappingTop(actor)) {
            actor.setY( actor.getY() - wrapped.getHeight() );
            result.addAll( wrappedCollisionStrategy.overlapping(actor, includeTags, excludeTags) );            
            actor.setY( actor.getY() - wrapped.getHeight() );
        }
        
        return result;
    }

    @Override
    public Set<Role> pixelOverlap( Actor actor, String[] includeTags, String[] excludeTags )
    {
        wrapped.normalise(actor);

        Set<Role> result = pixelOverlap2( actor, includeTags, excludeTags );
        
        if (wrapped.overlappingLeft(actor)) {
            actor.setX( actor.getX() + wrapped.getWidth() );
            result.addAll( pixelOverlap2(actor, includeTags, excludeTags) );
            actor.setX( actor.getX() - wrapped.getWidth() );
        }
        
        if (wrapped.overlappingRight(actor)) {
            actor.setX( actor.getX() - wrapped.getWidth() );
            result.addAll( pixelOverlap2(actor, includeTags, excludeTags) );   
            actor.setX( actor.getX() + wrapped.getWidth() );
        }
        
        return result;
    }
    
    public Set<Role> pixelOverlap2( Actor actor, String[] includeTags, String[] excludeTags )
    {
        Set<Role> result = wrappedCollisionStrategy.overlapping( actor, includeTags, excludeTags );
        
        if (wrapped.overlappingBottom(actor)) {
            actor.setY( actor.getY() + wrapped.getHeight() );
            result.addAll( wrappedCollisionStrategy.pixelOverlap(actor, includeTags, excludeTags) );            
            actor.setY( actor.getY() - wrapped.getHeight() );
        }

        if (wrapped.overlappingTop(actor)) {
            actor.setY( actor.getY() - wrapped.getHeight() );
            result.addAll( wrappedCollisionStrategy.pixelOverlap(actor, includeTags, excludeTags) );            
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