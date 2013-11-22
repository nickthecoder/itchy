/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.property;

import uk.co.nickthecoder.itchy.gui.CheckBox;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;

public class BooleanProperty<S> extends AbstractProperty<S, Boolean>
{
    public BooleanProperty( String label, String access, String key )
    {
        super(label, access, key);
    }

    public BooleanProperty( String label, String access )
    {
        super(label, access);
    }

    @Override
    public Boolean getDefaultValue()
    {
        return false;
    }

    @Override
    public Component createComponent( final S subject, boolean autoUpdate )
    {
        boolean value = this.getSafeValue(subject) == true;
        final CheckBox checkBox = new CheckBox(value);

        if (autoUpdate) {

            checkBox.addChangeListener(new ComponentChangeListener() {
                @Override
                public void changed()
                {
                    try {
                        BooleanProperty.this.update(subject, checkBox);
                    } catch (Exception e) {
                        // Do nothing
                    }
                }
            });
        }
        return checkBox;
    }

    @Override
    public void addChangeListener( Component component, ComponentChangeListener listener )
    {
        CheckBox checkBox = (CheckBox) component;
        checkBox.addChangeListener(listener);
    }

    @Override
    public void update( S subject, Component component ) throws Exception
    {
        CheckBox checkBox = (CheckBox) component;
        try {
            this.setValue(subject, checkBox.getValue());
            checkBox.removeStyle("error");
        } catch (Exception e) {
            checkBox.addStyle("error");
        }
    }

    @Override
    public Boolean parse( String value )
    {
        return Boolean.parseBoolean(value);
    }

    @Override
    public String getErrorText( Component component )
    {
        return null;
    }

}
