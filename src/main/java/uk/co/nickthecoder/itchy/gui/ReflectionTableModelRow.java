/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import uk.co.nickthecoder.itchy.util.BeanHelper;

public class ReflectionTableModelRow<T> implements TableModelRow
{
    private final T data;

    String[] attributeNames;

    public ReflectionTableModelRow( T object, String[] attributeNames )
    {
        this.data = object;
        this.attributeNames = attributeNames;
    }

    public T getData()
    {
        return this.data;
    }

    @Override
    public Object getData( int index )
    {
        String attributeName = this.attributeNames[index];
        if (attributeName == null) {
            return data;
        }
        try {
            Object result = BeanHelper.getProperty(this.data, attributeName);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
