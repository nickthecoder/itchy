/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import uk.co.nickthecoder.itchy.GraphicsContext;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.Keys;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseEvent;

public class Scroll extends PlainContainer implements ContainerLayout
{
    protected AbstractComponent child;

    private final VerticalScrollbar vScrollbar;
    private final HorizontalScrollbar hScrollbar;

    private int scrollY = 0;
    private int scrollX = 0;

    public Scroll(AbstractComponent child)
    {
        this.setType("scroll");

        this.setFill(true, true);
        this.setExpansion(1);

        this.child = child;
        this.addChild(child);
        this.child.addStyle("scrolled");

        this.vScrollbar = new VerticalScrollbar();
        this.addChild(this.vScrollbar);

        this.hScrollbar = new HorizontalScrollbar();
        this.addChild(this.hScrollbar);

        this.setNaturalWidth(300); // An arbitrary width
        this.setNaturalHeight(300); // An arbitrary height
        this.setLayout(this);
    }

    @Override
    public void setNaturalWidth(int width)
    {
        super.setNaturalWidth(width);
    }

    @Override
    public void setNaturalHeight(int height)
    {
        super.setNaturalHeight(height);
    }

    @Override
    public void onKeyDown(KeyboardEvent e)
    {
        if (e.symbol == Keys.PAGEUP) {
            this.scrollYBy((int) (-this.getHeight() * 0.9));
            e.stopPropagation();
        }
        if (e.symbol == Keys.PAGEDOWN) {
            this.scrollYBy((int) (this.getHeight() * 0.9));
            e.stopPropagation();
        }

        super.onKeyDown(e);
    }

    @Override
    public void onMouseDown(MouseButtonEvent event)
    {
        super.onMouseDown(event);

        if (event.button == MouseButtonEvent.BUTTON_WHEEL_UP) {
            this.scrollYBy(-50);
            event.stopPropagation();
        } else if (event.button == MouseButtonEvent.BUTTON_WHEEL_DOWN) {
            this.scrollYBy(50);
            event.stopPropagation();
        } else if (event.button == MouseButtonEvent.BUTTON_WHEEL_LEFT) {
            this.scrollXBy(-50);
            event.stopPropagation();
        } else if (event.button == MouseButtonEvent.BUTTON_WHEEL_RIGHT) {
            this.scrollXBy(50);
            event.stopPropagation();
        }

    }

    /**
     * Scrolls the viewport if needed to ensure that the component is visible
     * 
     * @param component
     *            A descendant of this Scroll
     */
    @Override
    public void ensureVisible(Component component)
    {
        int dy = this.scrollY;
        int dx = this.scrollX;
        int height = component.getHeight();
        int width = component.getWidth();

        int myHeight = this.getHeight() - this.getPaddingBottom();
        int myWidth = this.getWidth() - this.getPaddingRight();

        while (component != null) {
            if (component == this) {
                if (this.scrollY > dy) {
                    this.scrollYTo(dy);
                }
                if (this.scrollY + myHeight < dy + height) {
                    this.scrollYTo(dy + height - myHeight);
                }

                if (this.scrollX > dx) {
                    this.scrollXTo(dx);
                }
                if (this.scrollX + myWidth < dx + width) {
                    this.scrollXTo(dx + width - myWidth);
                }
            }
            dy += component.getY();
            dx += component.getX();
            component = component.getParent();
        }
    }

    public void scrollYTo(int value)
    {
        this.scrollY = value;

        int visible = this.getHeight() - this.getPaddingBottom() - this.getPaddingTop();
        int actual = this.child.getHeight();
        int max = actual - visible;

        if (this.scrollY > max) {
            this.scrollY = max;
        }
        if (this.scrollY < 0) {
            this.scrollY = 0;
        }
        this.layout(this);
        this.invalidate();
        this.vScrollbar.forceLayout();
    }

    public void scrollXTo(int value)
    {
        this.scrollX = value;

        int visible = this.getWidth() - this.getPaddingRight() - this.getPaddingLeft();
        int actual = this.child.getWidth();
        int max = actual - visible;

        if (this.scrollX > max) {
            this.scrollX = max;
        }
        if (this.scrollX < 0) {
            this.scrollX = 0;
        }
        this.layout(this);
        this.invalidate();
        this.hScrollbar.forceLayout();
    }

    public void scrollYBy(int delta)
    {
        this.scrollYTo(this.scrollY + delta);
    }

    public void scrollXBy(int delta)
    {
        this.scrollXTo(this.scrollX + delta);
    }

    @Override
    public void calculateRequirements(PlainContainer me)
    {
    }

    @Override
    public void layout(PlainContainer me)
    {
        this.child.setPosition(
            this.getPaddingLeft() + this.child.getMarginLeft() - this.scrollX,
            this.getPaddingTop() + this.child.getMarginTop() - this.scrollY,
            this.child.getRequiredWidth(),
            this.child.getRequiredHeight());

        this.vScrollbar.setPosition(
            this.getWidth() - this.vScrollbar.getRequiredWidth() -
                this.vScrollbar.getMarginLeft() - this.vScrollbar.getMarginRight(),
            this.getPaddingTop() + this.vScrollbar.getMarginTop(),

            this.vScrollbar.getRequiredWidth(),
            this.getHeight() - this.getPaddingTop() - this.getPaddingBottom() -
                this.vScrollbar.getMarginTop() - this.vScrollbar.getMarginBottom());

        this.hScrollbar.setPosition(
            this.getPaddingLeft() + this.hScrollbar.getMarginLeft(),
            this.getHeight() - this.hScrollbar.getRequiredHeight() -
                this.hScrollbar.getMarginTop() - this.hScrollbar.getMarginBottom(),

            this.getWidth() - this.getPaddingLeft() - this.getPaddingRight() -
                this.hScrollbar.getMarginLeft() - this.hScrollbar.getMarginRight(),
            this.hScrollbar.getRequiredHeight());

        this.vScrollbar.setVisible(this.getHeight() < this.child.getRequiredHeight());
        this.hScrollbar.setVisible(this.getWidth() < this.child.getRequiredWidth());

        if (this.vScrollbar.isVisible()) {
            this.vScrollbar.forceLayout();
        }
        if (this.hScrollbar.isVisible()) {
            this.hScrollbar.forceLayout();
        }
    }

    @Override
    public void render(GraphicsContext gc)
    {
        this.ensureLayedOut();
        this.renderBackground(gc);

        Rect rect = new Rect(
            this.child.getX() + this.scrollX,
            this.child.getY() + this.scrollY,
            this.getWidth() - this.getPaddingLeft() - this.getPaddingRight(),
            this.getHeight() - this.getPaddingBottom() - this.getPaddingTop()
            );

        GraphicsContext childGc = gc.window(rect);
        childGc.scroll(this.scrollX, this.scrollY);
        if (!childGc.empty()) {
            this.child.render(childGc);
        }

        if (this.vScrollbar.isVisible()) {
            rect = new Rect(this.vScrollbar.getX(), this.vScrollbar.getY(),
                this.vScrollbar.getWidth(), this.vScrollbar.getHeight());
            childGc = gc.window(rect);
            if (!childGc.empty()) {
                this.vScrollbar.render(childGc);
            }
        }

        if (this.hScrollbar.isVisible()) {
            rect = new Rect(this.hScrollbar.getX(), this.hScrollbar.getY(),
                this.hScrollbar.getWidth(), this.hScrollbar.getHeight());
            childGc = gc.window(rect);
            if (!childGc.empty()) {
                this.hScrollbar.render(childGc);
            }
        }
    }

    public class VerticalScrollbar extends DragableContainer implements ContainerLayout
    {
        PlainContainer scroller;

        private int dragY;

        public VerticalScrollbar()
        {
            super();
            this.setType("scrollbar");
            this.setLayout(this);

            this.scroller = new PlainContainer();
            this.scroller.setType("scroller");
            this.addChild(this.scroller);

            this.addStyle("v");
            this.scroller.addStyle("v");
        }

        @Override
        public void calculateRequirements(PlainContainer me)
        {
            int height = Scroll.this.getHeight() - this.getMarginTop() -
                this.getMarginBottom();
            int width = this.getPaddingLeft() + this.getPaddingRight() +
                this.scroller.getRequiredWidth() + this.scroller.getMarginLeft() +
                this.scroller.getMarginRight();

            this.setNaturalWidth(width);
            this.setNaturalHeight(height);
        }

        public int positionForScroll()
        {
            int visible = Scroll.this.getHeight() - Scroll.this.getPaddingBottom() -
                Scroll.this.getPaddingTop();
            int actual = Scroll.this.child.getHeight();
            int barExtent = this.getHeight() - this.scroller.getHeight();

            return barExtent * Scroll.this.scrollY / (actual - visible);
        }

        public int positionToScroll(int position)
        {
            int visible = Scroll.this.getHeight() - Scroll.this.getPaddingBottom() -
                Scroll.this.getPaddingTop();
            int actual = Scroll.this.child.getHeight();
            int barExtent = this.getHeight() - this.scroller.getHeight();

            return position * (actual - visible) / barExtent;
        }

        @Override
        public void layout(PlainContainer me)
        {
            int scrollBarHeight = VerticalScrollbar.this.getHeight();
            int clientHeight = Scroll.this.getHeight(); // Scroll.this.clientHeight;
            int contentHeight = Scroll.this.child.getHeight();

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

            float scrollerPosition = scrollerTravel * Scroll.this.scrollY / contentTravel;

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
        public void onClick(MouseButtonEvent mbe)
        {
            if (mbe == null) {
                return;
            }
            if (mbe.y < this.scroller.getY()) {
                Scroll.this.scrollYBy(-(int) (this.getHeight() * 0.9));
            } else {
                Scroll.this.scrollYBy((int) (this.getHeight() * 0.9));
            }
        }

        @Override
        public boolean acceptDrag(MouseButtonEvent e)
        {
            if (this.scroller.contains2(e)) {
                this.dragY = this.scroller.getY();
                return true;
            }
            return false;
        }

        @Override
        public void drag(MouseEvent mme, int dx, int dy)
        {
            // If the GUI is too slow to scroll in real time, then just comment
            // out these two lines
            int y = this.dragY + dy;
            Scroll.this.scrollYTo(this.positionToScroll(y));
        }

        @Override
        public void endDrag(MouseButtonEvent e, int dx, int dy)
        {
            int y = this.dragY + dy;
            Scroll.this.scrollYTo(this.positionToScroll(y));
        }

    }

    public class HorizontalScrollbar extends DragableContainer implements ContainerLayout
    {
        PlainContainer scroller;

        private int dragX;

        public HorizontalScrollbar()
        {
            super();
            this.setType("scrollbar");
            this.setLayout(this);

            this.scroller = new PlainContainer();
            this.scroller.setType("scroller");
            this.addChild(this.scroller);

            this.addStyle("h");
            this.scroller.addStyle("h");
        }

        @Override
        public void calculateRequirements(PlainContainer me)
        {
            int width = Scroll.this.getWidth() - this.getMarginLeft() -
                this.getMarginRight();
            int height = this.getPaddingTop() + this.getPaddingBottom() +
                this.scroller.getRequiredHeight() + this.scroller.getMarginTop() +
                this.scroller.getMarginBottom();

            this.setNaturalWidth(width);
            this.setNaturalHeight(height);
        }

        public int positionForScroll()
        {
            int visible = Scroll.this.getWidth() - Scroll.this.getPaddingRight() -
                Scroll.this.getPaddingLeft();
            int actual = Scroll.this.child.getWidth();
            int barExtent = this.getWidth() - this.scroller.getWidth();

            return barExtent * Scroll.this.scrollX / (actual - visible);
        }

        public int positionToScroll(int position)
        {
            int visible = Scroll.this.getWidth() - Scroll.this.getPaddingRight() -
                Scroll.this.getPaddingLeft();
            int actual = Scroll.this.child.getWidth();
            int barExtent = this.getWidth() - this.scroller.getWidth();

            return position * (actual - visible) / barExtent;
        }

        @Override
        public void layout(PlainContainer me)
        {
            int scrollBarWidth = HorizontalScrollbar.this.getWidth();
            int clientWidth = Scroll.this.getWidth(); // Scroll.this.clientHeight;
            int contentWidth = Scroll.this.child.getWidth();

            if (contentWidth <= 0) {
                setVisible(false);
                return;
            }

            float barSize = scrollBarWidth * clientWidth / (float) contentWidth;
            float scrollerTravel = scrollBarWidth - barSize;
            float contentTravel = contentWidth - clientWidth;

            if (contentTravel <= 0) {
                setVisible(false);
                return;
            }

            float scrollerPosition = scrollerTravel * Scroll.this.scrollX / contentTravel;

            int height = this.scroller.getRequiredHeight();
            int width = (int) barSize;

            this.scroller.height = height;
            this.scroller.width = width;

            this.scroller.setPosition(
                (int) scrollerPosition,
                this.getPaddingTop() + this.scroller.getMarginTop(),
                width,
                height);
        }

        @Override
        public void onClick(MouseButtonEvent mbe)
        {
            if (mbe == null) {
                return;
            }
            if (mbe.x < this.scroller.getX()) {
                Scroll.this.scrollXBy(-(int) (this.getWidth() * 0.9));
            } else {
                Scroll.this.scrollXBy((int) (this.getWidth() * 0.9));
            }
        }

        @Override
        public boolean acceptDrag(MouseButtonEvent e)
        {
            if (this.scroller.contains2(e)) {
                this.dragX = this.scroller.getX();
                return true;
            }
            return false;
        }

        @Override
        public void drag(MouseEvent mme, int dx, int dy)
        {
            // If the GUI is too slow to scroll in real time, then just comment
            // out these two lines
            int x = this.dragX + dx;
            Scroll.this.scrollXTo(this.positionToScroll(x));
        }

        @Override
        public void endDrag(MouseButtonEvent e, int dx, int dy)
        {
            int x = this.dragX + dx;
            Scroll.this.scrollXTo(this.positionToScroll(x));
        }

    }

}
