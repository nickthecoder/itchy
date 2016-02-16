package uk.co.nickthecoder.itchy.remote;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.PlainDirector;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.jame.Video;

public class Client
{
    private Socket socket;

    private PrintWriter out;

    private BufferedReader in;

    public void startClient(String hostName, int portNumber) throws UnknownHostException, IOException
    {
        try {
            socket = new Socket(hostName, portNumber);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            new ReaderThread().start();

        } catch (Exception e) {
            if (this.socket != null) {
                try {
                    socket.close();
                } catch (Exception e2) {
                    // Do nothing
                }
                socket = null;
                throw e;
            }
        }
    }

    public void stopClient()
    {
        try {
            out.close();
        } catch (Exception e) {
            // Do nothing
        }
        try {
            in.close();
        } catch (Exception e) {
            // Do nothing
        }
        try {
            socket.close();
        } catch (Exception e) {
            // Do nothing
        }
        socket = null;
    }

    private void processLine(String line)
    {
        int colon = line.indexOf(':');
        if (colon > 0) {
            String command = line.substring(0, colon);
            String paramString = line.substring(colon + 1);
            String[] parameters = paramString.split( " " );

            if (command.equals("game")) {
                beginGame(paramString);
            } else if (command.equals("resize")) {
                Itchy.resizeScreen( Integer.parseInt(parameters[0]), Integer.parseInt(parameters[1]));
            } else if (command.equals("flip")) {
                Video.flip();
            } else {
                System.out.println("Skipping unknown command : " + command);
            }
        }
    }

    private void beginGame(String name)
    {
        System.out.println("Game : " + name);
        File resourcesFile = new File(name, name + ".itchy");
        Resources resources = new Resources();
        try {
            resources.load(resourcesFile);
            resources.game.setDirector( new ClientDirector() );
            
        } catch (Exception e) {
            System.err.println("Failed to load resources " + resourcesFile);
            e.printStackTrace();
        }
    }

    class ClientDirector extends PlainDirector
    {
        
    }

    class ReaderThread extends Thread
    {
        public void run()
        {
            try {
                String line = in.readLine();
                if (line == null) {
                    stopClient();
                    return;
                } else {
                    processLine(line);
                }
            } catch (IOException e) {
            }
        }
    }
    
}
