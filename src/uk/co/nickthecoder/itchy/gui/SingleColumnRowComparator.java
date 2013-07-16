/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import java.util.Comparator;

public class SingleColumnRowComparator<T> implements Comparator<TableModelRow>
{
    private final int columnIndex;

    public SingleColumnRowComparator( int columnIndex )
    {
        this.columnIndex = columnIndex;
    }

    @Override
    public int compare( TableModelRow a, TableModelRow b )
    {
        @SuppressWarnings("unchecked")
        Comparable<T> ca = (Comparable<T>) a.getData(this.columnIndex);

        @SuppressWarnings("unchecked")
        T cb = (T) b.getData(this.columnIndex);

        if (ca == null) {
            if (cb == null) {
                return 0;
            } else {
                return 1;
            }
        } else {
            if (cb == null) {
                return -1;
            }
            return ca.compareTo(cb);
        }
    }

}
