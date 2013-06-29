package uk.co.nickthecoder.itchy;

import java.util.Iterator;
import java.util.LinkedList;

import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;

public class CompoundLayer extends Layer
{
    private final LinkedList<Layer> children;

    public CompoundLayer( Rect positionOnScreen )
    {
        super( positionOnScreen, false );
        this.children = new LinkedList<Layer>();
    }

    @Override
    public void render2( Rect clip, Surface destSurface )
    {

        for ( Iterator<Layer> i = this.children.iterator(); i.hasNext(); ) {
            Layer child = i.next();
            if ( child.isVisible() ) {

                child.render( clip, destSurface );

            }
            if ( child.isRemovePending() ) {
                i.remove();
            }
        }
    }

    public void add( Layer layer )
    {
        this.children.add( layer );
        layer.parent = this;
    }

    @Override
    public void clear()
    {
        for ( Layer child : this.children ) {
            child.clear();
        }
    }

    @Override
    public void destroy()
    {
        while ( this.children.size() > 0 ) {
            this.children.get(0).destroy();
            this.children.remove( 0 );
        }
        this.clear();
    }

}
