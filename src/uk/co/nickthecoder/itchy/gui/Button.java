package uk.co.nickthecoder.itchy.gui;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.jame.event.MouseButtonEvent;

public class Button extends ClickableContainer
{
    private final List<ActionListener> actionListeners = new ArrayList<ActionListener>();

    public Button()
    {
        super();
        this.type = "button";
        this.focusable = true;
    }

    public Button( String text )
    {
        this( new Label( text ) );
    }

    public Button( Component child )
    {
        this();

        this.addChild( child );
        this.setXAlignment( 0.5f );
        this.setYAlignment( 0.5f );
    }

    @Override
    public void onClick( MouseButtonEvent e )
    {
        this.focus();

        for ( ActionListener actionListener : this.actionListeners ) {
            actionListener.action();
        }
    }

    public void addActionListener( ActionListener actionListener )
    {
        this.actionListeners.add( actionListener );
    }
}
