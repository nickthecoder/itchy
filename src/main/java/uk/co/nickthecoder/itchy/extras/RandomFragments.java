package uk.co.nickthecoder.itchy.extras;

import java.awt.Point;

import uk.co.nickthecoder.itchy.extras.Fragments.PieceInProgress;

public class RandomFragments implements FragmentMethod
{
    @Override
    public void create(Fragments fragments)
    {
        for ( int i = 0; i < fragments.pieceCount; i ++ ) {
            Point point = fragments.findUnownedPoint();
            
            PieceInProgress pip = fragments.createPieceInProgress(i);
            fragments.setOwner(point.x, point.y, i);
            pip.setOwner(point.x, point.y);
        }

        while (fragments.toGo > 0) {

            int i = fragments.random.nextInt(fragments.pieceCount);

            if (fragments.piecesInProgress[i].edges.size() > 0) {
                int n = fragments.random.nextInt(fragments.piecesInProgress[i].edges.size());
                fragments.piecesInProgress[i].useEdge( n );
            }
        }

        fragments.createFragments( fragments.pieceCount );
        
    }

}
