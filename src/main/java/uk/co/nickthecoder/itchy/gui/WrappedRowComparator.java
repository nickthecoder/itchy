/*******************************************************************************
 * Copyright (c) 2014 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import java.util.Comparator;

public class WrappedRowComparator implements Comparator<TableModelRow>
{
    private final int columnIndex;

    private Comparator<Object> comparator;

    public WrappedRowComparator( int columnIndex, Comparator<Object> comparator )
    {
        this.columnIndex = columnIndex;
        this.comparator = comparator;
    }

    @Override
    public int compare( TableModelRow a, TableModelRow b )
    {
        Object objA = a.getData(this.columnIndex);
        Object objB = b.getData(this.columnIndex);

        if (objA == null) {
            if (objB == null) {
                return 0;
            } else {
                return 1;
            }
        } else {
            if (objB == null) {
                return -1;
            }
            return this.comparator.compare(objA, objB);
        }
    }

}
