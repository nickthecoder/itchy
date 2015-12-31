/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.PoseResource;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.AbstractComponent;
import uk.co.nickthecoder.itchy.gui.PlainContainer;
import uk.co.nickthecoder.itchy.gui.GridLayout;
import uk.co.nickthecoder.itchy.gui.HorizontalLayout;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.ImageComponent;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.VerticalLayout;
import uk.co.nickthecoder.itchy.gui.VerticalScroll;
import uk.co.nickthecoder.itchy.gui.Window;

public abstract class PosePicker extends Window
{
    protected final Resources resources;

    private PoseResource defaultPoseResource;

    public PosePicker( Resources resources )
    {
        this(resources, null);
    }

    public PosePicker( Resources resources, PoseResource defaultPoseResource )
    {
        super("Pick a Pose");
        this.resources = resources;
        this.defaultPoseResource = defaultPoseResource;

        this.clientArea.setLayout(new VerticalLayout());
        this.clientArea.setFill(true, false);

        PlainContainer container = new PlainContainer();
        container.setLayout(new VerticalLayout());
        VerticalScroll vs = new VerticalScroll(container);

        Component focus = this.createChoices(container);
        this.clientArea.addChild(vs);
        this.clientArea.addStyle("vScrolled");

        PlainContainer buttons = new PlainContainer();
        buttons.addStyle("buttonBar");
        buttons.setLayout(new HorizontalLayout());
        buttons.setXAlignment(0.5f);

        Button cancel = new Button("Cancel");
        cancel.addActionListener(new ActionListener() {

            @Override
            public void action()
            {
                PosePicker.this.hide();
            }

        });
        buttons.addChild(cancel);
        this.clientArea.addChild(buttons);

        if (focus != null) {
            focus.focus();
        }
    }

    protected Component createChoices( Container parent )
    {
        PlainContainer container = new PlainContainer();
        parent.addChild(container);

        Component focus = null;

        GridLayout gridLayout = new GridLayout(container, 5);
        container.setLayout(gridLayout);
        container.addStyle("pickGrid");

        for (String name : this.resources.poseNames()) {
            PoseResource poseResource = this.resources.getPoseResource(name);

            AbstractComponent component = this.createPoseButton(poseResource);
            if (poseResource == this.defaultPoseResource) {
                focus = component;
            }

            gridLayout.addChild(component);
        }
        gridLayout.endRow();

        return focus;
    }

    private AbstractComponent createPoseButton( final PoseResource poseResource )
    {
        // final Pose pose = poseResource.pose;
        PlainContainer container = new PlainContainer();

        container.setLayout(new VerticalLayout());
        container.setXAlignment(0.5f);

        ImageComponent img = new ImageComponent(poseResource.getThumbnail());
        Button button = new Button(img);
        button.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                PosePicker.this.hide();
                PosePicker.this.pick(poseResource);
            }
        });

        Label label = new Label(poseResource.getName());

        container.addChild(button);
        container.addChild(label);

        return container;
    }

    public abstract void pick( PoseResource poseResource );

}
