/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import uk.co.nickthecoder.itchy.Font;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.jame.JameException;

public abstract class FontPicker extends Window
{
    private final Resources resources;

    public static Label createExample(Font font)
    {
        Label example = new Label("AaBbCcDdEeFfGg");
        example.addStyle("exampleFont");
        example.setFont(font);

        return example;
    }

    public FontPicker(Resources resources)
    {
        this(resources, null);
    }

    public FontPicker(Resources resources, Font initialValue)
    {
        super("Pick a Font");
        this.resources = resources;
        clientArea.setLayout(new VerticalLayout());
        clientArea.setXAlignment(0.5f);

        PlainContainer container = new PlainContainer();
        container.setFill(true, false);
        VerticalScroll vs = new VerticalScroll(container);
        vs.setNaturalHeight(400);

        Component focus = this.createFonts(container, initialValue);
        clientArea.addChild(vs);
        clientArea.addStyle("vScrolled");

        PlainContainer buttons = new PlainContainer();
        buttons.addStyle("buttonBar");
        buttons.setLayout(new HorizontalLayout());
        buttons.setXAlignment(0.5f);

        Button cancel = new Button("Cancel");
        cancel.addActionListener(new ActionListener()
        {

            @Override
            public void action()
            {
                FontPicker.this.hide();
            }

        });
        buttons.addChild(cancel);
        clientArea.addChild(buttons);

        if (focus != null) {
            focus.focus();
        }
    }

    private Component createFonts(Container container, Font initialValue)
    {
        container.setLayout(new VerticalLayout());
        Component focus = null;

        for (String name : resources.fontNames()) {
            Font font = resources.getFont(name);

            try {
                AbstractComponent component = this.createButton(font);

                container.addChild(component);
                if (font == initialValue) {
                    focus = component;
                }
            } catch (JameException e) {
                // ignore it
            }
        }
        return focus;
    }

    private AbstractComponent createButton(final Font font)
        throws JameException
    {
        Button button = new Button();
        button.setLayout(new VerticalLayout());
        button.setXAlignment(0.5f);

        button.addChild(createExample(font));
        button.addChild(new Label(font.getName()));

        button.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                FontPicker.this.hide();
                FontPicker.this.pick(font);
            }
        });

        return button;
    }

    public abstract void pick(Font font);

}
