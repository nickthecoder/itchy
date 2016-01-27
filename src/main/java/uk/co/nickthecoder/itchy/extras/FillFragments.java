package uk.co.nickthecoder.itchy.extras;

import java.awt.Point;

import uk.co.nickthecoder.itchy.extras.Fragments.PieceInProgress;
import uk.co.nickthecoder.jame.RGBA;

/**
 * Flood fills the source image, stopping at boundaries, where the pixel is fully transparent.
 * This is only useful when a pose is not contiguous. e.g. a lower case "i" can be split into
 * two fragments, but an upper case "I" is not suitable, because all the pixels are contiguous.
 */
public class FillFragments implements FragmentMethod
{

    @Override
    public void create(Fragments fragments)
    {

        int piecesCreated = 0;
        while ((piecesCreated < fragments.pieceCount) && (fragments.toGo > 0)) {

            Point start = fragments.findUnownedPoint();
            RGBA rgba = fragments.getRGBA(start);
            if ( rgba.a == 0) {
                fragments.setOwner(start.x, start.y, fragments.pieceCount);
            } else {
                PieceInProgress pip = fragments.createPieceInProgress(piecesCreated);
                fragments.setOwner(start.x, start.y, piecesCreated);

                while ((fragments.toGo > 0) && (pip.edges.size() > 0) ) {
                    Point point = pip.edges.get(0);
                    RGBA color = fragments.getRGBA(point);
                    if (includeColor( color )) {
                        pip.useEdge(0);                        
                    } else if (ignoreColor(color)) {
                        pip.ignoreEdge(0);
                    } else {
                        fragments.setOwner(point.x,  point.y,  fragments.pieceCount);
                    }
                }
                piecesCreated ++;
            }            
        }

        fragments.createFragments( piecesCreated );
    }

    protected void startPoint( Point point )
    {
    }
        
    /**
     * Is this color OK to continue the fill process. The default behaviour is to stop only at transparent pixels.
     */
    public boolean includeColor( RGBA rgba )
    {
        return rgba.a > 0;
    }

    
    /**
     * Should this color be ignored, and therefore not be used by any of the fragments.
     */
    public boolean ignoreColor( RGBA rgba )
    {
        return rgba.a == 0;
    }
}
