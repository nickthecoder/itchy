/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.property;

import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.PoseResource;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.ComponentValidator;
import uk.co.nickthecoder.itchy.gui.PoseResourcePickerButton;

public class PoseResourceProperty<S> extends Property<S, PoseResource>
{
    public PoseResourceProperty( String key )
    {
        super(key);
    }

    @Override
    public Component createUnvalidatedComponent( final S subject )
    {
        Resources resources = Itchy.getGame().resources;

        PoseResource poseResource = this.getSafeValue(subject);

        final PoseResourcePickerButton pickerButton = new PoseResourcePickerButton(resources, poseResource);

        return pickerButton;
    }

    @Override
    public void addChangeListener( Component component, ComponentChangeListener listener )
    {
        PoseResourcePickerButton button = (PoseResourcePickerButton) component;
        button.addChangeListener(listener);
    }
    
    @Override
    public void addValidator( Component component, ComponentValidator validator )
    {
        PoseResourcePickerButton button = (PoseResourcePickerButton) component;
        button.addValidator(validator);
    }

    @Override
    public PoseResource getValueFromComponent( Component component )
    {
        PoseResourcePickerButton pickerButton = (PoseResourcePickerButton) component;
        return pickerButton.getValue();
    }

    @Override
    public void updateComponentValue( PoseResource value, Component component )
    {
        PoseResourcePickerButton pickerButton = (PoseResourcePickerButton) component;
        pickerButton.setValue(value);
    }

    @Override
    public PoseResource parse( String value )
    {
        if ("".equals(value) || (value == null)) {
            return null;
        }
        PoseResource result = Itchy.getGame().resources.getPoseResource(value);
        if (result == null) {
            throw new NullPointerException();
        }
        return result;
    }

    @Override
    public String getStringValue( S subject ) throws Exception
    {
        PoseResource value = this.getValue(subject);

        if (value == null) {
            return "";
        }
        return value.getName();
    }

}
