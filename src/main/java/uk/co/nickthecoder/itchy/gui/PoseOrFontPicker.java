/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import uk.co.nickthecoder.itchy.Font;
import uk.co.nickthecoder.itchy.PoseResource;
import uk.co.nickthecoder.itchy.Resources;

public abstract class PoseOrFontPicker extends PoseResourcePicker
{
    public static Label createExample( Font font)
    {
        Label example = new Label("Abc");
        example.addStyle("exampleFont");
        example.setFont(font);

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
            Font font = this.resources.getFont(name);

            AbstractComponent component = this.createFontButton(font);

            gridLayout.addChild(component);
        }
        gridLayout.endRow();

        return focus;
    }

    private AbstractComponent createFontButton( final Font font)
    {
        // final Pose pose = poseResource.pose;
        PlainContainer container = new PlainContainer();

        container.setLayout(new VerticalLayout());
        container.setXAlignment(0.5f);

        Label example = createExample(font);
        Button button = new Button(example);
        button.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                PoseOrFontPicker.this.hide();
                PoseOrFontPicker.this.pick(font);
            }
        });

        Label label = new Label(font.getName());

        container.addChild(button);
        container.addChild(label);

        return container;
    }

    @Override
    public abstract void pick( PoseResource poseResource );

    public abstract void pick( Font fontResource );

}
