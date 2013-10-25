/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.tetra;

/*
 * Thanks to Colin Fahey, for an excellent guide to all things tetrisy :
 * http://www.colinfahey.com/tetris/
 */

import java.awt.Point;
import java.util.Random;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Game;
import uk.co.nickthecoder.itchy.Launcher;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.ScrollableLayer;
import uk.co.nickthecoder.itchy.animation.Animation;
import uk.co.nickthecoder.itchy.extras.Explosion;
import uk.co.nickthecoder.itchy.extras.Fragment;
import uk.co.nickthecoder.itchy.extras.Timer;
import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Sound;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.Keys;

public class Tetra extends Game
{
    /**
     * A static reference to the Tetris object, which makes it easy for external classes to interact
     * with the game.
     */
    public static Tetra game;

    /**
     * The size of a tetris square
     */
    public static final int SCALE = 20;
    /**
     * The left edge of the playing area
     */
    public static final int LEFT = 55;
    /**
     * The bottom edge of the playing area
     */
    public static final int BOTTOM = 30;
    /**
     * The width of the playing area in squares
     */
    public static final int WIDTH = 10;
    /**
     * The height of the playing area in squares
     */
    public static final int HEIGHT = 20;

    /**
     * The scores for destroying N (0..4) lines at once.
     */
    public static final int[] SCORE_PER_LINE = new int[] { 0, 40, 100, 300, 1200 };

    private static final int[] cyan = new int[] { -1, 0, -2, 0, 1, 0, 0, 1, 0, -1, 0, -2, -1, 0,
        -2, 0, 1, 0, 0, 1, 0, -1, 0, -2 };
    private static final int[] yellow = new int[] { -1, 0, -1, -1, 0, -1, -1, 0, -1, -1, 0, -1, -1,
        0, -1, -1, 0, -1, -1, 0, -1, -1, 0, -1 };
    private static final int[] green = new int[] { 1, 0, 0, -1, -1, -1, 0, 1, 1, 0, 1, -1, 1, 0, 0,
        -1, -1, -1, 0, 1, 1, 0, 1, -1 };
    private static final int[] red = new int[] { -1, 0, 0, -1, 1, -1, 1, 0, 1, 1, 0, -1, -1, 0, 0,
        -1, 1, -1, 1, 0, 1, 1, 0, -1 };
    private static final int[] orange = new int[] { -1, 0, -1, -1, 1, 0, 0, -1, 1, -1, 0, 1, 1, 0,
        1, 1, -1, 0, 0, 1, -1, 1, 0, -1 };
    private static final int[] blue = new int[] { -1, 0, 1, 0, 1, -1, 0, -1, 0, 1, 1, 1, 1, 0, -1,
        0, -1, 1, 0, 1, 0, -1, -1, -1 };
    private static final int[] purple = new int[] { -1, 0, 1, 0, 0, -1, 0, 1, 0, -1, 1, 0, -1, 0,
        1, 0, 0, 1, 0, -1, 0, 1, -1, 0 };

    /**
     * The data for each of the tetris shapes. Each array holds a list of offsets from the tetris
     * shapes central square. The x and y coordinates are mushed together into a single array i.e.
     * x1a,y1a, x1b,y1b, x1c,y1c, x2a,y2a, x2b,y2b x2c, y2c, etc. The central square isn't included,
     * so there are (x,y) pairs in groups of three (as a tetris shape has four squares), and there
     * are 4 lots of these, one for each possible rotation.
     */
    private static final int[][] data = new int[][] { cyan, yellow, green, red, orange, blue,
        purple };

    /**
     * The costume names for each of the squares
     */
    private static final String[] names = new String[] { "cyan", "yellow", "green", "red",
        "orange", "blue", "purple" };

    /**
     * An array of size WIDTH +2, HEIGHT + 2, representing the pieces fixed on the tetris playing
     * area. It does not hold the piece currently falling, only the pieces that have already fallen.
     * Each entry in the grid is null if it is empty, or contains an Actor. The actor is how the
     * pieces are visible on the screen.
     */
    public Actor[][] grid;

    /**
     * The layer onto which the Actors are placed.
     */
    ScrollableLayer mainLayer;

    boolean playing = false;

    Timer escapeTimer;

    /**
     * A countdown timer, which regulates the speed of the game. The speed is changed in setLevel,
     * which is increased by one for each ten lines removed.
     */
    Timer timer;

    /**
     * The currently falling piece.
     */
    Piece piece;

    /**
     * The current level (1 to 10)
     */
    public int level = 1;

    /**
     * The current score.
     */
    public int score = 0;

    /**
     * Total number of lines removed
     */
    public int completedLines;

    public Tetra( Resources resources ) throws Exception
    {
        super(resources);
    }

    @Override
    protected void createLayers()
    {
        Rect screenRect = new Rect(0, 0, getWidth(), getHeight());

        this.mainLayer = new ScrollableLayer("main", screenRect, new RGBA(0, 0, 0));
        this.layers.add(this.mainLayer);
    }

    @Override
    public void onActivate()
    {
        super.onActivate();
        this.mainLayer.enableMouseListener(this);

        this.level = getStartingLevel();
    }

    @Override
    public void onMessage( String message )
    {
        if (message.equals("quit")) {
            this.end();
        }
        if (message.equals("play")) {
            this.play();
        }
        if (message.equals("levelUp")) {
            this.chooseLevel(getStartingLevel() + 1);
        }
        if (message.equals("levelDown")) {
            this.chooseLevel(getStartingLevel() - 1);
        }
    }

    @Override
    public void tick()
    {
        if (this.escapeTimer != null) {
            if (this.escapeTimer.isFinished()) {
                this.addEventListener(this);
                this.level = getStartingLevel();
                startScene("menu");
                this.escapeTimer = null;
            }
            return;
        }

        if (this.playing) {
            if (this.timer.isFinished()) {
                this.timer.reset();
                if (this.piece == null) {
                    createNextPiece();
                } else {
                    if (this.piece.down()) {
                        this.piece.fix();
                        createNextPiece();
                    }
                }
            }
        }
    }

    private void createNextPiece()
    {
        int n = new Random().nextInt(names.length);
        this.piece = new Piece(n, 5, 20);
        if (this.piece.isOverlapping()) {
            gameOver();
            setHighScore(this.score);
        }
    }

    @Override
    public boolean onKeyDown( KeyboardEvent ke )
    {
        if (ke.isReleased()) {
            return false;
        }

        if (ke.symbol == Keys.F12) {
            startEditor();
        }

        if (ke.symbol == Keys.F1) {
            debug();
        }

        if ((ke.symbol >= Keys.KEY_0) && (ke.symbol <= Keys.KEY_9)) {
            chooseLevel(ke.symbol - Keys.KEY_0);
        }

        if ((ke.symbol == Keys.ESCAPE) && (this.getSceneName().equals("main"))) {
            gameOver();
            this.resources.getSound("shatter").play();
            for (int x = 1; x <= WIDTH; x++) {
                for (int y = 1; y <= HEIGHT; y++) {
                    Actor actor = this.grid[x][y];
                    if (actor != null) {
                        kill(actor);
                        this.grid[x][y] = null;
                    }
                }
            }
            this.removeEventListener(this);
            this.escapeTimer = Timer.createTimerSeconds(2);
        }

        if (!this.playing) {
            if (ke.symbol == Keys.RETURN) {
                onMessage("play");
            }
        } else {
            if (this.piece != null) {
                if ((ke.symbol == Keys.DOWN) || (ke.symbol == Keys.SPACE)) {
                    this.piece.drop();
                }
                if (ke.symbol == Keys.UP) {
                    this.piece.rotate();
                }
                if (ke.symbol == Keys.LEFT) {
                    this.piece.slide(-1);
                }
                if (ke.symbol == Keys.RIGHT) {
                    this.piece.slide(1);
                }
            }
        }
        return false;
    }

    private void chooseLevel( int level )
    {
        if (level == 0) {
            level = 10;
        }
        if ((level > 10) || (level < 1)) {
            return;
        }

        getPreferences().putInt("startingLevel", level);
        if (!this.playing || this.level < level) {
            setLevel(level);
        }
    }

    public int getStartingLevel()
    {
        return getPreferences().getInt("startingLevel", 1);
    }

    public void clearLines()
    {
        int destroyed = 0;
        for (int y = 1; y <= HEIGHT; y++) {
            if (isLineFull(y)) {
                destroyed += 1;
                completedLine();

                for (int x = 1; x <= WIDTH; x++) {
                    kill(this.grid[x][y]);
                    this.grid[x][y] = null;
                }
            } else {
                for (int x = 1; x <= WIDTH; x++) {
                    if (destroyed > 0) {
                        if (this.grid[x][y] != null) {
                            moveDown(this.grid[x][y], destroyed);
                        }
                        this.grid[x][y - destroyed] = this.grid[x][y];
                        this.grid[x][y] = null;
                    }
                }
            }
        }

        Sound sound = null;
        if (destroyed == 4) {
            sound = this.resources.getSound("explode");
        } else if (destroyed == 0) {
            sound = this.resources.getSound("pop");
        } else {
            sound = this.resources.getSound("shatter");
        }
        if (sound != null) {
            sound.play();
        }

        this.score += SCORE_PER_LINE[destroyed] * this.level;
    }

    private void completedLine()
    {
        this.completedLines += 1;
        int level = 0;
        if (this.completedLines <= 0) {
            level = 1;
        } else if ((this.completedLines >= 1) && (this.completedLines <= 90)) {
            level = 1 + ((this.completedLines) / 10);
        } else if (this.completedLines >= 90) {
            level = 10;
        }
        if (level > this.level) {
            setLevel(level);
        }
    }

    public void setLevel( int level )
    {
        this.level = level;
        double delay = (11 - this.level) * 60.0 / 1000.0;
        this.timer = Timer.createTimerSeconds(delay);
    }

    public boolean isLineFull( int y )
    {
        for (int x = 1; x <= WIDTH; x++) {
            if (this.grid[x][y] == null) {
                return false;
            }
        }
        return true;
    }

    private void kill( Actor actor )
    {
        new Fragment()
            .pieces(5)
            .actor(actor)
            .createPoses("fragment");

        new Explosion(actor)
            .projectiles(5)
            .forwards()
            .speed(1, 3)
            .fade(3)
            // .spin(-0.2, 0.2)
            .createActor("fragment").activate();

        actor.kill();
    }

    private void moveDown( Actor actor, int lines )
    {
        // If its still moving down from a previous explosion, then finish that one before starting
        // a new one.
        Animation oldAnimation = actor.getAnimation();
        if (oldAnimation != null) {
            while (!oldAnimation.isFinished()) {
                oldAnimation.tick(actor);
            }
        }

        actor.event("moveDown" + lines);
    }

    private void gameOver()
    {
        if (this.piece != null) {
            for (Actor actor : this.piece.actors) {
                kill(actor);
            }

            this.resources.getSound("shatter").play();
        }
        this.playing = false;
        this.piece = null;
        this.timer = null;
    }

    private void play()
    {
        this.score = 0;
        this.completedLines = 0;
        setLevel(getStartingLevel());
        Actor dummy = new Actor(this.resources.getCostume(names[0]));

        this.grid = new Actor[WIDTH + 2][HEIGHT + 2];
        for (int x = 0; x < WIDTH + 2; x++) {
            for (int y = 0; y < HEIGHT + 2; y++) {
                if ((x == 0) || (y == 0) || (x == WIDTH + 1)) {
                    this.grid[x][y] = dummy;
                } else {
                    this.grid[x][y] = null;
                }
            }
        }
        startScene("main");
        this.playing = true;
    }

    public int getScore()
    {
        return this.score;
    }

    public int getHighScore()
    {
        return getPreferences().getInt("highScore", 0);
    }

    public void setHighScore( int value )
    {
        if (value > getHighScore()) {
            getPreferences().putInt("highScore", value);
        }
    }

    public void debug()
    {
        System.out.println();
        for (int y = HEIGHT + 1; y >= 0; y--) {
            for (int x = 0; x < WIDTH + 2; x++) {
                System.out.print(this.grid[x][y] == null ? " " : "X");
            }
            System.out.println();
        }
        System.out.println();
    }

    public class Piece
    {
        static final int ROTATIONS = 4;
        static final int PIECES = 4;

        int centerX;
        int centerY;
        int rotation = 0;

        Point[][] places;

        Actor[] actors;

        public Piece( int n, int x, int y )
        {

            this.centerX = x;
            this.centerY = y;

            this.places = new Point[ROTATIONS][PIECES];
            for (int r = 0; r < 4; r++) {
                this.places[r][0] = new Point(0, 0);
                for (int s = 0; s < 3; s++) {
                    int i = r * 6 + s * 2;
                    this.places[r][s + 1] = new Point(data[n][i], data[n][i + 1]);
                }
            }
            this.actors = new Actor[PIECES];
            for (int i = 0; i < PIECES; i++) {
                this.actors[i] = new Actor(Tetra.this.resources.getCostume(names[n]));
                Tetra.this.mainLayer.addTop(this.actors[i]);
                this.actors[i].activate();
            }
            update();

        }

        public void rotate()
        {
            this.rotation = (this.rotation + 1) % 4;
            if (isOverlapping()) {
                this.rotation = (this.rotation + 3) % 4;
            } else {
                this.update();
            }
        }

        public void slide( int dx )
        {
            this.centerX += dx;
            if (isOverlapping()) {
                this.centerX -= dx;
            }
            update();
        }

        public void drop()
        {
            int dropped = 0;
            while (!down()) {
                dropped += 1;
            }
            Tetra.this.score += dropped;
            update();
        }

        public boolean down()
        {
            this.centerY -= 1;
            if (isOverlapping()) {
                this.centerY += 1;
                update();
                return true;
            } else {
                update();
                return false;
            }
        }

        public void fix()
        {
            for (int i = 0; i < PIECES; i++) {
                Point point = this.places[this.rotation][i];
                int x = point.x + this.centerX;
                int y = point.y + this.centerY;
                Tetra.this.grid[x][y] = this.actors[i];
            }
            clearLines();
        }

        public boolean isOverlapping()
        {
            for (int i = 0; i < PIECES; i++) {
                Point point = this.places[this.rotation][i];
                int x = point.x + this.centerX;
                int y = point.y + this.centerY;
                if (Tetra.this.grid[x][y] != null) {
                    return true;
                }
            }
            return false;
        }

        public void update()
        {
            for (int i = 0; i < PIECES; i++) {
                Actor actor = this.actors[i];
                Point point = this.places[this.rotation][i];
                actor.moveTo(LEFT + (this.centerX + point.x) * SCALE, BOTTOM +
                    (this.centerY + point.y) * SCALE);
            }
        }

    }

    public static void main( String argv[] ) throws Exception
    {
        Launcher.main(new String[] { "tetra" });
    }

}
