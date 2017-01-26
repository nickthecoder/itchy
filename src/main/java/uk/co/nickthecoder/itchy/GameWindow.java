package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.itchy.gui.GuiView;
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
        this(game, game.resources.getGameInfo());
        this.game = game;
    }

    private GameWindow(Game game, GameInfo gameInfo)
    {
        super(gameInfo.title, gameInfo.width, gameInfo.height, true,
            Window.HIDDEN | (gameInfo.resizable ? Window.RESIZABLE : 0));
                
        renderer = new Renderer(this);
        surface = createSurface(gameInfo.width, gameInfo.height, true);
        texture = createTexture(gameInfo.width, gameInfo.height, true);
    }

    public void render()
    {

        GraphicsContext gc = new SurfaceGraphicsContext(surface);
        NewGraphicsContext newgc = new NewGraphicsContext(renderer, this.surface.getWidth(), this.surface.getHeight());

        // Do all the old (non-accellerated stuff first)
        for (GuiView window : game.getGUIViews()) {
            window.render(window.adjustGraphicsContext(gc));
        }

        renderer.setDrawColor(RGBA.BLACK);
        renderer.clear();

        texture.update(surface);
        renderer.copy(texture, 0, 0);

        if (game.layout != null) {
            for (Layer layer : game.layout.getLayersByZOrder()) {
                View view = layer.getView();
                // view.render(view.adjustGraphicsContext(gc));
                view.render(view.adjustGraphicsContext(newgc));
            }
        }

        // game.getGlassView().render(game.getGlassView().adjustGraphicsContext(gc));
        game.getGlassView().render(game.getGlassView().adjustGraphicsContext(newgc));

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

            } else if (we.getType() == WindowEventType.RESIZED) {

                surface.free();
                texture.destroy();
                int width = we.data1;
                int height = we.data2;

                surface = new Surface(width, height, true);
                texture = new Texture(renderer, surface);

                this.game.getDirector().onResize( width, height );

            }
        }
    }
}
