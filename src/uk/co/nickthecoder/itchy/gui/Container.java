/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
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

public class Container extends Component
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

    private int spacing = 0;

    private boolean fillX;

    private boolean fillY;

    public Container()
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

    public List<Component> getChildren()
    {
        return this.readOnlyChildren;
    }

    public void addChild( Component child )
    {
        if (child.parent != null) {
            throw new RuntimeException( "Component is already within another Container" );
        }
        this.children.add(child);
        child.parent = this;
        this.forceLayout();
        child.reStyle();
    }

    public void removeChild( Component child )
    {
        assert (child.parent == this);
        this.children.remove(child);
        child.parent = null;
        this.forceLayout();
    }

    public void clear()
    {
        this.children.clear();
        this.invalidate();
        this.forceLayout();
    }

    public void setPosition( int x, int y, int width, int height )
    {
        if ((width != this.width) || (height != this.height)) {
            this.forceLayout();
        }
        super.setPosition( x, y, width, height );
    }
    
    public void setPaddingTop( int value )
    {
        if (this.paddingTop != value) {
            this.paddingTop = value;
            this.forceLayout();
        }
    }

    public void setPaddingRight( int value )
    {
        if (this.paddingRight != value) {
            this.paddingRight = value;
            this.forceLayout();
        }
    }

    public void setPaddingBottom( int value )
    {
        if (this.paddingBottom != value) {
            this.paddingBottom = value;
            this.forceLayout();
        }
    }

    public void setPaddingLeft( int value )
    {
        if (this.paddingLeft != value) {
            this.paddingLeft = value;
            this.forceLayout();
        }
    }

    public int getPaddingTop()
    {
        return this.paddingTop;
    }

    public int getPaddingRight()
    {
        return this.paddingRight;
    }

    public int getPaddingBottom()
    {
        return this.paddingBottom;
    }

    public int getPaddingLeft()
    {
        return this.paddingLeft;
    }

    public int getSpacing()
    {
        return this.spacing;
    }

    public void setSpacing( int value )
    {
        if (this.spacing != value) {
            this.spacing = value;
            this.forceLayout();
        }
    }

    public void setLayout( Layout layout )
    {
        this.layout = layout;
        this.forceLayout();
    }

    public void setXAlignment( double value )
    {
        this.xAlignment = value;
        this.forceLayout();
    }

    public double getXAlignment()
    {
        return this.xAlignment;
    }

    public double getYAlignment()
    {
        return this.yAlignment;
    }

    public void setYAlignment( double value )
    {
        this.yAlignment = value;
        this.forceLayout();
    }

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

    public void forceLayout()
    {
        this.requirementsCalculated = false;
        this.layedOut = false;
        if (this.parent != null) {
            this.parent.forceLayout();
        }
    }

    /**
     * Used to ensure that the component is visble on screen.
     * Most containers do nothing, but a scrollable will scroll the client as appropriate, and
     * Notebooks will select the appropriate tab.
     */
    public void ensureVisible( Component child )
    {
        // Does nothing.
    }
    
    boolean previousFocus( Component from, Component stop )
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
                    if (child instanceof Container) {
                        Container container = (Container) child;
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

    boolean nextFocus( Component from, Component stop )
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
                    if (child instanceof Container) {
                        Container container = (Container) child;
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
    public boolean getFillX()
    {
        return this.fillX;
    }

    /**
     * @return True iff the child components should expand to fill the containers full height
     */
    public boolean getFillY()
    {
        return this.fillY;
    }

    /**
     * Used to determine if child components should expand to fill this containers full width and
     * height.
     */
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
                Rect rect = new Rect(child.getX(), child.getY(), child.getWidth(),
                        child.getHeight());
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
    public boolean mouseDown( MouseButtonEvent mbe )
    {
        ListIterator<Component> i = this.children.listIterator(this.children.size());
        while (i.hasPrevious()) {
            Component child = i.previous();

            if (child.isVisible()) {

                if (child.testMouseDown(mbe)) {
                    return true;
                }

            }
        }
        return false;
    }

}
