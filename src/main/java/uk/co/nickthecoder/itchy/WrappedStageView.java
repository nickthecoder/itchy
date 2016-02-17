/*******************************************************************************
 * Copyright (c) 2014 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.property.IntegerProperty;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.jame.Rect;

/**
 * Like a normal StageView, but wrapped, so that if an object moves off of one edge, it appears on the opposite side.
 */
public class WrappedStageView extends StageView implements Wrapped
{
    private static final List<Property<View,?>> properties = new ArrayList<Property<View,?>>();

    static {
        properties.addAll(StageView.properties);
        properties.add(new IntegerProperty<View>("top"));
        properties.add(new IntegerProperty<View>("right"));
        properties.add(new IntegerProperty<View>("bottom"));
        properties.add(new IntegerProperty<View>("left"));
    }
    
    @Override
    public List<Property<View,?>> getProperties()
    {
        return properties;
    }
    
    private int leftEdge = Integer.MIN_VALUE;
    private int rightEdge = Integer.MAX_VALUE;

    private int topEdge = Integer.MAX_VALUE;
    private int bottomEdge = Integer.MIN_VALUE;

    public WrappedStageView()
    {
        super();
    }

    public WrappedStageView( Rect position, Stage stage )
    {
        super(position, stage);
    }

    public void wrap( int top, int right, int bottom, int left )
    {
        this.topEdge = top;
        this.rightEdge = right;
        this.bottomEdge = bottom;
        this.leftEdge = left;
    }

    public void wrapLeftRight( int left, int right )
    {
        this.leftEdge = left;
        this.rightEdge = right;
    }

    public void wrapTopBottom( int top, int bottom )
    {
        this.topEdge = top;
        this.bottomEdge = bottom;
    }

    @Override
    public int getTop()
    {
        return this.topEdge;
    }
    
    public void setTop( int value )
    {
        this.topEdge = value;
    }
    
    @Override
    public int getRight()
    {
        return this.rightEdge;
    }

    public void setRight( int value )
    {
        this.rightEdge = value;
    }
    
    @Override
    public int getBottom()
    {
        return this.bottomEdge;
    }

    public void setBottom( int value )
    {
        this.bottomEdge = value;
    }
    
    @Override
    public int getLeft()
    {
        return this.leftEdge;
    }

    public void setLeft( int value )
    {
        this.leftEdge = value;
    }
    
    @Override
    public int getWidth()
    {
        return this.rightEdge - this.leftEdge;
    }

    @Override
    public int getHeight()
    {
        return this.topEdge - this.bottomEdge;
    }

    @Override
    public void normalise( Actor actor )
    {
        // Normalise the position, so that actor's x,y is within the "normal" part of the view
        while (actor.getX() < this.leftEdge) {
            actor.setX(actor.getX() + (this.rightEdge - this.leftEdge));
        }
        while (actor.getX() >= this.rightEdge) {
            actor.setX(actor.getX() - (this.rightEdge - this.leftEdge));
        }
        while (actor.getY() < this.bottomEdge) {
            actor.setY(actor.getY() + (this.topEdge - this.bottomEdge));
        }
        while (actor.getY() >= this.topEdge) {
            actor.setY(actor.getY() - (this.topEdge - this.bottomEdge));
        }
    }

    @Override
    protected void render( GraphicsContext gc, Actor actor, int alpha )
    {
        normalise(actor);

        render2(gc, actor, alpha);

        // Now check if its overlapping the left or right edges, and therefore needs to be rendered again.
        if (overlappingLeft(actor)) {
            actor.moveBy( this.rightEdge - this.leftEdge, 0);
            render2(gc, actor, alpha);
            normalise(actor);
        } else if (overlappingRight(actor)) {
            actor.moveBy( -this.rightEdge + this.leftEdge, 0);
            render2(gc, actor, alpha);
            normalise(actor);
        }
    }
    
    protected void render2( GraphicsContext gc, Actor actor, int alpha )
    {
        super.render(gc, actor, alpha);

        // Now check if its overlapping the top or bottom edges and therefore needs to be rendered again.
        if (overlappingTop(actor)) {
            actor.moveBy( 0, this.bottomEdge-this.topEdge);
            super.render(gc, actor, alpha);
            actor.moveBy( 0, this.topEdge-this.bottomEdge);
        } else if (overlappingBottom(actor)) {
            actor.moveBy( 0, this.topEdge-this.bottomEdge);
            super.render(gc, actor, alpha);
            actor.moveBy( 0, this.bottomEdge-this.topEdge);
        }

    }
    
    @Override
    public boolean overlappingLeft( Actor actor )
    {
        return actor.getX() < actor.getAppearance().getOffsetX();
    }

    @Override
    public boolean overlappingRight( Actor actor )
    {
        return actor.getX() + actor.getAppearance().getWidth() - actor.getAppearance().getOffsetX() > this.rightEdge;
    }

    @Override
    public boolean overlappingBottom( Actor actor )
    {
        return actor.getY() < actor.getAppearance().getHeight() - actor.getAppearance().getOffsetY();
    }

    @Override
    public boolean overlappingTop( Actor actor )
    {
        return actor.getY() + actor.getAppearance().getOffsetY() > this.topEdge;
    }


}
