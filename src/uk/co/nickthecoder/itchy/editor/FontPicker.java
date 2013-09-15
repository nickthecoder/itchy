/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.FontResource;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Button;
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
        example.setFont( fontResource.font );
        
        return example;
    }
    
    public FontPicker( Resources resources )
    {
        super("Pick a Font");
        this.resources = resources;

        Container container = new Container();
        VerticalScroll vs = new VerticalScroll(container);

        this.createFonts(container);
        this.clientArea.addChild(vs);
        this.clientArea.addStyle("vScrolled");
    }

    private void createFonts( Container container )
    {
        container.setLayout(new VerticalLayout());

        for (String name : this.resources.fontNames()) {
            FontResource fontResource = this.resources.getFontResource(name);

            try {
                Component component = this.createButton(fontResource);
    
                container.addChild(component);
            } catch (JameException e) {
                // ignore it
            }
        }
    }

    private Component createButton( final FontResource fontResource )
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
                FontPicker.this.destroy();
                FontPicker.this.pick(fontResource);
            }
        });

        return button;
    }

    public abstract void pick( FontResource fontResource );

}
