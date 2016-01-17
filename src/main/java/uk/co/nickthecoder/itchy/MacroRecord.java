package uk.co.nickthecoder.itchy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import uk.co.nickthecoder.jame.Events;
import uk.co.nickthecoder.jame.event.Event;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.ModifierKey;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;
import uk.co.nickthecoder.jame.event.QuitEvent;
import uk.co.nickthecoder.jame.event.StopPropagation;

/**
 * Intercepts the events from jame, and saves them to a file, in a format suitable for replaying via MacroPlayback.
 * 
 * Note. Only events KeyboardEvent, MouseButtonEvent, MouseMotionEvent and QuitEvent are saved. ResizeEvent and
 * WindowEvent are not saved.
 */
public class MacroRecord implements EventProcessor
{
    private PrintWriter output;

    private EventProcessor oldEventProcessor;

    private long tick;
    
    public MacroRecord(String filename) throws IOException
    {
        this(new File(filename));
    }

    public MacroRecord(File file) throws IOException
    {
        this.output = new PrintWriter(new FileWriter(file));
    }

    public void begin()
    {
        this.oldEventProcessor = Itchy.eventProcessor;
        Itchy.eventProcessor = this;
        this.tick = 0;
    }

    /**
     * Polls for all events, till none are left, and has Itchy process each event in the normal way. In addition,
     * records the events to a file, suitable for MacroPlayback to read.
     */
    public void run()
    {
        while (true) {
            Event event = Events.poll();
            if (event == null) {
                break;
            } else {

                if (event instanceof KeyboardEvent) {
                    if (stopRecording((KeyboardEvent) event)) {
                        end();
                        return;
                    }
                }

                try {
                    writeEvent(event);
                    Itchy.processEvent(event);
                } catch (StopPropagation e) {
                    // Do nothing
                } catch (Exception e) {
                    Itchy.handleException(e);
                }
            }
        }
        tick ++;
    }

    /**
     * The default behaviour is to test for Ctrl+ESCAPE.
     * 
     * @return True if the keyboard event is the one for stopping the macro recording
     */
    public boolean stopRecording(KeyboardEvent ke)
    {
        return (ke.isPressed() && (ke.scanCode == KeyboardEvent.ESCAPE) && (ModifierKey.CTRL.pressed(ke.modifiers)));
    }

    public void end()
    {
        if (this.output != null) {
            try {
                this.output.close();
            } catch (Exception e) {
                // Do nothing
            }

            Itchy.eventProcessor = oldEventProcessor;
        }
    }

    private void write(String key, Object... data)
    {
        output.print(this.tick);
        output.print( " " );
        output.print(key);
        for (Object d : data) {
            if (d instanceof Integer) {
                output.print(" ");
                output.print(d);
            } else {
                output.print(" '");
                output.print(d.toString());
                output.print("'");
            }
        }
        output.println();
    }

    private void writeEvent(Event e)
    {
        if (e instanceof KeyboardEvent) {
            KeyboardEvent ke = (KeyboardEvent) e;
            write(  "key", (int) ke.c, ke.modifiers, ke.scanCode, ke.state, ke.symbol);
        } else if (e instanceof MouseButtonEvent) {
            MouseButtonEvent mbe = (MouseButtonEvent) e;
            write("button", mbe.x, mbe.y, mbe.button, mbe.state, mbe.which);
        } else if (e instanceof MouseMotionEvent) {
            MouseMotionEvent mme = (MouseMotionEvent) e;
            write("mouse", mme.x, mme.y, mme.state);
        } else if (e instanceof QuitEvent) {
            write("quit");
        }
    }

}
