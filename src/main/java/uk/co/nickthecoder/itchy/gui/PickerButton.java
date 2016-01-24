/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import uk.co.nickthecoder.jame.event.MouseButtonEvent;

public class PickerButton<T> extends GuiButton
{
    protected final Map<String, T> map;

    protected final Label label;

    protected T value;

    protected final String title;

    protected final List<ComponentChangeListener> changeListeners;

    protected final List<ComponentValidator> validators;

    public PickerButton( String title, T current, Map<String, T> map )
    {
        super();

        this.title = title;

        this.map = map;
        this.addStyle("pickerButton");
        this.value = current;
        this.focusable = true;

        String labelString = "<select>";
        if (current != null) {
            labelString = getLabelString(current);
        }

        this.label = new Label(labelString);
        this.addChild(this.label);

        this.changeListeners = new ArrayList<ComponentChangeListener>();
        
        this.validators = new ArrayList<ComponentValidator>();
    }

    private String getLabelString( T value )
    {
        for (String key : this.map.keySet()) {
            if (value.equals(this.map.get(key))) {
                return key;
            }
        }
        return "<select>";
    }

    public void addChangeListener( ComponentChangeListener listener )
    {
        this.changeListeners.add(listener);
    }

    public void removeChangeListener( ComponentChangeListener listener )
    {
        this.changeListeners.remove(listener);
    }


    public void addValidator( ComponentValidator validator )
    {
        this.validators.add(validator);
    }

    public void removeValidator( ComponentValidator validator )
    {
        this.validators.remove(validator);
    }
    
    @Override
    public void onClick( final MouseButtonEvent e )
    {
        Picker<T> picker = new Picker<T>(this.title, this.map, this.getValue()) {
            @Override
            public void pick( String label, T object )
            {
                PickerButton.this.label.setText(label);
                PickerButton.this.value = object;
                fireChangeEvent();
            }
        };
        picker.show();
    }

    public void fireChangeEvent()
    {
        this.removeStyle("error");
        for (ComponentValidator validator : this.validators) {
            if ( ! validator.isValid() ) {
                this.addStyle("error");
            }
        }
        for (ComponentChangeListener listener : this.changeListeners) {
            listener.changed();
        }
    }

    public T getValue()
    {
        return this.value;
    }

    public void setValue( T value )
    {
        this.label.setText(getLabelString(value));
        this.value = value;
    }
}
