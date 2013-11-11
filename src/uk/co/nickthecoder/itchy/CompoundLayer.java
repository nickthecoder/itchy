/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;

public class CompoundLayer extends AbstractLayer
{
    private final LinkedList<Layer> children;

    /**
     * The color to fill the whole area before actors are rendered, or null if no fill should take
     * place.
     */
    public RGBA backgroundColor;

    public CompoundLayer( String name, Rect positionOnScreen )
    {
        this(name, positionOnScreen, null);
    }

    public CompoundLayer( String name, Rect positionOnScreen, RGBA background )
    {
        super(name, positionOnScreen);
        this.children = new LinkedList<Layer>();
        this.backgroundColor = background;
    }

    @Override
    public void render2( Rect clip, Surface destSurface )
    {
        if (this.backgroundColor != null) {
            destSurface.fill(clip, this.backgroundColor);
        }

        for (Layer child : this.children) {
            if (child.isVisible()) {

                child.render(clip, destSurface);

            }
        }
    }

    public void add( Layer layer )
    {
        this.children.add(layer);
        layer.setParent(this);
    }

    public void remove( Layer layer )
    {
        this.children.remove(layer);
    }

    public List<Layer> getChildren()
    {
        return Collections.unmodifiableList(this.children);
    }

    @Override
    public void clear()
    {
        for (Layer child : this.children) {
            child.clear();
        }
    }

    @Override
    public void reset()
    {
        for (Layer child : this.children) {
            child.reset();
        }
    }

    @Override
    public void destroy()
    {
        while (this.children.size() > 0) {
            this.children.get(0).destroy();
            this.children.remove(0);
        }
        this.clear();
    }

    @Override
    public String toString()
    {
        StringBuffer result = new StringBuffer();
        result.append(super.toString()).append("\n{\n");

        for (Layer layer : this.children) {
            result.append("Child : ").append(layer.toString()).append("\n");
        }
        result.append("}\n");
        return result.toString();
    }

}
