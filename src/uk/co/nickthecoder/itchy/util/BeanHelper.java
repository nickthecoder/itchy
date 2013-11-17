/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.script.ScriptException;

import uk.co.nickthecoder.itchy.script.ScriptedObject;

/**
 * 
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

            if (subject instanceof ScriptedObject) {
                if (((ScriptedObject) subject).getScriptedObject() == subject2) {
                    return ((ScriptedObject) subject).getProperty(attributeName.substring(dot + 1));
                }
            }

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
            if ((klass == null) && (value == null)) {
                // Damn, we don't know the class of the parameter, and its null, so look for ANY
                // method, which takes a single object as its parameter.

                for (Method method : subject.getClass().getMethods()) {
                    if (method.getName().equals(methodName)) {
                        if (method.getParameterTypes().length == 1) {
                            method.invoke(subject, value);
                            return;
                        }
                    }
                }

            } else {
                Class<?>[] argTypes = new Class<?>[1];
                argTypes[0] = klass;

                try {
                    Method method = subject.getClass().getMethod(methodName, argTypes);

                    method.invoke(subject, value);
                    return;

                } catch (NoSuchMethodException e) {
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
