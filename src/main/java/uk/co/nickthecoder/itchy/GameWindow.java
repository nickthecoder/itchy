package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Renderer;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.Texture;
import uk.co.nickthecoder.jame.Window;
import uk.co.nickthecoder.jame.event.EventForWindow;
import uk.co.nickthecoder.jame.event.WindowEvent;
import uk.co.nickthecoder.jame.event.WindowEventType;

public class GameWindow extends Window
{

    public Game game;

    public Renderer renderer;

    public Surface surface;

    public Texture texture;

    public GameWindow(Game game)
    {
        this(game.resources.getGameInfo());
        this.game = game;
    }

    private GameWindow(GameInfo gameInfo)
    {
        super(gameInfo.title, gameInfo.width, gameInfo.height, true, Window.HIDDEN);

        renderer = new Renderer(this);
        surface = createSurface(gameInfo.width, gameInfo.height, false);
        texture = createTexture(gameInfo.width, gameInfo.height, false);
    }

    public void render()
    {
        renderer.setDrawColor(RGBA.WHITE);
        renderer.clear();

        game.render(surface);
        texture.update(surface);
        renderer.copy(texture, 0, 0);

        renderer.present();
    }

    public void onEvent(EventForWindow efw)
    {
        if (efw instanceof WindowEvent) {
            WindowEvent we = (WindowEvent) efw;
            
            if (we.getType() == WindowEventType.CLOSE) {
                game.end();
                surface.free();
                texture.destroy();
                this.destroy();
            }
        }
    }
}
