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
        try {
            Object result = BeanHelper.getProperty( this.data, attributeName );
            return result;
        } catch ( Exception e ) {
            e.printStackTrace();
            return null;
        }
    }

    /*
    private Object reflect( Object data, String attributeName )
        throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException,
        NoSuchFieldException
    {
        int dot = attributeName.indexOf( "." );

        if ( dot > 0 ) {

            Object subject = this.reflect( data, attributeName.substring( 0, dot ) );
            return this.reflect( subject, attributeName.substring( dot + 1 ) );

        } else {

            // Look for a method called getXXX first
            Class<?>[] argTypes = new Class<?>[] {};
            String methodName = "get" + attributeName.substring( 0, 1 ).toUpperCase() + attributeName.substring( 1 );

            try {
                Method method = data.getClass().getMethod( methodName, argTypes );
                Object result = method.invoke( data );
                return result;
            } catch ( NoSuchMethodException e ) {
            }

            Field field = data.getClass().getDeclaredField( attributeName );
            return field.get( this.data );

        }
    }
    */

}
