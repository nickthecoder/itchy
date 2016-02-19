package uk.co.nickthecoder.itchy.remote;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.SimpleFrameRate;
import uk.co.nickthecoder.itchy.StandardSoundManager;

public class Server implements Runnable
{
    private int portNumber;

    private int players;

    private ClientConnection[] clientConnections;

    private ServerSocket serverSocket;

    private Thread thread;

    private File resourcesFile;

    public void startServer(File resourcesFile, int portNumber, int players) throws Exception
    {
        this.resourcesFile = resourcesFile;

        this.portNumber = portNumber;
        this.players = players;

        clientConnections = new ClientConnection[this.players];

        serverSocket = new ServerSocket(this.portNumber);

        thread = new Thread(this);
        thread.start();
        System.out.println("Server connection thread started");

        Itchy.frameRate.end();
        Itchy.frameRate = new ServerFrameRate();

        Resources resources = new Resources();
        resources.server = true;
        try {
            resources.load(resourcesFile);
        } catch (Exception e) {
            System.err.println("Failed to load resources " + resourcesFile);
            e.printStackTrace();
            try {
                this.stopServer();
            } catch (IOException e1) {
            }
            return;
        }

        System.out.println("Using RemoteSoundManager");
        Itchy.soundManager = new RemoteSoundManager(this);

        System.out.println("Server starting game");
        resources.getGame().start();
        System.out.println("Server started game");

    }

    public void stopServer() throws IOException
    {
        Itchy.frameRate.end();
        Itchy.frameRate = new SimpleFrameRate();

        Itchy.soundManager = new StandardSoundManager();

        for (ClientConnection cc : this.clientConnections) {
            if (cc != null) {
                cc.close();
                this.clientConnections[cc.getSlot()] = null;
            }
        }
        if (this.serverSocket != null) {
            this.serverSocket.close();
            this.serverSocket = null;
        }
    }

    @Override
    public void run()
    {
        // TODO Allow for connections to be dropped and reconnected.

        for (int slot = findEmptySlot(); slot >= 0; slot = findEmptySlot()) {

            // Server has been stopped?
            if (this.serverSocket == null) {
                System.out.println("Exiting connection thread");
                return;
            }

            try {
                System.out.println("Waiting for client to connect...");

                Socket socket = this.serverSocket.accept();
                ClientConnection clientConnection = new ClientConnection(
                    this.resourcesFile.getParentFile().getName(), slot, socket);

                clientConnections[slot] = clientConnection;
                System.out.println("Connected to client " + socket.getRemoteSocketAddress());
            } catch (IOException e) {
                if (this.serverSocket != null) {
                    e.printStackTrace();
                }
            }
        }

        for (ClientConnection connection : clientConnections) {
            connection.beginGame();
        }

        System.out.println("Connected to " + this.players + " players");

    }

    private int findEmptySlot()
    {
        int count = 0;
        for (ClientConnection connection : clientConnections) {
            if (connection == null) {
                return count;
            }
            count++;
        }
        return -1;
    }

    public void send(String command, Object... parameters)
    {
        if (clientConnections != null) {
            for (ClientConnection connection : clientConnections) {
                if (connection != null) {
                    connection.send(command, parameters);
                }
            }
        }
    }

    private class ServerFrameRate extends SimpleFrameRate
    {

        @Override
        public void doGameLogic()
        {
            // Allow each ClientConnection to process the command lines send from the client.
            for (ClientConnection connection : clientConnections) {
                if (connection != null) {
                    connection.tick();
                }
            }

            // Do I want to process events from the server too?
            // If not, do just call Itchy.tick instead of calling super.
            super.doGameLogic();
        }

        @Override
        public void doRedraw()
        {
            if (clientConnections != null) {
                for (ClientConnection connection : clientConnections) {
                    if (connection != null) {
                        connection.sendViews();
                    }
                }
            }
        }

    }
}
