/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.jame.Rect;

public abstract class AbstractView implements View
{
    protected static final List<Property<View,?>> properties = new ArrayList<Property<View,?>>();

    @Override
    public List<Property<View,?>> getProperties()
    {
        return properties;
    }
    
    protected Rect position;

    public boolean visible = true;

    public AbstractView()
    {
        this(new Rect(0,0,100,100));
    }

    public AbstractView( Rect position )
    {
        this.position = new Rect(position);
    }

    @Override
    public Rect getRelativeRect()
    {
        return this.position;
    }

    @Override
    public Rect getPosition()
    {
        return this.position;
    }

    public void setPosition( Rect rect )
    {
    	this.position = rect;
    }

    @Override
    public boolean contains( int x, int y )
    {
        return getPosition().contains(x, y);
    }
    
    public double getWorldX( int screenX )
    {
        Rect position = this.getPosition();
        return screenX - position.x;
    }
    
    public double getWorldY( int screenY )
    {
        Rect position = this.getPosition();
        return position.y - screenY + position.height;
    }

    public GraphicsContext adjustGraphicsContext( GraphicsContext gc )
    {
        return gc.window( this.position );
    }
    
    @Override
    public abstract void render( GraphicsContext gc );

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
