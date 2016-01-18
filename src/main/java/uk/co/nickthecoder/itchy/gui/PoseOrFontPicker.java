/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import uk.co.nickthecoder.itchy.FontResource;
import uk.co.nickthecoder.itchy.PoseResource;
import uk.co.nickthecoder.itchy.Resources;

public abstract class PoseOrFontPicker extends PosePicker
{
    public static Label createExample( FontResource fontResource )
    {
        Label example = new Label("Abc");
        example.addStyle("exampleFont");
        example.setFont(fontResource.font);

        return example;
    }

    public PoseOrFontPicker( Resources resources )
    {
        super(resources);
    }

    @Override
    protected Component createChoices( Container parent )
    {
        super.createChoices(parent);

        PlainContainer container = new PlainContainer();
        parent.addChild(container);

        Component focus = null;

        GridLayout gridLayout = new GridLayout(container, 5);
        container.setLayout(gridLayout);
        container.addStyle("pickGrid");

        for (String name : this.resources.fontNames()) {
            FontResource fontResource = this.resources.getFontResource(name);

            AbstractComponent component = this.createFontButton(fontResource);

            gridLayout.addChild(component);
        }
        gridLayout.endRow();

        return focus;
    }

    private AbstractComponent createFontButton( final FontResource fontResource )
    {
        // final Pose pose = poseResource.pose;
        PlainContainer container = new PlainContainer();

        container.setLayout(new VerticalLayout());
        container.setXAlignment(0.5f);

        Label example = createExample(fontResource);
        GuiButton button = new GuiButton(example);
        button.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                PoseOrFontPicker.this.hide();
                PoseOrFontPicker.this.pick(fontResource);
            }
        });

        Label label = new Label(fontResource.getName());

        container.addChild(button);
        container.addChild(label);

        return container;
    }

    @Override
    public abstract void pick( PoseResource poseResource );

    public abstract void pick( FontResource fontResource );

}
