/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;

public abstract class AbstractView implements View
{
    private ParentView<?> parent;

    protected Rect position;

    public boolean visible = true;

    public AbstractView( Rect position )
    {
        this.position = position;
    }

    @Override
    public Rect getRelativeRect()
    {
        return this.position;
    }

    @Override
    public ParentView<?> getParent()
    {
        return this.parent;
    }

    @Override
    public void setParent( ParentView<?> parent )
    {
        this.parent = parent;
    }

    @Override
    public Rect getPosition()
    {
        return this.position;
    }

    @Override
    public Rect getAbsolutePosition()
    {
        Rect rect = new Rect(this.position);
        for (ParentView<?> parent = this.getParent(); parent != null; parent = parent.getParent()) {
            parent.adjustChildRect(rect);
        }
        return rect;
    }

    @Override
    public boolean contains( int x, int y )
    {
        return getAbsolutePosition().contains(x, y);
    }

    @Override
    public void render( Surface destSurface, Rect parentClip, int offsetX, int offsetY )
    {
        // This is where we would like to draw onto the surface, without taking into account the
        // clipping parentClip.
        Rect newClip = new Rect(offsetX + this.position.x, offsetY + this.position.y, this.position.width, this.position.height);

        // Now lets ensure that parentClip is taken into account. i.e. newClip may get smaller, but
        // never larger.
        if (parentClip.x > newClip.x) {
            newClip.width -= parentClip.x - newClip.x;
            newClip.x = parentClip.x;
        }
        if (parentClip.y > newClip.y) {
            newClip.height -= parentClip.y - newClip.y;
            newClip.y = parentClip.y;
        }
        int rightDiff = (parentClip.x + parentClip.width) - (newClip.x + newClip.width);
        if (rightDiff < 0) {
            newClip.width += rightDiff;
        }
        int bottomDiff = (parentClip.y + parentClip.height) - (newClip.x + newClip.height);
        if (bottomDiff < 0) {
            newClip.height += bottomDiff;
        }

        render2(destSurface, newClip, offsetX + this.position.x, offsetY + this.position.y);

    }

    public abstract void render2( Surface destSurface, Rect parentClip, int offsetX, int offsetY );

    @Override
    public void reset()
    {
        // Do nothing
    }

    @Override
    public boolean isVisible()
    {
        return this.visible;
    }

    public void setVisible( boolean value )
    {
        this.visible = value;
    }
}
