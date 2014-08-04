/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import java.util.Iterator;
import java.util.Set;

import uk.co.nickthecoder.itchy.Font;
import uk.co.nickthecoder.itchy.Renderable;
import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Rect;

/**
 * Unlike the normal use of interfaces, there is really only one implementation of this interface, and that is
 * Component (and all of its sub-classes). This interface does NOT allow for different implementations for Components,
 * and in fact, no methods return ComponentInterfaces (they all return concrete classes such as Component).
 * 
 * So what is it here for? So that two classes for different branches of the Component hierarchy can share a meaningful
 * interface. TextWidget is such an interface, which brings together the similarities of TextBox and TextArea.
 *
 */
public interface ComponentInterface
{

    public String getType();

    public void setType( String type );

    public RootContainer getRoot();

    public Set<String> getStyles();

    public void addStyle( String style, boolean test );

    public void addStyle( String style );

    public void removeStyle( String style );

    public void reStyle();

    public Container getParent();

    public void remove();

    public boolean canFocus();

    public void focus();

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

}
