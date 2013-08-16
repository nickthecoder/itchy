/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import uk.co.nickthecoder.itchy.Font;
import uk.co.nickthecoder.itchy.animation.Profile;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.jame.RGBA;

/**
 * Holds meta data about a property, which makes dealing with properties much simpler, as much of
 * the work can be automated, rather than handling each property on an individual basis.
 * 
 * For Itchy Gurus only!
 * 
 * Currently, Actor and Behaviour have properties which can be edited within the SceneDesigner
 * 
 * @param <S>
 *        The propertie's subject, such as Actor, Behaviour, GameBehaviour etc.
 * @param <T>
 *        The type of the property, such as String, Integer, Double etc.
 */
public abstract class AbstractProperty<S, T>
{

    /**
     * Uses reflection to looks through all of the fields and methods for "Property" annotions, and
     * and for each one it finds, it creates the appropriate AbstractProperty and adds it to the
     * collection.
     * 
     * @param klass
     *        The class who to scan for Property annotations return A list of newly created
     *        AbstractProperty instances.
     */
    public static <SS> List<AbstractProperty<SS, ?>> findAnnotations( Class<? extends SS> klass )
    {
        List<AbstractProperty<SS, ?>> result = new ArrayList<AbstractProperty<SS, ?>>();
        addProperties(klass, result);
        return result;
    }

    /**
     * Uses reflection to looks through all of the fields and methods for "Property" annotions, and
     * and for each one it finds, it creates the appropriate AbstractProperty and adds it to the
     * collection.
     * 
     * @param klass
     *        The class who to scan for Property annotations
     * @param collection
     *        Where to add the newly created AbstractProperty instances. This is usually an empty
     *        list.
     */
    public static <SS> void addProperties( Class<? extends SS> klass,
        Collection<AbstractProperty<SS, ?>> collection )
    {
        addProperties(klass, "", collection);
    }

    private static <SS> void addProperties( Class<?> klass, String prefix,
        Collection<AbstractProperty<SS, ?>> collection )
    {
        addMethodProperties(klass, prefix, collection);

        for (Field field : klass.getFields()) {
            Property annotation = field.getAnnotation(Property.class);

            if (annotation != null) {

                AbstractProperty<SS, ?> property = createProperty(field.getType(),
                    field.getName(), prefix + field.getName(), annotation);

                if (property != null) {
                    collection.add(property);
                }
                if (annotation.recurse()) {
                    addProperties(field.getType(), prefix + field.getName() + ".", collection);
                }
            }
        }

    }

    private static <SS> void addMethodProperties( Class<?> klass, String prefix,
        Collection<AbstractProperty<SS, ?>> collection )
    {
        if ( klass.getSuperclass() != null ) {
            addMethodProperties( klass.getSuperclass(), prefix, collection );
        }
        
        for (Class<?> klass2 : klass.getInterfaces()) {
            addMethodProperties(klass2, prefix, collection);
        }

        for (Method method : klass.getDeclaredMethods()) {
            Property annotation = method.getAnnotation(Property.class);

            if (annotation != null) {
                System.out.println("Found Annotated Method " + method.getName());

                if (method.getName().startsWith("get")) {

                    String name = method.getName();
                    name = name.substring(3, 4).toLowerCase() + name.substring(4);

                    AbstractProperty<SS, ?> property = createProperty(method.getReturnType(),
                        prefix + name, name,
                        annotation);
                    if (property != null) {
                        collection.add(property);
                    }
                    if (annotation.recurse()) {
                        addProperties(method.getReturnType(), prefix + name + ".", collection);
                    }

                } else {
                    System.err.println("Unexpected Property on method : " +
                        method.getDeclaringClass() + "." + method.getName());
                }
            }
        }
        
    }


    private static <SS> AbstractProperty<SS, ?> createProperty(
        Class<?> klass, String access, String key, Property property )
    {
        String label = property.label();

        if (klass == int.class) {
            return new IntegerProperty<SS>(label, access, key);
        }
        if (klass == double.class) {
            return new DoubleProperty<SS>(label, access, key);
        }
        if (klass == String.class) {
            return new StringProperty<SS>(label, access, key);
        }
        if (klass == boolean.class) {
            return new BooleanProperty<SS>(label, access, key);
        }
        if (klass == RGBA.class) {
            return new RGBAProperty<SS>(label, access, key, property.allowNull(), property.alpha());
        }
        if (klass == Font.class) {
            return new FontProperty<SS>(label, access, key);
        }
        if (klass == Profile.class) {
            return new ProfileProperty<SS>(label, access, key);
        }
        if (property.recurse()) {
            return null;
        }

        System.err.println("Unexpected property : " + klass.getName() + "." + key);

        return null;
    }

    /**
     * The human readable label shown in the GUI
     */
    public String label;

    /**
     * Describes how to get/set the attribute, using JavaBean rules. For example, if a Behaviour has
     * a Property with an access of "radius", then getValue will look for a public attribute called
     * "radius", if this isn't found, then it will look for a method called "getRadius", taking no
     * arguments.
     * 
     * You can use "." to chain multiple bean accesses together, for example an access of
     * "foo.radius" will look for an attribute "foo" or a method "getFoo", and use its result to
     * look for an attribute called "radius" or a method called "getRadius".
     */
    public String access;

    /**
     * When loading/saving the property value, this is the name used. For example, a key of "radius"
     * may be saved like so :
     * 
     * <pre>
     * <property name="radius" value="1.0"/>
     * </pre>
     * 
     * Or maybe like so :
     * 
     * <pre>
     * <example radius="1.0"/>
     * </pre>
     * 
     * The default is for the key to be the same as 'access', but this may not be desirable in some
     * circumstances.
     */
    public String key;

    public AbstractProperty( String label, String access, String key )
    {
        this.label = label;
        this.access = access;
        this.key = key;
    }

    public AbstractProperty( String label, String access )
    {
        this(label, access, access);
    }

    public T getValue( S subject ) throws Exception
    {
        @SuppressWarnings("unchecked")
        T result = (T) BeanHelper.getProperty(subject, this.access);
        return result;
    }

    public String getStringValue( S subject ) throws Exception
    {
        T result = getValue( subject );
        if (result == null) {
            return null;
        } else {
            return result.toString();
        }
    }
    
    public void setValue( S subject, Object value ) throws Exception
    {
        BeanHelper.setProperty(subject, this.access, value);
    }

    /**
     * Sets the value using a String as the value, which is needed when the property is being read
     * from a file. The method 'parse' is used to convert the String into an object of the
     * appropriate type.
     * 
     * @param subject
     * @param value
     * @throws Exception
     */
    public void setValueByString( S subject, String value ) throws Exception
    {
        setValue(subject, parse(value));
    }

    /**
     * Creates a GUI component suitable for getting input from the end user.
     * 
     * Each subclass will return a different form of Component, for example a StringProperty wil
     * return a TextBox, and a BooleanProperty will return a CheckBox.
     * 
     * @param subject
     * @param autoUpdate
     *        If true, then the subject is updated whenever the component is changed (i.e. when text
     *        is typed into the text box, or when a checkbox is clicked etc).
     * @return
     * @throws Exception
     */
    public Component createComponent( final S subject, boolean autoUpdate )
        throws Exception
    {
        return this.createComponent(subject, autoUpdate, null);
    }

    /**
     * Does the same as the other version of createComponent, with the added feature that a listener
     * is notified of changes as the user types/clicks.
     * 
     * @param subject
     * @param autoUpdate
     * @param listener
     * @return
     * @throws Exception
     */
    public abstract Component createComponent( final S subject, boolean autoUpdate,
        ComponentChangeListener listener ) throws Exception;

    /**
     * Updates the subject based on the state of the Component. This is used when the Components are
     * created with an autoUpdate of false, in which case the subect's attributes are updated when
     * the "Ok" button is clicked, rather than while the user is editing the TextBox (or other
     * Component).
     * 
     * @param subject
     *        The subject who's attribute is to be updated.
     * @param component
     *        The GUI Component, which must be the same one that was created by the createComponent
     *        method.
     * @throws Exception
     */
    public abstract void update( S subject, Component component ) throws Exception;

    /**
     * Converts a String representation of the properties value into the appropriate. For example,
     * an IntegerProperty will use Integer.parseInt to return an Integer object.
     * 
     * @param value
     * @return
     */
    public abstract T parse( String value );

    @Override
    public String toString()
    {
        if ( StringUtils.equals(this.access, this.key) ) {
            return this.getClass().getName() + " " + this.label + " (" + this.access + ")";
        } else {
            return this.getClass().getName() + " " + this.label + " (" + this.access + " : " + this.key + ")";
        }
    }
}
