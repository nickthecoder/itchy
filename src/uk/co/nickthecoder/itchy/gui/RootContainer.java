package uk.co.nickthecoder.itchy.gui;

import java.util.Iterator;

import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.jame.Keys;
import uk.co.nickthecoder.jame.ModifierKey;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;

public abstract class RootContainer extends Container
{
    protected Rules rules;

    protected static Component focus;

    public RootContainer()
    {
        super();
        this.type = "root";
        this.rules = Itchy.singleton.getRules();
    }

    public void style( Component component )
    {
        if ( this.rules != null ) {
            this.rules.style( component );
        }
    }

    public void setRules( Rules rules )
    {
        this.rules = rules;
        this.reStyle();
    }

    public Rules getRules()
    {
        return this.rules;
    }

    @Override
    public RootContainer getRoot()
    {
        return this;
    }

    @Override
    public boolean mouseDown( MouseButtonEvent mbe )
    {
        if ( super.mouseDown( mbe ) ) {
            return true;
        }
        this.setFocus( null );
        return false;
    }

    public boolean keyDown( KeyboardEvent ke )
    {
        if ( ke.symbol == Keys.TAB ) {

            if ( ke.modifier( ModifierKey.SHIFT ) ) {

                if ( RootContainer.focus == null ) {
                    this.previousFocus( null, this );
                } else {
                    RootContainer.focus.parent.previousFocus( RootContainer.focus, RootContainer.focus );
                }

            } else {

                if ( RootContainer.focus == null ) {
                    this.nextFocus( null, this );
                } else {
                    RootContainer.focus.parent.nextFocus( RootContainer.focus, RootContainer.focus );
                }

            }
            return true;
        }

        if ( RootContainer.focus != null ) {
            if ( RootContainer.focus.onKeyDown( ke ) ) {
                return true;
            }

            Iterator<Container> i = RootContainer.focus.getAncestors();
            while ( i.hasNext() ) {
                Container container = i.next();
                if ( container == this ) {
                    break;
                }
                if ( container.onKeyDown( ke ) ) {
                    return true;
                }
            }
        }

        return false;
    }

    public void setFocus( Component component )
    {
        if ( RootContainer.focus == component ) {
            return;
        }
        if ( RootContainer.focus != null ) {
            RootContainer.focus.hasFocus = false;
            RootContainer.focus.removeStyle( "focus" );
            RootContainer.focus.onFocus( false );
        }
        RootContainer.focus = component;

        if ( RootContainer.focus != null ) {
            RootContainer.focus.hasFocus = true;
            RootContainer.focus.addStyle( "focus" );
            RootContainer.focus.onFocus( true );

            // Ensure that the newly focused component is visible, by scrolling any parent scrollables.
            for ( Container parent = component.getParent(); parent != null; parent = parent.getParent() ) {
                if ( parent instanceof Scrollable ) {
                    Scrollable scrollable = (Scrollable) parent;
                    scrollable.ensureVisible( component );
                }
            }
        }

    }

    public abstract void captureMouse( Component component );

    public abstract void releaseMouse( Component component );

}
