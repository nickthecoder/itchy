package uk.co.nickthecoder.itchy.gui;

import uk.co.nickthecoder.itchy.Itchy;

public class MessageDialog extends Window
{
    public MessageDialog( String title, String message )
    {
        super( title );
        this.clientArea.setLayout( new VerticalLayout() );

        this.clientArea.addChild( new Label( message ) );

        Container buttons = new Container();
        buttons.addStyle( "buttonBar" );
        this.clientArea.addChild( buttons );

        Button ok = new Button( "Ok" );
        ok.addActionListener( new ActionListener()
        {
            @Override
            public void action()
            {
                Itchy.singleton.hideWindow( MessageDialog.this );
            }
        } );
        buttons.addChild( ok );

    }

    @Override
    public void show()
    {
        Itchy.singleton.showWindow( this );
    }

}
