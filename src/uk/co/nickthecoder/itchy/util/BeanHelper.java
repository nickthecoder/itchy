package uk.co.nickthecoder.itchy.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BeanHelper
{
    public static Object getProperty( Object subject, String attributeName )
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException,
            NoSuchFieldException
    {
        int dot = attributeName.indexOf( "." );

        if ( dot > 0 ) {

            Object subject2 = getProperty( subject, attributeName.substring( 0, dot ) );
            return getProperty( subject2, attributeName.substring( dot + 1 ) );

        } else {

            // Look for a method called getXXX first
            Class<?>[] argTypes = new Class<?>[] {};
            String methodName = "get" + attributeName.substring( 0, 1 ).toUpperCase() + attributeName.substring( 1 );

            try {
                Method method = subject.getClass().getMethod( methodName, argTypes );
                Object result = method.invoke( subject );
                return result;
            } catch ( NoSuchMethodException e ) {
            }

            Field field = subject.getClass().getField( attributeName );
            //Field field = subject.getClass().getDeclaredField( attributeName );
            return field.get( subject );

        }
    }

    public static void setProperty( Object subject, String attributeName, Object value )
            throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException,
            NoSuchFieldException
    {
        Class<?> klass = value.getClass();

        if ( klass == Double.class ) {
            klass = double.class;
        } else if ( klass == Integer.class ) {
            klass = int.class;
        } else if ( klass == Float.class ) {
            klass = float.class;
        } else if ( klass == Character.class ) {
            klass = char.class;
        } else if ( klass == Byte.class ) {
            klass = byte.class;
        }

        setProperty( subject, attributeName, value, klass );
    }

    public static void setProperty( Object subject, String attributeName, Object value, Class<?> klass )
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException,
            NoSuchFieldException
    {
        int dot = attributeName.indexOf( "." );

        if ( dot > 0 ) {

            Object subject2 = getProperty( subject, attributeName.substring( 0, dot ) );
            setProperty( subject2, attributeName.substring( dot + 1 ), value );

        } else {

            // Look for a method called setXXX first
            Class<?>[] argTypes = new Class<?>[1];
            argTypes[0] = klass;

            String methodName = "set" + attributeName.substring( 0, 1 ).toUpperCase() + attributeName.substring( 1 );

            try {
                Method method = subject.getClass().getMethod( methodName, argTypes );

                method.invoke( subject, value );
                return;

            } catch ( NoSuchMethodException e ) {
            }


            Field field = subject.getClass().getField( attributeName );
            //Field field = subject.getClass().getDeclaredField( attributeName );
            field.set( subject, value );
        }
    }

}
