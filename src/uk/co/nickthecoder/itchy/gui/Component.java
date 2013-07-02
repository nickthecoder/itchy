package uk.co.nickthecoder.itchy.gui;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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

public abstract class Component
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

    public Component()
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

    public String getType()
    {
        return this.type;
    }

    public void setType( String type )
    {
        this.type = type;
        this.reStyle();
    }

    public RootContainer getRoot()
    {
        if (this.parent == null) {
            return null;
        }
        return this.parent.getRoot();
    }

    public Set<String> getStyles()
    {
        return this.styles;
    }

    public void addStyle( String style )
    {
        if (!this.styles.contains(style)) {
            this.styles.add(style);
            this.reStyle();
        }
    }

    public void removeStyle( String style )
    {
        if (this.styles.contains(style)) {
            this.styles.remove(style);
            this.reStyle();
        }
    }

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

    public Container getParent()
    {
        return this.parent;
    }

    public boolean onKeyDown( KeyboardEvent ke )
    {
        return false;
    }

    public boolean canFocus()
    {
        return this.focusable;
    }

    public void focus()
    {
        RootContainer root = this.getRoot();
        if (root != null) {
            root.setFocus(this);
        }
    }

    public void onFocus( boolean focus )
    {
    }

    public double getExpansion()
    {
        return this.expansion;
    }

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

    public boolean isVisible()
    {
        return this.visible;
    }

    public void setVisible( boolean value )
    {
        this.visible = value;
        if (this.parent != null) {
            this.parent.forceLayout();
        }
    }

    public int getMarginTop()
    {
        return this.marginTop;
    }

    public int getMarginLeft()
    {
        return this.marginLeft;
    }

    public int getMarginBottom()
    {
        return this.marginBottom;
    }

    public int getMarginRight()
    {
        return this.marginRight;
    }

    public void setMarginTop( int value )
    {
        if (this.marginTop != value) {
            this.marginTop = value;
            if (this.parent != null) {
                this.parent.forceLayout();
            }
        }
    }

    public void setMarginRight( int value )
    {
        if (this.marginRight != value) {
            this.marginRight = value;
            if (this.parent != null) {
                this.parent.forceLayout();
            }
        }
    }

    public void setMarginBottom( int value )
    {
        if (this.marginBottom != value) {
            this.marginBottom = value;
            if (this.parent != null) {
                this.parent.forceLayout();
            }
        }
    }

    public void setMarginLeft( int value )
    {
        if (this.marginLeft != value) {
            this.marginLeft = value;
            if (this.parent != null) {
                this.parent.forceLayout();
            }
        }
    }

    public void setMinimumWidth( int value )
    {
        if (this.minimumWidth != value) {
            this.minimumWidth = value;
            if (this.parent != null) {
                this.parent.forceLayout();
            }
        }
    }

    public void setMinimumHeight( int value )
    {
        if (this.minimumHeight != value) {
            this.minimumHeight = value;
            if (this.parent != null) {
                this.parent.forceLayout();
            }
        }
    }

    public void setMaximumWidth( int value )
    {
        if (this.maximumWidth != value) {
            this.maximumWidth = value;
            if (this.parent != null) {
                this.parent.forceLayout();
            }
        }
    }

    public void setMaximumHeight( int value )
    {
        if (this.maximumHeight != value) {
            this.maximumHeight = value;
            if (this.parent != null) {
                this.parent.forceLayout();
            }
        }
    }

    /**
     * Gets the required width of the component, and is based entirely on this component. It cannot
     * be dependent on the parent's width, because that would lead to a circular dependency.
     * 
     * @return The required width of the component
     */
    public abstract int getNaturalWidth();

    public abstract int getNaturalHeight();

    public int getRequiredWidth()
    {
        return Math.min(this.maximumWidth, Math.max(this.minimumWidth, this.getNaturalWidth()));
    }

    public int getRequiredHeight()
    {
        return Math.min(this.maximumHeight, Math.max(this.minimumHeight, this.getNaturalHeight()));
    }

    public Font getFont()
    {
        return this.font;
    }

    public void setFont( Font font )
    {
        if (this.font != font) {
            this.font = font;
            if (this.parent != null) {
                this.parent.forceLayout();
            }
        }
    }

    public int getFontSize()
    {
        return this.fontSize;
    }

    public void setFontSize( int fontSize )
    {
        if (this.fontSize != fontSize) {
            this.fontSize = fontSize;
            if (this.parent != null) {
                this.parent.forceLayout();
            }
        }
    }

    public int getX()
    {
        if (this.parent != null) {
            this.parent.ensureLayedOut();
        }
        return this.x;
    }

    public int getY()
    {
        if (this.parent != null) {
            this.parent.ensureLayedOut();
        }
        return this.y;
    }

    public int getWidth()
    {
        if (this.parent != null) {
            this.parent.ensureLayedOut();
        }
        return this.width;
    }

    public int getHeight()
    {
        if (this.parent != null) {
            this.parent.ensureLayedOut();
        }
        return this.height;
    }

    /**
     * Called by the parent's layout during the layout phase. If the parent has a free-layout, then
     * the position and size of children can be set arbitrarily by the application designer.
     */
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

    public void moveTo( int x, int y )
    {
        if ((this.x != x) || (this.y != y)) {
            this.x = x;
            this.y = y;
            this.invalidate();
        }
    }

    public void invalidate()
    {
        RootContainer root = this.getRoot();
        if (root != null) {
            root.invalidate();
        }
    }

    protected void render( GraphicsContext gc )
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

    public void setBackground( Renderable background )
    {
        if (this.background != background) {
            this.background = background;
            this.invalidate();
        }
    }

    public void setColor( RGBA color )
    {
        this.color = color;
        this.invalidate();
    }

    public RGBA getColor()
    {
        return this.color;
    }

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

    public Iterator<Container> getAncestors()
    {
        return new AncestorIterator();
    }

    public class AncestorIterator implements Iterator<Container>
    {
        private Container nextAncestor = Component.this.parent;

        @Override
        public boolean hasNext()
        {
            return (this.nextAncestor != null) && (this.nextAncestor.parent != null);
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

    /**
     * 
     * @param event
     *        The mouse event, where x and y are relative to the parent container.
     * @return
     */
    public boolean testMouseDown( MouseButtonEvent event )
    {
        if (this.contains2(event)) {

            int dx = this.x;
            int dy = this.y;

            event.x -= dx;
            event.y -= dy;
            try {
                return this.mouseDown(event);
            } finally {
                event.x += dx;
                event.y += dy;
            }
        }
        return false;
    }

    /**
     * 
     * @param event
     *        The mouse event, where x and y are relative to this component.
     */
    public boolean mouseDown( MouseButtonEvent event )
    {
        // This base class does nothing.
        return false;
    }

    /**
     * 
     * @param mbe
     *        The mouse event, where x and y are relative to this component.
     */
    public void mouseMove( MouseMotionEvent event )
    {
        // This base class does nothing.
    }

    /**
     * 
     * @param event
     *        The mouse event, where x and y are relative to this component.
     */
    public void mouseUp( MouseButtonEvent event )
    {
        // This base class does nothing.
    }

    public boolean contains( MouseEvent event )
    {
        return (event.x) >= 0 && (event.y >= 0) && (event.x < this.getWidth()) &&
                (event.y < this.getHeight());
    }

    public boolean contains2( MouseEvent event )
    {
        return (event.x) >= this.getX() && (event.y >= this.getY()) &&
                (event.x < this.getX() + this.getWidth()) &&
                (event.y < this.getY() + this.getHeight());
    }

    @Override
    public String toString()
    {
        return this.getClass().getName() + " Type: " + this.type + " Styles: " + this.styles;
    }

}
