package uk.co.nickthecoder.itchy.remote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import uk.co.nickthecoder.itchy.Itchy;

public class ClientConnection
{    
    private int slot;
    
    private Socket socket;
    
    private PrintWriter out;
    
    private BufferedReader in;
 
    private boolean closing;
    
    public ClientConnection( String gameName, int slot, Socket socket ) throws IOException
    {
        this.slot = slot;
        this.socket = socket;
        this.closing = false;

        in = new BufferedReader(new InputStreamReader( socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        
        out.println( "game:" + gameName);
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
    
    public void frameSnapshot()
    {
        try {
            out.println( "flip:");
        } catch (Exception e) {
            // Ignore exceptions when closing
            if (!closing) {
                e.printStackTrace();
            }
        }
    }
}
