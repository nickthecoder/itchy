/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.CostumeResource;
import uk.co.nickthecoder.itchy.Resources;

public class CostumePickerButton extends GuiButton implements ActionListener
{
    private Resources resources;

    private CostumeResource costumeResource;

    private List<ComponentChangeListener> changeListeners = new ArrayList<ComponentChangeListener>();

    private List<ComponentValidator> validators = new ArrayList<ComponentValidator>();

    private ImageComponent img;

    private Label label;

    public CostumePickerButton( Resources resources, CostumeResource costumeResource )
    {
        super();
        this.layout = new VerticalLayout();
        this.setXAlignment(0.5f);

        this.img = new ImageComponent(costumeResource.getThumbnail());
        this.label = new Label(costumeResource.getName());
        this.addChild(this.img);
        this.addChild(this.label);

        this.resources = resources;
        this.costumeResource = costumeResource;
        this.addActionListener(this);
    }

    public CostumeResource getValue()
    {
        return this.costumeResource;
    }

    public void setValue( CostumeResource costumeResource )
    {
        this.costumeResource = costumeResource;

        this.img.setImage(costumeResource.getThumbnail());
        this.label.setText(costumeResource.getName());

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
        CostumePicker picker = new CostumePicker(this.resources)
        {
            @Override
            public void pick( CostumeResource costumeResource )
            {
                setValue(costumeResource);
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
