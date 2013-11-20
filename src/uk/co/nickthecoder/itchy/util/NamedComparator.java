/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.util;

import java.util.Comparator;

public class NamedComparator implements Comparator<Named>
{

    @Override
    public int compare( Named o1, Named o2 )
    {
        return o1.getName().compareTo(o2.getName());
    }


}
