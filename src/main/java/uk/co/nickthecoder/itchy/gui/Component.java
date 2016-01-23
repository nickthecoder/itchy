/*******************************************************************************
 * Copyright (c) 2014 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import java.util.Iterator;
import java.util.Set;

import uk.co.nickthecoder.itchy.Font;
import uk.co.nickthecoder.itchy.GraphicsContext;
import uk.co.nickthecoder.itchy.Renderable;
import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

public interface Component
{

    public String getType();

    public void setType( String type );

    public Container getParent();

    public void setParent( Container parent );

    public RootContainer getRoot();

    public Set<String> getStyles();

    public void addStyle( String style, boolean test );

    public void addStyle( String style );

    public boolean hasStyle( String style );
    
    public void removeStyle( String style );

    public void reStyle();

    public void remove();

    public void onKeyDown( KeyboardEvent ke );

    public void onKeyUp( KeyboardEvent ke );

    public boolean canFocus();

    public void focus();

    public void lostFocus();

    public void onFocus( boolean focus );

    public double getExpansion();

    public void setExpansion( double value );

    public boolean isVisible();

    public void setVisible( boolean value );

    public int getMarginTop();

    public int getMarginLeft();

    public int getMarginBottom();

    public int getMarginRight();

    public void setMarginTop( int value );

    public void setMarginRight( int value );

    public void setMarginBottom( int value );

    public void setMarginLeft( int value );

    public void setMinimumWidth( int value );

    public void setMinimumHeight( int value );

    public void setMaximumWidth( int value );

    public void setMaximumHeight( int value );

    /**
     * Gets the required width of the component, and is based entirely on this component. It cannot be dependent on the parent's width,
     * because that would lead to a circular dependency.
     * 
     * @return The required width of the component
     */
    public int getNaturalWidth();

    public int getNaturalHeight();

    public int getRequiredWidth();

    public int getRequiredHeight();

    public Font getFont();

    public void setFont( Font font );

    public int getFontSize();

    public void setFontSize( int fontSize );

    public int getX();

    public int getY();

    public int getWidth();

    public int getHeight();

    /**
     * Called by the parent's layout during the layout phase. If the parent has a free-layout, then the position and size of children can be
     * set arbitrarily by the application designer.
     */
    public void setPosition( int x, int y, int width, int height );

    public void moveTo( int x, int y );

    public void invalidate();

    public void setBackground( Renderable background );

    public void setColor( RGBA color );

    public RGBA getColor();

    public boolean hasAncestor( String type );

    public boolean hasAncestorStyle( String style );

    public Rect getAbsolutePosition();

    public Iterator<Container> getAncestors();

    /**
     * @param event
     *        The mouse event, where x and y are relative to the parent container.
     * 
     * @return True is the mouse event has been handled, otherwise false.
     */
    public void mouseDown( MouseButtonEvent event );

    /**
     * @param event
     *        The mouse event, where x and y are relative to the parent container.
     * 
     * @return True is the mouse event has been handled, otherwise false.
     */
    public void mouseMove( MouseMotionEvent event );

    /**
     * @param event
     *        The mouse event, where x and y are relative to the parent container.
     * 
     * @return True is the mouse event has been handled, otherwise false.
     */
    public void mouseUp( MouseButtonEvent event );

    /**
     * 
     * @param event
     *        The mouse event, where x and y are relative to this component.
     */
    public void onMouseDown( MouseButtonEvent event );

    /**
     * 
     * @param event
     *        The mouse event, where x and y are relative to this component.
     */
    public void onMouseUp( MouseButtonEvent event );

    /**
     * @param event
     *        The mouse event, where x and y are relative to this component.
     */
    public void onMouseMove( MouseMotionEvent event );

    /**
     * Given an event whose x,y are relative to this Component, check if the event is within this Component.
     * 
     * @param event
     * @return true iff the mouse event took place within the component.
     */
    public boolean contains( MouseEvent event );

    /**
     * Given an event whose x,y are relative to my parent Container, check if the event is within this Component.
     * 
     * @param event
     * @return true iff the mouse event took place within this Component.
     */
    public boolean contains2( MouseEvent event );

    /**
     * Return this, if the event is within this component, otherwise null
     */
    public Component getComponent( MouseEvent me );

    public String getTooltip();

    public void setTooltip( String tooltip );

    public void render( GraphicsContext gc );

}