/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.script.ScriptException;

import uk.co.nickthecoder.itchy.Itchy;

/**
 * Allows Java Bean style getting and setting of attributes, using reflection.
 * As well as the usual Java Bean get/set, this class also supports getting and setting attributes
 * from scripted languages' objects via ScriptManager's getProperty and setProperty methods.
 */
public class BeanHelper
{
    public static Object getProperty( Object subject, String attributeName )
        throws IllegalArgumentException, IllegalAccessException, InvocationTargetException,
        SecurityException, NoSuchFieldException, ScriptException
    {
        int dot = attributeName.indexOf(".");

        if (dot > 0) {

            Object subject2 = getProperty(subject, attributeName.substring(0, dot));

            return getProperty(subject2, attributeName.substring(dot + 1));

        } else {

            // Look for a method called getXXX first
            Class<?>[] argTypes = new Class<?>[] {};
            String methodName = "get" + attributeName.substring(0, 1).toUpperCase() +
                attributeName.substring(1);

            try {
                Method method = subject.getClass().getMethod(methodName, argTypes);
                Object result = method.invoke(subject);
                return result;
            } catch (NoSuchMethodException e) {
            }

            try {
                Field field = subject.getClass().getField(attributeName);
                // Field field = subject.getClass().getDeclaredField( attributeName );
                return field.get(subject);
            } catch (NoSuchFieldException e) {
                if (subject instanceof Map<?, ?>) {
                    Map<?, ?> map = (Map<?, ?>) subject;
                    return map.get(attributeName);
                }

                // It isn't a java field, or a getXXX method, or a map
                // Maybe "subject" is a SCRIPTED object, and therefore doesn't follow java's bean conventions.
                try {
                    Object result = Itchy.getGame().getScriptManager().getAttribute(subject, attributeName);
                    return result;
                } catch ( Exception e2 ) {
                    // We'll throw e, rather than e2, so do nothing here.
                }
                
                throw e;
            }
        }
    }

    public static void setProperty( Object subject, String attributeName, Object value )
        throws IllegalArgumentException, SecurityException, IllegalAccessException,
        InvocationTargetException, NoSuchFieldException, ScriptException
    {
        Class<?> klass = value == null ? null : value.getClass();

        if (klass == Double.class) {
            klass = double.class;
        } else if (klass == Integer.class) {
            klass = int.class;
        } else if (klass == Float.class) {
            klass = float.class;
        } else if (klass == Character.class) {
            klass = char.class;
        } else if (klass == Byte.class) {
            klass = byte.class;
        }

        setProperty(subject, attributeName, value, klass);
    }

    public static void setProperty( Object subject, String attributeName, Object value,
        Class<?> klass )
        throws IllegalArgumentException, IllegalAccessException,
        InvocationTargetException, SecurityException, NoSuchFieldException, ScriptException
    {
        int dot = attributeName.indexOf(".");

        if (dot > 0) {

            Object subject2 = getProperty(subject, attributeName.substring(0, dot));
            setProperty(subject2, attributeName.substring(dot + 1), value);

        } else {

            String methodName = "set" + attributeName.substring(0, 1).toUpperCase() +
                attributeName.substring(1);

            // Look for a method called setXXX first
            for (Method method : subject.getClass().getMethods()) {
                if (method.getName().equals(methodName)) {
                    if (method.getParameterTypes().length == 1) {
                        try {
                            method.invoke(subject, value);
                            return;
                        } catch (IllegalAccessException e1) {
                        } catch (IllegalArgumentException e2) {
                        } catch (InvocationTargetException e3) {
                        }
                    }
                }
            }


            try {
                Field field = subject.getClass().getField(attributeName);
                // Field field = subject.getClass().getDeclaredField( attributeName );
                field.set(subject, value);
            } catch (NoSuchFieldException e) {
                
                if (subject instanceof Map<?, ?>) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> map = (Map<String, Object>) subject;
                    map.put(attributeName, value);
                } else {
                    
                    // It isn't a java field, or a setXXX method, or a map
                    // Maybe "subject" is a SCRIPTED object, and therefore doesn't follow java's bean conventions.
                    try {
                        Itchy.getGame().getScriptManager().setAttribute(subject, attributeName, value);
                        return;
                    } catch ( Exception e2 ) {
                        // We'll throw e, rather than e2, so do nothing here.
                    }
                    
                    throw e;
                }
            }

        }
    }

    private Object subject;

    private String access;

    public BeanHelper( Object subject, String access )
    {
        this.subject = subject;
        this.access = access;
    }

    public Object get() throws IllegalArgumentException, IllegalAccessException,
        InvocationTargetException, SecurityException, NoSuchFieldException, ScriptException
    {
        return getProperty(this.subject, this.access);
    }

    public void set( Object value ) throws IllegalArgumentException, SecurityException,
        IllegalAccessException, InvocationTargetException, NoSuchFieldException, ScriptException
    {
        setProperty(this.subject, this.access, value);
    }
}
