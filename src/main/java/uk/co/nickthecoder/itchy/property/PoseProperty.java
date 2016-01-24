/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.property;

import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Pose;
import uk.co.nickthecoder.itchy.PoseResource;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentValidator;
import uk.co.nickthecoder.itchy.gui.PosePickerButton;

public class PoseProperty<S> extends Property<S, Pose>
{
    public PoseProperty( String key )
    {
        super(key);
    }

    @Override
    public Pose getDefaultValue()
    {
        return null;
    }

    @Override
    public Component createComponent( final S subject, final boolean autoUpdate )
    {
        Resources resources = Itchy.getGame().resources;

        PoseResource poseResource = resources.getPoseResource(this.getSafeValue(subject));

        final PosePickerButton pickerButton = new PosePickerButton(resources, poseResource);
        // pickerButton.setCompact(true);

        if (autoUpdate) {

            pickerButton.addChangeListener(new ComponentChangeListener() {

                @Override
                public void changed()
                {
                    try {
                        PoseProperty.this.update(subject, pickerButton);
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
        PosePickerButton button = (PosePickerButton) component;
        button.addChangeListener(listener);
    }
    
    @Override
    public void addValidator( Component component, ComponentValidator validator )
    {
        PosePickerButton button = (PosePickerButton) component;
        button.addValidator(validator);
    }

    @Override
    public void update( S subject, Component component ) throws Exception
    {
        PosePickerButton pickerButton = (PosePickerButton) component;
        try {
            this.setValue(subject, pickerButton.getValue().pose);
            pickerButton.removeStyle("error");
        } catch (Exception e) {
            pickerButton.addStyle("error");
        }
    }

    @Override
    public void refresh( S subject, Component component ) throws Exception
    {
        PosePickerButton pickerButton = (PosePickerButton) component;
        Resources resources = Itchy.getGame().resources;
        Pose pose = this.getValue(subject);
        PoseResource fontResource = resources.getPoseResource(pose);
        pickerButton.setValue(fontResource);
    }

    @Override
    public Pose parse( String value )
    {
        if ("".equals(value) || (value == null)) {
            return null;
        }
        Pose result = Itchy.getGame().resources.getPose(value);
        if (result == null) {
            throw new NullPointerException();
        }
        return result;
    }

    @Override
    public String getStringValue( S subject ) throws Exception
    {
        Pose value = this.getValue(subject);

        PoseResource pr = Itchy.getGame().resources.getPoseResource(value);
        if (pr == null) {
            System.err.println("PoseProperty not found.");
            return "";
        }
        return pr.getName();
    }

    @Override
    public String getErrorText( Component component )
    {
        return null;
    }

}
