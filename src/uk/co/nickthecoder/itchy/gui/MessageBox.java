package uk.co.nickthecoder.itchy.gui;

public class MessageBox extends Window
{
    public static void show( String title, String message )
    {
        MessageBox messageBox = new MessageBox( title, message );
        messageBox.show();
    }

    public MessageBox( String title, String message )
    {
        super( title );

        this.clientArea.addChild( new Label( message ) );
        this.clientArea.setLayout( new VerticalLayout() );
        Container buttons = new Container();
        buttons.addStyle( "buttonBar" );

        this.createButtons( buttons );

        this.clientArea.addChild( buttons );

    }

    protected void createButtons( Container buttons )
    {
        Button ok = new Button( "Ok" );
        ok.addActionListener( new ActionListener()
        {
            @Override
            public void action()
            {
                MessageBox.this.onOk();
            }
        } );
        buttons.addChild( ok );
    }

    protected void onOk()
    {
        this.destroy();
    }

}
