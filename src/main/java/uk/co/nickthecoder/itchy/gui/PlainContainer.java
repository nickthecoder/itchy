/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import uk.co.nickthecoder.itchy.GraphicsContext;
import uk.co.nickthecoder.itchy.util.Reversed;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

public class PlainContainer extends AbstractComponent implements Container
{
    public static final int NOT_SET = -1;

    protected Layout layout;

    private boolean layedOut;

    private boolean requirementsCalculated;

    private final List<Component> children;

    private final List<Component> readOnlyChildren;

    private double xAlignment = 0;

    private double yAlignment = 0;

    private int naturalWidth;
    private int naturalHeight;

    private int paddingTop;
    private int paddingRight;
    private int paddingBottom;
    private int paddingLeft;

    private int xSpacing = 0;
    private int ySpacing = 0;

    private boolean fillX;

    private boolean fillY;

    public PlainContainer()
    {
        super();

        this.layedOut = false;
        this.requirementsCalculated = false;

        this.naturalWidth = NOT_SET;
        this.naturalHeight = NOT_SET;

        this.children = new ArrayList<Component>();
        this.readOnlyChildren = Collections.unmodifiableList(this.children);
        this.layout = new HorizontalLayout();
        this.type = "container";
    }

    @Override
    public List<Component> getChildren()
    {
        return this.readOnlyChildren;
    }

    @Override
    public void addChild( Component child )
    {
        if (child.getParent() != null) {
            throw new RuntimeException("Component is already within another Container");
        }
        this.children.add(child);
        child.setParent(this);
        this.forceLayout();
        child.reStyle();
    }

    @Override
    public void addChild( int index, Component child )
    {
        if (child.getParent() != null) {
            throw new RuntimeException("Component is already within another Container");
        }
        this.children.add(index, child);
        child.setParent(this);
        this.forceLayout();
        child.reStyle();
    }

    @Override
    public void removeChild( Component child )
    {
        assert (child.getParent() == this);
        this.children.remove(child);
        child.setParent(null);
        this.forceLayout();
    }

    @Override
    public void clear()
    {
        for (Component child : this.children) {
            child.setParent(null);
        }
        this.children.clear();
        this.invalidate();
        this.forceLayout();
    }

    @Override
    public void setPosition( int x, int y, int width, int height )
    {
        if ((width != this.width) || (height != this.height)) {
            this.forceLayout();
        }
        super.setPosition(x, y, width, height);
    }

    @Override
    public void setPaddingTop( int value )
    {
        if (this.paddingTop != value) {
            this.paddingTop = value;
            this.forceLayout();
        }
    }

    @Override
    public void setPaddingRight( int value )
    {
        if (this.paddingRight != value) {
            this.paddingRight = value;
            this.forceLayout();
        }
    }

    @Override
    public void setPaddingBottom( int value )
    {
        if (this.paddingBottom != value) {
            this.paddingBottom = value;
            this.forceLayout();
        }
    }

    @Override
    public void setPaddingLeft( int value )
    {
        if (this.paddingLeft != value) {
            this.paddingLeft = value;
            this.forceLayout();
        }
    }

    @Override
    public int getPaddingTop()
    {
        return this.paddingTop;
    }

    @Override
    public int getPaddingRight()
    {
        return this.paddingRight;
    }

    @Override
    public int getPaddingBottom()
    {
        return this.paddingBottom;
    }

    @Override
    public int getPaddingLeft()
    {
        return this.paddingLeft;
    }

    @Override
    public int getXSpacing()
    {
        return this.xSpacing;
    }

    @Override
    public int getYSpacing()
    {
        return this.ySpacing;
    }

    @Override
    public void setXSpacing( int value )
    {
        if (this.xSpacing != value) {
            this.xSpacing = value;
            this.forceLayout();
        }
    }

    @Override
    public void setYSpacing( int value )
    {
        if (this.ySpacing != value) {
            this.ySpacing = value;
            this.forceLayout();
        }
    }

    @Override
    public void setLayout( Layout layout )
    {
        this.layout = layout;
        this.forceLayout();
    }

    @Override
    public void setXAlignment( double value )
    {
        this.xAlignment = value;
        this.forceLayout();
    }

    @Override
    public double getXAlignment()
    {
        return this.xAlignment;
    }

    @Override
    public double getYAlignment()
    {
        return this.yAlignment;
    }

    @Override
    public void setYAlignment( double value )
    {
        this.yAlignment = value;
        this.forceLayout();
    }

    @Override
    public void ensureLayedOut()
    {
        if (!this.layedOut) {
            if (this.layout != null) {
                this.layout.calculateRequirements(this);
                this.layout.layout(this);
            }
            this.requirementsCalculated = true;
            this.layedOut = true;
        }
    }

    @Override
    public void forceLayout()
    {
        this.requirementsCalculated = false;
        this.layedOut = false;
        if (this.parent != null) {
            this.parent.forceLayout();
        }
    }

    /**
     * Used to ensure that the component is visble on screen. Most containers do nothing, but a scrollable will scroll the client as
     * appropriate, and Notebooks will select the appropriate tab.
     */
    @Override
    public void ensureVisible( Component child )
    {
        // Does nothing.
    }

    public boolean previousFocus( Component from, Component stop )
    {
        boolean found = from == null;

        for (Component child : Reversed.list(this.children)) {

            if (found) {

                if (child == stop) {
                    return true;
                }

                if (child.isVisible()) {

                    if (child.canFocus()) {
                        child.focus();
                        return true;
                    }
                    if (child instanceof PlainContainer) {
                        PlainContainer container = (PlainContainer) child;
                        if (container.previousFocus(null, stop)) {
                            return true;
                        }
                    }
                }
            }
            if (child == from) {
                found = true;
            }
        }

        if (this.parent != null) {
            if (this.parent.previousFocus(this, stop)) {
                return true;
            }
        }

        if (from != null) {

            return this.previousFocus(null, stop);

        }

        return false;
    }

    @Override
    public void focus()
    {
        if (this.canFocus()) {
            super.focus();
        } else {
            if (!nextFocus(null, null)) {
                super.focus();
            }
        }
    }

    public boolean nextFocus( Component from, Component stop )
    {
        boolean found = from == null;

        for (Component child : this.children) {

            if (found) {

                if (child == stop) {
                    return true;
                }

                if (child.isVisible()) {

                    if (child.canFocus()) {
                        child.focus();
                        return true;
                    }
                    if (child instanceof PlainContainer) {
                        PlainContainer container = (PlainContainer) child;
                        if (container.nextFocus(null, stop)) {
                            return true;
                        }
                    }
                }
            }
            if (child == from) {
                found = true;
            }
        }

        if (stop == null) {
            return false;
        }

        if (this.parent != null) {
            if (this.parent.nextFocus(this, stop)) {
                return true;
            }
        }

        if (from != null) {

            return this.nextFocus(null, stop);

        }

        return false;

    }

    private void ensureRequirementsCalculated()
    {
        if (!this.requirementsCalculated) {
            this.layout.calculateRequirements(this);
        }
        this.requirementsCalculated = true;
    }

    @Override
    public int getNaturalWidth()
    {
        this.ensureRequirementsCalculated();
        return this.naturalWidth;
    }

    @Override
    public int getNaturalHeight()
    {
        this.ensureRequirementsCalculated();
        return this.naturalHeight;
    }

    /**
     * @return True iff the child components should expand to fill the containers full width
     */
    @Override
    public boolean getFillX()
    {
        return this.fillX;
    }

    /**
     * @return True iff the child components should expand to fill the containers full height
     */
    @Override
    public boolean getFillY()
    {
        return this.fillY;
    }

    /**
     * Used to determine if child components should expand to fill this containers full width and height.
     */
    @Override
    public void setFill( boolean x, boolean y )
    {
        this.fillX = x;
        this.fillY = y;
        this.forceLayout();
    }

    /**
     * Set by the containers layout during calculateRequirements
     */
    void setNaturalWidth( int width )
    {
        if (width != this.naturalWidth) {
            this.naturalWidth = width;
            if (this.parent != null) {
                this.parent.forceLayout();
            }
        }
    }

    /**
     * Set by the containers layout during calculateRequirements
     */
    void setNaturalHeight( int height )
    {
        if (height != this.naturalHeight) {
            this.naturalHeight = height;
            if (this.parent != null) {
                this.parent.forceLayout();
            }
        }
    }

    @Override
    public void render( GraphicsContext gc )
    {
        this.ensureLayedOut();

        super.render(gc);

        for (Component child : this.children) {
            if (child.isVisible()) {
                Rect rect = new Rect(child.getX(), child.getY(), child.getWidth(), child.getHeight());
                GraphicsContext childGc = gc.window(rect);
                if (!childGc.empty()) {
                    child.render(childGc);
                }
            }
        }
    }

    @Override
    public void reStyle()
    {
        super.reStyle();
        for (Component child : this.children) {
            child.reStyle();
        }
    }

    @Override
    public boolean onMouseDown( MouseButtonEvent event )
    {
        ListIterator<Component> i = this.children.listIterator(this.children.size());
        while (i.hasPrevious()) {
            Component child = i.previous();

            if (child.isVisible()) {

                if (child.mouseDown(event)) {
                    return true;
                }

            }
        }
        return false;
    }

    @Override
    public boolean onMouseUp( MouseButtonEvent event )
    {
        ListIterator<Component> i = this.children.listIterator(this.children.size());
        while (i.hasPrevious()) {
            Component child = i.previous();

            if (child.isVisible()) {

                if (child.mouseUp(event)) {
                    return true;
                }

            }
        }
        return false;
    }

    @Override
    public boolean onMouseMove( MouseMotionEvent event )
    {
        ListIterator<Component> i = this.children.listIterator(this.children.size());
        while (i.hasPrevious()) {
            Component child = i.previous();

            if (child.isVisible()) {

                if (child.mouseMove(event)) {
                    return true;
                }

            }
        }
        return false;
    }

    /**
     * Return the lowest level component at the given coordinates.
     * 
     * @return Null if (x,y) is not within this RootContainer, otherwise, the lowest level component containing (x,y).
     *         If there is no lower level component, then this RootContainer is returned.
     */
    @Override
    public Component getComponent( MouseEvent me )
    {
        int origX = me.x;
        int origY = me.y;

        try {
            if (this.contains(me)) {
                for (Component child : this.getChildren()) {
                    if (child.isVisible()) {
                        me.x = origX - child.getX();
                        me.y = origY - child.getY();
                        Component temp = child.getComponent(me);
                        if (temp != null) {
                            return temp;
                        }
                    }
                }

                return this;
            }

        } finally {
            me.x = origX;
            me.y = origY;
        }

        return null;
    }

}
