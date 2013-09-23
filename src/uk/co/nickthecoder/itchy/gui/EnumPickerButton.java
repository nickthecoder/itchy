/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import java.util.HashMap;

public class EnumPickerButton<E extends Enum<?>> extends PickerButton<E>
{
    
    public static <F extends Enum<?>> HashMap<String,F> createHashMap(Class<F> c)
    {
        HashMap<String,F> result = new HashMap<String,F>();
        
        for (F o: c.getEnumConstants()) {
            result.put(o.name(), o);
        }
        
        return result;
    }
    
    @SuppressWarnings("unchecked")
    public EnumPickerButton( String title, E current )
    {
        super(title, current, (HashMap<String,E>) createHashMap(current.getClass()));
    }

}
