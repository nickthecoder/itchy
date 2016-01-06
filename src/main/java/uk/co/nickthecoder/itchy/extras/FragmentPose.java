package uk.co.nickthecoder.itchy.extras;

import java.awt.Point;
import java.util.LinkedList;
import java.util.Random;

import uk.co.nickthecoder.itchy.DynamicPoseResource;
import uk.co.nickthecoder.itchy.ImagePose;
import uk.co.nickthecoder.itchy.Pose;
import uk.co.nickthecoder.itchy.PoseResource;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.jame.Surface;

/**
 * Breaks an image into a set of fragments, if you glued all of the fragments together, you
 * would end up with the original image.
 * 
 * Typically used to explode an image into many pieces.
 * 
 * Each of the fragments are one clump of pixels, however, this clump will include transparent pixels
 * of the source image, and therefore one clump can appear to be two pieces.
 * 
 * Each fragment has its origin at its middle, and this will NOT be the same as the source image's offset.
 * The allows the fragments to rotate naturally.
 * 
 * TODO Allow for the fragments' offsets to be the same as the source's, so that the explosion can start off
 * looking like the original image.
 * Maybe use an Animation to change their offsets???
 */
public class FragmentPose
{
    /**
     * Added to the end of the source Pose's name.
     */
    public String suffix = "-fragment-";

    /**
     * The number of pieces to fragment the pose into.
     */
    public int pieceCount = 5;

    /**
     * Set to zero, for different results each time.
     * Using a fixed random seed will give identical shaped fragments every time.
     */
    public long randomSeed = 0l;

    /**
     * The source surface
     */
    private Surface source;

    /**
     * Which piece number (0..pieceCount-1) owns the given pixel. -1 for no owner yet.
     * Starts out as all -1
     */
    private int[][] owner;

    private Piece[] pieces;

    /**
     * The number of pixels yet to assign to one of the Pieces.
     */
    private long toGo;

    private Random random;
    
    public void filter(Resources resources, PoseResource poseResource)
    {
        Pose pose = poseResource.pose;
        source = pose.getSurface();

        pieces = new Piece[this.pieceCount];
        toGo = source.getWidth() * source.getHeight();
        owner = new int[source.getWidth()][source.getHeight()];

        random = new Random();
        if (this.randomSeed != 0) {
            random.setSeed(randomSeed);
        }

        for (int x = 0; x < source.getWidth(); x++) {
            for (int y = 0; y < source.getHeight(); y++) {
                owner[x][y] = -1;
            }
        }

        for (int i = 0; i < this.pieceCount; i++) {
            int x = random.nextInt(source.getWidth());
            int y = random.nextInt(source.getHeight());
            pieces[i] = new Piece(i);
            setOwner(x, y, i);
        }

        while (toGo > 0) {
            int i = random.nextInt(this.pieceCount);
            pieces[i].grow();
        }

        for (int i = 0; i < this.pieceCount; i++) {
            ImagePose newPose = new ImagePose(pieces[i].crop());

            newPose.setDirection(pose.getDirection());
            newPose.setOffsetX(newPose.getSurface().getWidth() / 2);
            newPose.setOffsetY(newPose.getSurface().getHeight() / 2);

            String name = poseResource.getName() + this.suffix + i + 1;
            PoseResource newPoseResource = new DynamicPoseResource(resources, name, newPose);
            resources.addPose(newPoseResource);
        }

        // Allow the garbage collector to free resources now that we are finished.
        pieces = null;
        source = null;
        owner = null;
    }

    private void setOwner(int x, int y, int owner)
    {
        if (this.owner[x][y] != -1) {
            return;
        }
        this.owner[x][y] = owner;
        this.toGo--;
        this.pieces[owner].setOwner(x, y);
    }

    /**
     * Inner class Piece, is one fragment of the larger image. Each piece has its own surface which is the same size as
     * the original image. This surface starts off completely transparent, and then one pixel is chosen as the seed
     * point for this fragment. The seed pixel is set to the same as the original image. As the piece grows, more pixels
     * are set until every pixel on the origianal image has been copied to one of the pieces.
     */
    class Piece
    {
        int owner;
        LinkedList<Point> edges;
        int pixelCount;
        Surface surface;
        int minX;
        int maxX;
        int minY;
        int maxY;

        Piece(int index)
        {
            this.edges = new LinkedList<Point>();
            this.owner = index;
            int width = FragmentPose.this.source.getWidth();
            int height = FragmentPose.this.source.getHeight();
            surface = new Surface(width, height, true);
            minX = width - 1;
            maxX = 0;
            minY = height - 1;
            maxY = 0;
        }

        void setOwner(int x, int y)
        {
            this.surface.setPixel(x, y, FragmentPose.this.source.getPixelColor(x, y));
            addEdge(x + 1, y);
            addEdge(x - 1, y);
            addEdge(x, y - 1);
            addEdge(x, y + 1);

            if (x < minX) {
                minX = x;
            }
            if (x > maxX) {
                maxX = x;
            }
            if (y < minY) {
                minY = y;
            }
            if (y > maxY) {
                maxY = y;
            }
        }

        private void addEdge(int x, int y)
        {
            if ((x >= 0) && (x < FragmentPose.this.source.getWidth()) && (y >= 0)
                            && (y < FragmentPose.this.source.getHeight())) {
                this.edges.add(new Point(x, y));
            }
        }

        private void grow()
        {
            if (this.edges.size() > 0) {
                int i = FragmentPose.this.random.nextInt(this.edges.size());
                Point point = this.edges.get(i);
                this.edges.remove(i);
                FragmentPose.this.setOwner(point.x, point.y, this.owner);
            }
        }

        public Surface crop()
        {
            Surface result = new Surface(maxX - minX, maxY - minY, true);
            surface.blit(result, -minX, -minY);
            return result;
        }
    }
}
