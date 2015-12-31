/*******************************************************************************
 * Copyright (c) 2014 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import java.util.List;

import uk.co.nickthecoder.itchy.GraphicsContext;

public interface Container extends Component
{

    public List<Component> getChildren();

    public void addChild( Component child );

    public void addChild( int index, Component child );

    public void removeChild( Component child );

    public void clear();

    public void setPaddingTop( int value );

    public void setPaddingRight( int value );

    public void setPaddingBottom( int value );

    public void setPaddingLeft( int value );

    public int getPaddingTop();

    public int getPaddingRight();

    public int getPaddingBottom();

    public int getPaddingLeft();

    public int getXSpacing();

    public int getYSpacing();

    public void setXSpacing( int value );

    public void setYSpacing( int value );

    public void setLayout( Layout layout );

    public void setXAlignment( double value );

    public double getXAlignment();

    public double getYAlignment();

    public void setYAlignment( double value );

    public void ensureLayedOut();

    public void forceLayout();

    /**
     * Used to ensure that the component is visble on screen. Most containers do nothing, but a scrollable will scroll the client as
     * appropriate, and Notebooks will select the appropriate tab.
     */
    public void ensureVisible( Component child );

    @Override
    public void focus();

    @Override
    public int getNaturalWidth();

    @Override
    public int getNaturalHeight();

    /**
     * @return True iff the child components should expand to fill the containers full width
     */
    public boolean getFillX();

    /**
     * @return True iff the child components should expand to fill the containers full height
     */
    public boolean getFillY();

    /**
     * Used to determine if child components should expand to fill this containers full width and height.
     */
    public void setFill( boolean x, boolean y );

    public void render( GraphicsContext gc );

    public boolean nextFocus( Component from, Component stop );

    public boolean previousFocus( Component from, Component stop );

}