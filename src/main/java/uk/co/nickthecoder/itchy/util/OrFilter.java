package uk.co.nickthecoder.itchy.util;

/**
 * Accepts if any of the collection of filters accepts.
 */
public class OrFilter<M> extends CompoundFilter<M>
{
    public OrFilter()
    {
        super();
    }
    
    @SafeVarargs
    public OrFilter( Filter<M>... filters )
    {
        super( filters );
    }
    
    @Override
    public boolean accept(M subject)
    {
        for ( Filter<M> filter : filters ) {
            if ( filter.accept( subject ) ) {
                return true;
            }
        }
        return false;
    }
}
