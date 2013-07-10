package uk.co.nickthecoder.itchy.util;

import java.awt.Point;
import java.util.LinkedList;
import java.util.Random;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Costume;
import uk.co.nickthecoder.itchy.ImagePose;
import uk.co.nickthecoder.itchy.Pose;
import uk.co.nickthecoder.itchy.PoseResource;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;

public class Fragment
{
    private Actor actor;

    private String poseName = "default";

    private int[][] owner;

    private Surface source;

    private int pieceCount = 10;

    private Random random;

    private Piece[] pieces;

    private int toGo;

    public Fragment()
    {
        this.random = new Random();
    }

    public Fragment actor( Actor actor )
    {
        this.actor = actor;
        return this;
    }

    public Fragment pose( String poseName )
    {
        this.poseName = poseName;
        return this;
    }

    public Fragment pieces( int value )
    {
        this.pieceCount = value;
        return this;
    }

    public void create( String destPose )
    {
        fragment(this.actor.getCostume(), this.poseName, destPose);
    }

    public void fragment( Costume costume, String srcPose, String destPose )
    {
        if (costume.getPose(destPose) != null) {
            return;
        }

        Pose pose = costume.getPose(srcPose);
        this.source = pose.getSurface();

        this.pieces = new Piece[this.pieceCount];
        this.toGo = this.source.getWidth() * this.source.getHeight();
        this.owner = new int[this.source.getWidth()][this.source.getHeight()];

        for (int x = 0; x < this.source.getWidth(); x++) {
            for (int y = 0; y < this.source.getHeight(); y++) {
                this.owner[x][y] = -1;
            }
        }

        for (int i = 0; i < this.pieceCount; i++) {
            int x = this.random.nextInt(this.source.getWidth());
            int y = this.random.nextInt(this.source.getHeight());
            this.pieces[i] = new Piece(i);
            setOwner(x, y, i);
        }

        while (this.toGo > 0) {
            int i = this.random.nextInt(this.pieceCount);
            this.pieces[i].grow();
        }

        for (int i = 0; i < this.pieceCount; i++) {
            ImagePose newPose = new ImagePose(
                this.pieces[i].surface,
                pose.getOffsetX(),
                pose.getOffsetY());
            
            newPose.setDirection(pose.getDirection());
            costume.addPose(destPose, new PoseResource(destPose, newPose));
        }
    }

    private void setOwner( int x, int y, int owner )
    {
        if (this.owner[x][y] != -1) {
            return;
        }
        this.owner[x][y] = owner;
        this.toGo--;
        this.pieces[owner].setOwner(x, y);
    }

    public class Piece
    {
        int owner;
        LinkedList<Point> edges;
        int pixelCount;
        Surface surface = new Surface(Fragment.this.source.getWidth(),
            Fragment.this.source.getHeight(), true);

        Piece( int index )
        {
            this.edges = new LinkedList<Point>();
            this.owner = index;
        }

        void setOwner( int x, int y )
        {
            System.out.println("Fragment setOwner " + x + "," + y);
            this.surface.fill(new Rect(x, y, 1, 1), Fragment.this.source.getPixelColor(x, y));
            addEdge(x + 1, y);
            addEdge(x - 1, y);
            addEdge(x, y - 1);
            addEdge(x, y + 1);
        }

        private void addEdge( int x, int y )
        {
            if ((x >= 0) &&
                (x < Fragment.this.source.getWidth()) &&
                (y >= 0) &&
                (y < Fragment.this.source.getHeight())) {
                this.edges.add(new Point(x, y));
            }
        }

        private void grow()
        {
            if (this.edges.size() > 0) {
                int i = Fragment.this.random.nextInt(this.edges.size());
                Point point = this.edges.get(i);
                this.edges.remove(i);
                Fragment.this.setOwner(point.x, point.y, this.owner);
            }
        }
    }

}
