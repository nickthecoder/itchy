/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.co.nickthecoder.jame.event.MouseButtonEvent;

public class PickerButton<T> extends Button
{
    protected final HashMap<String, T> hashMap;

    protected final Label label;

    protected T value;

    protected final String title;
    
    protected final List<ComponentChangeListener> changeListeners;


    public PickerButton( String title, T current, HashMap<String, T> hashMap )
    {
        super();
        
        this.title = title;

        this.hashMap = hashMap;
        this.addStyle("pickerButton");
        this.value = current;
        this.focusable = true;

        String labelString = "<select>";
        if (current != null) {
            for (String key : hashMap.keySet()) {
                if (current.equals(hashMap.get(key))) {
                    labelString = key;
                    break;
                }
            }
        }

        this.label = new Label(labelString);
        this.addChild(this.label);

        this.changeListeners = new ArrayList<ComponentChangeListener>();
    }

    public void addChangeListener( ComponentChangeListener listener )
    {
        this.changeListeners.add(listener);
    }

    public void removeChangeListener( ComponentChangeListener listener )
    {
        this.changeListeners.remove(listener);
    }
    
    @Override
    public void onClick( final MouseButtonEvent e )
    {        
        Picker<T> picker = new Picker<T>(this.title, this.hashMap, this.getValue()) {
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
        for (ComponentChangeListener listener : this.changeListeners) {
            listener.changed();
        } 
    }

    public T getValue()
    {
        return this.value;
    }
}
