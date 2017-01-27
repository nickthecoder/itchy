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
        
        System.out.println( "Created GameWindow " + this );
    }

    private GameWindow(Game game, GameInfo gameInfo)
    {
        super(gameInfo.title, gameInfo.width, gameInfo.height, true,
            Window.HIDDEN | (gameInfo.resizable ? Window.RESIZABLE : 0));

        renderer = new Renderer(this);
        rebuild(gameInfo.width, gameInfo.height);
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
                view.render(view.adjustGraphicsContext(newgc));
            }
        }

        game.getGlassView().render(game.getGlassView().adjustGraphicsContext(newgc));

        renderer.present();
    }

    public void onEvent(EventForWindow efw)
    {
        if (efw instanceof WindowEvent) {
            WindowEvent we = (WindowEvent) efw;

            if (we.getType() == WindowEventType.CLOSE) {

                game.end();
                free();
                this.destroy();

            } else if (we.getType() == WindowEventType.RESIZED) {

                int width = we.data1;
                int height = we.data2;

                free();
                rebuild(width, height);

                System.out.println("GameWindow Resized " + width + "," + height + " " + this);
                this.game.getDirector().onResize(width, height);

            } else if (we.getType() == WindowEventType.MINIMIZED) {
                System.out.println("GameWindow minimized " + game.getTitle() + " " + this);

            } else if (we.getType() == WindowEventType.RESTORED) {
                System.out.println("GameWindow restored " + game.getTitle() + " " + this);
                rebuild(game.getWidth(), game.getHeight());
            
            } else if (we.getType() == WindowEventType.SHOWN) {
                System.out.println("GameWindow shown " + game.getTitle() + " " + this);
                rebuild(game.getWidth(), game.getHeight());

            } else if (we.getType() == WindowEventType.HIDDEN) {
                System.out.println("GameWindow hidden " + game.getTitle() + " " + this);
                free();

            } else {
                System.out.println("Window Event " + we.getType());
            }
        }
    }

    private void free()
    {
        surface.free();
        texture.destroy();
    }

    private void rebuild(int width, int height)
    {
        surface = createSurface(width, height, true);
        texture = createTexture(width, height, true);
    }
}
