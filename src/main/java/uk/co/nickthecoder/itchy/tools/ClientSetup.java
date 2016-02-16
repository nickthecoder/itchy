package uk.co.nickthecoder.itchy.tools;

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
import uk.co.nickthecoder.itchy.property.IntegerProperty;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.PropertySubject;
import uk.co.nickthecoder.itchy.property.StringProperty;
import uk.co.nickthecoder.itchy.remote.Client;

public class ClientSetup implements Page, PropertySubject<ClientSetup>
{
    private static List<Property<ClientSetup, ?>> properties = new ArrayList<Property<ClientSetup, ?>>();

    static {
        properties.add(new StringProperty<ClientSetup>("host"));
        properties.add(new IntegerProperty<ClientSetup>("port"));
    }

    @Override
    public List<Property<ClientSetup, ?>> getProperties()
    {
        return properties;
    }

    public String host = "localhost";

    public int port = 1717;

    private Container page;

    private Label message;

    private Client client;

    @Override
    public String getName()
    {
        return "Client";
    }

    @Override
    public Component createPage()
    {
        page = new PlainContainer();
        page.setLayout(new VerticalLayout());
        page.setFill(true, true);

        createSetupPage();
        return page;
    }

    private void createSetupPage()
    {
        page.clear();

        final PropertiesForm<ClientSetup> form = new PropertiesForm<ClientSetup>(this, getProperties());
        form.autoUpdate = true;

        page.addChild(form.createForm());
        form.container.setExpansion(1);

        message = new Label("Not connected");
        page.addChild(message);

        Container buttons = new PlainContainer();
        page.addChild(buttons);

        Button connect = new Button("Connect");
        buttons.addChild(connect);
        connect.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                if (form.isOk()) {
                    connect();
                }
            }
        });
    }

    private void connect()
    {
        client = new Client();
        try {
            message.setText("Connecting");
            client.startClient(host, port);
        } catch (Exception e) {
            message.setText("Error: " + e.toString());
            e.printStackTrace();
        }

        message.setText("Connected. Waiting for others players to join.");
    }

}
