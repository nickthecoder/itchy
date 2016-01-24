/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.animation;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.ComponentValidator;
import uk.co.nickthecoder.itchy.gui.GuiButton;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.ImageComponent;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.VerticalLayout;

public class EasePickerButton extends GuiButton implements ActionListener
{
    private Ease ease;

    private List<ComponentChangeListener> changeListeners = new ArrayList<ComponentChangeListener>();

    private List<ComponentValidator> validators = new ArrayList<ComponentValidator>();

    private ImageComponent img;

    private Label label;

    public EasePickerButton( Ease ease )
    {
        super();
        this.setLayout(new VerticalLayout());
        this.setXAlignment(0.5f);

        this.label = new Label(ease.getName());
        this.img = new ImageComponent(ease.getThumbnail());

        this.addChild(this.img);
        this.addChild(this.label);

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
        this.label.setText(ease.getName());
        this.img.setImage(ease.getThumbnail());


        this.removeStyle("error");
        for (ComponentValidator validator : this.validators) {
            if ( ! validator.isValid() ) {
                this.addStyle("error");
            }
        }
        for (ComponentChangeListener listener : this.changeListeners) {
            listener.changed();
        }
    }

    @Override
    public void action()
    {
        EasePicker picker = new EasePicker(getValue())
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

    public void addValidator( ComponentValidator validator )
    {
        this.validators.add(validator);
    }

    public void removeChangeListener( ComponentValidator validator )
    {
        this.validators.remove(validator);
    }

}
