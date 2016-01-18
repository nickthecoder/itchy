/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.PoseResource;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.jame.Surface;

public class PosePickerButton extends GuiButton implements ActionListener
{
    private Resources resources;

    private PoseResource poseResource;

    private List<ComponentChangeListener> changeListeners = new ArrayList<ComponentChangeListener>();

    private ImageComponent img;

    private Label label;

    public PosePickerButton( Resources resources, PoseResource poseResource )
    {
        super();
        this.layout = new VerticalLayout();
        this.setXAlignment(0.5f);

        if (poseResource == null) {
            Surface surface = new Surface(1, 1, true);
            this.img = new ImageComponent(surface);
            this.label = new Label("<none>");
        } else {
            this.img = new ImageComponent(poseResource.getThumbnail());
            this.label = new Label(poseResource.getName());
        }
        this.addChild(this.img);
        this.addChild(this.label);

        this.resources = resources;
        this.poseResource = poseResource;
        this.addActionListener(this);
    }

    public PoseResource getValue()
    {
        return this.poseResource;
    }

    public void setValue( PoseResource poseResource )
    {
        this.poseResource = poseResource;

        this.img.setImage(poseResource.getThumbnail());
        this.label.setText(poseResource.getName());

        for (ComponentChangeListener listener : this.changeListeners) {
            listener.changed();
        }
    }

    @Override
    public void action()
    {
        PosePicker picker = new PosePicker(this.resources)
        {
            @Override
            public void pick( PoseResource poseResource )
            {
                setValue(poseResource);
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