package uk.co.nickthecoder.itchy.util;

/**
 * Accepts only if all of the collection of filters accept.
 */
public class AndFilter<M> extends CompoundFilter<M>
{
    @Override
    public boolean accept(M subject)
    {
        for ( Filter<M> filter : filters ) {
            if ( ! filter.accept( subject ) ) {
                return false;
            }
        }
        return true;
    }
}
