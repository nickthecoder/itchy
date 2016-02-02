/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.property;

import uk.co.nickthecoder.itchy.Layer;
import uk.co.nickthecoder.itchy.Layout;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.ComponentValidator;
import uk.co.nickthecoder.itchy.gui.LayerPickerButton;

public abstract class LayerProperty<S> extends Property<S, Layer>
{
    
    public LayerProperty( String key )
    {
        super(key);
    }
    
    public abstract Layout getLayout();

    @Override
    public Component createUnvalidatedComponent( final S subject, final boolean autoUpdate )
    {
        Layer layer = this.getSafeValue(subject);
        final LayerPickerButton pickerButton = new LayerPickerButton(getLayout(), layer);

        if (autoUpdate) {

            pickerButton.addChangeListener(new ComponentChangeListener() {

                @Override
                public void changed()
                {
                    try {
                        LayerProperty.this.updateSubject(subject, pickerButton);
                    } catch (Exception e) {
                        // Do nothing
                    }
                }
            });
        }

        return pickerButton;
    }

    @Override
    public void addChangeListener( Component component, ComponentChangeListener listener )
    {
        LayerPickerButton button = (LayerPickerButton) component;
        button.addChangeListener(listener);
    }

    @Override
    public void addValidator( Component component, ComponentValidator validator)
    {
        LayerPickerButton button = (LayerPickerButton) component;
        button.addValidator(validator);
    }

    @Override
    public Layer getValueFromComponent( Component component )
    {
        LayerPickerButton pickerButton = (LayerPickerButton) component;
        return pickerButton.getValue();
    }

    @Override
    public void updateComponentValue( Layer value, Component component )
    {
        LayerPickerButton pickerButton = (LayerPickerButton) component;
        pickerButton.setValue(value);
    }

    @Override
    public Layer parse( String value )
    {
        if ("".equals(value) || (value == null)) {
            return null;
        }
        Layer result = getLayout().findLayer(value);
        if (result == null) {
            throw new NullPointerException();
        }
        return result;
    }

    @Override
    public String getStringValue( S subject ) throws Exception
    {
        Layer layer = this.getValue(subject);

        if (layer == null) {
            return "";
        }
        return layer.getName();
    }

}
