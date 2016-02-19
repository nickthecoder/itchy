/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

public class GenericCompoundView<V extends View> extends AbstractView implements ParentView<V>, MouseListener
{
    private final LinkedList<V> children;

    public String name;

    public GenericCompoundView( String name, Rect position )
    {
        super(position);
        this.name = name;
        this.children = new LinkedList<V>();
    }

    @Override
    public void adjustChildRect( Rect rect )
    {
        rect.x += this.position.x;
        rect.y += this.position.y;
    }

    
    @Override
    public void render( GraphicsContext gc )
    {
        for (V child : this.children) {
            if (child.isVisible()) {

                child.render( child.adjustGraphicsContext(gc));

            }
        }
    }

    @Override
    public void add( V view )
    {
        if (view.getParent() != null) {
            throw new RuntimeException("Already has a ParentView");
        }
        this.children.add(view);
        view.setParent(this);
    }

    @Override
    public void add( int index, V view )
    {
        if (view.getParent() != null) {
            throw new RuntimeException("Already has a ParentView");
        }
        this.children.add(index, view);
        view.setParent(this);
    }

    @Override
    public void clear()
    {
        while (this.children.size() > 0) {
            this.remove(this.children.get(0));
        }
    }

    @Override
    public void remove( V view )
    {
        this.children.remove(view);
        view.setParent(null);
    }

    @Override
    public List<V> getChildren()
    {
        return Collections.unmodifiableList(this.children);
    }

    @Override
    public void reset()
    {
        for (View child : this.children) {
            child.reset();
        }
    }

    @Override
    public void onMouseDown( MouseButtonEvent event )
    {
        for (ListIterator<V> i = this.children.listIterator(this.children.size()); i.hasPrevious();) {
            V view = i.previous();
            if (view instanceof MouseListener) {
                ((MouseListener) view).onMouseDown(event);
            }
        }
    }

    @Override
    public void onMouseUp( MouseButtonEvent event )
    {
        for (ListIterator<V> i = this.children.listIterator(this.children.size()); i.hasPrevious();) {

            V view = i.previous();
            if (view instanceof MouseListener) {
                ((MouseListener) view).onMouseUp(event);
            }
        }
    }

    @Override
    public void onMouseMove( MouseMotionEvent event )
    {
        for (ListIterator<V> i = this.children.listIterator(this.children.size()); i.hasPrevious();) {
            V view = i.previous();
            if (view instanceof MouseListener) {
                ((MouseListener) view).onMouseMove(event);
            }
        }
    }

    @Override
    public String toString()
    {
        StringBuffer result = new StringBuffer();
        result.append("CompoundView ").append(this.name).append(" ( ");
        for (V view : this.children) {
            result.append(view).append(", ");
        }
        result.append(" )");

        return result.toString();
    }


}
