package uk.co.nickthecoder.itchy.remote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import uk.co.nickthecoder.itchy.Game;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Layer;
import uk.co.nickthecoder.itchy.View;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.event.Event;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;
import uk.co.nickthecoder.jame.event.StopPropagation;

public class ClientConnection
{    
    private int slot;
    
    private Socket socket;
    
    private PrintWriter out;
    
    private BufferedReader in;
 
    private boolean closing;
    
    private Queue<String> commands;

    /**
     * The clipping rectangle of the graphics context.
     * Used to ensure that "bounds" commands are sent only when needed (when the clip/offsets have changed).
     */
    private ViewBounds viewBounds;
    
    public ClientConnection( String gameName, int slot, Socket socket ) throws IOException
    {
        this.slot = slot;
        this.socket = socket;
        this.closing = false;
        this.commands = new ConcurrentLinkedQueue<String>();

        in = new BufferedReader(new InputStreamReader( socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        
        new ReaderThread().start();
        
        out.println( "!game:" + gameName);
    }
    
    public void beginGame()
    {
        out.println( "resize:" + Itchy.getGame().getWidth() + "," + Itchy.getGame().getHeight() );        
    }
    
    public void close()
    {
        this.closing = true;
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.close();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.socket = null;
    }
    
    public int getSlot()
    {
        return slot;
    }
    
    public void sendViews()
    {
        //System.out.println( "ClientConnection.sendViews" );
        try {
            Game game = Itchy.getGame();
            RemoteGraphicsContext gc = new RemoteGraphicsContext( this, new Rect( 0,0,game.getWidth(), game.getHeight() ));
            for (Layer layer : game.getLayout().getLayers()) {
                View view = layer.getView();
                view.render(view.adjustGraphicsContext(gc));
            }

            out.println( "flip:");
        } catch (Exception e) {
            // Ignore exceptions when closing
            if (!closing) {
                e.printStackTrace();
            }
        }
    }
    
    public void bounds( ViewBounds cgvb )
    {        
        if (! cgvb.equals(this.viewBounds)) {
            this.viewBounds = cgvb;
            send("bounds",
                this.viewBounds.clip.x, this.viewBounds.clip.y,
                this.viewBounds.clip.width, this.viewBounds.clip.height,
                this.viewBounds.ox, this.viewBounds.oy
            );
        }
    }
    
    public void send( String command, Object... parameters )
    {
        out.print( command );
        out.print(":");
        boolean notFirst = false;
        for (Object o : parameters) {
            if (notFirst) {
                out.print(",");
            } else {
                notFirst = true;
            }
            out.print( o.toString());
        }
        out.println();
    }
    
    private void processCommand(String commandLine)
    {
        // System.out.println("Server processCommand " + commandLine);

        try {

            int colon = commandLine.indexOf(':');
            if (colon < 0) {
                System.err.println( "Badly formed command line : " + commandLine);
                return;
            }
            String command = commandLine.substring(0, colon);
            String paramString = commandLine.substring(colon + 1);
            String[] parameters = paramString.split(",");

        
            if (command.equals( "keyboard" ) ) {
                keyboard(
                    parameters[0].charAt(0),
                    Integer.parseInt(parameters[1]),
                    Integer.parseInt(parameters[2]),
                    Integer.parseInt(parameters[3]),
                    Integer.parseInt(parameters[4])
                );

            } else if (command.equals( "mouseButton")) {
                mouseButton(
                    Integer.parseInt(parameters[0]),
                    Integer.parseInt(parameters[1]),
                    Integer.parseInt(parameters[2]),
                    Integer.parseInt(parameters[3])
                );
                
            } else if (command.equals( "mouseMotion")) {
                mouseMotion(
                    Integer.parseInt(parameters[0]),
                    Integer.parseInt(parameters[1]),
                    Integer.parseInt(parameters[2])
                );
                
            }
            
        } catch (Exception e) {
            System.err.println("Failed to process command line : " + commandLine);
            e.printStackTrace();
        }
    }
    
    private void keyboard( char c, int modifiers, int scanCode, int state, int symbol )
    {
        KeyboardEvent ke = new KeyboardEvent();
        ke.c = (char) c;
        ke.modifiers = modifiers;
        ke.scanCode = scanCode;
        ke.state = state;
        ke.symbol = symbol;
        
        processEvent( ke );
    }
    
    private void mouseButton( int x, int y, int state, int button )
    {
        MouseButtonEvent mbe = new MouseButtonEvent();
        mbe.x = x;
        mbe.y = y;
        mbe.state = state;
        mbe.button = button;
            
        processEvent(mbe);
    }

    private void mouseMotion( int x, int y, int state )
    {
        MouseMotionEvent mme = new MouseMotionEvent();
        mme.x = x;
        mme.y = y;
        mme.state = state;
        
        processEvent(mme);
    }
    
    private void processEvent( Event event )
    {
        try {
            Itchy.processEvent(event);
        } catch (StopPropagation e) {
            // Do nothing
        }
    }
    
    public void tick()
    {
        for (String line = commands.poll(); line != null; line = commands.poll()) {
            processCommand(line);
        }
    }
    
    private class ReaderThread extends Thread
    {
        @Override
        public void run()
        {
            System.out.println( "Reading commands from the client." );
            while (socket != null) {
                try {
                    String line = in.readLine();
                    if (line == null) {
                        close();
                        return;
                    } else {
                        if (line.startsWith("!")) {
                            processCommand(line);
                        } else {
                            commands.add(line);
                        }
                    }
                } catch (IOException e) {
                    if (socket != null) {
                        System.err.println("Client failed to read data from the server.");
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
