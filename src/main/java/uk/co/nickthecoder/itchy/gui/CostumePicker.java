/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import uk.co.nickthecoder.itchy.Costume;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.jame.Surface;

public abstract class CostumePicker extends Window
{
    private final Resources resources;

    private final String nullText;

    public CostumePicker( Resources resources )
    {
        this(resources, null);
    }

    public CostumePicker( Resources resources, String nullText )
    {
        super("Pick a Costume");
        this.nullText = nullText;
        this.resources = resources;

        PlainContainer container = new PlainContainer();
        VerticalScroll vs = new VerticalScroll(container);

        this.createCostumes(container);
        this.clientArea.addChild(vs);
    }

    private void createCostumes( PlainContainer container )
    {
        GridLayout gridLayout = new GridLayout(container, 6);
        container.addStyle("pickGrid");
        container.setLayout(gridLayout);

        if (this.nullText != null) {
            gridLayout.addChild(this.createButton(null));
        }

        for (String name : this.resources.costumeNames()) {
            Costume costume = this.resources.getCostume(name);

            AbstractComponent component = this.createButton(costume);

            gridLayout.addChild(component);
        }
        gridLayout.endRow();
    }

    private AbstractComponent createButton( final Costume costume)
    {
        // final Pose pose = poseResource.pose;
        PlainContainer container = new PlainContainer();
        container.setLayout(new VerticalLayout());
        container.setXAlignment(0.5f);

        Button button;
        Surface surface = costume == null ? null : costume.getThumbnail();
        if (surface == null) {
            button = new Button(costume == null ? this.nullText : costume.getName());
        } else {
            ImageComponent img = new ImageComponent(surface);
            button = new Button(img);
        }

        button.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                CostumePicker.this.hide();
                CostumePicker.this.pick(costume);
            }
        });

        Label label = new Label(costume == null ? this.nullText : costume.getName());

        container.addChild(button);
        container.addChild(label);

        return container;
    }

    public abstract void pick( Costume costume );

}
