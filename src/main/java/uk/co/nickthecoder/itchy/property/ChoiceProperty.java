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
     * A Fluent version of "put"
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
    public Component createComponent( final S subject, boolean autoUpdate )
    {
        final PickerButton<T> button = new PickerButton<T>("Choice", this.getSafeValue(subject), this.map);
        if (autoUpdate) {

            button.addChangeListener(new ComponentChangeListener() {
                @Override
                public void changed()
                {
                    try {
                        ChoiceProperty.this.update(subject, button);
                    } catch (Exception e) {
                        // Do nothing
                    }
                }
            });
        }
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
        for (T value : this.map.values()) {
            return value;
        }
        return null;
    }

    @Override
    public void update( S subject, Component component ) throws Exception
    {
        @SuppressWarnings("unchecked")
        PickerButton<T> button = (PickerButton<T>) component;
        this.setValue(subject, button.getValue());
    }

    @Override
    public void refresh( S subject, Component component ) throws Exception
    {
        @SuppressWarnings("unchecked")
        PickerButton<T> button = (PickerButton<T>) component;
        button.setValue(this.getValue(subject));
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

    @Override
    public String getErrorText( Component component )
    {
        return null;
    }
}
