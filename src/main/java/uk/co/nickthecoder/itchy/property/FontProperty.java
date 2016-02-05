/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.property;

import uk.co.nickthecoder.itchy.Font;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.ComponentValidator;
import uk.co.nickthecoder.itchy.gui.FontPickerButton;

public class FontProperty<S> extends Property<S, Font>
{
    public FontProperty( String key )
    {
        super(key);
    }

    @Override
    public Component createUnvalidatedComponent( final S subject)
    {
        Resources resources = Itchy.getGame().resources;

        Font font = this.getSafeValue(subject);

        final FontPickerButton pickerButton = new FontPickerButton(resources, font);
        pickerButton.setCompact(true);

        return pickerButton;
    }

    @Override
    public void addChangeListener( Component component, ComponentChangeListener listener )
    {
        FontPickerButton button = (FontPickerButton) component;
        button.addChangeListener(listener);
    }
    
    @Override
    public void addValidator( Component component, ComponentValidator validator )
    {
        FontPickerButton button = (FontPickerButton) component;
        button.addValidator(validator);
    }

    @Override
    public Font getValueFromComponent( Component component )
    {
        FontPickerButton pickerButton = (FontPickerButton) component;
        return pickerButton.getValue();
    }

    @Override
    public void updateComponentValue( Font value, Component component )
    {
        FontPickerButton pickerButton = (FontPickerButton) component;
        pickerButton.setValue(value);
    }

    @Override
    public Font parse( String value )
    {
        if ("".equals(value) || (value == null)) {
            return null;
        }
        Font result = Itchy.getGame().resources.getFont(value);
        if (result == null) {
            throw new NullPointerException();
        }
        return result;
    }

    @Override
    public String getStringValue( S subject ) throws Exception
    {
        Font font = this.getValue(subject);

        return font.getName();
    }

}
