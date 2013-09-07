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

public class EasePickerButton extends Button implements ActionListener
{
    private Ease ease;

    private List<ComponentChangeListener> changeListeners = new ArrayList<ComponentChangeListener>();
    
    public EasePickerButton( Ease ease )
    {
        super(NumericAnimation.getEaseName(ease));
        this.ease = ease;
        this.addActionListener(this);
    }

    public Ease getValue()
    {
        return this.ease;
    }

    public void setValue( Ease ease )
    {
        this.ease = ease;
        ((Label) (this.getChildren().get(0))).setText(NumericAnimation.getEaseName(ease) );

        for (ComponentChangeListener listener : this.changeListeners) {
            listener.changed();
        }
    }

    @Override
    public void action()
    {
        EasePicker picker = new EasePicker()
        {
            @Override
            public void pick( Ease ease )
            {
                setValue(ease);
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
