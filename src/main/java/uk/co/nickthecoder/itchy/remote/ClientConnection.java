package uk.co.nickthecoder.itchy.remote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import uk.co.nickthecoder.itchy.Game;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.jame.Rect;

public class ClientConnection
{    
    private int slot;
    
    private Socket socket;
    
    private PrintWriter out;
    
    private BufferedReader in;
 
    private boolean closing;
    
    /**
     * The clipping rectangle of the graphics context.
     * Used to ensure that "rect" commands are send only when needed (when the clip has changed).
     */
    private ViewBounds viewBounds;
    
    public ClientConnection( String gameName, int slot, Socket socket ) throws IOException
    {
        this.slot = slot;
        this.socket = socket;
        this.closing = false;

        in = new BufferedReader(new InputStreamReader( socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        
        out.println( "#game:" + gameName);
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
            game.getViews().render(gc);

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
}
