/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import uk.co.nickthecoder.itchy.Focusable;
import uk.co.nickthecoder.itchy.Font;
import uk.co.nickthecoder.itchy.GraphicsContext;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Renderable;
import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;
import uk.co.nickthecoder.jame.event.TextInputEvent;

public abstract class AbstractComponent implements Focusable, Component
{
    Container parent;

    private double expansion;

    private int marginTop = 0;
    private int marginRight = 0;
    private int marginBottom = 0;
    private int marginLeft = 0;

    protected int x;

    protected int y;

    protected int width;

    protected int height;

    protected int minimumWidth = Integer.MIN_VALUE;
    protected int minimumHeight = Integer.MIN_VALUE;

    protected int maximumWidth = Integer.MAX_VALUE;
    protected int maximumHeight = Integer.MAX_VALUE;

    protected Surface surface;

    private boolean visible;

    private final Set<String> styles;

    private Font font;

    private int fontSize;

    private RGBA color;

    protected Renderable background;

    protected String type;

    protected boolean focusable = false;

    boolean hasFocus = false;

    protected String tooltip = null;

    public AbstractComponent()
    {
        x = 0;
        y = 0;
        width = 0;
        height = 0;
        expansion = 0;
        parent = null;
        visible = true;
        styles = new HashSet<String>();
        type = "component";
    }

    @Override
    public String getType()
    {
        return type;
    }

    @Override
    public void setType(String type)
    {
        this.type = type;
        this.reStyle();
    }

    @Override
    public RootContainer getRoot()
    {
        if (parent == null) {
            return null;
        }
        return parent.getRoot();
    }

    @Override
    public Set<String> getStyles()
    {
        return styles;
    }

    @Override
    public void addStyle(String style, boolean test)
    {
        if (test) {
            this.addStyle(style);
        } else {
            this.removeStyle(style);
        }
    }

    @Override
    public void addStyle(String style)
    {
        if (!styles.contains(style)) {
            styles.add(style);
            this.reStyle();
        }
    }

    @Override
    public boolean hasStyle(String style)
    {
        return styles.contains(style);
    }

    @Override
    public void removeStyle(String style)
    {
        if (styles.contains(style)) {
            styles.remove(style);
            this.reStyle();
        }
    }

    @Override
    public void reStyle()
    {
        try {
            RootContainer root = this.getRoot();
            if (root != null) {
                root.style(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Container getParent()
    {
        return parent;
    }

    @Override
    public void setParent(Container container)
    {
        parent = container;
    }

    @Override
    public void remove()
    {
        if (parent != null) {
            this.getParent().removeChild(this);
        }
    }

    @Override
    public void onKeyDown(KeyboardEvent ke)
    {
    }

    @Override
    public void onKeyUp(KeyboardEvent ke)
    {
    }
    
    @Override
    public void onTextInput(TextInputEvent tie)
    {
    }

    @Override
    public boolean canFocus()
    {
        return focusable;
    }

    @Override
    public void focus()
    {
        RootContainer root = this.getRoot();
        if (root != null) {
            root.setFocus(this);
        }
    }

    @Override
    public void lostFocus()
    {
        RootContainer root = this.getRoot();
        if (root != null) {
            root.setFocus(null);
        }
    }

    @Override
    public void onFocus(boolean focus)
    {
    }

    @Override
    public double getExpansion()
    {
        return expansion;
    }

    @Override
    public void setExpansion(double value)
    {
        if (value != expansion) {
            expansion = value;

            this.invalidate();
            if (parent != null) {
                parent.forceLayout();
            }
        }
    }

    @Override
    public boolean isVisible()
    {
        return visible;
    }

    @Override
    public void setVisible(boolean value)
    {
        visible = value;
        if (parent != null) {
            parent.forceLayout();
        }
    }

    @Override
    public int getMarginTop()
    {
        return marginTop;
    }

    @Override
    public int getMarginLeft()
    {
        return marginLeft;
    }

    @Override
    public int getMarginBottom()
    {
        return marginBottom;
    }

    @Override
    public int getMarginRight()
    {
        return marginRight;
    }

    @Override
    public void setMarginTop(int value)
    {
        if (marginTop != value) {
            marginTop = value;
            if (parent != null) {
                parent.forceLayout();
            }
        }
    }

    @Override
    public void setMarginRight(int value)
    {
        if (marginRight != value) {
            marginRight = value;
            if (parent != null) {
                parent.forceLayout();
            }
        }
    }

    @Override
    public void setMarginBottom(int value)
    {
        if (marginBottom != value) {
            marginBottom = value;
            if (parent != null) {
                parent.forceLayout();
            }
        }
    }

    @Override
    public void setMarginLeft(int value)
    {
        if (marginLeft != value) {
            marginLeft = value;
            if (parent != null) {
                parent.forceLayout();
            }
        }
    }

    @Override
    public void setMinimumWidth(int value)
    {
        if (minimumWidth != value) {
            minimumWidth = value;
            if (parent != null) {
                parent.forceLayout();
            }
        }
    }

    @Override
    public void setMinimumHeight(int value)
    {
        if (minimumHeight != value) {
            minimumHeight = value;
            if (parent != null) {
                parent.forceLayout();
            }
        }
    }

    @Override
    public void setMaximumWidth(int value)
    {
        if (maximumWidth != value) {
            maximumWidth = value;
            if (parent != null) {
                parent.forceLayout();
            }
        }
    }

    @Override
    public void setMaximumHeight(int value)
    {
        if (maximumHeight != value) {
            maximumHeight = value;
            if (parent != null) {
                parent.forceLayout();
            }
        }
    }

    @Override
    public abstract int getNaturalWidth();

    @Override
    public abstract int getNaturalHeight();

    @Override
    public int getRequiredWidth()
    {
        return Math.min(maximumWidth, Math.max(minimumWidth, this.getNaturalWidth()));
    }

    @Override
    public int getRequiredHeight()
    {
        return Math.min(maximumHeight, Math.max(minimumHeight, this.getNaturalHeight()));
    }

    @Override
    public Font getFont()
    {
        if ( font == null ) {
            Itchy.getGame().getStylesheet().style(this);
        }
        return font;
    }

    private boolean overrideFontRule;

    void setFontFromRule(Font font)
    {
        if (!overrideFontRule) {
            setFont(font);
            overrideFontRule = false;
        }
    }

    @Override
    public void setFont(Font font)
    {
        if (this.font != font) {
            this.font = font;
            if (parent != null) {
                parent.forceLayout();
            }
        }
        overrideFontRule = true;
    }

    @Override
    public int getFontSize()
    {
        return fontSize;
    }

    @Override
    public void setFontSize(int fontSize)
    {
        if (this.fontSize != fontSize) {
            this.fontSize = fontSize;
            if (parent != null) {
                parent.forceLayout();
            }
        }
    }

    @Override
    public int getX()
    {
        if (parent != null) {
            parent.ensureLayedOut();
        }
        return x;
    }

    @Override
    public int getY()
    {
        if (parent != null) {
            parent.ensureLayedOut();
        }
        return y;
    }

    @Override
    public int getWidth()
    {
        if (parent != null) {
            parent.ensureLayedOut();
        }
        return width;
    }

    @Override
    public int getHeight()
    {
        if (parent != null) {
            parent.ensureLayedOut();
        }
        return height;
    }

    @Override
    public void setPosition(int x, int y, int width, int height)
    {
        if ((this.x != x) || (this.y != y) || (this.width != width) || (this.height != height)) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.invalidate();
        }
    }

    @Override
    public void moveTo(int x, int y)
    {
        if ((this.x != x) || (this.y != y)) {
            this.x = x;
            this.y = y;
            this.invalidate();
        }
    }

    @Override
    public void invalidate()
    {
        RootContainer root = this.getRoot();
        if (root != null) {
            root.invalidate();
        }
    }

    @Override
    public void render(GraphicsContext gc)
    {
        this.renderBackground(gc);
    }

    protected void renderBackground(GraphicsContext gc)
    {
        if (background != null) {
            Surface surface = new Surface(this.getWidth(), this.getHeight(), true);
            background.render(surface);
            gc.blit(surface, 0, 0, Surface.BlendMode.COMPOSITE);
            surface.free();
        }
    }

    @Override
    public void setBackground(Renderable background)
    {
        if (this.background != background) {
            this.background = background;
            this.invalidate();
        }
    }

    @Override
    public void setColor(RGBA color)
    {
        this.color = color;
        this.invalidate();
    }

    @Override
    public RGBA getColor()
    {
        return color;
    }

    @Override
    public boolean hasAncestor(String type)
    {
        for (Iterator<Container> i = this.getAncestors(); i.hasNext();) {
            Component ancestor = i.next();

            if (type.equals(ancestor.getType())) {
                return true;
            }
        }
        return false;

    }

    @Override
    public boolean hasAncestorStyle(String style)
    {
        for (Iterator<Container> i = this.getAncestors(); i.hasNext();) {
            Component ancestor = i.next();

            if (ancestor.getStyles().contains(style)) {
                return true;
            }
        }
        return false;

    }

    @Override
    public Rect getAbsolutePosition()
    {
        Rect rect = new Rect(x, y, this.getWidth(), this.getHeight());

        Container parent = this.parent;
        while (parent != null) {
            rect.x += parent.getX();
            rect.y += parent.getY();

            parent = parent.getParent();
        }

        return rect;
    }

    @Override
    public Iterator<Container> getAncestors()
    {
        return new AncestorIterator();
    }

    public class AncestorIterator implements Iterator<Container>
    {
        private Container nextAncestor = parent;

        @Override
        public boolean hasNext()
        {
            return (nextAncestor != null) && (nextAncestor.getParent() != null);
        }

        @Override
        public void remove()
        {
        }

        @Override
        public Container next()
        {
            Container result = nextAncestor;
            nextAncestor = nextAncestor.getParent();
            return result;
        }
    }

    @Override
    public void mouseDown(MouseButtonEvent event)
    {
        if (this.contains2(event)) {

            int dx = x;
            int dy = y;

            event.x -= dx;
            event.y -= dy;
            try {
                this.onMouseDown(event);
            } finally {
                event.x += dx;
                event.y += dy;
            }
        }
    }

    @Override
    public void mouseMove(MouseMotionEvent event)
    {
        if (this.contains2(event)) {

            int dx = x;
            int dy = y;

            event.x -= dx;
            event.y -= dy;
            try {
                this.onMouseMove(event);
            } finally {
                event.x += dx;
                event.y += dy;
            }
        }
    }

    @Override
    public void mouseUp(MouseButtonEvent event)
    {
        if (this.contains2(event)) {

            int dx = x;
            int dy = y;

            event.x -= dx;
            event.y -= dy;
            try {
                this.onMouseUp(event);
            } finally {
                event.x += dx;
                event.y += dy;
            }
        }
    }

    @Override
    public void onMouseDown(MouseButtonEvent event)
    {
    }

    @Override
    public void onMouseUp(MouseButtonEvent event)
    {
    }

    @Override
    public void onMouseMove(MouseMotionEvent event)
    {
    }

    @Override
    public boolean contains(MouseEvent event)
    {
        return (event.x) >= 0 && (event.y >= 0) && (event.x < this.getWidth()) && (event.y < this.getHeight());
    }

    @Override
    public boolean contains2(MouseEvent event)
    {
        return (event.x) >= this.getX() && (event.y >= this.getY()) && (event.x < this.getX() + this.getWidth()) &&
            (event.y < this.getY() + this.getHeight());
    }

    @Override
    public Component getComponent(MouseEvent me)
    {
        if (this.contains(me)) {
            return this;
        }

        return null;
    }

    @Override
    public String getTooltip()
    {
        return tooltip;
    }

    @Override
    public void setTooltip(String tooltip)
    {
        this.tooltip = tooltip;
    }

    @Override
    public String toString()
    {
        return this.getClass().getName() + " Type: " + type + " Styles: " + styles + " (" + x + "," + y + ") - (" +
            width + "," + height + ")";
    }

}
