/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.property;

import uk.co.nickthecoder.itchy.Font;
import uk.co.nickthecoder.itchy.FontResource;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.editor.FontPickerButton;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;

public class FontProperty<S> extends AbstractProperty<S, Font>
{
    public FontProperty( String label, String access )
    {
        super(label, access);
    }

    public FontProperty( String label, String access, String key )
    {
        super(label, access, key);
    }

    @Override
    public Font getDefaultValue()
    {
        return null;
    }

    @Override
    public Component createComponent( final S subject, final boolean autoUpdate )
    {
        Resources resources = Itchy.getGame().resources;

        FontResource fontResource = resources.getFontResource(this.getSafeValue(subject));

        final FontPickerButton pickerButton = new FontPickerButton(resources, fontResource);
        pickerButton.setCompact(true);

        if (autoUpdate) {

            pickerButton.addChangeListener(new ComponentChangeListener() {

                @Override
                public void changed()
                {
                    try {
                        FontProperty.this.update(subject, pickerButton);
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
        FontPickerButton button = (FontPickerButton) component;
        button.addChangeListener(listener);
    }

    @Override
    public void update( S subject, Component component ) throws Exception
    {
        FontPickerButton pickerButton = (FontPickerButton) component;
        try {
            this.setValue(subject, pickerButton.getValue().font);
            pickerButton.removeStyle("error");
        } catch (Exception e) {
            pickerButton.addStyle("error");
        }
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
    public String getErrorText( Component component )
    {
        return null;
    }

}