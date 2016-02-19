package uk.co.nickthecoder.itchy.remote;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Appearance;
import uk.co.nickthecoder.itchy.GraphicsContext;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Pose;
import uk.co.nickthecoder.itchy.PoseResource;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.TextPose;
import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.Surface.BlendMode;

public class RemoteGraphicsContext extends GraphicsContext
{
    private ClientConnection clientConnection;

    public RemoteGraphicsContext(ClientConnection cc, Rect clip)
    {
        super(clip);
        this.clientConnection = cc;
    }

    @Override
    public void blit(Surface surface, int x, int y, BlendMode blendMode)
    {
        System.out.println("Blitting arbitrary surface not supported");
    }

    @Override
    public void blit(Surface surface, Rect origSrcRect, int x, int y, BlendMode blendMode)
    {
        System.out.println("Blitting arbitrary surface not supported");
    }

    @Override
    public void fill(Rect rect, RGBA color)
    {
        send("fill", color, rect.x, rect.y, rect.width, rect.height);
    }

    @Override
    public void render(Actor actor, int alpha)
    {
        ViewBounds bounds = new ViewBounds();
        bounds.ox = this.ox;
        bounds.oy = this.oy;
        bounds.clip = this.clip;
        
        clientConnection.bounds(bounds);

        Resources resources = Itchy.getGame().resources;
        Appearance appearance = actor.getAppearance();
        Pose pose = appearance.getPose();

        if (pose instanceof TextPose) {
            TextPose textPose = (TextPose) pose;
            send( "text",
                (int) actor.getX(), (int) actor.getY(),
                textPose.getText(),
                textPose.getFont().getName(),
                textPose.getColor(),
                (int) textPose.getFontSize(),
                (int) appearance.getDirection(),
                appearance.getScale(),
                (int) appearance.getAlpha());
            
        } else {
            PoseResource pr = resources.getPoseResource(pose);
            if (pr == null) {
                //System.err.println("Skipping unknown pose for : " + actor);
            } else {

                send(
                    "actor",
                    (int) actor.getX(), (int) actor.getY(),
                    pr.getName(),
                    (int) appearance.getDirection(),
                    appearance.getScale(),
                    (int) appearance.getAlpha());
            }
        }
    }

    private void send(String command, Object... parameters)
    {
        this.clientConnection.send(command, parameters);
    }

    @Override
    protected GraphicsContext duplicate()
    {
        return new RemoteGraphicsContext(
            this.clientConnection,
            new Rect(0, 0, Itchy.getGame().getWidth(), Itchy.getGame().getHeight()));
    }

}
