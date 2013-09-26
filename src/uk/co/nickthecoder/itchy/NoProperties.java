/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.lang.reflect.Constructor;

public class NoProperties
{
    public static Object createProperties( String className )
    {
        try {
            Class<?> klass = Class.forName(className);
            Constructor<?> constructor = klass.getConstructor();
            return constructor.newInstance();
        } catch (Exception e) {
            return new NoProperties();
        }
    }

    public static boolean isValidClassName( String name )
    {
        try {
            Class.forName(name);
            return true;

        } catch (Exception e) {
            return false;
        }
    }
}
