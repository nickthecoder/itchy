package uk.co.nickthecoder.itchy.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class CompoundFilter<M> implements Filter<M>
{
    protected List<Filter<M>> filters;
    
    public CompoundFilter()
    {
        this.filters = new ArrayList<Filter<M>>();        
    }

    @SafeVarargs
    public CompoundFilter( Filter<M>... filters )
    {
        this();
        this.filters.addAll( Arrays.asList(filters) );
    }
    
    /**
     * 
     * @param filter
     * @return this
     */
    public CompoundFilter<M> add( Filter<M> filter )
    {
        this.filters.add( filter );
        return this;
    }
    
    /**
     * 
     * @param filter
     * @return this
     */
    public CompoundFilter<M> remove( Filter<M> filter )
    {
        this.filters.remove( filter );
        return this;
    }
    
}
