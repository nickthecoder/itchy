/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.FontResource;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.ImageComponent;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.VerticalLayout;

public class FontPickerButton extends Button implements ActionListener
{
    private Resources resources;

    private FontResource fontResource;

    private List<ComponentChangeListener> changeListeners = new ArrayList<ComponentChangeListener>();

    private Label example;

    private Label label;

    public FontPickerButton( Resources resources, FontResource fontResource )
    {
        super();
        this.layout = new VerticalLayout();
        this.setXAlignment(0.5f);
        
        this.example = FontPicker.createExample(fontResource);
        this.label = new Label(fontResource.getName());
        this.addChild(this.example);
        this.addChild(this.label);

        this.resources = resources;
        this.fontResource = fontResource;
        this.addActionListener(this);
    }

    public FontResource getValue()
    {
        return this.fontResource;
    }

    public void setValue( FontResource fontResource )
    {
        this.fontResource = fontResource;

        this.example.setFont(fontResource.font);
        this.label.setText(fontResource.getName());

        for (ComponentChangeListener listener : this.changeListeners) {
            listener.changed();
        }
    }

    @Override
    public void action()
    {
        FontPicker picker = new FontPicker(this.resources)
        {
            @Override
            public void pick( FontResource fontResource )
            {
                setValue(fontResource);
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
