/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.util;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Reversed<T> implements Iterable<T>
{
    private final List<T> original;

    public Reversed( List<T> original )
    {
        this.original = original;
    }

    @Override
    public Iterator<T> iterator()
    {
        final ListIterator<T> i = this.original.listIterator(this.original.size());

        return new Iterator<T>() {
            @Override
            public boolean hasNext()
            {
                return i.hasPrevious();
            }

            @Override
            public T next()
            {
                return i.previous();
            }

            @Override
            public void remove()
            {
                i.remove();
            }
        };
    }

    public static <S> Reversed<S> list( List<S> original )
    {
        return new Reversed<S>(original);
    }
}
