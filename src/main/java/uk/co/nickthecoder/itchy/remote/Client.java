package uk.co.nickthecoder.itchy.remote;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Costume;
import uk.co.nickthecoder.itchy.Font;
import uk.co.nickthecoder.itchy.GraphicsContext;
import uk.co.nickthecoder.itchy.ImagePose;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.ManagedSound;
import uk.co.nickthecoder.itchy.PlainDirector;
import uk.co.nickthecoder.itchy.Pose;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.SimpleFrameRate;
import uk.co.nickthecoder.itchy.SurfaceGraphicsContext;
import uk.co.nickthecoder.itchy.TextPose;
import uk.co.nickthecoder.itchy.TextStyle;
import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Rect;

public class Client
{
    private Socket socket;

    private PrintWriter out;

    private BufferedReader in;

    private Queue<String> commands;

    private Actor actor;

    private GraphicsContext graphicsContext;

    public void startClient(String hostName, int portNumber) throws UnknownHostException, IOException
    {
        actor = new Actor(ImagePose.getDummyPose());
        graphicsContext = new SurfaceGraphicsContext(Itchy.getDisplaySurface());

        commands = new ConcurrentLinkedQueue<String>();

        Thread readerThread;
        try {
            socket = new Socket(hostName, portNumber);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            readerThread = new ReaderThread();
            readerThread.start();
            System.out.println("Started reader thread");

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

        System.out.println("Waiting for the resources to load");
        while (this.resources == null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }

        System.out.println("Using ClientFrameRate");
        Itchy.getGame().setFrameRate( new ClientFrameRate() );
        
        System.out.println("Using RemoteEventProcessor");
        Itchy.getGame().eventProcessor = new RemoteEventProcessor(this);
        
        System.out.println("Starting client game");
        resources.game.start();

    }

    public void stopClient()
    {
        Itchy.getGame().setFrameRate( new SimpleFrameRate() );

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

    public void send(String command, Object... parameters)
    {
        out.print(command);
        out.print(":");
        boolean notFirst = false;
        for (Object o : parameters) {
            if (notFirst) {
                out.print(",");
            } else {
                notFirst = true;
            }
            out.print(o.toString());
        }
        out.println();
    }

    private void processCommand(String commandLine)
    {
        // System.out.println("Client processCommand " + commandLine);

        try {

            int colon = commandLine.indexOf(':');
            if (colon < 0) {
                System.out.println("Badly formed commandLine " + commandLine);
            }
            String command = commandLine.substring(0, colon);
            String paramString = commandLine.substring(colon + 1);
            String[] parameters = paramString.split(",");

            if (command.equals("!game")) {
                beginGame(paramString);

            } else if (command.equals("resize")) {
                Itchy.resizeScreen(Integer.parseInt(parameters[0]), Integer.parseInt(parameters[1]), false);

            } else if (command.equals("fill")) {
                fill(RGBA.parse(parameters[0]),
                    new Rect(
                        Integer.parseInt(parameters[1]),
                        Integer.parseInt(parameters[2]),
                        Integer.parseInt(parameters[3]),
                        Integer.parseInt(parameters[4])
                    ));

            } else if (command.equals("bounds")) {
                bounds(
                    new Rect(
                        Integer.parseInt(parameters[0]),
                        Integer.parseInt(parameters[1]),
                        Integer.parseInt(parameters[2]),
                        Integer.parseInt(parameters[3])
                    ),
                    Integer.parseInt(parameters[4]),
                    Integer.parseInt(parameters[5]));

            } else if (command.equals("flip")) {
                // TODO VIDEO : Video.flip();

            } else if (command.equals("actor")) {
                renderActor(
                    Integer.parseInt(parameters[0]),
                    Integer.parseInt(parameters[1]),
                    parameters[2],
                    Integer.parseInt(parameters[3]),
                    Double.parseDouble(parameters[4]),
                    Integer.parseInt(parameters[5]));

            } else if (command.equals("text")) {
                renderText(
                    Integer.parseInt(parameters[0]),
                    Integer.parseInt(parameters[1]),
                    parameters[2],
                    parameters[3],
                    RGBA.parse(parameters[4]),
                    Integer.parseInt(parameters[5]),
                    Integer.parseInt(parameters[6]),
                    Double.parseDouble(parameters[7]),
                    Integer.parseInt(parameters[8]));

            } else if (command.equals("playSound")) {
                playSound(parameters[0], parameters[1]);

            } else if (command.equals("stopSound")) {
                stopSound(parameters[0]);

            } else if (command.equals("stopSounds")) {
                stopSounds();

            } else {
                System.out.println("Skipping unknown command : " + command);
            }

        } catch (Exception e) {
            System.err.println("Failed to process command line : " + commandLine);
            e.printStackTrace();
        }
    }

    private void playSound(String costumeName, String eventName)
    {
        System.out.println("Play " + costumeName + "." + eventName);
        Costume costume = resources.getCostume(costumeName);
        if (costume == null) {
            System.out.println("Costume not found : " + costumeName);
        } else {
            ManagedSound ms = costume.getCostumeSound(eventName);
            if (ms == null) {
                System.out.println("Managed Sound not found for event : " + eventName);
            } else {

                System.out.println("Playing the sound " + ms.soundResource.getName());
                Itchy.getGame().soundManager.play(actor, eventName, ms);

            }
        }
    }

    private void stopSound(String eventName)
    {
        Itchy.getGame().soundManager.end(actor, eventName);
    }

    private void stopSounds()
    {
        Itchy.getGame().soundManager.stopAll();
    }

    private void renderText(int x, int y, String text, String fontName, RGBA color, int fontSize, int direction,
        double scale,
        int alpha)
    {
        Font font = resources.getFont(fontName);
        if (font != null) {
            TextStyle textStyle = new TextStyle(font, fontSize);
            textStyle.color = color;
            TextPose pose = new TextPose(text, textStyle);
            actor.getAppearance().setPose(pose);

            actor.moveTo(x, y);
            actor.setDirection(direction);
            actor.getAppearance().setScale(scale);

            graphicsContext.render(actor, alpha);
        }
    }

    private void renderActor(int x, int y, String poseName, int direction, double scale, int alpha)
    {
        Pose pose = resources.getPose(poseName);
        actor.getAppearance().setPose(pose);

        actor.moveTo(x, y);
        actor.setDirection(direction);
        actor.getAppearance().setScale(scale);

        graphicsContext.render(actor, alpha);
    }

    private void bounds(Rect rect, int ox, int oy)
    {
        this.graphicsContext = new SurfaceGraphicsContext(Itchy.getDisplaySurface());
        this.graphicsContext.window(rect);
        this.graphicsContext.scroll(-ox, -oy);
    }

    private void fill(RGBA color, Rect rect)
    {
        graphicsContext.fill(rect, color);
    }

    private Resources resources;

    private void beginGame(String name)
    {
        System.out.println("Client beginGame : " + name);
        File resourcesFile = new File(new File("resources", name), name + ".itchy");
        Resources loadingResources = new Resources();
        loadingResources.client = true;

        try {
            loadingResources.load(resourcesFile);
            loadingResources.game.setDirector(new ClientDirector());
            this.resources = loadingResources;

        } catch (Exception e) {
            System.err.println("Failed to load resources " + resourcesFile);
            e.printStackTrace();
        }
    }

    class ClientDirector extends PlainDirector
    {
        @Override
        public void tick()
        {
            for (String line = commands.poll(); line != null; line = commands.poll()) {
                processCommand(line);
            }
        }
    }

    private class ReaderThread extends Thread
    {
        @Override
        public void run()
        {
            while (socket != null) {
                try {
                    String line = in.readLine();
                    if (line == null) {
                        stopClient();
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

    private class ClientFrameRate extends SimpleFrameRate
    {
        @Override
        public void doRedraw()
        {
            // System.out.println( "Not flipping!");
            // Do nothing - redrawing is done as soon as the data is received from the server (including the flipping
            // of the buffers).
        }
    }

}
