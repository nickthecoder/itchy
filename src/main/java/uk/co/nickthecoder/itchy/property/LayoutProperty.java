/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.property;

import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Layout;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentValidator;
import uk.co.nickthecoder.itchy.gui.LayoutPickerButton;

public class LayoutProperty<S> extends Property<S, Layout>
{
    public LayoutProperty( String key )
    {
        super(key);
    }

    @Override
    public Component createComponent( final S subject, final boolean autoUpdate )
    {
        Resources resources = Itchy.getGame().resources;

        Layout layout = this.getSafeValue(subject);
        final LayoutPickerButton pickerButton = new LayoutPickerButton(resources, layout);

        if (autoUpdate) {

            pickerButton.addChangeListener(new ComponentChangeListener() {

                @Override
                public void changed()
                {
                    try {
                        LayoutProperty.this.updateSubject(subject, pickerButton);
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
        LayoutPickerButton button = (LayoutPickerButton) component;
        button.addChangeListener(listener);
    }

    @Override
    public void addValidator( Component component, ComponentValidator validator)
    {
        LayoutPickerButton button = (LayoutPickerButton) component;
        button.addValidator(validator);
    }

    @Override
    public void updateSubject( S subject, Component component ) throws Exception
    {
        LayoutPickerButton pickerButton = (LayoutPickerButton) component;
        try {
            this.setValue(subject, pickerButton.getValue());
            pickerButton.removeStyle("error");
        } catch (Exception e) {
            pickerButton.addStyle("error");
        }
    }

    @Override
    public void updateComponent( S subject, Component component ) throws Exception
    {
        LayoutPickerButton pickerButton = (LayoutPickerButton) component;
        Layout layout = this.getValue(subject);
        pickerButton.setValue(layout);
    }

    @Override
    public Layout parse( String value )
    {
        if ("".equals(value) || (value == null)) {
            return null;
        }
        Layout result = Itchy.getGame().resources.getLayout(value);
        if (result == null) {
            throw new NullPointerException();
        }
        return result;
    }

    @Override
    public String getStringValue( S subject ) throws Exception
    {
        Layout layout = this.getValue(subject);

        if (layout == null) {
            return "";
        }
        return layout.getName();
    }

    @Override
    public String getErrorText( Component component )
    {
        return null;
    }

}
