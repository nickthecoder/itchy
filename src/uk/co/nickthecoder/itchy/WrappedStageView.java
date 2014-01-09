/*******************************************************************************
 * Copyright (c) 2014 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;

/**
 * Like a normal StageView, but wrapped, so that if an object moves off of one edge, it appears on the opposite side.
 */
public class WrappedStageView extends StageView implements Wrapped
{
    private int leftEdge = Integer.MIN_VALUE;
    private int rightEdge = Integer.MAX_VALUE;

    private int topEdge = Integer.MAX_VALUE;
    private int bottomEdge = Integer.MIN_VALUE;

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

    @Override
    public int getRight()
    {
        return this.rightEdge;
    }

    @Override
    public int getBottom()
    {
        return this.bottomEdge;
    }

    @Override
    public int getLeft()
    {
        return this.leftEdge;
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
    protected void render( Surface destSurface, Rect clip, int tx, int ty, Actor actor )
    {
        normalise(actor);

        render2(destSurface, clip, tx, ty, actor);

        // Now check if its overlapping the left or right edges, and therefore needs to be rendered again.
        if (overlappingLeft(actor)) {
            render2(destSurface, clip, tx + (this.rightEdge - this.leftEdge), ty, actor);
        } else if (overlappingRight(actor)) {
            render2(destSurface, clip, tx - (this.rightEdge - this.leftEdge), ty, actor);
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

    protected void render2( Surface destSurface, Rect clip, int tx, int ty, Actor actor )
    {
        super.render(destSurface, clip, tx, ty, actor);

        // Now check if its overlapping the top or bottom edges and therefore needs to be rendered again.
        if (overlappingBottom(actor)) {
            super.render(destSurface, clip, tx, ty - (this.topEdge - this.bottomEdge), actor);
        } else if (overlappingTop(actor)) {
            super.render(destSurface, clip, tx, ty + (this.topEdge - this.bottomEdge), actor);
        }

    }

}
