/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.property;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.co.nickthecoder.itchy.Font;
import uk.co.nickthecoder.itchy.animation.Ease;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.util.BeanHelper;
import uk.co.nickthecoder.itchy.util.ClassName;
import uk.co.nickthecoder.itchy.util.StringUtils;
import uk.co.nickthecoder.jame.RGBA;

/**
 * Holds meta data about a property, which makes dealing with properties much simpler, as much of the work can be automated, rather than
 * handling each property on an individual basis.
 * 
 * For Itchy Gurus only!
 * 
 * Currently, Actor and Role have properties which can be edited within the SceneDesigner
 * 
 * @param <S>
 *        The property's subject, such as Actor, Role, GameRole etc.
 * @param <T>
 *        The type of the property, such as String, Integer, Double etc.
 */
public abstract class AbstractProperty<S, T> implements Comparable<AbstractProperty<S, ?>>
{
    /**
     * Uses reflection to looks through all of the fields and methods for "Property" annotations, and and for each one it finds, it creates
     * the appropriate AbstractProperty and adds it to the collection.
     * 
     * @param klass
     *        The class who to scan for Property annotations return A list of newly created AbstractProperty instances.
     */
    public static <SS> List<AbstractProperty<SS, ?>> findAnnotations( Class<? extends SS> klass )
    {
        List<AbstractProperty<SS, ?>> result = new ArrayList<AbstractProperty<SS, ?>>();
        addProperties(klass, result);

        Collections.sort(result);
        return result;
    }

    /**
     * Uses reflection to looks through all of the fields and methods for "Property" annotions, and and for each one it finds, it creates
     * the appropriate AbstractProperty and adds it to the collection.
     * 
     * @param klass
     *        The class who to scan for Property annotations
     * @param collection
     *        Where to add the newly created AbstractProperty instances. This is usually an empty list.
     */
    public static <SS> void addProperties( Class<? extends SS> klass, Collection<AbstractProperty<SS, ?>> collection )
    {
        addProperties(klass, "", collection);
    }

    private static <SS> void addProperties( Class<?> klass, String prefix, Collection<AbstractProperty<SS, ?>> collection )
    {
        addMethodProperties(klass, prefix, collection);

        for (Field field : klass.getFields()) {
            Property annotation = field.getAnnotation(Property.class);

            if (annotation != null) {
                addProperty(field.getType(), annotation, field.getName(), prefix, collection);
            }
        }

    }

    private static <SS> void addMethodProperties( Class<?> klass, String prefix, Collection<AbstractProperty<SS, ?>> collection )
    {
        if (klass.getSuperclass() != null) {
            addMethodProperties(klass.getSuperclass(), prefix, collection);
        }

        for (Class<?> klass2 : klass.getInterfaces()) {
            addMethodProperties(klass2, prefix, collection);
        }

        for (Method method : klass.getDeclaredMethods()) {
            Property annotation = method.getAnnotation(Property.class);

            if (annotation != null) {

                if (method.getName().startsWith("get")) {

                    String name = method.getName();
                    name = name.substring(3, 4).toLowerCase() + name.substring(4);

                    addProperty(method.getReturnType(), annotation, name, prefix, collection);

                } else {
                    System.err.println("Unexpected Property on method : " +
                        method.getDeclaringClass() + "." + method.getName());
                }
            }
        }

    }

    private static <SS> AbstractProperty<SS, ?> createProperty(
        Class<?> klass, String access, String key, Property annotation )
    {
        AbstractProperty<SS, ?> result = null;

        if (klass == ClassName.class) {
            try {
                result = new ClassNameProperty<SS>(annotation.baseClass(), key);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            result = createProperty(klass, key);
        }

        if (result != null) {
            result.access = access;
            result.label = annotation.label();
            result.hint = annotation.hint();
            result.sortOrder = annotation.sortOrder();
            result.addAliases(annotation.aliases());
        }

        if (result instanceof StringProperty) {
            StringProperty<?> property = (StringProperty<?>) result;
            property.multiLine(annotation.multiLine());
        }
        if (result instanceof RGBAProperty) {
            RGBAProperty<?> property = (RGBAProperty<?>) result;
            property.allowNull(annotation.allowNull());
            property.includeAlpha(annotation.alpha());
        }

        return result;
    }

    private static <SS> void addProperty( Class<?> klass, Property annotation, String name, String prefix,
        Collection<AbstractProperty<SS, ?>> collection )
    {
        if (annotation.recurse()) {

            addProperties(klass, prefix + name + ".", collection);

        } else {
            AbstractProperty<SS, ?> property = createProperty(klass, prefix + name, name, annotation);

            if (property != null) {
                property.sortOrder = annotation.sortOrder();
            }
            if (annotation.aliases().length > 0) {
                property.addAliases(annotation.aliases());
            }
            collection.add(property);
        }

    }

    private static <SS> AbstractProperty<SS, ?> createProperty( Class<?> klass, String key )
    {
        if (klass == int.class || klass == Integer.class) {
            return new IntegerProperty<SS>(key);
        }
        if (klass == double.class || klass == Double.class) {
            return new DoubleProperty<SS>(key);
        }
        if (klass == boolean.class || klass == Boolean.class) {
            return new BooleanProperty<SS>(key);
        }
        if (klass == String.class) {
            return new StringProperty<SS>(key);
        }
        if (klass == File.class) {
            return new FileProperty<SS>(key);
        }
        if (klass == RGBA.class) {
            return new RGBAProperty<SS>(key);
        }
        if (klass == Font.class) {
            return new FontProperty<SS>(key);
        }
        if (klass == Ease.class) {
            return new EaseProperty<SS>(key);
        }
        if (Enum.class.isAssignableFrom(klass)) {

            @SuppressWarnings("unchecked")
            AbstractProperty<SS, Enum<?>> result = new EnumProperty<SS, Enum<?>>(key, (Class<Enum<?>>) klass);
            return result;
        }

        System.err.println("Unexpected property : " + klass.getName() + "#" + key);
        return null;
    }

    public int sortOrder = 0;

    /**
     * The human readable label shown in the GUI
     */
    public String label;

    /**
     * Green text to the right of the component giving a very brief information about the field. For example, units (seconds, degrees etc).
     */
    public String hint;

    /**
     * Describes how to get/set the attribute, using JavaBean rules. For example, if a Role has a Property with an access of "radius", then
     * getValue will look for a public attribute called "radius", if this isn't found, then it will look for a method called "getRadius",
     * taking no arguments.
     * 
     * You can use "." to chain multiple bean accesses together, for example an access of "foo.radius" will look for an attribute "foo" or a
     * method "getFoo", and use its result to look for an attribute called "radius" or a method called "getRadius".
     */
    public String access;

    /**
     * When loading/saving the property value, this is the name used. For example, a key of "radius" may be saved like so :
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
     * The default is for the key to be the same as 'access', but this may not be desirable in some circumstances.
     */
    public String key;

    /**
     * An alternative names for this property. This is used so that properties can be renamed, and loading from a file which uses the old
     * name will still work.
     */
    public Set<String> aliases;

    private static String labelFromKey( String key )
    {
        key = key.substring(0, 1).toUpperCase() + key.substring(1);
        Pattern pattern = Pattern.compile("([A-Z])");
        Matcher matcher = pattern.matcher(key);
        return matcher.replaceAll(" $1").trim();
        // return key.replaceAll("[A-Z]", " \\1").trim();
    }

    public AbstractProperty( String key )
    {
        this.label = labelFromKey(key);
        this.key = key;
        this.access = key;
        this.aliases = new HashSet<String>();
    }

    public void addAliases( String[] values )
    {
        for (String value : values) {
            this.aliases.add(value);
        }
    }

    public T getValue( S subject ) throws Exception
    {
        @SuppressWarnings("unchecked")
        T result = (T) BeanHelper.getProperty(subject, this.access);
        return result;
    }

    public T getSafeValue( S subject )
    {
        try {
            return getValue(subject);
        } catch (Exception e) {
            return getDefaultValue();
        }
    }

    public abstract T getDefaultValue();

    public String getStringValue( S subject ) throws Exception
    {
        T result = getValue(subject);
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
     * Sets the value using a String as the value, which is needed when the property is being read from a file. The method 'parse' is used
     * to convert the String into an object of the appropriate type.
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
     * Each subclass will return a different form of Component, for example a StringProperty wil return a TextBox, and a BooleanProperty
     * will return a CheckBox.
     * 
     * @param subject
     * @param autoUpdate
     *        If true, then the subject is updated whenever the component is changed (i.e. when text is typed into the text box, or when a
     *        checkbox is clicked etc).
     * 
     * @return A component which allows the user to change this property's value. It could be a simple component, such as a TextBox, or a
     *         Container containing multiple child Components.
     * 
     * @throws Exception
     */
    public abstract Component createComponent( final S subject, boolean autoUpdate );

    public abstract void addChangeListener( Component component, ComponentChangeListener listener );

    /**
     * Updates the subject based on the state of the Component. This is used when the Components are created with an autoUpdate of false, in
     * which case the subect's attributes are updated when the "Ok" button is clicked, rather than while the user is editing the TextBox (or
     * other Component).
     * 
     * @param subject
     *        The subject who's attribute is to be updated.
     * @param component
     *        The GUI Component, which must be the same one that was created by the createComponent method.
     * @throws Exception
     */
    public abstract void update( S subject, Component component ) throws Exception;

    /**
     * Refreshes the component based on the state of the subject.
     * 
     * @param subject
     * @param component
     * @throws Exception
     */
    public abstract void refresh( S subject, Component component ) throws Exception;

    /**
     * Checks if the entered value is valid, and if so, returns null, otherwise it returns the error message explaining what if wrong with
     * the entered value.
     * 
     * @param component
     *        The component created via {@link #createComponent(Object, boolean)}
     * @return Null if there is no error, otherwise a descriptive error message.
     */
    public abstract String getErrorText( Component component );

    /**
     * Converts a String representation of the properties value into the appropriate. For example, an IntegerProperty will use
     * Integer.parseInt to return an Integer object.
     * 
     * @param stringValue
     *        The string representation of the value
     * @return The actual value, for example, if this is a RGBAProperty, then the return value will be a RGBA object.
     */
    public abstract T parse( String stringValue );

    @Override
    public String toString()
    {
        if (StringUtils.equals(this.access, this.key)) {
            return this.getClass().getName() + " " + this.label + " (" + this.access + ")";
        } else {
            return this.getClass().getName() + " " + this.label + " (" + this.access + " : " + this.key + ")";
        }
    }

    @Override
    public int compareTo( AbstractProperty<S, ?> other )
    {
        if ( other == null) {
            return -1;
        }
        
        int diff = this.sortOrder - other.sortOrder;
        return diff == 0 ? this.label.compareTo(other.label) : diff;
    }

    /**
     * A Fluent setter for the label
     * 
     * @param label
     * @return this
     */
    public AbstractProperty<S, T> label( String label )
    {
        this.label = label;
        return this;
    }

    /**
     * A Fluent setter for access
     * 
     * @param access
     * @return this
     */
    public AbstractProperty<S, T> access( String access )
    {
        this.access = access;
        return this;
    }

    /**
     * A Fluent setter for hint
     * 
     * @param access
     * @return this
     */
    public AbstractProperty<S, T> hint( String access )
    {
        this.hint = access;
        return this;
    }

}
