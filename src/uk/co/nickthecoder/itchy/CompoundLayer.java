package uk.co.nickthecoder.itchy;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;

public class CompoundLayer extends Layer
{
    private final LinkedList<Layer> children;
    
    public CompoundLayer( String name, Rect positionOnScreen )
    {
        super(name, positionOnScreen);
        this.children = new LinkedList<Layer>();
    }

    @Override
    public void render2( Rect clip, Surface destSurface )
    {

        for (Iterator<Layer> i = this.children.iterator(); i.hasNext();) {
            Layer child = i.next();
            if (child.isVisible()) {

                child.render(clip, destSurface);

            }
            if (child.isRemovePending()) {
                i.remove();
            }
        }
    }

    public void add( Layer layer )
    {
        this.children.add(layer);
        layer.parent = this;
    }

    public void remove( Layer layer)
    {
        this.children.remove(layer);
    }
    
    public List<Layer> getChildren()
    {
        return Collections.unmodifiableList(this.children);
    }
    
    @Override
    public void clear()
    {
        for (Layer child : this.children) {
            child.clear();
        }
    }

    @Override
    public void destroy()
    {
        while (this.children.size() > 0) {
            this.children.get(0).destroy();
            this.children.remove(0);
        }
        this.clear();
    }

    @Override
    public String toString()
    {
        StringBuffer result = new StringBuffer();
        result.append(super.toString()).append("\n");
        
        for ( Layer layer : this.children ) {
            result.append("Child : " ).append( layer.toString() ).append("\n");
        }
        
        return result.toString();
    }
    
}
