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
    public PoseResource getDefaultValue()
    {
        return null;
    }

    @Override
    public Component createComponent( final S subject, final boolean autoUpdate )
    {
        Resources resources = Itchy.getGame().resources;

        PoseResource poseResource = this.getSafeValue(subject);

        final PoseResourcePickerButton pickerButton = new PoseResourcePickerButton(resources, poseResource);

        if (autoUpdate) {

            pickerButton.addChangeListener(new ComponentChangeListener() {

                @Override
                public void changed()
                {
                    try {
                        PoseResourceProperty.this.update(subject, pickerButton);
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
    public void update( S subject, Component component ) throws Exception
    {
        PoseResourcePickerButton pickerButton = (PoseResourcePickerButton) component;
        try {
            this.setValue(subject, pickerButton.getValue());
            pickerButton.removeStyle("error");
        } catch (Exception e) {
            pickerButton.addStyle("error");
        }
    }

    @Override
    public void refresh( S subject, Component component ) throws Exception
    {
        PoseResourcePickerButton pickerButton = (PoseResourcePickerButton) component;
        PoseResource poseResource = this.getValue(subject);
        pickerButton.setValue(poseResource);
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
            System.err.println("PoseProperty not found.");
            return "";
        }
        return value.getName();
    }

    @Override
    public String getErrorText( Component component )
    {
        return null;
    }

}