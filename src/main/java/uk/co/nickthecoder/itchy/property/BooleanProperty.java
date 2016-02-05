/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.property;

import uk.co.nickthecoder.itchy.gui.CheckBox;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentValidator;

public class BooleanProperty<S> extends Property<S, Boolean>
{

    public BooleanProperty( String key )
    {
        super(key);
        this.defaultValue = false;
    }

    @Override
    public Component createUnvalidatedComponent( final S subject)
    {
        boolean value = (this.getSafeValue(subject) == true);
        final CheckBox checkBox = new CheckBox(value);

        return checkBox;
    }

    @Override
    public void addChangeListener( Component component, ComponentChangeListener listener )
    {
        CheckBox checkBox = (CheckBox) component;
        checkBox.addChangeListener(listener);
    }
    
    @Override
    public void addValidator( Component component, ComponentValidator validator )
    {
        CheckBox checkBox = (CheckBox) component;
        checkBox.addValidator(validator);
    }

    @Override
    public Boolean getValueFromComponent( Component component )
    {
        CheckBox checkBox = (CheckBox) component;
        return checkBox.getValue();
    }

    @Override
    public void updateComponentValue( Boolean value, Component component )
    {
        CheckBox checkBox = (CheckBox) component;
        checkBox.setValue(value);
    }

    @Override
    public Boolean parse( String value )
    {
        return Boolean.parseBoolean(value);
    }

}
