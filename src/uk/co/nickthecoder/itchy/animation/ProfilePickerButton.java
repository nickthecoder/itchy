/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.animation;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.Label;

public class ProfilePickerButton extends Button implements ActionListener
{
    private Profile profile;

    private List<ComponentChangeListener> changeListeners = new ArrayList<ComponentChangeListener>();
    
    public ProfilePickerButton( Profile profile )
    {
        super(NumericAnimation.getProfileName(profile));
        this.profile = profile;
        this.addActionListener(this);
    }

    public Profile getValue()
    {
        return this.profile;
    }

    public void setValue( Profile profile )
    {
        this.profile = profile;
        ((Label) (this.getChildren().get(0))).setText(NumericAnimation.getProfileName(profile) );

        for (ComponentChangeListener listener : this.changeListeners) {
            listener.changed();
        }
    }

    @Override
    public void action()
    {
        ProfilePicker picker = new ProfilePicker()
        {
            @Override
            public void pick( Profile profile )
            {
                setValue(profile);
            }
        };
        picker.show();
    }

    public void addChangeListener( ComponentChangeListener ccl )
    {
        this.changeListeners.add(ccl);
    }
    
    public void removeChangeListener( ComponentChangeListener ccl )
    {
        this.changeListeners.remove(ccl);
    }

}
