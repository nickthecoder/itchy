/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.extras;

import java.awt.Point;
import java.util.LinkedList;
import java.util.Random;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Costume;
import uk.co.nickthecoder.itchy.ImagePose;
import uk.co.nickthecoder.itchy.Pose;
import uk.co.nickthecoder.itchy.PoseResource;
import uk.co.nickthecoder.jame.Surface;

/**
 * Fragments an actor's pose in lots of random shaped pieces. The pieces are added to the actor's costume with the given pose name. The
 * recommended name to use is "fragment", but you can choose any name you like.
 * 
 * The fragments are only created once, so if you call fragment twice, with the same pose name, then you will still only have the fragments
 * from the first call.
 * 
 * This algorithm is inefficient (in memory, and speed), so ideally don't use it while the game is playing. Instead, call it during a Role's
 * init method.
 * 
 * This class is often used in conjunction with ExplosionRole, to produce an explosion with the pieces of the actor flying apart in
 * different directions.
 */
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

    public void createPoses( String destPose )
    {
        fragment(this.actor.getCostume(), this.poseName, destPose);
    }

    /**
     * This is the work horse.
     * 
     * @param costume
     *        The costume used as the source of the pose, and the destination of the fragments
     * @param srcPose
     *        The name of the pose to use as the source
     * @param destPose
     *        The name of the poses which are created.
     */
    private void fragment( Costume costume, String srcPose, String destPose )
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
            costume.addPose(destPose, new PoseResource(newPose));
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

    /**
     * Inner class Piece, is one fragment of the larger image. Each piece has its own surface which is the same size as the original image.
     * This surface starts off completely transparent, and then one pixel is chosen as the seed point for this fragment. The seed pixel is
     * set to the same as the original image. As the piece grows, more pixels are set until every pixel on the origianal image has been
     * copied to one of the pieces.
     */
    class Piece
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
            this.surface.setPixel(x, y, Fragment.this.source.getPixelColor(x, y));
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
