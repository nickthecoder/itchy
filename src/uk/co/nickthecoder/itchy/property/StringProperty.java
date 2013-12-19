/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.property;

import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.TextBox;

public class StringProperty<S> extends AbstractProperty<S, String>
{
    public StringProperty( String label, String access, String key )
    {
        super(label, access, key);
    }

    public StringProperty( String label, String access )
    {
        super(label, access);
    }

    @Override
    public String getDefaultValue()
    {
        return "";
    }

    @Override
    public Component createComponent( final S subject, boolean autoUpdate )
    {
        final TextBox box = new TextBox(this.getSafeValue(subject));
        if (autoUpdate) {
            box.addChangeListener(new ComponentChangeListener() {
                @Override
                public void changed()
                {
                    try {
                        StringProperty.this.update(subject, box);
                    } catch (Exception e) {
                        // Do nothing
                    }
                }
            });
        }
        return box;
    }

    @Override
    public void addChangeListener( Component component, ComponentChangeListener listener )
    {
        TextBox textBox = (TextBox) component;
        textBox.addChangeListener(listener);
    }

    @Override
    public void update( S subject, Component component ) throws Exception
    {
        TextBox textBox = (TextBox) component;
        try {
            this.setValue(subject, textBox.getText());
            textBox.removeStyle("error");
        } catch (Exception e) {
            textBox.addStyle("error");
            throw e;
        }
    }

    @Override
    public void refresh( S subject, Component component ) throws Exception
    {
        TextBox textBox = (TextBox) component;
        textBox.setText(this.getValue(subject));
    }

    @Override
    public String parse( String value )
    {
        return value;
    }

    @Override
    public String getErrorText( Component component )
    {
        return null;
    }

}
