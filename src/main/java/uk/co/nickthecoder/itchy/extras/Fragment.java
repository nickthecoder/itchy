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
import uk.co.nickthecoder.itchy.DynamicPoseResource;
import uk.co.nickthecoder.itchy.ImagePose;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Pose;
import uk.co.nickthecoder.itchy.PoseResource;
import uk.co.nickthecoder.jame.Surface;

/**
 * Fragments a pose in lots of random shaped pieces.
 * The pieces can be added to a costume using a single event name.
 * The recommended name is "fragment", but you can choose any name you like.
 * 
 * The fragments are only created once, so if you call fragment twice, with the same pose name,
 * then you will still only have the fragments from the first call.
 * 
 * This algorithm is inefficient (in memory, and speed), so ideally don't use it while the game is playing.
 * 
 * This class is often used in conjunction with Explosion, to produce an explosion with the pieces of
 * the actor flying apart in different directions.
 * 
 * Uses a "Fluent" interface, so many methods return "this".
 */
public class Fragment
{
    private int pieceCount = 10;

    private Random random;

    private ImagePose[] results;
    
    // The following attributes are used during the fragmentation process only.
    
    private int[][] owner;

    private Surface source;

    private Piece[] pieces;

    private int toGo;

    
    public Fragment()
    {
        this.random = new Random();
    }

    public Fragment pieces( int value )
    {
        this.pieceCount = value;
        return this;
    }

    public Fragment randomSeed( int seed )
    {
        this.random.setSeed(seed);
        return this;
    }

    public Fragment fragment( Pose pose )
    {
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

        this.results = new ImagePose[this.pieceCount];
        
        for (int i = 0; i < this.pieceCount; i++) {
            ImagePose newPose = new ImagePose(
                this.pieces[i].surface,
                pose.getOffsetX(),
                pose.getOffsetY());

            newPose.setDirection(pose.getDirection());
            
            this.results[i] = newPose;
        }
        
        return this;
    }

    /**
     * A convenience method, uses the actor's current pose, and adds the fragments to the actor's costume
     * with an event name of "fragment".
     */
    public Fragment createPoses( Actor actor )
    {
        return createPoses( actor, "fragment" );
    }
    
    public Fragment createPoses( Actor actor, String eventName )
    {
        return fragment( actor.getAppearance().getPose()).addToCostume(actor.getCostume(), eventName );
    }
    
    public Fragment addToCostume( Costume costume )
    {
        return addToCostume( costume, "fragment" );
    }
    
    public Fragment addToCostume( Costume costume, String eventName )
    {
        for ( int i = 0; i < this.pieceCount; i ++ ) {
            PoseResource pr = new DynamicPoseResource(Itchy.getGame().resources, eventName, results[i]);
            costume.addPose( eventName, pr );
        }
        return this;
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
