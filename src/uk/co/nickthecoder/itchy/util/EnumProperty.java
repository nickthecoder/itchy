/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.util;

import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.EnumPickerButton;

public class EnumProperty<S, E extends Enum<?>> extends AbstractProperty<S, E>
{
    private Class<E> klass;

    public EnumProperty( String label, String access, String key, Class<E> klass )
    {
        super(label, access, key);
        this.klass = klass;
    }

    public EnumProperty( String label, String access, Class<E> klass )
    {
        super(label, access);
        this.klass = klass;
    }

    @Override
    public E getDefaultValue()
    {
        return null;
    }

    @Override
    public Component createComponent( final S subject, boolean autoUpdate )
    {
        final EnumPickerButton<E> button = new EnumPickerButton<E>("Picker", this.getSafeValue(subject));

        if (autoUpdate) {
            button.addChangeListener(new ComponentChangeListener() {

                @Override
                public void changed()
                {
                    try {
                        EnumProperty.this.update(subject, button);
                    } catch (Exception e) {
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
        EnumPickerButton<E> button = (EnumPickerButton<E>) component;
        button.addChangeListener(listener);
    }

    @Override
    public void update( S subject, Component component ) throws Exception
    {
        @SuppressWarnings("unchecked")
        EnumPickerButton<E> button = (EnumPickerButton<E>) component;

        setValue(subject, button.getValue());
    }

    @Override
    public E parse( String value )
    {
        for (E possible : this.klass.getEnumConstants()) {
            if (possible.name().equals(value)) {
                return possible;
            }
        }
        return null;
    }

    @Override
    public String getStringValue( S subject ) throws Exception
    {
        E value = getValue(subject);
        return value.name();
    }

    @Override
    public String getErrorText( Component component )
    {
        return null;
    }

}
