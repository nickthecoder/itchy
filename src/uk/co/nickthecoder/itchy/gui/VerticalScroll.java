/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import uk.co.nickthecoder.itchy.GraphicsContext;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.event.Event;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.Keys;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseEvent;

public class VerticalScroll extends Container implements Layout
{
    protected Component child;

    private final Scrollbar scrollbar;

    private int scroll = 0;

    public VerticalScroll( Component child )
    {
        this.setType("vScroll");

        this.setFill(true, true);
        this.setExpansion(1);

        this.child = child;
        this.addChild(child);
        this.child.addStyle( "vScrolled" );

        this.scrollbar = new Scrollbar();
        this.addChild(this.scrollbar);

        this.setNaturalHeight(300); // An arbitrary height
        this.setLayout(this);
    }

    @Override
    public void setNaturalHeight( int height )
    {
        super.setNaturalHeight(height);
    }

    @Override
    public boolean onKeyDown( KeyboardEvent e )
    {
        if (e.symbol == Keys.PAGEUP) {
            this.scrollBy((int) (-this.getHeight() * 0.9));
            return true;
        }
        if (e.symbol == Keys.PAGEDOWN) {
            this.scrollBy((int) (this.getHeight() * 0.9));
            return true;
        }

        return super.onKeyDown(e);
    }

    @Override
    public boolean mouseDown( MouseButtonEvent mbe )
    {
        if (super.mouseDown(mbe)) {
            return true;
        }

        if (mbe.button == Event.BUTTON_WHEELUP) {
            this.scrollBy(-50);
            return true;
        } else if (mbe.button == Event.BUTTON_WHEELDOWN) {
            this.scrollBy(50);
            return true;
        }

        return false;
    }

    /**
     * Scrolls the viewport if needed to ensure that the component is visible
     * 
     * @param component
     *        A descendant of this VerticalScroll
     */
    @Override
    public void ensureVisible( Component component )
    {
        int dy = this.scroll;
        int height = component.getHeight();

        int myHeight = this.getHeight() - this.getPaddingBottom();

        while (component != null) {
            if (component == this) {
                if (this.scroll > dy) {
                    this.scrollTo(dy);
                }
                if (this.scroll + myHeight < dy + height) {
                    this.scrollTo(dy + height - myHeight);
                }
            }
            dy += component.getY();
            component = component.getParent();
        }
    }

    public void scrollTo( int value )
    {
        this.scroll = value;

        int visible = this.getHeight() - this.getPaddingBottom() - this.getPaddingTop();
        int actual = this.child.getHeight();
        int max = actual - visible;

        if (this.scroll > max) {
            this.scroll = max;
        }
        if (this.scroll < 0) {
            this.scroll = 0;
        }
        this.layout(this);
        this.invalidate();
        this.scrollbar.forceLayout();
    }

    public void scrollBy( int delta )
    {
        this.scrollTo(this.scroll + delta);
    }

    @Override
    public void calculateRequirements( Container me )
    {
        int width = this.child.getRequiredWidth() + this.getPaddingLeft() + this.getPaddingRight() +
            this.child.getMarginLeft() + this.child.getMarginRight();

        this.setNaturalWidth(width);
    }

    @Override
    public void layout( Container me )
    {
        int width = this.getFillX() ? this.getWidth() - this.getPaddingLeft() -
            this.getPaddingRight() - this.child.getMarginLeft() - this.child.getMarginRight()
            : this.child.getRequiredWidth();

        this.child.setPosition(
            this.getPaddingLeft() + this.child.getMarginLeft(),
            this.getPaddingTop() + this.child.getMarginTop() - this.scroll,
            width,
            this.child.getRequiredHeight());

        this.scrollbar.setPosition(
            this.getWidth() - this.scrollbar.getRequiredWidth() -
                this.scrollbar.getMarginLeft() - this.scrollbar.getMarginRight(),
            this.getPaddingTop() + this.scrollbar.getMarginTop(),
            this.scrollbar.getRequiredWidth(),
            this.getHeight() - this.getPaddingTop() - this.getPaddingBottom() -
                this.scrollbar.getMarginTop() - this.scrollbar.getMarginBottom());
    }

    @Override
    public void render( GraphicsContext gc )
    {
        this.ensureLayedOut();
        this.renderBackground(gc);

        Rect rect = new Rect(this.child.getX(), this.child.getY() + this.scroll,
            this.child.getWidth(), this.getHeight() - this.getPaddingBottom() -
                this.getPaddingTop());
        GraphicsContext childGc = gc.window(rect);
        childGc.scroll(0, this.scroll);
        if (!childGc.empty()) {
            this.child.render(childGc);
        }

        if (this.scrollbar.isVisible()) {
            rect = new Rect(this.scrollbar.getX(), this.scrollbar.getY(),
                this.scrollbar.getWidth(), this.scrollbar.getHeight());
            childGc = gc.window(rect);
            if (!childGc.empty()) {
                this.scrollbar.render(childGc);
            }
        }
    }

    public class Scrollbar extends DragableContainer implements Layout
    {
        Container scroller;

        private int dragY;

        public Scrollbar()
        {
            super();
            this.setType("scrollbar");
            this.setLayout(this);

            this.scroller = new Container();
            this.scroller.setType("scroller");
            this.addChild(this.scroller);

            this.addStyle("v");
            this.scroller.addStyle("v");
        }

        @Override
        public void calculateRequirements( Container me )
        {
            int height = VerticalScroll.this.getHeight() - this.getMarginTop() -
                this.getMarginBottom();
            int width = this.getPaddingLeft() + this.getPaddingRight() +
                this.scroller.getRequiredWidth() + this.scroller.getMarginLeft() +
                this.scroller.getMarginRight();

            this.setNaturalWidth(width);
            this.setNaturalHeight(height);

            if (VerticalScroll.this.getHeight() >= VerticalScroll.this.child.getRequiredHeight()) {
                setVisible(false);
            }
        }

        public int positionForScroll()
        {
            int visible = VerticalScroll.this.getHeight() - VerticalScroll.this.getPaddingBottom() -
                VerticalScroll.this.getPaddingTop();
            int actual = VerticalScroll.this.child.getHeight();
            int barExtent = this.getHeight() - this.scroller.getHeight();

            return barExtent * VerticalScroll.this.scroll / (actual - visible);
        }

        public int positionToScroll( int position )
        {
            int visible = VerticalScroll.this.getHeight() - VerticalScroll.this.getPaddingBottom() -
                VerticalScroll.this.getPaddingTop();
            int actual = VerticalScroll.this.child.getHeight();
            int barExtent = this.getHeight() - this.scroller.getHeight();

            return position * (actual - visible) / barExtent;
        }

        @Override
        public void layout( Container me )
        {
            int scrollBarHeight = Scrollbar.this.getHeight();
            int clientHeight = VerticalScroll.this.getHeight(); // VerticalScroll.this.clientHeight;
            int contentHeight = VerticalScroll.this.child.getHeight();

            if (contentHeight <= 0) {
                setVisible(false);
                return;
            }

            float barSize = scrollBarHeight * clientHeight / (float) contentHeight;
            float scrollerTravel = scrollBarHeight - barSize;
            float contentTravel = contentHeight - clientHeight;

            if (contentTravel <= 0) {
                setVisible(false);
                return;
            }

            float scrollerPosition = scrollerTravel * VerticalScroll.this.scroll / contentTravel;

            int width = this.scroller.getRequiredWidth();
            int height = (int) barSize;

            this.scroller.height = height;
            this.scroller.width = width;

            this.scroller.setPosition(
                this.getPaddingLeft() + this.scroller.getMarginLeft(),
                (int) scrollerPosition,
                width,
                height);
        }

        @Override
        public void onClick( MouseButtonEvent mbe )
        {
            if (mbe == null) {
                return;
            }
            if (mbe.y < this.scroller.getY()) {
                VerticalScroll.this.scrollBy(-(int) (this.getHeight() * 0.9));
            } else {
                VerticalScroll.this.scrollBy((int) (this.getHeight() * 0.9));
            }
        }

        @Override
        public boolean acceptDrag( MouseButtonEvent e )
        {
            if (this.scroller.contains2(e)) {
                this.dragY = this.scroller.getY();
                return true;
            }
            return false;
        }

        @Override
        public void drag( MouseEvent mme, int dx, int dy )
        {
            // If the GUI is too slow to scroll in real time, then just comment
            // out these two lines
            int y = this.dragY + dy;
            VerticalScroll.this.scrollTo(this.positionToScroll(y));
        }

        @Override
        public void endDrag( MouseButtonEvent e, int dx, int dy )
        {
            int y = this.dragY + dy;
            VerticalScroll.this.scrollTo(this.positionToScroll(y));
        }

    }

}
