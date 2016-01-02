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
import uk.co.nickthecoder.itchy.Renderable;
import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

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
        this.x = 0;
        this.y = 0;
        this.width = 0;
        this.height = 0;
        this.expansion = 0;
        this.parent = null;
        this.visible = true;
        this.styles = new HashSet<String>();
        this.type = "component";
    }

    @Override
    public String getType()
    {
        return this.type;
    }

    @Override
    public void setType( String type )
    {
        this.type = type;
        this.reStyle();
    }

    @Override
    public RootContainer getRoot()
    {
        if (this.parent == null) {
            return null;
        }
        return this.parent.getRoot();
    }

    @Override
    public Set<String> getStyles()
    {
        return this.styles;
    }

    @Override
    public void addStyle( String style, boolean test )
    {
        if (test) {
            this.addStyle(style);
        } else {
            this.removeStyle(style);
        }
    }

    @Override
    public void addStyle( String style )
    {
        if (!this.styles.contains(style)) {
            this.styles.add(style);
            this.reStyle();
        }
    }

    @Override
    public void removeStyle( String style )
    {
        if (this.styles.contains(style)) {
            this.styles.remove(style);
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
        return this.parent;
    }

    public void setParent( Container container )
    {
        this.parent = container;
    }

    @Override
    public void remove()
    {
        if (this.parent != null) {
            this.getParent().removeChild(this);
        }
    }

    @Override
    public void onKeyDown( KeyboardEvent ke )
    {
    }

    @Override
    public void onKeyUp( KeyboardEvent ke )
    {
    }

    @Override
    public boolean canFocus()
    {
        return this.focusable;
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
    public void onFocus( boolean focus )
    {
    }

    @Override
    public double getExpansion()
    {
        return this.expansion;
    }

    @Override
    public void setExpansion( double value )
    {
        if (value != this.expansion) {
            this.expansion = value;

            this.invalidate();
            if (this.parent != null) {
                this.parent.forceLayout();
            }
        }
    }

    @Override
    public boolean isVisible()
    {
        return this.visible;
    }

    @Override
    public void setVisible( boolean value )
    {
        this.visible = value;
        if (this.parent != null) {
            this.parent.forceLayout();
        }
    }

    @Override
    public int getMarginTop()
    {
        return this.marginTop;
    }

    @Override
    public int getMarginLeft()
    {
        return this.marginLeft;
    }

    @Override
    public int getMarginBottom()
    {
        return this.marginBottom;
    }

    @Override
    public int getMarginRight()
    {
        return this.marginRight;
    }

    @Override
    public void setMarginTop( int value )
    {
        if (this.marginTop != value) {
            this.marginTop = value;
            if (this.parent != null) {
                this.parent.forceLayout();
            }
        }
    }

    @Override
    public void setMarginRight( int value )
    {
        if (this.marginRight != value) {
            this.marginRight = value;
            if (this.parent != null) {
                this.parent.forceLayout();
            }
        }
    }

    @Override
    public void setMarginBottom( int value )
    {
        if (this.marginBottom != value) {
            this.marginBottom = value;
            if (this.parent != null) {
                this.parent.forceLayout();
            }
        }
    }

    @Override
    public void setMarginLeft( int value )
    {
        if (this.marginLeft != value) {
            this.marginLeft = value;
            if (this.parent != null) {
                this.parent.forceLayout();
            }
        }
    }

    @Override
    public void setMinimumWidth( int value )
    {
        if (this.minimumWidth != value) {
            this.minimumWidth = value;
            if (this.parent != null) {
                this.parent.forceLayout();
            }
        }
    }

    @Override
    public void setMinimumHeight( int value )
    {
        if (this.minimumHeight != value) {
            this.minimumHeight = value;
            if (this.parent != null) {
                this.parent.forceLayout();
            }
        }
    }

    @Override
    public void setMaximumWidth( int value )
    {
        if (this.maximumWidth != value) {
            this.maximumWidth = value;
            if (this.parent != null) {
                this.parent.forceLayout();
            }
        }
    }

    @Override
    public void setMaximumHeight( int value )
    {
        if (this.maximumHeight != value) {
            this.maximumHeight = value;
            if (this.parent != null) {
                this.parent.forceLayout();
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
        return Math.min(this.maximumWidth, Math.max(this.minimumWidth, this.getNaturalWidth()));
    }

    @Override
    public int getRequiredHeight()
    {
        return Math.min(this.maximumHeight, Math.max(this.minimumHeight, this.getNaturalHeight()));
    }

    @Override
    public Font getFont()
    {
        return this.font;
    }

    private boolean overrideFontRule;

    void setFontFromRule( Font font )
    {
        if (!this.overrideFontRule) {
            setFont(font);
            this.overrideFontRule = false;
        }
    }

    @Override
    public void setFont( Font font )
    {
        if (this.font != font) {
            this.font = font;
            if (this.parent != null) {
                this.parent.forceLayout();
            }
        }
        this.overrideFontRule = true;
    }

    @Override
    public int getFontSize()
    {
        return this.fontSize;
    }

    @Override
    public void setFontSize( int fontSize )
    {
        if (this.fontSize != fontSize) {
            this.fontSize = fontSize;
            if (this.parent != null) {
                this.parent.forceLayout();
            }
        }
    }

    @Override
    public int getX()
    {
        if (this.parent != null) {
            this.parent.ensureLayedOut();
        }
        return this.x;
    }

    @Override
    public int getY()
    {
        if (this.parent != null) {
            this.parent.ensureLayedOut();
        }
        return this.y;
    }

    @Override
    public int getWidth()
    {
        if (this.parent != null) {
            this.parent.ensureLayedOut();
        }
        return this.width;
    }

    @Override
    public int getHeight()
    {
        if (this.parent != null) {
            this.parent.ensureLayedOut();
        }
        return this.height;
    }

    @Override
    public void setPosition( int x, int y, int width, int height )
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
    public void moveTo( int x, int y )
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

    public void render( GraphicsContext gc )
    {
        this.renderBackground(gc);
    }

    protected void renderBackground( GraphicsContext gc )
    {
        if (this.background != null) {
            Surface surface = new Surface(this.getWidth(), this.getHeight(), true);
            this.background.render(surface);
            gc.blit(surface, 0, 0, Surface.BlendMode.COMPOSITE);
            surface.free();
        }
    }

    @Override
    public void setBackground( Renderable background )
    {
        if (this.background != background) {
            this.background = background;
            this.invalidate();
        }
    }

    @Override
    public void setColor( RGBA color )
    {
        this.color = color;
        this.invalidate();
    }

    @Override
    public RGBA getColor()
    {
        return this.color;
    }

    @Override
    public boolean hasAncestor( String type )
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
    public boolean hasAncestorStyle( String style )
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
        Rect rect = new Rect(this.x, this.y, this.getWidth(), this.getHeight());

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
        private Container nextAncestor = AbstractComponent.this.parent;

        @Override
        public boolean hasNext()
        {
            return (this.nextAncestor != null) && (this.nextAncestor.getParent() != null);
        }

        @Override
        public void remove()
        {
        }

        @Override
        public Container next()
        {
            Container result = this.nextAncestor;
            this.nextAncestor = this.nextAncestor.getParent();
            return result;
        }
    }

    @Override
    public boolean mouseDown( MouseButtonEvent event )
    {
        if (this.contains2(event)) {

            int dx = this.x;
            int dy = this.y;

            event.x -= dx;
            event.y -= dy;
            try {
                return this.onMouseDown(event);
            } finally {
                event.x += dx;
                event.y += dy;
            }
        }
        return false;
    }

    @Override
    public boolean mouseMove( MouseMotionEvent event )
    {
        if (this.contains2(event)) {

            int dx = this.x;
            int dy = this.y;

            event.x -= dx;
            event.y -= dy;
            try {
                return this.onMouseMove(event);
            } finally {
                event.x += dx;
                event.y += dy;
            }
        }
        return false;
    }

    @Override
    public boolean mouseUp( MouseButtonEvent event )
    {
        if (this.contains2(event)) {

            int dx = this.x;
            int dy = this.y;

            event.x -= dx;
            event.y -= dy;
            try {
                return this.onMouseUp(event);
            } finally {
                event.x += dx;
                event.y += dy;
            }
        }
        return false;
    }

    @Override
    public boolean onMouseDown( MouseButtonEvent event )
    {
        // This base class does nothing.
        return false;
    }

    @Override
    public boolean onMouseUp( MouseButtonEvent event )
    {
        // This base class does nothing.
        return false;
    }

    @Override
    public boolean onMouseMove( MouseMotionEvent event )
    {
        // This base class does nothing.
        return false;
    }

    @Override
    public boolean contains( MouseEvent event )
    {
        return (event.x) >= 0 && (event.y >= 0) && (event.x < this.getWidth()) && (event.y < this.getHeight());
    }

    @Override
    public boolean contains2( MouseEvent event )
    {
        return (event.x) >= this.getX() && (event.y >= this.getY()) && (event.x < this.getX() + this.getWidth()) &&
            (event.y < this.getY() + this.getHeight());
    }

    @Override
    public Component getComponent( MouseEvent me )
    {
        if (this.contains(me)) {
            return this;
        }

        return null;
    }

    @Override
    public String getTooltip()
    {
        return this.tooltip;
    }

    @Override
    public void setTooltip( String tooltip )
    {
        this.tooltip = tooltip;
    }

    @Override
    public String toString()
    {
        return this.getClass().getName() + " Type: " + this.type + " Styles: " + this.styles + " (" + this.x + "," + this.y + ") - (" +
            this.width + "," + this.height + ")";
    }

}
