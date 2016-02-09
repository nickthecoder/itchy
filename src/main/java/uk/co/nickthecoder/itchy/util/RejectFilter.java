package uk.co.nickthecoder.itchy.util;

/**
 * Does not accept anything.
 */
public class RejectFilter<M> implements Filter<M>
{
    /**
     * @return false;
     */
    @Override
    public boolean accept(M subject)
    {
        return false;
    }
}
