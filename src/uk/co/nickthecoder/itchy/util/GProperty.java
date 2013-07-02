package uk.co.nickthecoder.itchy.util;

import java.lang.reflect.InvocationTargetException;

import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;

public abstract class GProperty<S, T>
{
    public String label;

    public String access;

    public GProperty( String label, String access )
    {
        this.label = label;
        this.access = access;
    }

    public T getValue( S subject ) throws IllegalArgumentException, SecurityException,
        IllegalAccessException, InvocationTargetException, NoSuchFieldException
    {
        @SuppressWarnings("unchecked")
        T result = (T) BeanHelper.getProperty(subject, this.access);
        return result;
    }

    public void setValue( S subject, Object value ) throws IllegalArgumentException,
        SecurityException, IllegalAccessException, InvocationTargetException, NoSuchFieldException
    {
        BeanHelper.setProperty(subject, this.access, value);
    }

    public Component createComponent( final S subject, boolean autoUpdate )
        throws IllegalArgumentException, SecurityException, IllegalAccessException,
        InvocationTargetException, NoSuchFieldException
    {
        return this.createComponent(subject, autoUpdate, null);
    }

    public abstract T parse( String value );

    public abstract Component createComponent( final S subject, boolean autoUpdate,
            ComponentChangeListener listener ) throws IllegalArgumentException, SecurityException,
        IllegalAccessException, InvocationTargetException, NoSuchFieldException;

    public abstract void update( S subject, Component component ) throws Exception;

    @Override
    public String toString()
    {
        return "Property " + this.label + " (" + this.access + ")";
    }
}
