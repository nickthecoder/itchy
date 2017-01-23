package uk.co.nickthecoder.itchy.remote;

import uk.co.nickthecoder.itchy.EventProcessor;
import uk.co.nickthecoder.jame.Events;
import uk.co.nickthecoder.jame.event.Event;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;
import uk.co.nickthecoder.jame.event.QuitEvent;

public class RemoteEventProcessor implements EventProcessor
{
    private Client client;

    public RemoteEventProcessor(Client client)
    {
        this.client = client;
    }

    @Override
    public void begin()
    {
    }

    @Override
    public void end()
    {
    }

    @Override
    public void run()
    {
        while (true) {
            Event event = Events.poll();
            if (event == null) {
                break;
            }

            if (event instanceof KeyboardEvent) {
                System.out.println( "Sending key event ");
                KeyboardEvent ke = (KeyboardEvent) event;
                // TODO Sending symbol twice
                client.send("keyboard", ke.symbolValue, ke.modifiers, ke.scanCodeValue, ke.pressed, ke.symbolValue);
                
            } else if (event instanceof MouseMotionEvent) {
                MouseMotionEvent mme = (MouseMotionEvent) event;
                client.send("mouseMotion", mme.x, mme.y, mme.state);
                
            } else if (event instanceof MouseButtonEvent) {
                System.out.println( "Sending mouse button event ");
                MouseButtonEvent mbe = (MouseButtonEvent) event;
                client.send("mouseButton", mbe.x, mbe.y, mbe.pressed, mbe.button );

            } else if (event instanceof QuitEvent) {
                client.stopClient();
            }

        }
    }

}
