/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.FontResource;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.AbstractComponent;
import uk.co.nickthecoder.itchy.gui.PlainContainer;
import uk.co.nickthecoder.itchy.gui.HorizontalLayout;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.VerticalLayout;
import uk.co.nickthecoder.itchy.gui.VerticalScroll;
import uk.co.nickthecoder.itchy.gui.Window;
import uk.co.nickthecoder.jame.JameException;

public abstract class FontPicker extends Window
{
    private final Resources resources;

    public static Label createExample( FontResource fontResource )
    {
        Label example = new Label("AaBbCcDdEeFfGg");
        example.addStyle("exampleFont");
        example.setFont(fontResource.font);

        return example;
    }

    public FontPicker( Resources resources )
    {
        this(resources, null);
    }

    public FontPicker( Resources resources, FontResource initialValue )
    {
        super("Pick a Font");
        this.resources = resources;
        this.clientArea.setLayout(new VerticalLayout());
        this.clientArea.setXAlignment(0.5f);

        PlainContainer container = new PlainContainer();
        container.setFill(true, false);
        VerticalScroll vs = new VerticalScroll(container);
        vs.setNaturalHeight(400);

        Component focus = this.createFonts(container, initialValue);
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
                FontPicker.this.hide();
            }

        });
        buttons.addChild(cancel);
        this.clientArea.addChild(buttons);

        if (focus != null) {
            focus.focus();
        }
    }

    private Component createFonts( Container container, FontResource initialValue )
    {
        container.setLayout(new VerticalLayout());
        Component focus = null;

        for (String name : this.resources.fontNames()) {
            FontResource fontResource = this.resources.getFontResource(name);

            try {
                AbstractComponent component = this.createButton(fontResource);

                container.addChild(component);
                if (fontResource == initialValue) {
                    focus = component;
                }
            } catch (JameException e) {
                // ignore it
            }
        }
        return focus;
    }

    private AbstractComponent createButton( final FontResource fontResource )
        throws JameException
    {
        Button button = new Button();
        button.setLayout(new VerticalLayout());
        button.setXAlignment(0.5f);

        button.addChild(createExample(fontResource));
        button.addChild(new Label(fontResource.getName()));

        button.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                FontPicker.this.hide();
                FontPicker.this.pick(fontResource);
            }
        });

        return button;
    }

    public abstract void pick( FontResource fontResource );

}
