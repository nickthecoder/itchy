/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.extras;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Costume;
import uk.co.nickthecoder.itchy.DynamicPoseResource;
import uk.co.nickthecoder.itchy.ImagePose;
import uk.co.nickthecoder.itchy.Pose;
import uk.co.nickthecoder.itchy.PoseResource;
import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Surface;

/**
 * Fragments a pose in lots of random shaped pieces. The pieces can be added to a costume using a single event name. The
 * recommended name is "fragment", but you can choose any name you like.
 * 
 * The fragments are only created once, so if you call fragment twice, with the same pose name, then you will still only
 * have the fragments from the first call.
 * 
 * This algorithm is inefficient (in memory, and speed), so ideally don't use it while the game is playing.
 * 
 * This class is often used in conjunction with Explosion, to produce an explosion with the pieces of the actor flying
 * apart in different directions.
 * 
 * Uses a "Fluent" interface, so many methods return "this".
 */
public class Fragments
{
    protected int pieceCount = 10;

    protected Random random;

    protected Fragment[] fragments;

    protected double direction;

    protected FragmentMethod fragmentMethod = new RandomFragments();
    
    protected int index;

    // The following attributes are used during the fragmentation process only.

    protected int[][] owner;

    protected Pose sourcePose;

    protected Surface source;

    protected PieceInProgress[] piecesInProgress;

    protected int toGo;

    
    public Fragments()
    {
        this.random = new Random();
    }

    public int getPieceCount()
    {
        return this.pieceCount;
    }

    public Fragments pieces(int value)
    {
        this.pieceCount = value;
        return this;
    }

    public Fragments randomSeed(int seed)
    {
        this.random.setSeed(seed);
        return this;
    }

    public Fragments method( FragmentMethod fragmentMethod )
    {
        this.fragmentMethod = fragmentMethod;
        return this;
    }
    
    public Fragments create(Costume costume)
    {
        return create( costume.getPose( "default" ) );
    }
    
    public Fragments create(Pose pose)
    {
        this.direction = pose.getDirection();
        this.sourcePose = pose;
        this.source = pose.getSurface();
        
        this.piecesInProgress = new PieceInProgress[this.pieceCount];
        this.toGo = this.source.getWidth() * this.source.getHeight();
        this.owner = new int[this.source.getWidth()][this.source.getHeight()];

        for (int x = 0; x < this.source.getWidth(); x++) {
            for (int y = 0; y < this.source.getHeight(); y++) {
                this.owner[x][y] = -1;
            }
        }

        this.fragmentMethod.create(this);
        
        return this;
    }

    /**
     * A convenience method, uses the actor's current pose, and adds the fragments to the actor's costume with an event
     * name of "fragment".
     */
    public Fragments createPoses(Actor actor)
    {
        return createPoses(actor, "fragment");
    }

    /**
     * Fragments the actor's current pose, and adds the result to the actors costume. If the costume already had one or
     * more poses with that name, then nothing is done.
     * 
     * @return this (Fluent API)
     */
    public Fragments createPoses(Actor actor, String eventName)
    {
        if (actor.getCostume().getPose(eventName) == null) {
            create(actor.getAppearance().getPose()).addToCostume(actor.getCostume(), eventName);
        }
        return this;
    }
    
    public RGBA getRGBA( int x, int y )
    {
        return this.source.getPixelRGBA(x,  y);
    }
    
    public RGBA getRGBA( Point point )
    {
        return this.source.getPixelRGBA(point.x,  point.y);
    }

    public Fragments addToCostume(Costume costume)
    {
        return addToCostume(costume, "fragment");
    }

    public Fragments addToCostume(Costume costume, String eventName)
    {
        for (int i = 0; i < this.pieceCount; i++) {
            PoseResource pr = new DynamicPoseResource(eventName, fragments[i].pose);
            costume.addPose(eventName, pr);
        }
        return this;
    }

    protected boolean setOwner(int x, int y, int owner)
    {
        if (this.owner[x][y] != -1) {
            return false;
        }
        this.owner[x][y] = owner;
        this.toGo--;
        return true;
    }

    protected int findPointType = 0;
    protected List<Point> unownedPoints;
    
    protected Point findUnownedPoint()
    {
        if ( this.findPointType == 0 ) {
            // We may pick a pixel that is already owned, if so, lets try 10 times, and then give up.
            for (int loop = 0; loop < 10; loop++) {
                int x = this.random.nextInt(this.source.getWidth());
                int y = this.random.nextInt(this.source.getHeight());
    
                if (this.owner[x][y] == -1) {
                    return new Point(x, y );
                }
            }
            // There's probably few places let, so lets put all unowned points in a list, and pick one at random.
            this.findPointType ++;
            unownedPoints = new LinkedList<Point>();
            for ( int x = 0; x < this.source.getWidth(); x ++ ) {
                for ( int y = 0; y < this.source.getHeight(); y ++ ) {
                    if (this.owner[x][y] == -1) {
                        unownedPoints.add(new Point(x, y));
                    }
                }
            }
        }
        
        if (this.unownedPoints.size() == 0) {
            throw new RuntimeException( "All points have been used" );
        }
        
        int index = this.random.nextInt(this.unownedPoints.size());
        Point point = this.unownedPoints.get(index);
        this.unownedPoints.remove(index);
        
        return point;
    }
    
    protected void createFragments( int count )
    {
        
        this.fragments = new Fragment[count];
    
        for (int i = 0; i < count; i++) {
            ImagePose newPose = new ImagePose(this.piecesInProgress[i].surface, sourcePose.getOffsetX(), sourcePose.getOffsetY());
    
            newPose.setDirection(sourcePose.getDirection());
    
            this.fragments[i] = this.piecesInProgress[i].createPiece();
    
        }
    
        // Allow objects to be freed, as they are no longer needed.
        this.findPointType = 0;
        this.unownedPoints = null;
        this.piecesInProgress = null;
        this.owner = null;
        this.sourcePose = null;
        this.source = null;
    }


    public void useFragment(Actor actor)
    {
        if (this.index >= this.fragments.length) {
            this.index = 0;
        }
        Fragment piece = this.fragments[this.index];
        this.index += 1;

        double scale = actor.getAppearance().getScale();

        actor.getAppearance().setPose(piece.pose);
        // Careful to account for source poses with a non-zero direction!
        actor.setHeading(actor.getDirection() - this.direction);                
        actor.moveForwards(-scale * piece.dx, scale * piece.dy);
    }

    /**
     * One piece after fragmentation.
     */
    static class Fragment
    {
        public ImagePose pose;
        /**
         * The position of the piece relative to the whole, unfragmented pose. i.e. The amount to add to the fragmented
         * actor to have it superimpose over the original.
         */
        public int dx;
        public int dy;
        public double direction;

        public Fragment(ImagePose pose, int dx, int dy)
        {
            this.pose = pose;
            this.dx = dx;
            this.dy = dy;
        }
    }
    public PieceInProgress createPieceInProgress( int index )
    {
        this.piecesInProgress[index] = new PieceInProgress( index );
        return this.piecesInProgress[index];
    }
    
    /**
     * Inner class PieceInProgress, is one fragment of the larger image. Each piece has its own surface which is the
     * same size as the original image. This surface starts off completely transparent, and then one pixel is chosen as
     * the seed point for this fragment. The seed pixel is set to the same as the original image. As the piece grows,
     * more pixels are set until every pixel on the original image has been copied to one of the pieces.
     */
    class PieceInProgress
    {
        int owner;
        LinkedList<Point> edges;
        int pixelCount;
        Surface surface;

        int maxX = 0;
        int minX = Fragments.this.source.getWidth();
        int maxY = 0;
        int minY = Fragments.this.source.getHeight();

        public PieceInProgress(int index)
        {
            this.edges = new LinkedList<Point>();
            this.owner = index;
            this.surface = new Surface(Fragments.this.source.getWidth(), Fragments.this.source.getHeight(), true);
        }

        void setOwner(int x, int y)
        {
            this.surface.setPixel(x, y, Fragments.this.source.getPixelColor(x, y));
            addEdge(x + 1, y);
            addEdge(x - 1, y);
            addEdge(x, y - 1);
            addEdge(x, y + 1);

            if (x < minX)
                minX = x;
            if (y < minY)
                minY = y;
            if (x > maxX)
                maxX = x;
            if (y > maxY)
                maxY = y;
        }

        private void addEdge(int x, int y)
        {
            if ((x >= 0) && (x < Fragments.this.source.getWidth()) && (y >= 0)
                            && (y < Fragments.this.source.getHeight())) {
                if (Fragments.this.owner[x][y] == -1) {
                    this.edges.add(new Point(x, y));
                }
            }
        }
        
        protected void ignoreEdge( int i )
        {
            Point point = this.edges.get(i);
            this.edges.remove(i);
            Fragments.this.setOwner(point.x,  point.y,  pieceCount);
        }
        
        protected void useEdge( int i )
        {
            Point point = this.edges.get(i);
            this.edges.remove(i);
            if (Fragments.this.setOwner(point.x,  point.y,  this.owner)) {
                this.setOwner(point.x, point.y);
            }
        }
            

        public Fragment createPiece()
        {
            int width = maxX - minX;
            int height = maxY - minY;

            int dx = Fragments.this.sourcePose.getOffsetX() - minX - width / 2;
            int dy = Fragments.this.sourcePose.getOffsetY() - minY - height / 2;

            // Clip to just the pixels used by this piece.
            Surface surface = new Surface(width, height, true);
            this.surface.blit(surface, -minX, -minY, Surface.BlendMode.COMPOSITE);

            ImagePose pose = new ImagePose(surface);
            pose.setOffsetX(width / 2);
            pose.setOffsetY(height / 2);
            pose.setDirection(Fragments.this.sourcePose.getDirection());
            Fragment piece = new Fragment(pose, dx, dy);

            return piece;
        }
    }

}
