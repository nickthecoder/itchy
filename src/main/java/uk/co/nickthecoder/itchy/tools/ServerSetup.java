package uk.co.nickthecoder.itchy.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.PlainContainer;
import uk.co.nickthecoder.itchy.gui.PropertiesForm;
import uk.co.nickthecoder.itchy.gui.VerticalLayout;
import uk.co.nickthecoder.itchy.property.GameProperty;
import uk.co.nickthecoder.itchy.property.IntegerProperty;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.PropertySubject;
import uk.co.nickthecoder.itchy.remote.Server;

public class ServerSetup implements Page, PropertySubject<ServerSetup>
{
    private static List<Property<ServerSetup, ?>> properties = new ArrayList<Property<ServerSetup,?>>();

    static {
        properties.add( new GameProperty<ServerSetup>("game").access("resourcesFile").allowNull(false) );
        properties.add( new IntegerProperty<ServerSetup>( "port" ) );
        properties.add( new IntegerProperty<ServerSetup>( "players" ));
    }
    
    @Override
    public List<Property<ServerSetup, ?>> getProperties()
    {
        return properties;
    }

    public File resourcesFile;
    
    public int port = 1717;
    
    public int players = 1;
    
    private Container page;
    
    private Label message;

    private Server server;
    
    
    @Override
    public String getName()
    {
        return "Server";
    }

    @Override
    public Component createPage()
    {
        page = new PlainContainer();
        page.setLayout( new VerticalLayout() );
        page.setFill(true,  true);

        createSetupPage();
        return page;
    }
    
    private void createSetupPage()
    {
        page.clear();
        
        final PropertiesForm<ServerSetup> form = new PropertiesForm<ServerSetup>(this, getProperties());
        form.autoUpdate = true;
        
        page.addChild(form.createForm());
        form.container.setExpansion(1);
        
        Container buttons = new PlainContainer();
        page.addChild( buttons );
        
        Button ok = new Button( "OK" );
        buttons.addChild( ok );
        ok.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                if (form.isOk()) {
                    startServer();
                } else {
                    message.setText("Choose a game, port and number of players.");
                }
            }
        }); 
    }
    
    private void createStatusPage()
    {
        page.clear();
        
        message = new Label( "Server starting" );
        page.addChild( message );
        
        Container buttons = new PlainContainer();
        page.addChild( buttons );
        
        Button stop = new Button( "Stop" );
        buttons.addChild( stop );
        stop.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                stopServer();
            }
        }); 

    }

    private void startServer()
    {
        System.out.println("Staring server for game " + this.resourcesFile );
        createStatusPage();
        server = new Server();
        try {
            server.startServer(resourcesFile, port, players);
        } catch (Exception e) {
            message.setText( "Server startup failed" );
            e.printStackTrace();
        }
    }

    private void stopServer()
    {
        try {
            server.stopServer();
        } catch (IOException e) {
            System.err.println( "Error while stopping server" );
            e.printStackTrace();
        }
        createSetupPage();
    }
    
}
