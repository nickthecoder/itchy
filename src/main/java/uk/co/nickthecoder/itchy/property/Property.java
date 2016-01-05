/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.property;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.util.BeanHelper;
import uk.co.nickthecoder.itchy.util.StringUtils;

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
public abstract class Property<S, T>
{

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
    }

    private static String keyFromAccess( String access )
    {
        if (access.indexOf('.') >= 0) {
            return access.substring(access.lastIndexOf('.')+1);
        }
        return access;
    }

    public Property( String access )
    {
        this.access = access;
        this.key = keyFromAccess(access);
        this.label = labelFromKey(this.key);
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

    /**
     * A Fluent setter for the label
     * 
     * @param label
     * @return this
     */
    public Property<S, T> label( String label )
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
    public Property<S, T> access( String access )
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
    public Property<S, T> hint( String access )
    {
        this.hint = access;
        return this;
    }

    public Property<S, T> aliases( String... aliases )
    {
        addAliases(aliases);
        return this;
    }
}
