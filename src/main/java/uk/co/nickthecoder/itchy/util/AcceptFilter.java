package uk.co.nickthecoder.itchy.util;

/**
 * Accepts everything.
 */
public class AcceptFilter<M> implements Filter<M>
{
    /**
     * @return true
     */
    @Override
    public boolean accept(M subject)
    {
        return true;
    }
}
