/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Costume;
import uk.co.nickthecoder.itchy.Resources;

public class CostumePickerButton extends GuiButton implements ActionListener
{
    private Resources resources;

    private Costume costume;

    private List<ComponentChangeListener> changeListeners = new ArrayList<ComponentChangeListener>();

    private List<ComponentValidator> validators = new ArrayList<ComponentValidator>();

    private ImageComponent img;

    private Label label;

    public CostumePickerButton(Resources resources, Costume costume)
    {
        super();
        layout = new VerticalLayout();
        this.setXAlignment(0.5f);

        img = new ImageComponent(costume.getThumbnail());
        label = new Label(costume.getName());
        this.addChild(img);
        this.addChild(label);

        this.resources = resources;
        this.costume = costume;
        this.addActionListener(this);
    }

    public Costume getValue()
    {
        return costume;
    }

    public void setValue(Costume costume)
    {
        this.costume = costume;

        img.setImage(costume.getThumbnail());
        label.setText(costume.getName());

        this.removeStyle("error");
        for (ComponentValidator validator : validators) {
            if (!validator.isValid()) {
                this.addStyle("error");
            }
        }
        for (ComponentChangeListener listener : changeListeners) {
            listener.changed();
        }
    }

    @Override
    public void action()
    {
        CostumePicker picker = new CostumePicker(resources)
        {
            @Override
            public void pick(Costume costume)
            {
                setValue(costume);
            }
        };
        picker.show();
    }

    public void addChangeListener(ComponentChangeListener ccl)
    {
        changeListeners.add(ccl);
    }

    public void removeChangeListener(ComponentChangeListener ccl)
    {
        changeListeners.remove(ccl);
    }

    public void addValidator(ComponentValidator validator)
    {
        validators.add(validator);
    }

    public void removeChangeListener(ComponentValidator validator)
    {
        validators.remove(validator);
    }

}
