package uk.co.nickthecoder.itchy.gui;

import uk.co.nickthecoder.itchy.GraphicsContext;
import uk.co.nickthecoder.jame.Keys;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseEvent;

public class VerticalScroll extends Container implements Layout, Scrollable
{
    protected Component child;

    private final Scrollbar scrollbar;

    private int scroll = 0;

    private int clientHeight = 300;

    public VerticalScroll( Component child )
    {
        this.setType( "vScroll" );

        this.setFill( true, true );
        this.setExpansion( 1 );

        this.child = child;
        this.addChild( child );

        this.scrollbar = new Scrollbar();
        this.addChild( this.scrollbar );

        this.setLayout( this );
    }

    public void setClientHeight( int height )
    {
        if ( this.clientHeight != height ) {
            this.clientHeight = height;
            this.forceLayout();
            if ( this.parent != null ) {
                this.parent.forceLayout();
            }
        }
    }

    public int getClientHeight()
    {
        return this.clientHeight;
    }

    @Override
    public boolean onKeyDown( KeyboardEvent e )
    {
        if ( e.symbol == Keys.PAGEUP ) {
            this.scrollBy( (int) ( -this.getHeight() * 0.9 ) );
            return true;
        }
        if ( e.symbol == Keys.PAGEDOWN ) {
            this.scrollBy( (int) ( this.getHeight() * 0.9 ) );
            return true;
        }

        return super.onKeyDown( e );
    }

    @Override
    public boolean mouseDown( MouseButtonEvent mbe )
    {
        if ( super.mouseDown( mbe ) ) {
            return true;
        }

        if ( mbe.button == MouseButtonEvent.BUTTON_WHEELUP ) {
            this.scrollBy( -50 );
            return true;
        } else if ( mbe.button == MouseButtonEvent.BUTTON_WHEELDOWN ) {
            this.scrollBy( 50 );
            return true;
        }

        return false;
    }

    /**
     * Scrolls the viewport if needed to ensure that the component is visible
     * @param component A descendant of this VerticalScroll
     */
    @Override
    public void ensureVisible( Component component )
    {
        int dy = this.scroll;
        int height = component.getHeight();

        int myHeight = this.getHeight() - this.getPaddingBottom();

        while ( component != null ) {
            if ( component == this ) {
                if ( this.scroll > dy ) {
                    this.scrollTo( dy );
                }
                if ( this.scroll + myHeight < dy + height ) {
                    this.scrollTo( dy + height - myHeight );
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

        if ( this.scroll > max ) {
            this.scroll = max;
        }
        if ( this.scroll < 0 ) {
            this.scroll = 0;
        }
        this.layout( this );
        this.invalidate();
        this.scrollbar.update();

    }

    public void scrollBy( int delta )
    {
        this.scrollTo( this.scroll + delta );
    }

    @Override
    public void calculateRequirements( Container me )
    {
        int width = this.child.getRequiredWidth() + this.getPaddingLeft() + this.getPaddingRight()
            + this.child.getMarginLeft() + this.child.getMarginRight();
        int height = this.clientHeight + this.getPaddingTop() + this.getPaddingBottom();

        this.setNaturalWidth( width );
        this.setNaturalHeight( height );

    }

    @Override
    public void layout( Container me )
    {
        int width = this.getFillX() ? this.getWidth() - this.getPaddingLeft() - this.getPaddingRight()
            - this.child.getMarginLeft() - this.child.getMarginRight() : this.child.getRequiredWidth();

        this.child.setPosition( this.getPaddingLeft() + this.child.getMarginLeft(),
            this.getPaddingTop() + this.child.getMarginTop() - this.scroll, width, this.child.getRequiredHeight() );

        this.scrollbar.setPosition(
            this.getWidth() - this.scrollbar.getRequiredWidth() - this.scrollbar.getMarginRight()
                - this.getPaddingRight(), this.getPaddingTop() + this.scrollbar.getMarginTop(),
            this.scrollbar.getRequiredWidth(), this.getHeight() - this.getPaddingTop() - this.getPaddingBottom()
                - this.scrollbar.getMarginTop() - this.scrollbar.getMarginBottom() );
    }

    @Override
    public void render( GraphicsContext gc )
    {
        this.ensureLayedOut();
        this.renderBackground( gc );

        Rect rect = new Rect( this.child.getX(), this.child.getY() + this.scroll, this.child.getWidth(),
            this.getHeight() - this.getPaddingBottom() - this.getPaddingTop() );
        GraphicsContext childGc = gc.window( rect );
        childGc.scroll( 0, this.scroll );
        if ( !childGc.empty() ) {
            this.child.render( childGc );
        }

        if ( this.scrollbar.isVisible() ) {
            rect = new Rect( this.scrollbar.getX(), this.scrollbar.getY(), this.scrollbar.getWidth(),
                this.scrollbar.getHeight() );
            childGc = gc.window( rect );
            if ( !childGc.empty() ) {
                this.scrollbar.render( childGc );
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
            this.setType( "scrollbar" );
            this.setLayout( this );

            this.scroller = new Container();
            this.scroller.setType( "scroller" );
            this.addChild( this.scroller );

        }

        @Override
        public void calculateRequirements( Container me )
        {
            this.setNaturalWidth( this.getPaddingLeft() + this.getPaddingRight() + this.scroller.getRequiredWidth()
                + this.scroller.getMarginLeft() + this.scroller.getMarginRight() );
            this.setNaturalHeight( VerticalScroll.this.getHeight() - this.getMarginTop() - this.getMarginBottom() );

            double maxScroll = VerticalScroll.this.child.getRequiredHeight() - VerticalScroll.this.getRequiredHeight()
                - VerticalScroll.this.getPaddingTop() - VerticalScroll.this.getPaddingBottom();
            this.setVisible( maxScroll > 0 );
        }

        public int positionForScroll()
        {
            int visible = VerticalScroll.this.getHeight() - VerticalScroll.this.getPaddingBottom()
                - VerticalScroll.this.getPaddingTop();
            int actual = VerticalScroll.this.child.getHeight();
            int barExtent = this.getHeight() - this.scroller.getHeight();

            return barExtent * VerticalScroll.this.scroll / ( actual - visible );
        }

        public int positionToScroll( int position )
        {
            int visible = VerticalScroll.this.getHeight() - VerticalScroll.this.getPaddingBottom()
                - VerticalScroll.this.getPaddingTop();
            int actual = VerticalScroll.this.child.getHeight();
            int barExtent = this.getHeight() - this.scroller.getHeight();

            return position * ( actual - visible ) / barExtent;
        }

        @Override
        public void layout( Container me )
        {
            int visible = 100; // VerticalScroll.this.getRequiredHeight() -
                               // VerticalScroll.this.getPaddingBottom() -
                               // VerticalScroll.this.getPaddingTop();
            int actual = 200; // VerticalScroll.this.child.getRequiredHeight();

            int height = this.getRequiredHeight() * visible / actual;

            this.scroller.setPosition( this.getPaddingLeft() + this.scroller.getMarginLeft(), 0,
                this.scroller.getRequiredWidth(), height );

        }

        public void update()
        {
            int visible = VerticalScroll.this.getRequiredHeight() - VerticalScroll.this.getPaddingBottom()
                - VerticalScroll.this.getPaddingTop();
            int actual = VerticalScroll.this.child.getRequiredHeight();

            int height = this.getRequiredHeight() * visible / actual;

            this.scroller.setPosition( this.getPaddingLeft() + this.scroller.getMarginLeft(), this.positionForScroll(),
                this.scroller.getRequiredWidth(), height );

        }

        @Override
        public void onClick( MouseButtonEvent mbe )
        {
            if ( mbe == null ) {
                return;
            }
            if ( mbe.y < this.scroller.getY() ) {
                VerticalScroll.this.scrollBy( -(int) ( this.getHeight() * 0.9 ) );
            } else {
                VerticalScroll.this.scrollBy( (int) ( this.getHeight() * 0.9 ) );
            }
        }

        @Override
        public boolean acceptDrag( MouseButtonEvent e )
        {
            if ( this.scroller.contains2( e ) ) {
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
            VerticalScroll.this.scrollTo( this.positionToScroll( y ) );
        }

        @Override
        public void endDrag( MouseButtonEvent e, int dx, int dy )
        {
            int y = this.dragY + dy;
            VerticalScroll.this.scrollTo( this.positionToScroll( y ) );
        }

    }

}
