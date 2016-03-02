/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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

    
    public void remove( String tag, M member )
    {
        Set<M> result = this.membersByTag.get(tag);
        if (result != null) {
            result.remove(member);
        }
    }
    
}
