package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.jame.Events;
import uk.co.nickthecoder.jame.event.Event;
import uk.co.nickthecoder.jame.event.StopPropagation;

public class NormalEventProcessor implements EventProcessor
{
    public void begin()
    {
        Itchy.getGame().eventProcessor = this;
    }
    
    /**
     * Polls for all events, till none are left, and has Itchy process each event in the normal way.
     */
    public void run()
    {
        while (true) {
            Event event = Events.poll();
            if (event == null) {
                break;
            } else {
                try {
                    Itchy.processEvent(event);
                } catch (StopPropagation e) {
                    // Do nothing
                } catch (Exception e) {
                    Itchy.handleException(e);
                }
            }
        }
        
    }
    
    public void end()
    {
        // Do nothing
    }
}
