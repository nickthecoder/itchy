/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class TagCollection<M>
{
    private HashMap<String, Set<M>> membersByTag;

    public TagCollection()
    {
        this.membersByTag = new HashMap<String, Set<M>>();
    }

    public void add( String tag, M member )
    {
        Set<M> members = this.membersByTag.get(tag);
        if (members == null) {
            members = new HashSet<M>();
            this.membersByTag.put(tag, members);
        }
        members.add(member);
    }

    public Set<M> getTagMembers( String tag )
    {
        Set<M> result = this.membersByTag.get(tag);
        if (result == null) {
            return Collections.<M> emptySet();
        }
        return new HashSet<M>(result);
    }

    /**
     * Iterate over all members that have any of the tags listed. Each member should be included only once.
     * @param tags
     * @return
     */
    public Iterator<M> iterateTagMembers( String... tags )
    {
        if (tags.length == 0) {
            return new NullIterator<M>();
        } else if (tags.length == 1) {
            return getTagMembers( tags[0] ).iterator();
        } else {
            CompoundIterator<M> result = new CompoundIterator<M>();
            result.add( getTagMembers( tags[0] ).iterator() );
            
            for ( int i = 1; i < tags.length; i ++ ) {
                Iterator<M> oneTag = getTagMembers( tags[i] ).iterator();
                // We cannot just join them together, because we could get duplicates, so we need to filter out
                // any members that have any tags earlier in the list.
                Filter<M> removeDuplicates = removeDuplicatesFilter( tags, i-1 );
                
                result.add( new FilteredIterator<M>( oneTag, removeDuplicates ) );
            }
            return result;
        }
    }
    
    private Filter<M> removeDuplicatesFilter( String[] tags, int n )
    {
        if ( n == 0 ) {
            return new WithoutTagFilter<M>( tags[0] );
        } else {
            CompoundFilter<M> result = new AndFilter<M>();
            for ( int i = 0; i < n; i ++ ) {
                result.add( new WithoutTagFilter<M>( tags[ i ] ) );
            }
            return result;
        }
    }
    
    public void remove( String tag, M member )
    {
        Set<M> result = this.membersByTag.get(tag);
        if (result != null) {
            result.remove(member);
        }
    }
    
    class WithoutTagFilter<N> implements Filter<N>
    {
        private Set<M> members;
        
        public WithoutTagFilter( String tag )
        {
            this.members = membersByTag.get(tag);
        }
        
        @Override
        public boolean accept(N subject)
        {
            return ! members.contains(subject);
        }
        
    }
    
}
