/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Font;
import uk.co.nickthecoder.itchy.Resources;

public class FontPickerButton extends GuiButton implements ActionListener
{
    private Resources resources;

    private Font font;

    private List<ComponentChangeListener> changeListeners = new ArrayList<ComponentChangeListener>();

    private List<ComponentValidator> validators = new ArrayList<ComponentValidator>();

    private Label example;

    private Label label;

    public FontPickerButton( Resources resources, Font font)
    {
        super();
        this.layout = new VerticalLayout();
        this.setXAlignment(0.5f);

        this.example = FontPicker.createExample(font);
        this.label = new Label(font.getName());
        this.addChild(this.example);
        this.addChild(this.label);

        this.resources = resources;
        this.font = font;
        this.addActionListener(this);
    }

    public Font getValue()
    {
        return this.font;
    }

    public void setCompact( boolean value )
    {
        this.example.setVisible(!value);
    }

    public void setValue( Font font)
    {
        this.font = font;

        this.example.setFont(font);
        this.label.setText(font.getName());


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
        FontPicker picker = new FontPicker(this.resources, this.getValue())
        {
            @Override
            public void pick( Font font)
            {
                setValue(font);
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
    
    public void addValidator( ComponentValidator validator)
    {
        this.validators.add(validator);
    }

    public void removeValidator( ComponentValidator validator )
    {
        this.validators.remove(validator);
    }

}
