/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class TagMembership<M>
{
    private TagCollection<M> tagCollection;

    private Set<String> tags;

    private M member;

    public TagMembership( TagCollection<M> tagCollection, M member )
    {
        this.tagCollection = tagCollection;
        this.member = member;
        this.tags = new HashSet<String>();
    }

    public M getMember()
    {
        return this.member;
    }

    public boolean hasTag( String tag )
    {
        return this.tags.contains(tag);
    }

    public void add( String tag )
    {
        this.tags.add(tag);
        this.tagCollection.add(tag, this.member);
    }

    public void remove( String tag )
    {
        this.tags.remove(tag);
        this.tagCollection.remove(tag, this.member);
    }

    public void removeAll()
    {
        for (Iterator<String> i = this.tags.iterator(); i.hasNext();) {
            String tag = i.next();
            i.remove();
            this.tagCollection.remove(tag, this.member);
        }
    }

    public void removeAllExcept( String except )
    {
        for (Iterator<String> i = this.tags.iterator(); i.hasNext();) {
            String tag = i.next();
            if (!except.equals(tag)) {
                i.remove();
                this.tagCollection.remove(tag, this.member);
            }
        }
    }

}
