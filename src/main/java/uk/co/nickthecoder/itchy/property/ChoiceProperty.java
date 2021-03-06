/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.property;

import java.util.HashMap;

import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentValidator;
import uk.co.nickthecoder.itchy.gui.PickerButton;

public class ChoiceProperty<S, T> extends Property<S, T>
{
    private HashMap<String, T> map;
    
    public ChoiceProperty( String key )
    {
        super(key);
        this.map = new HashMap<String, T>();
    }

    /**
     * A Fluent API for adding a value to the map of allowed value/label pairs.
     * 
     * @return this
     */
    public ChoiceProperty<S, T> add( String label, T value )
    {
        this.map.put(label, value);
        return this;
    }

    public void put( String label, T value )
    {
        this.map.put(label, value);
    }

    @Override
    public Component createUnvalidatedComponent( final S subject)
    {
        final PickerButton<T> button = new PickerButton<T>("Choice", this.getSafeValue(subject), this.map);
        return button;
    }

    @Override
    public void addChangeListener( Component component, ComponentChangeListener listener )
    {
        @SuppressWarnings("unchecked")
        PickerButton<T> button = (PickerButton<T>) component;
        button.addChangeListener(listener);
    }
    
    @Override
    public void addValidator( Component component, ComponentValidator validator )
    {
        @SuppressWarnings("unchecked")
        PickerButton<T> button = (PickerButton<T>) component;
        button.addValidator(validator);
    }

    @Override
    public T getDefaultValue()
    {
        // If a default value hasn't been specified, but we don't allow nulls, 
        // then return an arbitrary item from the list (the first).
        if ((this.defaultValue == null) && (!allowNull))  {
            for (T value : this.map.values()) {
                return value;
            }
        }
        
        return this.defaultValue;
    }

    @Override
    public T getValueFromComponent( Component component )
    {
        @SuppressWarnings("unchecked")
        PickerButton<T> button = (PickerButton<T>) component;
        return button.getValue();
    }

    @Override
    public void updateComponentValue( T value, Component component )
    {
        @SuppressWarnings("unchecked")
        PickerButton<T> button = (PickerButton<T>) component;
        button.setValue(value);
    }

    @Override
    public String getStringValue( S subject ) throws Exception
    {
        T result = getValue(subject);

        for (String key : this.map.keySet()) {
            T value = this.map.get(key);
            if (value.equals(result)) {
                return key;
            }
        }
        return null;
    }

    @Override
    public T parse( String key )
    {
        return this.map.get(key);
    }
}
