package uk.co.nickthecoder.itchy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import uk.co.nickthecoder.jame.Events;
import uk.co.nickthecoder.jame.event.Event;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;
import uk.co.nickthecoder.jame.event.QuitEvent;

/**
 * Ignores the real events from the keyboard, mouse etc, and instead, simulates events read from a file. See
 * Itchy.eventProcessor.
 */
public class MacroPlayback implements EventProcessor
{
    private EventProcessor oldEventProcessor;

    private int tick = 0;

    private BufferedReader input;

    private TickNumberedEvent tickNumberedEvent;

    public MacroPlayback(String filename) throws IOException
    {
        this(new File(filename));
    }

    public MacroPlayback(File file) throws IOException
    {
        input = new BufferedReader(new FileReader(file));
        readEvent();
    }

    /**
     * 
     * @return True iff the end of file was reached.
     */
    public void readEvent()
    {
        this.tickNumberedEvent = null;

        String line;
        try {
            line = input.readLine();
        } catch (Exception e) {
            System.err.println("Failed to read macro line");
            return;
        }

        if (line == null)
            return;

        line = line.trim();

        // Skip blank lines
        if (line.equals("")) {
            readEvent();
            return;
        }

        try {
            String[] parts = line.split("\\s+");
            for (int i = 0; i < parts.length; i++) {
                String part = parts[i];
                if (part.startsWith("'") && part.endsWith("'")) {
                    part = part.substring(1, part.length() - 1);
                    parts[i] = part;
                }
            }

            String type = parts[1];
            Event event = null;

            if (type.equals("key")) {
                /* Can't create keyboardEvents correctly */
                KeyboardEvent ke = new KeyboardEvent();
                //ke.symbolValue = Integer.parseInt(parts[2]); /* TODO, we have a value missing - no "c" */
                ke.modifiers = Integer.parseInt(parts[3]);
                ke.scanCodeValue = Integer.parseInt(parts[4]);
                ke.pressed = Boolean.parseBoolean(parts[5]);
                ke.symbolValue = Integer.parseInt(parts[6]);
                event = ke;

            } else if (type.equals("button")) {
                MouseButtonEvent mbe = new MouseButtonEvent();
                mbe.x = Integer.parseInt(parts[2]);
                mbe.y = Integer.parseInt(parts[3]);
                mbe.button = Integer.parseInt(parts[4]);
                mbe.pressed = Boolean.parseBoolean(parts[5]);
                mbe.button = Integer.parseInt(parts[6]);
                event = mbe;

            } else if (type.equals("mouse")) {
                MouseMotionEvent mme = new MouseMotionEvent();
                mme.x = Integer.parseInt(parts[2]);
                mme.y = Integer.parseInt(parts[3]);
                mme.state = Integer.parseInt(parts[4]);

            } else if (type.equals("quit")) {
                event = new QuitEvent();

            } else {
                System.err.println("Unexpected event : " + line + ". Ignoring");
            }

            if (event != null) {
                tickNumberedEvent = new TickNumberedEvent();
                tickNumberedEvent.event = event;
                tickNumberedEvent.tick = Long.parseLong(parts[0]);
            }

        } catch (Exception e) {
            System.err.println("Failed to parse Macro : " + line + ".Skipping");
            readEvent();
            return;
        }
    }

    public void begin()
    {
        this.oldEventProcessor = Itchy.getGame().eventProcessor;
        Itchy.getGame().eventProcessor = this;
    }

    /**
     * Polls for all events, till none are left, and has Itchy process each event in the normal way.
     */
    public void run()
    {

        // Ignore the actual events
        while (true) {
            Event event = Events.poll();
            if (event == null) {
                break;
            }

            if (event instanceof QuitEvent) {
                Itchy.processEvent(event);
            }
        }

        while (true) {
            if (tickNumberedEvent == null) {
                end();
                return;
            }

            if (tickNumberedEvent.tick == this.tick) {
                Itchy.processEvent(tickNumberedEvent.event);
                readEvent();
            } else {
                break;
            }
        }

        tick++;
    }

    public void end()
    {
        if (this.input != null) {
            try {
                this.input.close();
            } catch (Exception e) {
                // Do nothing
            }
        }
        Itchy.getGame().eventProcessor = oldEventProcessor;
    }

    private class TickNumberedEvent
    {
        public Event event;
        public long tick;
    }
}
